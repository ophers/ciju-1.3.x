/*
 * Copyright (C) 2012-2016 Opher Shachar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ciju.client;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.event.PrintEvent;
import org.ciju.client.event.EventDispatcher;
import org.ciju.client.impl.apache.ApacheConnection;
import org.ciju.client.impl.ipp.Handler;
import org.ciju.client.ipp.IppConnection;

/**
 *
 * @author Opher Shachar
 */
public class PrintServer extends PrintServiceLookup {

    private final SecurityManager sm;
    private final URI uri;
    private final Proxy proxy;

    // Logging facilities
    private static final String packageName;
    /* package */ static final Logger logger;
    static {
        String name = PrintServer.class.getName();
        packageName = name.substring(0, name.lastIndexOf('.'));
        logger = Logger.getLogger(packageName, "org/ciju/ResourceStrings");
    }
    /* package */ static final ResourceBundle resourceStrings = ResourceBundle.getBundle("org/ciju/ResourceStrings");

    // Register the IPP ContentHandler and URLStreamHandler
    private static final String REGISTER_HANDLERS = packageName + ".RegisterHandlers";
    private static final boolean hndlrs;
    static {
        hndlrs = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return registerHandlers();
            }
        });
    }
    
    private static boolean registerHandlers() {
        try {
            boolean doit = Boolean.parseBoolean(System.getProperty(REGISTER_HANDLERS, "true"));
            if (doit) {
                // Register content handler
                String pkgs = System.getProperty("java.content.handler.pkgs", "");
                StringBuilder prop = new StringBuilder(packageName);
                if (pkgs.length() > 0)
                    prop.append('|').append(pkgs);
                System.setProperty("java.content.handler.pkgs", prop.toString());
                
                // Register protocol handler
                pkgs = System.getProperty("java.protocol.handler.pkgs", "");
                prop.setLength(packageName.length());
                if (pkgs.length() > 0)
                    prop.append('|').append(pkgs);
                System.setProperty("java.protocol.handler.pkgs", prop.toString());
                return true;
            }
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "FAILED TO REGISTER IPP CONTENTHANDLER AND/OR URLSTREAMHANDLER!", ex);
        }
        return false;
    }

    // Event Dispatcher thread initialization
    private static Thread eventDispatchThread = null;
    private static EventDispatcher eventDispatcher = null;
    
    /**
     * This method starts the event dispatch thread the first time it
     * is called.  The event dispatch thread will be started only
     * if someone registers a listener and an event is fired.
     */
    private static synchronized void startEventDispatchThreadIfNecessary() {
        if (eventDispatchThread == null) {
            logger.log(Level.INFO, "STARTING EVENT DISPATCH THREAD.");
            eventDispatcher = new EventDispatcher();
            eventDispatchThread = new Thread(eventDispatcher);
            eventDispatchThread.setDaemon(true);
            eventDispatchThread.start();
        }
    }
    
    protected static void enqueuePrintEvent(PrintEvent event, List<?> listeners) {
        startEventDispatchThreadIfNecessary();
        eventDispatcher.enqueuePrintEvent(event, listeners);
    }

    public PrintServer() {
        sm = System.getSecurityManager();
        uri = null;
        proxy = null;
    }
    
    protected PrintServer(URI uri, Proxy proxy) {
        if (!uri.getScheme().equalsIgnoreCase("ipp") && 
            !uri.getScheme().equalsIgnoreCase("ipps"))
            throw new IllegalArgumentException(resourceStrings.getString("ONLY 'IPP' AND 'IPPS' URIS ARE SUPPORTED."));
        sm = System.getSecurityManager();
        this.uri = uri;
        this.proxy = proxy;
    }

    private enum Type { CUPS, DEFAULT }
    private static Type checkServerType(URI uri, Proxy proxy) {
        // TODO: devise a check to determine IPP Server type
        return Type.CUPS;
    }

    /**
     * Create an instance of a {@link PrintServer} given its {@link URI uri} and
     * a {@link Proxy}.
     * 
     * @param uri the URI of the Print Server
     * @param proxy the proxy to use. May be null.
     * @return
     */
    public static PrintServer create(URI uri, Proxy proxy) {
        switch (checkServerType(uri, proxy)) {
            case CUPS:
                return new CupsServer(uri, proxy);
            case DEFAULT:
                return new PrintServer(uri, proxy);
            default:
                logger.logp(Level.SEVERE, PrintServer.class.getName(), "create",
                        "THE SERVER REPRESENTED BY {0} IS UNSUPPORTED!", uri);
                throw new AssertionError("The server represented by " + uri + " is unsupported!");
        }
    }

    /**
     * Get the value of uri
     *
     * @return the value of uri
     */
    public URI getUri() {
        return uri;
    }

    protected IppConnection getConnection() throws IOException {
        if (uri == null)
            throw new IllegalStateException(resourceStrings.getString("THIS DEFAULT INSTANCE HAS NO URI TO A PRINT-SERVER."));
        
        return getConnection(uri, proxy);
    }

    private enum ConnLib { URLC, APACHE }
    private static ConnLib connLib;
    /* package */ static IppConnection getConnection(URI uri, Proxy proxy) throws IOException {
        if (connLib == null) {
            // This is the first connection to be requested. Decide on a connection library.
            synchronized (PrintServer.class) {
                /* FWIW: The Double-checked locking pattern here is safe as connLib is an enum. */
                if (connLib == null) {
                    try {
                        IppConnection conn = new ApacheConnection(uri, proxy);
                        connLib = ConnLib.APACHE;
                        return conn;
                    } catch (NoClassDefFoundError e) {
                        // Apache http client is not available.
                        connLib = ConnLib.URLC;
                    }
                }
            }
        }
        switch (connLib) {
            case APACHE:
                return new ApacheConnection(uri, proxy);
            case URLC:
                return Handler.openConnection(uri, proxy);
            default:
                // We should never get here
                throw new AssertionError(
                        resourceStrings.getString("UNEXPETED CONNECTION LIBRARY CONFIGURED!"));
        }
    }

    @Override
    public PrintService[] getPrintServices(DocFlavor flavor, AttributeSet attributes) {
        if (sm != null)
            sm.checkPrintJobAccess();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintService[] getPrintServices() {
        if (sm != null)
            sm.checkPrintJobAccess();
        // This default instance has no clue as to how to interrogate a Print-CupsServer
        return new PrintService[0];
    }

    @Override
    public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] flavors, AttributeSet attributes) {
        if (sm != null)
            sm.checkPrintJobAccess();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintService getDefaultPrintService() {
        if (sm != null)
            sm.checkPrintJobAccess();
        try {
            IppConnection urlc = getConnection();
//            urlc.
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

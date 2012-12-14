/*
 * Copyright (C) 2012 Opher Shachar
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import javax.print.event.PrintEvent;
import org.ciju.client.event.DispatchPrintEvent;
import org.ciju.client.event.EventDispatcher;
import org.ciju.client.ipp.Handler;
import org.ciju.client.ipp.IppURLConnection;
import org.ciju.cups.CupsServer;

/**
 *
 * @author Opher Shachar
 */
public class PrintServer extends PrintServiceLookup {

    private final SecurityManager sm;
    private final URI uri;
    private final Proxy proxy;
    private enum Type { CUPS }

    // Logging facilities
    private static final String packageName;
    /* package */ static final Logger logger;
    static {
        String name = PrintServer.class.getName();
        packageName = name.substring(0, name.lastIndexOf('.'));
        logger = Logger.getLogger(packageName);
    }

    // Register the IPP ContentHandler and URLStreamHandler
    private static final String REGISTER_HANDLERS = "org.ciju.client.RegisterHandlers";
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
                StringBuilder prop = new StringBuilder(packageName)
                        .append('|')
                        .append(System.getProperty("java.content.handler.pkgs", ""));
                if (prop.length() == packageName.length() + 1)
                    prop.setLength(packageName.length());
                System.setProperty("java.content.handler.pkgs", prop.toString());
                
                // Register protocol handler
                prop.setLength(0);
                prop.append(packageName)
                    .append('|')
                    .append(System.getProperty("java.protocol.handler.pkgs", ""));
                if (prop.length() == packageName.length() + 1)
                    prop.setLength(packageName.length());
                System.setProperty("java.protocol.handler.pkgs", prop.toString());
                return true;
            }
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "Failed to register IPP ContentHandler and/or URLStreamHandler!", ex);
        }
        return false;
    }

    // Event Dispatcher thread initialization
    private static Thread eventDispatchThread = null;
    private static EventDispatcher eventDispatcher = null;
    private static DispatchPrintEvent dispatchOther = null;
    
    /**
     * This method starts the event dispatch thread the first time it
     * is called.  The event dispatch thread will be started only
     * if someone registers a listener and an event is fired.
     */
    private static synchronized void startEventDispatchThreadIfNecessary() {
        if (eventDispatchThread == null) {
            logger.log(Level.INFO, "Starting event dispatch thread.");
            eventDispatcher = new EventDispatcher(dispatchOther);
            eventDispatchThread = new Thread(eventDispatcher);
            eventDispatchThread.setDaemon(true);
            eventDispatchThread.start();
        }
    }
    
    protected static synchronized void setDispatchPrintEvent(DispatchPrintEvent dpe) {
        if (eventDispatchThread != null)
            throw new IllegalStateException("Event dispatch thread already started.");
        else if (dispatchOther != null)
            throw new IllegalStateException("Custom print event dispacher already set.");
        dispatchOther = dpe;
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
            throw new IllegalArgumentException("Only 'ipp' and 'ipps' URIs are supported.");
        sm = System.getSecurityManager();
        this.uri = uri;
        this.proxy = proxy;
    }

    public static PrintServer create(URI uri, Proxy proxy) {
        switch (checkServerType(uri, proxy)) {
            case CUPS:
                return new CupsServer(uri, proxy);
            default:
                throw new AssertionError();
        }
    }

    private static Type checkServerType(URI uri, Proxy proxy) {
        // Only Type currently known
        return Type.CUPS;
    }

    /**
     * Get the value of uri
     *
     * @return the value of uri
     */
    public URI getUri() {
        return uri;
    }

    protected IppURLConnection getConnection() throws IOException {
        if (uri == null)
            throw new IllegalStateException("This default instance has no URI to a Print-Server.");
        
        return getConnection(uri, proxy);
    }

    /* package */ static IppURLConnection getConnection(URI uri, Proxy proxy) throws IOException {
        if (hndlrs) {
            if (proxy == null)
                return (IppURLConnection) uri.toURL().openConnection();
            else
                return (IppURLConnection) uri.toURL().openConnection(proxy);
        }
        else {
            return Handler.openConnection(uri, proxy);
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
            IppURLConnection urlc = getConnection();
//            urlc.
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

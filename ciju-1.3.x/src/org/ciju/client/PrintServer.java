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
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import org.ciju.client.ipp.Handler;
import org.ciju.client.ipp.IppURLConnection;

/**
 *
 * @author Opher Shachar
 */
public class PrintServer extends PrintServiceLookup {

    private URI uri;

    public PrintServer() {
    }

    public PrintServer(URI uri) {
        if (!uri.getScheme().equalsIgnoreCase("ipp") && !uri.getScheme().equalsIgnoreCase("ipps"))
            throw new IllegalArgumentException("Only 'ipp' and 'ipps' URIs are supported.");
        this.uri = uri;
    }

    /**
     * Get the value of uri
     *
     * @return the value of uri
     */
    public URI getUri() {
        return uri;
    }

    protected IppURLConnection getConnection(Proxy proxy) throws IOException {
        if (uri == null)
            throw new IllegalStateException("This default instance has no URI to a Print-Server.");
        
        try {
            return (IppURLConnection) uri.toURL().openConnection(proxy);
        }
        catch (MalformedURLException e) {
            return Handler.openConnection(uri, proxy);
        }
    }
    
    @Override
    public PrintService[] getPrintServices(DocFlavor flavor, AttributeSet attributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintService[] getPrintServices() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] flavors, AttributeSet attributes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintService getDefaultPrintService() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

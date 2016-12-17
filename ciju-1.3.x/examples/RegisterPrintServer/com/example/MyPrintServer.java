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

package com.example;

import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.AttributeSet;
import org.ciju.client.PrintServer;

/**
 *
 * @author opher
 */
public class MyPrintServer extends javax.print.PrintServiceLookup {
    
    private static final PrintServiceLookup PS = initPrintServer();
    private static PrintServiceLookup initPrintServer() {
        // Use this package privilege instead of calling code's
        return AccessController.doPrivileged(new PrivilegedAction<PrintServiceLookup>() {
            public PrintServiceLookup run() {
                try {
                    // Normally get URI and authentication dynamically from configuration somewhere
                    final URI uri = new URI("ipps", "ps1.sales.local", "/", null);
                    final PasswordAuthentication authn = new PasswordAuthentication("user", "password".toCharArray());
                    // Create a org.​ciju.​client.PrintServer
                    return PrintServer.create(uri, null, authn);
                } catch (URISyntaxException ex) {
                    throw new RuntimeException(ex);
                    // Alternatively, log the error and return null
                }
            }
        });
    }

    // Delegate all methods to PS
    @Override
    public PrintService[] getPrintServices(DocFlavor flavor, AttributeSet attributes) {
        return PS.getPrintServices(flavor, attributes);
    }

    @Override
    public PrintService[] getPrintServices() {
        return PS.getPrintServices();
    }

    @Override
    public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] flavors, AttributeSet attributes) {
        return PS.getMultiDocPrintServices(flavors, attributes);
    }

    @Override
    public PrintService getDefaultPrintService() {
        return PS.getDefaultPrintService();
    }
}

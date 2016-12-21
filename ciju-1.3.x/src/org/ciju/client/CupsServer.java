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
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.util.logging.Level;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.standard.RequestingUserName;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.CupsEncoding;
import org.ciju.ipp.CupsRequest;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppException;
import org.ciju.ipp.IppResponse;
import org.ciju.ipp.attribute.GenericAttribute;


public class CupsServer extends PrintServer {

//    // Logging facilities
//    /* package */ static final Logger logger;
//    static {
//        String name = PrintServer.class.getName();
//        logger = Logger.getLogger(name.substring(0, name.lastIndexOf('.')));
//    }

    public CupsServer(URI uri, Proxy proxy, PasswordAuthentication authn) {
        super(uri, proxy, authn);
    }

    private CupsRequest createRequest(CupsEncoding.OpCode opCode, IppEncoding.GroupTag gTag) {
        CupsRequest req = new CupsRequest(opCode, gTag);
        req.addOperationAttribute(new GenericAttribute("printer-uri", getUri(), IppEncoding.ValueTag.URI));
        req.addOperationAttribute(new RequestingUserName(getUserName(), req.getLocale()));
        return req;
    }

    @Override
    public PrintService[] getPrintServices(DocFlavor flavor, AttributeSet attributes) {
        return super.getPrintServices(flavor, attributes); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintService[] getPrintServices() {
        return super.getPrintServices(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] flavors, AttributeSet attributes) {
        return super.getMultiDocPrintServices(flavors, attributes); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintService getDefaultPrintService() {
        try {
            CupsRequest req = createRequest(CupsEncoding.OpCode.CUPS_GET_DEFAULT, IppEncoding.GroupTag.END);
            IppConnection conn = getConnection().setIppRequest(req);
            CupsPrinter prt = new CupsPrinter(this);
            IppResponse<CupsPrinter> resp = conn.getContent(prt);
            return resp.getObject();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IppException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

}

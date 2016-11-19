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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import javax.print.Doc;
import static org.ciju.client.PrintServer.resourceStrings;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.CupsEncoding;
import org.ciju.ipp.CupsRequest;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppException;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppObjectFactory;
import org.ciju.ipp.attribute.GenericAttribute;

/**
 *
 * @author Opher Shachar
 */
public class CupsPrinter extends IppPrinter {

    protected CupsPrinter(URI uri, Proxy proxy) {
        super(uri, proxy);
    }

    // Stub for some unique CUPS Printer methods...

    /**
     * The CUPS-Get-PPD operation gets a PPD file from the server. If the PPD file is found
     * the PPD file is returned.<br/>
     * If the PPD file cannot be served by the local server because it is an external printer,
     * an {@link IppException} with status <tt>cups-see-other</tt> is thrown with the correct
     * URI to use in the {@linkplain IppException#getIppResponse() included response}.<br/>
     * If the PPD file does not exist, an {@link IppException} with status <tt>client-error-not-found</tt>
     * is thrown.
     * 
     * @return The PPD file for this printer.
     * @throws IOException if an I/O error occurs.
     * @throws IppException if the returned status-code doesn't indicate <i>success</i>.
     * @deprecated
     */
    @Deprecated
    public Doc getPPD() throws IOException, IppException {
        CupsRequest req = new CupsRequest(CupsEncoding.OpCode.CUPS_GET_PPD, IppEncoding.GroupTag.END);
        req.addOperationAttribute(new GenericAttribute("printer-uri", this.getUri(), IppEncoding.ValueTag.URI));
        IppConnection conn = getConnection().setIppRequest(req);
        return conn.getContent((IppObject) null).getDoc();
    }

    public void setAsDefault() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void rejectJobs() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void acceptJobs() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     *
     * @return
     */
    @Override
    public Collection<CupsJob> getJobs() {
        return getJobs(new ArrayList<CupsJob>(), new IppObjectFactory<CupsJob>() {
            public CupsJob create(IppEncoding.GroupTag gt) {
                if (!canCreate(gt))
                    throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("CANNOT CREATE THIS TYPE OF OBJECT: {0}"), gt));
                return new CupsJob(CupsPrinter.this);
            }

            public boolean canCreate(IppEncoding.GroupTag gt) {
                return gt == IppEncoding.GroupTag.JOB;
            }
        });
    }
}

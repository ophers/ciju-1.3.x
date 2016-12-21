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
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.print.Doc;
import javax.print.attribute.standard.RequestingUserName;
import static org.ciju.client.PrintServer.logger;
import static org.ciju.client.PrintServer.resourceStrings;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.CupsEncoding;
import org.ciju.ipp.CupsRequest;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppException;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppObjectFactory;
import org.ciju.ipp.IppResponse;

/**
 *
 * @author Opher Shachar
 */
public class CupsPrinter extends IppPrinter {

    private final CupsServer prtsrv;

    protected CupsPrinter(CupsServer prtsrv, URI uri) {
        super(prtsrv, uri);
        this.prtsrv = prtsrv;
    }

    protected CupsPrinter(CupsServer prtsrv) {
        super(prtsrv);
        this.prtsrv = prtsrv;
    }

    private IppResponse<IppObject> getContent(IppConnection conn) throws IppException, IOException {
        return conn.getContent((IppObject) null);
    }

    private CupsRequest createRequest(CupsEncoding.OpCode opCode, IppEncoding.GroupTag gTag) {
        CupsRequest req = new CupsRequest(opCode, gTag);
        req.addOperationAttribute(getPrinterUri());
        req.addOperationAttribute(new RequestingUserName(prtsrv.getUserName(), req.getLocale()));
        return req;
    }

    @Override
    protected IppConnection getConnection() throws IOException {
        URI uri = getPrinterUri().getURI();
        try {
            // create a relative uri
            uri = new URI(null, null, uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException ex) {
            // cannot happen
            logger.log(Level.SEVERE, null, ex);
        }
        return prtsrv.getConnection(uri);
    }

    // Stub for some unique CUPS Printer methods...
    /**
     * The CUPS-Get-PPD operation gets a PPD file from the server. If the PPD file is found
     * the PPD file is returned.<br>
     * If the PPD file cannot be served by the local server because it is an external printer,
     * an {@link IppException} with status <tt>cups-see-other</tt> is thrown with the correct
     * URI to use in the {@linkplain IppException#getIppResponse() included response}.<br>
     * If the PPD file does not exist, an {@link IppException} with status <tt>client-error-not-found</tt>
     * is thrown.
     * 
     * @return The PPD file for this printer.
     * @throws IOException if an I/O error occurs.
     * @throws IppException if the returned status-code doesn't indicate <i>success</i>.
     */
    public Doc getPPD() throws IOException, IppException {
        CupsRequest req = createRequest(CupsEncoding.OpCode.CUPS_GET_PPD, IppEncoding.GroupTag.END);
        IppConnection conn = getConnection().setIppRequest(req);
        return getContent(conn).getDoc();
    }

    /**
     * The CUPS-Set-Default operation (0x400A) sets the default printer destination for all
     * clients when a resource name of "/printers" is specified.
     * 
     * @throws IOException if an I/O error occurs.
     * @throws IppException if the returned status-code doesn't indicate <i>success</i>.
     */
    public void setAsDefault() throws IOException, IppException {
        CupsRequest req = createRequest(CupsEncoding.OpCode.CUPS_SET_DEFAULT, IppEncoding.GroupTag.END);
        IppConnection conn = getConnection().setIppRequest(req);
        getContent(conn);
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

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
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.MultiDocPrintJob;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterURI;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import static org.ciju.client.PrintServer.logger;
import static org.ciju.client.PrintServer.resourceStrings;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppException;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppObjectFactory;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppResponse;

/**
 *
 * @author Opher Shachar
 */
public class IppPrinter extends IppObject implements PrintService, MultiDocPrintService {
    private final HashPrintServiceAttributeSet psas;
    /* See javadoc for overview. Presumably there'll be few (if more than one)
       listners registering but many more events fireing */
    private final CopyOnWriteArrayList<PrintServiceAttributeListener> psall;
    private final PrintServer prtsrv;

    protected IppPrinter(PrintServer prtsrv, URI uri) {
        if (uri == null)
            throw new NullPointerException("Uri cannot be null!");
        this.prtsrv = prtsrv;
        psall = new CopyOnWriteArrayList<PrintServiceAttributeListener>();
        psas = new HashPrintServiceAttributeSet();
    }

    protected IppPrinter(PrintServer prtsrv) {
        this.prtsrv = prtsrv;
        psall = new CopyOnWriteArrayList<PrintServiceAttributeListener>();
        psas = new HashPrintServiceAttributeSet();
    }

    /**
     * Get the value of printer-uri
     * @return the value of printer-uri
     */
    public PrinterURI getPrinterUri() {
        return getAttribute(PrinterURI.class);
    }

    private IppResponse<IppObject> getContent(IppConnection conn) throws IppException, IOException {
        return conn.getContent((IppObject) null);
    }

    private IppRequest createRequest(IppEncoding.OpCode opCode, IppEncoding.GroupTag gTag) {
        IppRequest req = new IppRequest(opCode, gTag);
        req.addOperationAttribute(getPrinterUri());
        req.addOperationAttribute(new RequestingUserName(prtsrv.getUserName(), req.getLocale()));
        return req;
    }

    public Collection<? extends IppJob> getJobs() {
        return getJobs(new ArrayList<IppJob>(), new IppObjectFactory<IppJob>() {
            public IppJob create(IppEncoding.GroupTag gt) {
                if (!canCreate(gt))
                    throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("CANNOT CREATE THIS TYPE OF OBJECT: {0}"), gt));
                return new IppJob(IppPrinter.this);
            }

            public boolean canCreate(IppEncoding.GroupTag gt) {
                return gt == IppEncoding.GroupTag.JOB;
            }
        });
    }

    public <T extends IppJob> Collection<T> getJobs(Collection<T> coll, IppObjectFactory<T> fact) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public <T extends IppJob> void getJobs(ListIterator<T> iter, IppObjectFactory<T> fact) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected IppConnection getConnection() throws IOException {
        URI uri = getPrinterUri().getURI();
        try {
            if (uri.isAbsolute() && uri.getAuthority().equals("localhost"))
                // create a relative uri
                uri = new URI(null, null, uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException ex) {
            // cannot happen
            logger.log(Level.SEVERE, null, ex);
        }
        return prtsrv.getConnection(uri);
    }

    public String getName() {
        return getAttribute(PrinterName.class).toString();
    }

    public DocPrintJob createPrintJob() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public MultiDocPrintJob createMultiDocPrintJob() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) {
        if (listener != null)
            psall.add(listener);
    }

    public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) {
        if (listener != null)
            psall.remove(listener);
    }
    
    private void raisePrintServiceAttributeEvent(PrintServiceAttributeEvent psae) {
        for (PrintServiceAttributeListener psal : psall)
            psal.attributeUpdate(psae);
    }
    
    private void enqueuePrintServiceAttributeEvent(PrintServiceAttributeEvent psae) {
        if (!psall.isEmpty())
            PrintServer.enqueuePrintEvent(psae, psall);
    }

    public PrintServiceAttributeSet getAttributes() {
        return AttributeSetUtilities.unmodifiableView(psas);
    }

    public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
        return category.cast(psas.get(category));
    }

    public DocFlavor[] getSupportedDocFlavors() {
        // FIXME: Get the 'document-format-supported' attribute from a Get-Printer-Attributes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isDocFlavorSupported(DocFlavor flavor) {
        for (DocFlavor flavor1 : getSupportedDocFlavors() /* by contract never null */)
            if (flavor.equals(flavor1))
                return true;
        return false;
    }

    public Class<?>[] getSupportedAttributeCategories() {
        // FIXME: How to accomplish this???
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isAttributeCategorySupported(Class<? extends Attribute> category) {
        return getSupportedAttributeValues(category, null, null) != null;
    }

    public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
        // FIXME: Get the 'xxx-default' attribute from a Get-Printer-Attributes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object getSupportedAttributeValues(Class<? extends Attribute> category, DocFlavor flavor, AttributeSet attributes) {
        // FIXME: How to accomplish this???
        // For now just get the 'xxx-supported' attribute from a Get-Printer-Attributes
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isAttributeValueSupported(Attribute attrval, DocFlavor flavor, AttributeSet attributes) {
        if (attributes == null)
            attributes = new HashAttributeSet(attrval);
        else
            attributes.add(attrval);
        AttributeSet unsup = getUnsupportedAttributes(flavor, attributes);
        return unsup == null || !unsup.containsKey(attrval.getCategory());
    }

    public AttributeSet getUnsupportedAttributes(DocFlavor flavor, AttributeSet attributes) {
        // FIXME: Accomplish this with a Validate-Job IPP Operation
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ServiceUIFactory getServiceUIFactory() {
        // We won't provide a UI any time soon ...
        return null;
    }

    protected boolean addAttribute(Attribute a) {
        return psas.add(a);
    }

    protected boolean addAllAttributes(AttributeSet as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean seenAG;
    protected boolean newAttributeGroup(IppEncoding.GroupTag gt) {
        if (gt == IppEncoding.GroupTag.PRINTER)
            if (seenAG)
                throw new IllegalStateException("Already seen this GroupTag. Perhaps you need to use an IppMultiObject?");
            else
                return seenAG = true;
        else
            return false;
    }
    
// <editor-fold desc="IPP operations">
    
    public void disable() throws IOException, IppException {
        IppRequest req = createRequest(IppEncoding.OpCode.DISABLE_PRINTER, IppEncoding.GroupTag.END);
        IppConnection conn = getConnection().setIppRequest(req);
        getContent(conn);
    }
    
// </editor-fold>
}

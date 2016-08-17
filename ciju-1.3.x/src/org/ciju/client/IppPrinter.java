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
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
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
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import static org.ciju.client.PrintServer.resourceStrings;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppObjectFactory;

/**
 *
 * @author Opher Shachar
 */
public class IppPrinter extends IppObject implements PrintService, MultiDocPrintService {
    private HashPrintServiceAttributeSet psas;
    /* See javadoc for overview. Presumably there'll be few (if more than one)
       listners registering but many more events fireing */
    private final CopyOnWriteArrayList<PrintServiceAttributeListener> psall;
    private final URI uri;
    private final Proxy proxy;

    protected IppPrinter(URI uri, Proxy proxy) {
        if (uri == null)
            throw new NullPointerException("Uri cannot be null!");
        this.uri = uri;
        this.proxy = proxy;
        psall = new CopyOnWriteArrayList<PrintServiceAttributeListener>();
    }

    /**
     * Get the value of uri
     *
     * @return the value of uri
     */
    public URI getUri() {
        return uri;
    }

    public Collection<? extends IppJob> getJobs() {
        return getJobs(new ArrayList<IppJob>(), new IppObjectFactory<IppJob>() {
            public IppJob create(IppEncoding.GroupTag gt) {
                if (!canCreate(gt))
                    throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("CANNOT CREATE THIS TYPE OF OBJECT: {0}"), new Object[] {gt}));
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
        return PrintServer.getConnection(uri, proxy);
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
        // FIXME: Get the 'printer-description' attribute from a Get-Printer-Attributes
        // psas = ...
        return AttributeSetUtilities.unmodifiableView(psas);
    }

    public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
        return category.cast(getAttributes().get(category));
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected boolean addAllAttributes(AttributeSet as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected boolean newAttributeGroup(IppEncoding.GroupTag gt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

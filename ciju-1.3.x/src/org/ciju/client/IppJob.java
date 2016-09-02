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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.print.CancelablePrintJob;
import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.MultiDoc;
import javax.print.MultiDocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAttributeEvent;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import static org.ciju.client.PrintServer.logger;
import static org.ciju.client.PrintServer.resourceStrings;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.attribute.GenericAttributeSet;

/**
 *
 * @author Opher Shachar
 */
public class IppJob extends IppObject implements DocPrintJob, MultiDocPrintJob, CancelablePrintJob {

    private final IppPrinter printer;
    /* See javadoc for overview. Presumably there'll be few (if more than one)
       listners registering but many more events fireing */
    private final CopyOnWriteArrayList<PrintJobListener> pjll;
    private final CopyOnWriteArrayList<PrintJobAttributeListenerEntry> pjall;

    protected IppJob(IppPrinter printer) {
        this.printer = printer;
        this.pjll = new CopyOnWriteArrayList<PrintJobListener>();
        this.pjall = new CopyOnWriteArrayList<PrintJobAttributeListenerEntry>();
    }

    public PrintService getPrintService() {
        return printer;
    }

    public PrintJobAttributeSet getAttributes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addPrintJobListener(PrintJobListener listener) {
        if (listener != null)
            pjll.add(listener);
    }

    public void removePrintJobListener(PrintJobListener listener) {
        if (listener != null)
            pjll.remove(listener);
    }

    private void raisePrintJobEvent(PrintJobEvent pje) {
        switch (pje.getPrintEventType()) {
            case PrintJobEvent.DATA_TRANSFER_COMPLETE:
                for (PrintJobListener pjl : pjll)
                    pjl.printDataTransferCompleted(pje);
                break;
            case PrintJobEvent.REQUIRES_ATTENTION:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobRequiresAttention(pje);
                break;
            case PrintJobEvent.JOB_CANCELED:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobCanceled(pje);
                break;
            case PrintJobEvent.JOB_FAILED:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobFailed(pje);
                break;
            case PrintJobEvent.JOB_COMPLETE:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobCompleted(pje);
                break;
            case PrintJobEvent.NO_MORE_EVENTS:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobNoMoreEvents(pje);
                break;
            default:
                logger.logp(Level.SEVERE, this.getClass().getName(), "raisePrintJobEvent",
                        "PLEASE REPORT TO THE DEVELOPER: THIS PRINTEVENTTYPE {0} IS UNKNOWN!",
                        pje.getPrintEventType());
                assert false : MessageFormat.format(
                        resourceStrings.getString("PLEASE REPORT TO THE DEVELOPER: THIS PRINTEVENTTYPE {0} IS UNKNOWN!"),
                        pje.getPrintEventType());
        }
    }
    
    private void enqueuePrintJobEvent(PrintJobEvent pje) {
        if (!pjll.isEmpty())
            PrintServer.enqueuePrintEvent(pje, pjll);
    }

    public void addPrintJobAttributeListener(PrintJobAttributeListener listener, PrintJobAttributeSet attributes) {
        if (listener != null) {
            pjall.add(new PrintJobAttributeListenerEntry(listener, attributes));
        }
    }

    public void removePrintJobAttributeListener(PrintJobAttributeListener listener) {
        if (listener != null)
            for (int i = 0; i < pjall.size(); i++) {
                PrintJobAttributeListenerEntry pjale = pjall.get(i);
                if (listener.equals(pjale.listner)) {
                    pjall.remove(i);
                    break;
                }
            }
    }

    private List<PrintJobAttributeListener> getListeners(PrintJobAttributeEvent pjae) {
        final ArrayList<PrintJobAttributeListener> list;
        if (pjall.isEmpty())
            return new ArrayList<PrintJobAttributeListener>(0);
        else
            list = new ArrayList<PrintJobAttributeListener>(pjall.size());
        for (PrintJobAttributeListenerEntry pjale : pjall) {
            if (pjale.attributes == null)
                list.add(pjale.listner);
            else {
                PrintJobAttributeSet attrs = pjae.getAttributes();
                for (Attribute pja : pjale.attributes.toArray())
                    if (attrs.containsKey(pja.getCategory())) {
                        list.add(pjale.listner);
                        break;
                    }
            }
        }
        
        list.trimToSize();
        return list;
    }
    
    private void raisePrintJobAttributeEvent(PrintJobAttributeEvent pjae) {
        for (PrintJobAttributeListener pjal : getListeners(pjae))
            pjal.attributeUpdate(pjae);
    }
    
    private void enqueuePrintJobAttributeEvent(PrintJobAttributeEvent pjae) {
        final List<PrintJobAttributeListener> ll = getListeners(pjae);
        if (!ll.isEmpty())
            PrintServer.enqueuePrintEvent(pjae, ll);
    }

    public void print(Doc doc, PrintRequestAttributeSet attributes) throws PrintException {
        print(doc, new GenericAttributeSet(attributes));
    }

    public void print(MultiDoc multiDoc, PrintRequestAttributeSet attributes) throws PrintException {
        print(multiDoc, new GenericAttributeSet(attributes));
    }

    public void print(Doc doc, GenericAttributeSet attributes) throws PrintException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void print(MultiDoc multiDoc, GenericAttributeSet attributes) throws PrintException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void cancel() throws PrintException {
        IppRequest ipp = new IppRequest(IppEncoding.OpCode.CANCEL_JOB, IppEncoding.GroupTag.JOB);
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected boolean addAttribute(Attribute a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected boolean addAllAttributes(AttributeSet as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean seenAG;
    protected boolean newAttributeGroup(IppEncoding.GroupTag gt) {
        if (gt == IppEncoding.GroupTag.JOB)
            if (seenAG)
                throw new IllegalStateException("Already seen this GroupTag. Perhaps you need to use an IppMultiObject?");
            else
                return seenAG = true;
        else
            return false;
    }

    private static class PrintJobAttributeListenerEntry {
        
        private final PrintJobAttributeListener listner;
        private final PrintJobAttributeSet attributes;

        public PrintJobAttributeListener getListner() {
            return listner;
        }

        public PrintJobAttributeSet getAttributes() {
            return attributes;
        }

        /**
         * @param listner musn't be null
         */
        public PrintJobAttributeListenerEntry(PrintJobAttributeListener listner, PrintJobAttributeSet attributes) {
            if (listner == null)
                throw new NullPointerException("listner");
            this.listner = listner;
            this.attributes = attributes;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + this.listner.hashCode();
            hash = 59 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final PrintJobAttributeListenerEntry other = (PrintJobAttributeListenerEntry) obj;
            if (!this.listner.equals(other.listner))
                return false;
            return this.attributes == other.attributes || (this.attributes != null && this.attributes.equals(other.attributes));
        }

    }
}

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

import java.util.ArrayList;
import javax.print.CancelablePrintJob;
import javax.print.Doc;
import javax.print.DocPrintJob;
import javax.print.MultiDoc;
import javax.print.MultiDocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobListener;

/**
 *
 * @author Opher Shachar
 */
public class IppJob implements DocPrintJob, MultiDocPrintJob, CancelablePrintJob {

    private IppPrinter printer;
    private ArrayList<PrintJobListener> pjll;
    private ArrayList<Entry> pjall;

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

    public void addPrintJobAttributeListener(PrintJobAttributeListener listener, PrintJobAttributeSet attributes) {
        if (listener != null) {
            pjall.add(new Entry(listener, attributes));
        }
    }

    public void removePrintJobAttributeListener(PrintJobAttributeListener listener) {
        if (listener != null) {
            pjall.remove(new Entry(listener, null));
        }
    }

    public void print(Doc doc, PrintRequestAttributeSet attributes) throws PrintException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void print(MultiDoc multiDoc, PrintRequestAttributeSet attributes) throws PrintException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void cancel() throws PrintException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class Entry {
        
        private final PrintJobAttributeListener listner;
        private final PrintJobAttributeSet attributes;

        public PrintJobAttributeListener getListner() {
            return listner;
        }

        public PrintJobAttributeSet getAttributes() {
            return attributes;
        }

        public Entry(PrintJobAttributeListener listner, PrintJobAttributeSet attributes) {
            this.listner = listner;
            this.attributes = attributes;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.listner != null ? this.listner.hashCode() : 0);
            hash = 59 * hash + (this.attributes != null ? this.attributes.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Entry other = (Entry) obj;
            if (this.listner != other.listner && (this.listner == null || !this.listner.equals(other.listner))) {
                return false;
            }
            return true;
        }

    }
}

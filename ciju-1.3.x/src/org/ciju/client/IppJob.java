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
import java.util.List;
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
    private ArrayList<PrintJobAttributeListener> pjall;
    private ArrayList<PrintJobAttributeSet> pjasl;

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
            pjall.add(listener);
            pjasl.add(attributes);
        }
    }

    public void removePrintJobAttributeListener(PrintJobAttributeListener listener) {
        if (listener != null) {
            int pos = pjall.indexOf(listener);
            pjall.remove(pos);
            pjasl.remove(pos);
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

}

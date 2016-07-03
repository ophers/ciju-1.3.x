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

import javax.print.Doc;
import javax.print.MultiDoc;
import javax.print.PrintException;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobHoldUntil;
import javax.print.attribute.standard.MediaSize;
import org.ciju.client.impl.ipp.attribute.CupsJobHoldUntil;
import org.ciju.client.impl.ipp.attribute.CupsMediaSize;
import org.ciju.ipp.attribute.GenericAttributeSet;

/**
 *
 * @author Opher Shachar
 */
public class CupsJob extends IppJob {

    protected CupsJob(CupsPrinter printer) {
        super(printer);
    }

    @Override
    public void print(MultiDoc multiDoc, GenericAttributeSet attributes) throws PrintException {
        super.print(multiDoc, substitute(attributes));
    }

    @Override
    public void print(Doc doc, GenericAttributeSet attributes) throws PrintException {
        super.print(doc, substitute(attributes));
    }

    /* package */ static GenericAttributeSet substitute(GenericAttributeSet attributes) {
        Attribute attr;
        // Substitute JobHoldUntil attribute
        attr = attributes.get("job-hold-until");
        if (attr != null && attr instanceof JobHoldUntil)
            attributes.add(new CupsJobHoldUntil((JobHoldUntil) attr));
        // Substitute MediaSize attribute
        attr = attributes.get("media-size");
        if (attr != null && attr instanceof MediaSize) {
            attributes.remove(attr);
            attributes.add(new CupsMediaSize((MediaSize) attr));
        }
        return attributes;
    }

    /* package */ static PrintRequestAttributeSet substitute(PrintRequestAttributeSet attributes) {
        Attribute attr;
        // Substitute JobHoldUntil attribute
        attr = attributes.get(JobHoldUntil.class);
        if (attr != null)
            attributes.add(new CupsJobHoldUntil((JobHoldUntil) attr));
        return attributes;
    }

    // Stub for some unique CUPS Job methods...
    public void moveJob(CupsPrinter printer) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Doc getDocument(int num) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}

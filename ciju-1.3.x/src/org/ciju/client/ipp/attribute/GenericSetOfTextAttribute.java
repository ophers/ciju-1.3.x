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

package org.ciju.client.ipp.attribute;

import java.util.Collection;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.TextSyntax;


public class GenericSetOfTextAttribute extends SetOfTextSyntax
implements DocAttribute, PrintJobAttribute, PrintRequestAttribute, PrintServiceAttribute, SupportedValuesAttribute {

    public GenericSetOfTextAttribute(Collection<? extends TextSyntax> attrs) {
        super(attrs);
    }

    public GenericSetOfTextAttribute(TextSyntax attr) {
        super(attr);
    }

    public Class<? extends Attribute> getCategory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

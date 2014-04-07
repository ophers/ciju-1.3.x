/*
 * Copyright (C) 2013 Opher Shachar
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

package org.ciju.client.impl.ipp.attribute;

import javax.print.attribute.Attribute;
import javax.print.attribute.standard.*;
import static org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public enum Attributes {
    COPIES(Copies.class, ValueTag.INTEGER)
    ;
    
    private Class<? extends Attribute> attrClass;
    private ValueTag syntax;
    Attributes(Class<? extends Attribute> c, ValueTag s) {
        attrClass = c;
        syntax = s;
    }
    
    public static Attribute create(String attr, ValueTag s, Object o) {
        Attributes a = Attributes.valueOf(attr);
//        PageRanges pr = (PageRanges) a.attrClass.newInstance();
        
        return null;
    }

}

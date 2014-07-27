/*
 * Copyright (C) 2012-2014 Opher Shachar
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

package org.ciju.ipp.attribute;

import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
import org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public class GenericValue {
    
    private final ValueTag vt;
    private final Object   value;

    public GenericValue(ValueTag vt, Object value) {
        this.vt = vt;
        this.value = value;
    }

    public ValueTag getValueTag() {
        return vt;
    }

    public Object getValue() {
        return value;
    }
    
    /* package */ static void validateSyntax(Object o) {
        if (o == null)
            throw new NullPointerException("element");
        
        if (o instanceof DateTimeSyntax ||
            o instanceof EnumSyntax ||
            o instanceof IntegerSyntax ||
            o instanceof ResolutionSyntax ||
            o instanceof SetOfIntegerSyntax ||
            o instanceof Size2DSyntax ||
            o instanceof TextSyntax ||
            o instanceof URISyntax)
            return;
        if (o instanceof int[])
            if (((int[]) o).length == 2)
                return;
            else
                throw new IllegalArgumentException("array must hold exactly two elements.");

        throw new ClassCastException("Argument does not implement a known Syntax.");
    }
}

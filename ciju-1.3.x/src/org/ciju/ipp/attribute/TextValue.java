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

import java.util.Locale;
import javax.print.attribute.TextSyntax;

/**
 *
 * @author Opher Shachar
 */
public class TextValue extends TextSyntax {
    private static final long serialVersionUID = -93227186360522961L;

    private final byte[] ba;
    
    public TextValue(String value, Locale locale) {
        super(value, locale);
        ba = null;
    }

    public TextValue(byte[] ba, Locale locale) {
        super("", locale);
        this.ba = ba.clone();
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getValue() {
        return super.getValue(); //To change body of generated methods, choose Tools | Templates.
    }
}

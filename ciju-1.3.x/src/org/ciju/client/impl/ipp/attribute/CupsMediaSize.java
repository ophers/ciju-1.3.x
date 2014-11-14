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

package org.ciju.client.impl.ipp.attribute;

import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import org.ciju.ipp.attribute.GenericAttribute;

/**
 *
 * @author Opher Shachar
 */
public class CupsMediaSize extends GenericAttribute implements PrintRequestAttribute {
    private static final long serialVersionUID = -1890225517384313627L;

    public CupsMediaSize(MediaSize o) {
        super("media", Media.class);
        add(String.format("Custom.%.2fx%.2f%s",
                o.getX(Size2DSyntax.MM),
                o.getY(Size2DSyntax.MM),
                "mm"));
    }
}

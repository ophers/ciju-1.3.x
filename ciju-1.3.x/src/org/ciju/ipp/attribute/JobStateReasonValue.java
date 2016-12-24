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
package org.ciju.ipp.attribute;

import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.JobStateReason;

/**
 *
 * @author Opher Shachar
 */
class JobStateReasonValue extends JobStateReason {

    private static final long serialVersionUID = -8301378134759216819L;

    private final String s;

    public JobStateReasonValue(String s) {
        super(0);
        this.s = s;
    }

    @Override
    protected EnumSyntax[] getEnumValueTable() {
        return new EnumSyntax[] { this };
    }

    @Override
    protected String[] getStringTable() {
        return new String[] { s };
    }
}

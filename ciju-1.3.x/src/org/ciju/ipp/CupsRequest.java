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
package org.ciju.ipp;

import java.util.Locale;

/**
 * This class is just so an {@linkplain IppRequest} may be created with CUPS'
 * operation codes.
 * @author opher
 */
public class CupsRequest extends IppRequest {

    public CupsRequest(CupsEncoding.OpCode opCode, IppEncoding.GroupTag firstGroupTag) {
        this(opCode, 1, Locale.getDefault(), firstGroupTag);
    }

    public CupsRequest(CupsEncoding.OpCode opCode, int requestId, IppEncoding.GroupTag firstGroupTag) {
        this(opCode, requestId, Locale.getDefault(), firstGroupTag);
    }

    public CupsRequest(CupsEncoding.OpCode opCode, Locale locale, IppEncoding.GroupTag firstGroupTag) {
        this(opCode, 1, locale, firstGroupTag);
    }

    public CupsRequest(CupsEncoding.OpCode opCode, int requestId, Locale locale, IppEncoding.GroupTag firstGroupTag) {
        super((short) opCode.getValue(), requestId, locale, firstGroupTag);
    }
}

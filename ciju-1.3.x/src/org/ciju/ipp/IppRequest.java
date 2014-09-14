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

package org.ciju.ipp;

import java.util.Locale;
import org.ciju.ipp.IppEncoding.OpCode;
import static org.ciju.ipp.IppTransport.resourceStrings;

/**
 *
 * @author Opher Shachar
 */
public class IppRequest extends BaseIppObject {
    
    private final Locale locale;
    
    public IppRequest(OpCode opCode) {
        this(opCode, 1, Locale.getDefault());
    }

    public IppRequest(OpCode opCode, int requestId) {
        this(opCode, requestId, Locale.getDefault());
    }

    public IppRequest(OpCode opCode, Locale locale) {
        this(opCode, 1, locale);
    }

    public IppRequest(OpCode opCode, int requestId, Locale locale) {
        super((short) opCode.getValue(), requestId);
        this.locale = validate(locale);
    }

    private Locale validate(Locale locale) throws NullPointerException, IllegalArgumentException {
        if (locale == null) {
            throw new NullPointerException("locale");
        } else if (locale.getLanguage().length() == 0) {
            throw new IllegalArgumentException(resourceStrings.getString("LOCALE CANNOT HAVE AN EMPTY LANGUAGE FIELD."));
        }
        return locale;
    }

    public OpCode getOpCode() {
        return OpCode.valueOf(getCode());
    }

    public Locale getLocale() {
        return locale;
    }
 }

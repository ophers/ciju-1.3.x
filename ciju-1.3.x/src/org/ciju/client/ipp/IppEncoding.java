/*
 * Copyright (C) 2012 Opher Shachar, Ladpc Ltd.
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

package org.ciju.client.ipp;

/**
 *
 * @author Opher Shachar
 */
public class IppEncoding {

    public static final int PORT = 631;
    
    public enum ValueTag {
        // The following specifies the "out-of-band" values
        UNSUPPORTED(0X10),
        // 0X11 is reserved
        UNKNOWN(0X12),
        NO_VALUE(0X13),
        // 0x14-0x1F are reserved        
        
        // The following specifies the integer values
        // 0x20 is reserved        
        INTEGER(0x21),
        BOOLEAN(0x22),
        ENUM(0X23),
        // 0x24-0x2F are reserved        
        
        // The following specifies the octetString values
        OCTET_STRING(0x30),
        DATE_TIME(0x31),
        RESOLUTION(0x32),
        RANGE_OF_INTEGER(0x33),
        // 0x34 is reserved        
        TEXT_WITH_LANGUAGE(0x35),
        NAME_WITH_LANGUAGE(0x36),
        // 0x37-0x3F are reserved        
        
        // The following specifies the character-string values
        // 0x40 is reserved
        TEXT_WITHOUT_LANGUAGE(0x41),
        NAME_WITHOUT_LANGUAGE(0x42),
        // 0x43 is reserved
        KEYWORD(0x44),
        URI(0x45),
        URI_SCHEME(0x46),
        CHARSET(0x47),
        NATURAL_LANGUAGE(0x48),
        MIME_MEDIA_TYPE(0x49),
        // 0x4A-0x5F are reserved
        
        // 0x60-0x7E are reserved
        // 0x7F is reserved for extending types beyond the 255 values
        // 0x80-0xFF are reserved
        // 0x00000000-0x3FFFFFFF are reserved for IETF standard track documents
        // 0x40000000-0x7FFFFFFF are reserved for vendor extensions
        
        // The following are special markers 
        TEXT(0),
        NAME(0)
        ;
        
        private int value;

        private ValueTag(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
}

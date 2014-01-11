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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Opher Shachar
 */
public class IppEncoding {

    public static final int PORT = 631;
    
    public enum GroupTag {
        // 0x00 is reserved for future IETF standard track document
        OPERATION(0x01),        // operation-attributes-tag
        JOB(0x02),              // job-attributes-tag
        END(0x03),              // end-of-attributes-tag
        PRINTER(0x04),          // printer-attributes-tag
        UNSUPPORTED(0x05),      // unsupported-attributes-tag
        
        // From RFC3995: IPP/1.1 - Event Notifications and Subscriptions
        SUBSCRIPTION(0x06),     // subscription-attributes-tag
        EVENT(0x07),            // event-notification-attributes-tag
        
        // 0x08 is reserved
        
        // From PWG5100.5: IPP/2.2 - Document Object
        DOCUMENT(0x09)          // document-attributes-tag
        // 0x0A-0x0F are reserved
        ;
        
        private final int value;
        
        private GroupTag(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, GroupTag> vmap = initVmap();
        private static Map<Integer, GroupTag> initVmap() {
            final GroupTag[] dts = GroupTag.values();
            final HashMap<Integer, GroupTag> vmap = new HashMap<Integer, GroupTag>(dts.length * 4/3 + 1);
            for (GroupTag e : dts)
                vmap.put(e.value, e);
            return vmap;
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer must match an identifier used to declare an enum constant in this type.
         * @param i the value 
         * @return the enum constant with the specified value
         * @throws IllegalArgumentException if this enum type has no constant with the specified value
         */
        public static GroupTag valueOf(Integer i) {
            GroupTag e = vmap.get(i);
            if (e != null)
                return e;
            if (i == null)
                throw new NullPointerException("Integer is null");
            throw new IllegalArgumentException(String.format("No enum const has value 0x%02X", i));
        }
    }
    
    
    public enum ValueTag {
        // The following specifies the "out-of-band" values
        UNSUPPORTED(0x10),
        // 0x11 is reserved
        UNKNOWN(0x12),
        NO_VALUE(0x13),
        // 0x14 is reserved
        // From RFC3380: IPP/1.1 - Job and Printer Set Operations
        NOT_SETTABLE(0x15),
        DELETE_ATTRIBUTE(0x16),
        ADMIN_DEFINE(0x17),
        // 0x18-0x1F are reserved        
        
        // The following specifies the integer values
        // 0x20 is reserved        
        INTEGER(0x21),
        BOOLEAN(0x22),
        ENUM(0x23),
        // 0x24-0x2F are reserved        
        
        // The following specifies the octetString values
        OCTET_STRING(0x30),
        DATE_TIME(0x31),
        RESOLUTION(0x32),
        RANGE_OF_INTEGER(0x33),
        // 0x34 - see below
        TEXT_WITH_LANGUAGE(0x35),
        NAME_WITH_LANGUAGE(0x36),
        // 0x37 - see below
        // 0x38-0x3F are reserved
        
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
        // 0x4A - see below
        // 0x4B-0x5F are reserved
        
        // From RFC3382: IPP/1.1 - The 'collection' attribute syntax
        BEGIN_COLLECTION(0x34),        
        END_COLLECTION(0x37),        
        MEMBER_ATTR_NAME(0x4A),        
        
        // 0x60-0x7E are reserved
        // 0x7F is reserved for extending types beyond the 255 values
        // 0x80-0xFF are reserved
        // 0x00000000-0x3FFFFFFF are reserved for IETF standard track documents
        // 0x40000000-0x7FFFFFFF are reserved for vendor extensions
        
        // The following are special markers 
        TEXT(0),
        NAME(0)
        ;
        
        private final int value;

        private ValueTag(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, ValueTag> vmap = initVmap();
        private static Map<Integer, ValueTag> initVmap() {
            final ValueTag[] vts = ValueTag.values();
            final HashMap<Integer, ValueTag> vmap = new HashMap<Integer, ValueTag>(vts.length * 4/3 + 1);
            for (ValueTag e : vts)
                vmap.put(e.value, e);
            return vmap;
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer must match an identifier used to declare an enum constant in this type.
         * @param i the value 
         * @return the enum constant with the specified value
         * @throws IllegalArgumentException if this enum type has no constant with the specified value
         */
        public static ValueTag valueOf(Integer i) {
            ValueTag e = vmap.get(i);
            if (e != null)
                return e;
            if (i == null)
                throw new NullPointerException("Integer is null");
            throw new IllegalArgumentException(String.format("No enum const has value 0x%02X", i));
        }
    }
    
    
    public enum OpCode {
        // 0x0001 is reserved
        PRINT_JOB(0x0002),
        PRINT_URI(0x0003),
        VALIDATE_JOB(0x0004),
        CREATE_JOB(0x0005),
        SEND_DOCUMENT(0x0006),
        SEND_URI(0x0007),
        CANCEL_JOB(0x0008),
        GET_JOB_ATTRIBUTES(0x0009),
        GET_JOBS(0x000A),
        GET_PRINTER_ATTRIBUTES(0x000B),
        HOLD_JOB(0x000C),
        RELEASE_JOB(0x000D),
        RESTART_JOB(0x000E),
        // 0x000F is reserved
        PAUSE_PRINTER(0x0010),
        RESUME_PRINTER(0x0011),
        PURGE_JOBS(0x0012),
        // From RFC3380: IPP/1.1 - Job and Printer Set Operations
        SET_PRINTER_ATTRIBUTES(0x0013),
        SET_JOB_ATTRIBUTES(0x0014),
        GET_PRINTER_SUPPORTED_VALUES(0x0015),
        // From RFC3995: IPP/1.1 - Event Notifications and Subscriptions
        CREATE_PRINTER_SUBSCRIPTIONS(0x0016),
        CREATE_JOB_SUBSCRIPTIONS(0x0017),
        GET_SUBSCRIPTION_ATTRIBUTES(0x0018),
        GET_SUBSCRIPTIONS(0x0019),
        RENEW_SUBSCRIPTION(0x001A),
        CANCEL_SUBSCRIPTION(0x001B),
        // From RFC3996: IPP/1.1 - The 'ippget' Delivery Method for Event Notifications
        GET_NOTIFICATIONS(0x001C),
        // 0x001D-0x0021 are reserved
        // From RFC3998: IPP/1.1 - Job and Printer Administrative Operations
        ENABLE_PRINTER(0x0022),
        DISABLE_PRINTER(0x0023),
        PAUSE_PRINTER_AFTER_CURRENT_JOB(0x0024),
        HOLD_NEW_JOBS(0x0025),
        RELEASE_HELD_NEW_JOBS(0x0026),
        DEACTIVATE_PRINTER(0x0027),
        ACTIVATE_PRINTER(0x0028),
        RESTART_PRINTER(0x0029),
        SHUTDOWN_PRINTER(0x002A),
        STARTUP_PRINTER(0x002B),
        REPROCESS_JOB(0x002C),
        CANCEL_CURRENT_JOB(0x002D),
        SUSPEND_CURRENT_JOB(0x002E),
        RESUME_JOB(0x002F),
        PROMOTE_JOB(0x0030),
        SCHEDULE_JOB_AFTER(0x0031),
        // 0x0032 is reserved
        // From PWG5100.5: IPP/2.2 - Document Object
        CANCEL_DOCUMENT(0x0033),
        GET_DOCUMENT_ATTRIBUTES(0x0034),
        GET_DOCUMENTS(0x0035),
        DELETE_DOCUMENT(0x0036),
        SET_DOCUMENT_ATTRIBUTES(0x0037),
        // From PWG5100.11: IPP/2.1 - Job and Printer Extensions - Set 2
        CANCEL_JOBS(0x0038),
        CANCEL_MY_JOBS(0x0039),
        RESUBMIT_JOB(0x003A),
        CLOSE_JOB(0x003B),
        // From PWG5100.13: IPP/2.1 - Job and Printer Extensions – Set 3
        IDENTIFY_PRINTER(0x003C),
        VALIDATE_DOCUMENT(0x003D),
        ;
        
        private final int value;

        private OpCode(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, OpCode> vmap = initVmap();
        private static Map<Integer, OpCode> initVmap() {
            final OpCode[] ocs = OpCode.values();
            final HashMap<Integer, OpCode> vmap = new HashMap<Integer, OpCode>(ocs.length * 4/3 + 1);
            for (OpCode e : ocs)
                vmap.put(e.value, e);
            return vmap;
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer must match an identifier used to declare an enum constant in this type.
         * @param i the value 
         * @return the enum constant with the specified value
         * @throws IllegalArgumentException if this enum type has no constant with the specified value
         */
        public static OpCode valueOf(Integer i) {
            OpCode e = vmap.get(i);
            if (e != null)
                return e;
            if (i == null)
                throw new NullPointerException("Integer is null");
            throw new IllegalArgumentException(String.format("No enum const has value 0x%02X", i));
        }
    }
}

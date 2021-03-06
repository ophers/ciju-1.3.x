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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.JobMessageFromOperator;
import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterLocation;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.PrinterMessageFromOperator;
import javax.print.attribute.standard.PrinterName;

/**
 *
 * @author Opher Shachar
 */
public class IppEncoding {

    public static final int PORT = 631;
    public static final short DEF_VERSION = 0x0101;
    private static final String BADENUM = IppTransport.resourceStrings.getString("NO ENUM CONST HAS VALUE 0X%02X");
    
    public enum GroupTag {
        // 0x00 is reserved for future IETF standard track document
        /** operation-attributes-tag */             OPERATION(0x01),
        /** job-attributes-tag */                   JOB(0x02),
        /** end-of-attributes-tag */                END(0x03),
        /** printer-attributes-tag */               PRINTER(0x04),
        /** unsupported-attributes-tag */           UNSUPPORTED(0x05),
        
        // From RFC3995: IPP/1.1 - Event Notifications and Subscriptions
        /** subscription-attributes-tag */          SUBSCRIPTION(0x06),
        /** event-notification-attributes-tag */    EVENT(0x07),
        
        // From PWG5100.5: IPP/2.2 - Document Object
        /** document-attributes-tag */              DOCUMENT(0x09),
        
        // 0x08, 0x0A-0x0F are reserved
        RESERVED(-1)
        ;
        
        private final int value;
        
        private GroupTag(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, GroupTag> vmap;
        static {
            final GroupTag[] dts = GroupTag.values();
            vmap = new HashMap<Integer, GroupTag>(dts.length * 4/3 + 1);
            for (GroupTag e : dts)
                if (e.value >= 0)
                    vmap.put(e.value, e);
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer must match an identifier used to declare an enum constant in this type.
         * @param i the value 
         * @return the enum constant with the specified value
         * @throws IllegalArgumentException if this enum type has no constant with the specified value
         */
        public static GroupTag valueOf(int i) {
            GroupTag e = vmap.get(i);
            if (e != null)
                return e;
            throw new IllegalArgumentException(String.format(BADENUM, i));
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
        OCTET_STRING(0x30, 1023),
        DATE_TIME(0x31),
        RESOLUTION(0x32),
        RANGE_OF_INTEGER(0x33),
        // 0x34 - see below
        TEXT_WITH_LANGUAGE(0x35, 1023) /* limit w/o the 'natural-language' part */,
        NAME_WITH_LANGUAGE(0x36, 255) /* limit w/o the 'natural-language' part */,
        // 0x37 - see below
        // 0x38-0x3F are reserved
        
        // The following specifies the character-string values
        // 0x40 is reserved
        TEXT_WITHOUT_LANGUAGE(0x41, 1023),
        NAME_WITHOUT_LANGUAGE(0x42, 255),
        // 0x43 is reserved
        KEYWORD(0x44, 255),
        URI(0x45, 1023),
        URI_SCHEME(0x46, 63),
        CHARSET(0x47, 63),
        NATURAL_LANGUAGE(0x48, 63),
        MIME_MEDIA_TYPE(0x49, 255),
        // 0x4A - see below
        // 0x4B-0x5F are reserved
        
        // From RFC3382: IPP/1.1 - The 'collection' attribute syntax
        BEGIN_COLLECTION(0x34),        
        END_COLLECTION(0x37),        
        MEMBER_ATTR_NAME(0x4A, KEYWORD.MAX),        
        
        // 0x60-0x7E are reserved
        // 0x7F is reserved for extending types beyond the 255 values
        // 0x80-0xFF are reserved
        // 0x00000000-0x3FFFFFFF are reserved for IETF standard track documents
        // 0x40000000-0x7FFFFFFF are reserved for vendor extensions
        RESERVED(-1),

        // The following are special markers for lookup purposes
        TEXT(-1),
        NAME(-1)
        ;
        
        private final int value;
        public  final int MAX;

        private ValueTag(int value, int max) {
            this.value = value;
            this.MAX = max;
        }
        
        private ValueTag(int value) {
            this.value = value;
            this.MAX = Short.MAX_VALUE;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, ValueTag> vmap;
        static {
            final ValueTag[] vts = ValueTag.values();
            vmap = new HashMap<Integer, ValueTag>(vts.length * 4/3 + 1);
            for (ValueTag e : vts)
                if (e.value >= 0)
                    vmap.put(e.value, e);
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer must match an identifier used to declare an enum constant in this type.
         * @param i the value 
         * @return the enum constant with the specified value
         * @throws IllegalArgumentException if this enum type has no constant with the specified value
         */
        public static ValueTag valueOf(int i) {
            ValueTag e = vmap.get(i);
            if (e != null)
                return e;
            throw new IllegalArgumentException(String.format(BADENUM, i));
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
        
        // Represents all opcodes we do not currently process
        RESERVED(-1)
        ;
        
        private final int value;

        private OpCode(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, OpCode> vmap;
        static {
            final OpCode[] ocs = OpCode.values();
            vmap = new HashMap<Integer, OpCode>(ocs.length * 4/3 + 1);
            for (OpCode e : ocs)
                if (e.value >= 0)
                    vmap.put(e.value, e);
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer must match an identifier used to declare an enum constant in this type.
         * @param i the value 
         * @return the enum constant with the specified value
         * @throws IllegalArgumentException if this enum type has no constant with the specified value
         */
        public static OpCode valueOf(int i) {
            OpCode e = vmap.get(i & 0xffff);
            if (e != null)
                return e;
            throw new IllegalArgumentException(String.format(BADENUM, i));
        }
    }
    
    
    public enum StatusCode {
        SUCCESSFUL_OK(0x0000),
        SUCCESSFUL_OK_IGNORED_OR_SUBSTITUTED_ATTRIBUTES(0x0001),
        SUCCESSFUL_OK_CONFLICTING_ATTRIBUTES(0x0002),
        INFORMATIONAL(0x0100),
        REDIRECTION(0x0200),
        // The range 0x0300 to 0x03FF is reserved
        CLIENT_ERROR_BAD_REQUEST(0x0400),
        CLIENT_ERROR_FORBIDDEN(0x0401),
        CLIENT_ERROR_NOT_AUTHENTICATED(0x0402),
        CLIENT_ERROR_NOT_AUTHORIZED(0x0403),
        CLIENT_ERROR_NOT_POSSIBLE(0x0404),
        CLIENT_ERROR_TIMEOUT(0x0405),
        CLIENT_ERROR_NOT_FOUND(0x0406),
        CLIENT_ERROR_GONE(0x0407),
        CLIENT_ERROR_REQUEST_ENTITY_TOO_LARGE(0x0408),
        CLIENT_ERROR_REQUEST_VALUE_TOO_LONG(0x0409),
        CLIENT_ERROR_DOCUMENT_FORMAT_NOT_SUPPORTED(0x040A),
        CLIENT_ERROR_ATTRIBUTES_OR_VALUES_NOT_SUPPORTED(0x040B),
        CLIENT_ERROR_URI_SCHEME_NOT_SUPPORTED(0x040C),
        CLIENT_ERROR_CHARSET_NOT_SUPPORTED(0x040D),
        CLIENT_ERROR_CONFLICTING_ATTRIBUTES(0x040E),
        CLIENT_ERROR_COMPRESSION_NOT_SUPPORTED(0x040F),
        CLIENT_ERROR_COMPRESSION_ERROR(0x0410),
        CLIENT_ERROR_DOCUMENT_FORMAT_ERROR(0x0411),
        CLIENT_ERROR_DOCUMENT_ACCESS_ERROR(0x0412),
        SERVER_ERROR_INTERNAL_ERROR(0x0500),
        SERVER_ERROR_OPERATION_NOT_SUPPORTED(0x0501),
        SERVER_ERROR_SERVICE_UNAVAILABLE(0x0502),
        SERVER_ERROR_VERSION_NOT_SUPPORTED(0x0503),
        SERVER_ERROR_DEVICE_ERROR(0x0504),
        SERVER_ERROR_TEMPORARY_ERROR(0x0505),
        SERVER_ERROR_NOT_ACCEPTING_JOBS(0x0506),
        SERVER_ERROR_BUSY(0x0507),
        SERVER_ERROR_JOB_CANCELED(0x0508),
        SERVER_ERROR_MULTIPLE_DOCUMENT_JOBS_NOT_SUPPORTED(0x0509),
        // From RFC3380: IPP/1.1 - Job and Printer Set Operations
        CLIENT_ERROR_ATTRIBUTES_NOT_SETTABLE(0x0413),
        // From RFC3995: IPP/1.1 - Event Notifications and Subscriptions
        SUCCESSFUL_OK_IGNORED_SUBSCRIPTIONS(0x0003),
        SUCCESSFUL_OK_TOO_MANY_EVENTS(0x0005),
        CLIENT_ERROR_IGNORED_ALL_SUBSCRIPTIONS(0x0414),
        CLIENT_ERROR_TOO_MANY_SUBSCRIPTIONS(0x0415),
        // From RFC3996: IPP/1.1 - The 'ippget' Delivery Method for Event Notifications
        SUCCESSFUL_OK_EVENTS_COMPLETE(0x0007),
        // From RFC3998: IPP/1.1 - Job and Printer Administrative Operations
        SERVER_ERROR_PRINTER_IS_DEACTIVATED(0x050A),
        // From PWG5100.7: IPP/2.1 - Job Extensions
        SERVER_ERROR_TOO_MANY_JOBS(0x050B),
        SERVER_ERROR_TOO_MANY_DOCUMENTS(0x050C),
        // From PWG5100.13: IPP/2.1 - Job and Printer Extensions – Set 3
        CLIENT_ERROR_DOCUMENT_PASSWORD_ERROR(0x0418),
        CLIENT_ERROR_DOCUMENT_PERMISSION_ERROR(0x0419),
        CLIENT_ERROR_DOCUMENT_SECURITY_ERROR(0x041A),
        CLIENT_ERROR_DOCUMENT_UNPRINTABLE_ERROR(0x041B),
        
        // Represents all status codes we do not currently process
        RESERVED(-1)
        ;
        
        private final int value;

        private StatusCode(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        
        private static final Map<Integer, StatusCode> vmap;
        static {
            final StatusCode[] scs = StatusCode.values();
            vmap = new HashMap<Integer, StatusCode>(scs.length * 4/3 + 1);
            for (StatusCode e : scs)
                if (e.value >= 0)
                    vmap.put(e.value, e);
        }
        
        /**
         * Returns the enum constant of this type with the specified value. 
         * The integer should match an identifier used to declare an enum constant in this type.
         * If the value has no match then the lowest valued enum constant of the "status prefix"
         * is returned or {@link #RESERVED} if the "status code" is reserved.
         * @param i the value 
         * @return the enum constant with the specified value or the lowest valued enum constant
         *  of the "status prefix" or {@link #RESERVED}
         */
        public static StatusCode valueOf(int i) {
            StatusCode e = vmap.get(i & 0xffff);
            if (e != null)
                return e;
            else if (i <= 0x00ff)
                return SUCCESSFUL_OK;
            else if (i <= 0x01ff)
                return INFORMATIONAL;
            else if (i <= 0x02ff)
                return REDIRECTION;
            else if (i <= 0x03ff)
                return RESERVED;
            else if (i <= 0x04ff)
                return CLIENT_ERROR_BAD_REQUEST;
            else if (i <= 0x05ff)
                return SERVER_ERROR_INTERNAL_ERROR;
            else
                return RESERVED;
        }
    }
    
    
    public static final Map<Class<? extends Attribute>, Integer> LengthLimits;
    static {
        HashMap<Class<? extends Attribute>, Integer> ll =
                // in the argument the first number is the elements count
                new HashMap<Class<? extends Attribute>, Integer>(7 * 4/3 + 1);
//        ll.put(StatusMessage.class, 255);                 // TODO: Define StatusMessage Attribute class
//        ll.put(Message.class, 127);                       // TODO: Define Message Attribute class
        ll.put(OutputDeviceAssigned.class, 127);
        ll.put(JobMessageFromOperator.class, 127);
        ll.put(PrinterName.class, 127);
        ll.put(PrinterLocation.class, 127);
        ll.put(PrinterInfo.class, 127);
        ll.put(PrinterMakeAndModel.class, 127);
        ll.put(PrinterMessageFromOperator.class, 127);
        LengthLimits = Collections.unmodifiableMap(ll);
    }
}

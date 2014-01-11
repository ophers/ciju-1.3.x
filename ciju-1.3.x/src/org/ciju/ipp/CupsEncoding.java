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
public class CupsEncoding extends org.ciju.ipp.IppEncoding {

    /**
     * gggg
     */
    public enum OpCode {
        // CUPS 1.0 and up
        PRINT_JOB(0x0002),
        VALIDATE_JOB(0x0004),
        CANCEL_JOB(0x0008),
        GET_JOB_ATTRIBUTES(0x0009),
        GET_JOBS(0x000A),
        GET_PRINTER_ATTRIBUTES(0x000B),
        PAUSE_PRINTER(0x0010),
        RESUME_PRINTER(0x0011),
        PURGE_JOBS(0x0012),
        /** Get the default destination. */
        CUPS_GET_DEFAULT(0x4001),
        /** Get all of the available printers. */
        CUPS_GET_PRINTERS(0x4002),
        /** Add or modify a printer. */
        CUPS_ADD_MODIFY_PRINTER(0x4003),
        /** Delete a printer. */
        CUPS_DELETE_PRINTER(0x4004),
        /** Get all of the available printer classes. */
        CUPS_GET_CLASSES(0x4005),
        /** Add or modify a printer class. */
        CUPS_ADD_MODIFY_CLASS(0x4006),
        /** Delete a printer class. */
        CUPS_DELETE_CLASS(0x4007),
        /** Accept jobs on a printer or printer class. */
        CUPS_ACCEPT_JOBS(0x4008),
        /** Reject jobs on a printer or printer class. */
        CUPS_REJECT_JOBS(0x4009),
        /** Set the default destination. */
        CUPS_SET_DEFAULT(0x400A),

        // CUPS 1.1 and up
        CREATE_JOB(0x0005),
        SEND_DOCUMENT(0x0006),
        HOLD_JOB(0x000C),
        RELEASE_JOB(0x000D),
        RESTART_JOB(0x000E),
        SET_JOB_ATTRIBUTES(0x0014),
        /** Get all of the available devices. */
        CUPS_GET_DEVICES(0x400B),
        /** Get all of the available PPDs. */
        CUPS_GET_PPDS(0x400C),
        /** Move a job to a different printer. */
        CUPS_MOVE_JOB(0x400D),

        // CUPS 1.2 and up
        CREATE_PRINTER_SUBSCRIPTIONS(0x0016),
        CREATE_JOB_SUBSCRIPTIONS(0x0017),
        GET_SUBSCRIPTION_ATTRIBUTES(0x0018),
        GET_SUBSCRIPTIONS(0x0019),
        RENEW_SUBSCRIPTION(0x001A),
        CANCEL_SUBSCRIPTION(0x001B),
        GET_NOTIFICATIONS(0x001C),
        /** Accepts jobs on a printer. */
        ENABLE_PRINTER(0x0022),
        /** Rejects jobs on a printer. */
        DISABLE_PRINTER(0x0023),
        /** Authenticate a job for printing. */
        CUPS_AUTHENTICATE_JOB(0x400E),

        // CUPS 1.3 and up
        /** Get a PPD file. */
        CUPS_GET_PPD(0x400F),

        // CUPS 1.4 and up
        /** Get a document file from a job. */
        CUPS_GET_DOCUMENT(0x4027)
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

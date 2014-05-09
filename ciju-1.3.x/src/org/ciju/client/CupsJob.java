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

package org.ciju.client;

import javax.print.Doc;

/**
 *
 * @author Opher Shachar
 */
public class CupsJob extends IppJob {

    protected CupsJob(CupsPrinter printer) {
        super(printer);
    }

    // Stub for some unique CUPS Job methods...
    public void moveJob(CupsPrinter printer) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Doc getDocument(int num) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}

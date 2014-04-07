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

import java.net.Proxy;
import java.net.URI;

/**
 *
 * @author Opher Shachar
 */
public class CupsPrinter extends IppPrinter {

    protected CupsPrinter(URI uri, Proxy proxy) {
        super(uri, proxy);
    }

    // Stub for some unique CUPS Printer methods...
    public String getPPD() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public void setAsDefault() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void rejectJobs() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void acceptJobs() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}

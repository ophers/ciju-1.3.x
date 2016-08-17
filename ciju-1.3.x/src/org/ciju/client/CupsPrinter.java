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

package org.ciju.client;

import java.net.Proxy;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import static org.ciju.client.PrintServer.resourceStrings;
import org.ciju.ipp.IppEncoding;
import org.ciju.ipp.IppObjectFactory;

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

    /**
     *
     * @return
     */
    @Override
    public Collection<CupsJob> getJobs() {
        return getJobs(new ArrayList<CupsJob>(), new IppObjectFactory<CupsJob>() {
            public CupsJob create(IppEncoding.GroupTag gt) {
                if (!canCreate(gt))
                    throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("CANNOT CREATE THIS TYPE OF OBJECT: {0}"), new Object[] {gt}));
                return new CupsJob(CupsPrinter.this);
            }

            public boolean canCreate(IppEncoding.GroupTag gt) {
                return gt == IppEncoding.GroupTag.JOB;
            }
        });
    }
}

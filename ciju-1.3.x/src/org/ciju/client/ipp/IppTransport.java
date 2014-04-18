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

package org.ciju.client.ipp;

import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppObject;
import java.io.InputStream;
import java.io.OutputStream;
import org.ciju.ipp.IppResponse;

/**
 *
 * @author Opher Shachar
 */
public class IppTransport {

    /**
     *
     * @param os
     * @param ipp
     */
    public static void writeRequest(OutputStream os, IppRequest ipp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param inputStream
     * @param contentLength
     * @return an {@link IppResponse} object from the response.
     */
    public static IppResponse<IppObject> processResponse(InputStream inputStream, long contentLength) {
        return processResponse(inputStream, contentLength, null);
    }

    /**
     *
     * @param inputStream
     * @param contentLength
     * @param obj
     * @param <T>
     * @return an {@link IppResponse} object encompassing the given obj.
     */
    public static <T extends IppObject> IppResponse<T> processResponse(InputStream inputStream, long contentLength, T obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

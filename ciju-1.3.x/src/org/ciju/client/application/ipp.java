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

package org.ciju.client.application;

import org.ciju.client.ipp.IppTransport;
import java.io.IOException;
import java.net.ContentHandler;
import java.net.ContentHandlerFactory;
import java.net.URLConnection;

/**
 * Class to perform getContent() for application/ipp MIME type.
 * Also implements ContentHandlerFactory interface.
 *
 * @author Opher Shachar
 */
public class ipp extends ContentHandler implements ContentHandlerFactory
{
    private static ipp singleton = new ipp(); 


    /**
     * {@inheritDoc} Reads an IPP response from the input stream of {@link URLConnection urlc} and
     * creates an IPP object.
     * @return an IPP object representing an IPP response. <tt>null</tt> if response
     *      is malformed.
     */
    public Object getContent(URLConnection urlc) throws IOException {
        return IppTransport.processResponse(urlc.getInputStream(), urlc.getContentLength());
    }


    /**
     * Return the singleton instance of this class
     *
     * @param mimetype only supports application/ipp
     * @return an instance of this class (a sub-type of {@link ContentHandler})
     *      if {@code mimetype} is supported or null otherwise.
     */
    public ContentHandler createContentHandler(String mimetype) {
        if (mimetype.equalsIgnoreCase("application/ipp"))
            return singleton;
        else
            return null;
    }

}

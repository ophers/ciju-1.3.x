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

/**
 * This {@linkplain RuntimeException} is thrown when a complete IPP response was not
 * successfully decoded from the response stream due to some IPP protocol compliance
 * issue (not an <tt>IOException</tt>).
 * 
 * @author Opher Shachar
 */
public class IppFailedException extends RuntimeException {

    private static final long serialVersionUID = 7036429978114340721L;

    private IppResponse resp;
    
    /**
     * Creates a new instance of <tt>IppFailedException</tt> with no detail
     * message.
     */
    public IppFailedException() {
    }

    /**
     * Constructs an instance of <tt>IppFailedException</tt> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public IppFailedException(String msg) {
        super(msg);
    }
    
    /**
     * Creates a new instance of <tt>IppFailedException</tt> with no detail
     * message.
     * 
     * @param resp the, possibly partial, IPP response.
     */
    public IppFailedException(IppResponse resp) {
        this.resp = resp;
    }

    /**
     * Constructs an instance of <tt>IppFailedException</tt> with the specified
     * detail message.
     *
     * @param msg the detail message.
     * @param resp the, possibly partial, IPP response.
     */
    public IppFailedException(String msg, IppResponse resp) {
        super(msg);
        this.resp = resp;
    }
    
    /**
     * The IPP response.
     * @return the, possibly partial, IPP response or null if not available.
     */
    public IppResponse getIppResponse() {
        return resp;
    }
    
    /* package */ IppFailedException setIppResponse(IppResponse resp) {
        if (this.resp != null)
            throw new IllegalStateException("resp");
        this.resp = resp;
        return this;
    }
}

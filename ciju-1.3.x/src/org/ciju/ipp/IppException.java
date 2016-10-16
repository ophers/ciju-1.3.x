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

import java.net.URI;
import javax.print.AttributeException;
import javax.print.DocFlavor;
import javax.print.FlavorException;
import javax.print.PrintException;
import javax.print.URIException;
import javax.print.attribute.Attribute;
import org.ciju.ipp.IppEncoding.GroupTag;
import org.ciju.ipp.IppEncoding.StatusCode;
import org.ciju.ipp.attribute.AttributeGroup;
import org.ciju.ipp.attribute.GenericAttribute;

/**
 *
 * @author Opher Shachar
 */
public class IppException extends PrintException implements AttributeException, FlavorException, URIException {

    private static final long serialVersionUID = -323653169232572916L;

    private IppResponse resp;
    
    /**
     * Creates a new instance of <tt>IppException</tt> with no detail message.
     * @param resp The, possibly partial, IPP response.
     */
    public IppException(IppResponse resp) {
        this.resp = resp;
    }

    /**
     * Constructs an instance of <tt>IppException</tt> with the specified
     * detail message.
     *
     * @param msg Detail message, or null if no detail message.
     * @param resp The, possibly partial, IPP response.
     */
    public IppException(String msg, IppResponse resp) {
        super(msg);
        this.resp = resp;
    }

    /**
     * Constructs an instance of <tt>IppException</tt> chaining the supplied
     * exception.
     *
     * @param e Chained exception.
     * @param resp The, possibly partial, IPP response.
     */
    public IppException(Exception e, IppResponse resp) {
        super(e);
        this.resp = resp;
    }

    /**
     * Constructs an instance of <tt>IppException</tt> with the given detail
     * message and chained exception.
     * 
     * @param msg Detail message, or null if no detail message.
     * @param e Chained exception.
     * @param resp The, possibly partial, IPP response.
     */
    public IppException(String msg, Exception e, IppResponse resp) {
        super(msg, e);
        this.resp = resp;
    }
    
    public IppResponse getIppResponse() {
        return resp;
    }
    
    public Class[] getUnsupportedAttributes() {
        // TODO: Implement once we are able to map GenericAttribute to JPS Attribute
        return null;
    }

    private Attribute[] uvs;
    public Attribute[] getUnsupportedValues() {
        if (uvs == null)
            for (AttributeGroup ag : resp.getAttributeGroups())
                if (ag.groupTag() == GroupTag.UNSUPPORTED)
                    // There should be one such attribute group if at all
                    uvs = ag.toArray();
        return uvs;
    }

    private DocFlavor[] ufs;
    public DocFlavor[] getUnsupportedFlavors() {
        if (ufs == null)
//            if (resp.getStatusCode() == StatusCode.CLIENT_ERROR_DOCUMENT_FORMAT_NOT_SUPPORTED)
            for (AttributeGroup ag : resp.getAttributeGroups())
                if (ag.groupTag() == GroupTag.UNSUPPORTED)
                    // There should be one such attribute group if at all
                    for (Attribute attr : ag)
                        if (attr.getName().equals("document-format"))
                            if (attr instanceof GenericAttribute) {
                                GenericAttribute ga = (GenericAttribute) attr;
                                ufs = new DocFlavor[ga.size()];
                                int i = 0;
                                for (Object df : ga)
                                    ufs[i++] = new DocFlavor(df.toString(), "[B");
                            } else {
                                ufs = new DocFlavor[] { new DocFlavor(attr.toString(), "[B") };
                            }
        return ufs;
    }

    public URI getUnsupportedURI() {
        return null;
    }

    public int getReason() {
        return URIException.URIOtherProblem;
    }
}

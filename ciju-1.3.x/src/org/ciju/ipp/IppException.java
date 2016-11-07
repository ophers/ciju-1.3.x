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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import javax.print.AttributeException;
import javax.print.DocFlavor;
import javax.print.FlavorException;
import javax.print.PrintException;
import javax.print.URIException;
import javax.print.attribute.Attribute;
import org.ciju.ipp.IppEncoding.GroupTag;
import org.ciju.ipp.IppEncoding.StatusCode;
import org.ciju.ipp.IppEncoding.ValueTag;
import org.ciju.ipp.attribute.AttributeGroup;
import org.ciju.ipp.attribute.GenericAttribute;

/**
 * Thrown when an IPP response of <em>Client Error</em> or <em>Server Error</em> is returned.<p>
 * <b>Note</b>: To supply the {@link #getUnsupportedFlavors() getUnsupportedFlavors} and {@link #getUnsupportedURI() getUnsupportedURI}
 * methods the constructors need the {@linkplain IppRequest} argument, as the IPP spec doesn't require
 * the response to contain any attributes with the corresponding errors (as they're kinda <em>fatal</em>).
 * Passing <tt>null</tt> for the <em>req</em> parameter will have those methods return null,
 * if the response doesn't contain relevant attributes.
 * @author Opher Shachar
 */
public class IppException extends PrintException implements AttributeException, FlavorException, URIException {

    private static final long serialVersionUID = 8410477945210898784L;

    private final IppResponse resp;
    private final Attribute[] uvs;
    private final DocFlavor[] ufs;
    private final URI         uri;

    /**
     * Creates a new instance of <tt>IppException</tt> with a detail message from the response.
     * @param resp The, possibly partial, IPP response.
     * @param req  The, possibly <tt>null</tt>, IPP request.
     */
    public IppException(IppResponse resp, IppRequest req) {
        super(getIppMessage(resp));
        this.resp = resp;
        this.uvs  = populate_uvs();
        this.ufs  = populate_ufs(req);
        this.uri  = getUri(req);
    }

    /**
     * Constructs an instance of <tt>IppException</tt> with the specified
     * detail message.
     *
     * @param msg Detail message, or null if no detail message.
     * @param resp The, possibly partial, IPP response.
     * @param req  The, possibly <tt>null</tt>, IPP request.
     */
    public IppException(String msg, IppResponse resp, IppRequest req) {
        super(msg);
        this.resp = resp;
        this.uvs  = populate_uvs();
        this.ufs  = populate_ufs(req);
        this.uri  = getUri(req);
    }

    private static String getIppMessage(IppResponse resp) {
        StringBuilder msg;
        Attribute attr = resp.getAttribute("status-message", GroupTag.OPERATION, ValueTag.TEXT);
        if (attr == null)
            msg = new StringBuilder(resp.getStatusCode().toString());
        else
            msg = new StringBuilder(attr.toString());

        attr = resp.getAttribute("detailed-status-message", GroupTag.OPERATION, ValueTag.TEXT);
        if (attr != null)
            msg.append("\nDetails: ").append(attr);

        attr = resp.getAttribute("document-access-error", GroupTag.OPERATION, ValueTag.TEXT);
        if (attr != null)
            msg.append("\nDocument Access Error: ").append(attr);

        return msg.toString();
    }

    private Attribute[] populate_uvs() {
        for (Iterator<AttributeGroup> it = resp.getAttributeGroups().iterator(); it.hasNext();) {
            AttributeGroup ag = it.next();
            if (ag.groupTag() == GroupTag.UNSUPPORTED) {
                // There should be one such attribute group if at all
                it.remove();
                return ag.toArray();
            }
        }
        return null;
    }

    private DocFlavor[] populate_ufs(IppRequest req) {
        DocFlavor[] ufs = null;
        for (Attribute attr : uvs)
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
        if (ufs == null && resp.getStatusCode() == StatusCode.CLIENT_ERROR_DOCUMENT_FORMAT_NOT_SUPPORTED) {
            ufs = new DocFlavor[] { req.getDoc().getDocFlavor() };
        }
        return ufs;
    }

    private URI getUri(IppRequest req) {
        URI uri = null;
        try {
            if (resp.getStatusCode() == StatusCode.CLIENT_ERROR_DOCUMENT_ACCESS_ERROR ||
                resp.getStatusCode() == StatusCode.CLIENT_ERROR_URI_SCHEME_NOT_SUPPORTED)
                if (req.getDoc() != null &&
                    req.getDoc().getDocFlavor().getRepresentationClassName().equals("java.net.URL"))
                    uri = ((URL) req.getDoc().getPrintData()).toURI();
                else
                    for (Attribute attr : uvs)
                        if (attr.getName().equals("document-uri"))
                            uri = new URI(attr.toString());
        }
        catch (IOException ex) { }
        catch (URISyntaxException ex) { }
        return uri;
    }

    public IppResponse getIppResponse() {
        return resp;
    }

    public Class[] getUnsupportedAttributes() {
        // TODO: Implement once we are able to map GenericAttribute to JPS Attribute
        return null;
    }

    public Attribute[] getUnsupportedValues() {
        return uvs;
    }

    public DocFlavor[] getUnsupportedFlavors() {
        return ufs;
    }

    public URI getUnsupportedURI() {
        return uri;
    }

    public int getReason() {
        switch (resp.getStatusCode()) {
            case CLIENT_ERROR_DOCUMENT_ACCESS_ERROR:
                return URIInaccessible;
            case CLIENT_ERROR_URI_SCHEME_NOT_SUPPORTED:
                return URISchemeNotSupported;
            default:
                return URIOtherProblem;
        }
    }
}

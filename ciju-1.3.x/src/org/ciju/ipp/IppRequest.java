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
import java.util.Locale;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.FlavorException;
import javax.print.PrintException;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import org.ciju.ipp.IppEncoding.GroupTag;
import org.ciju.ipp.IppEncoding.OpCode;
import org.ciju.ipp.IppEncoding.ValueTag;
import static org.ciju.ipp.IppTransport.resourceStrings;
import org.ciju.ipp.attribute.GenericAttribute;

/**
 *
 * @author Opher Shachar
 */
public class IppRequest extends BaseIppObject {
    private static Conformity defaultConformity = Conformity.LENIENT;

    /**
     * Sets the default {@link IppObject.Conformity Conformity} for subsequent IPP
     * requests.
     * 
     * @param defaultConformity the {@linkplain IppObject.Conformity Conformity} to
     *      use as the new default.
     */
    public static void setDefaultConformity(Conformity defaultConformity) {
        IppRequest.defaultConformity = defaultConformity;
    }
    
    final Conformity conformity = defaultConformity;
    private final Locale locale;
    protected enum DocDataFlavor { READER, STREAM };
    private DocDataFlavor ddf;
    private Doc doc;
    
    public IppRequest(OpCode opCode, GroupTag firstGroupTag) {
        this(opCode, 1, Locale.getDefault(), firstGroupTag);
    }

    public IppRequest(OpCode opCode, int requestId, GroupTag firstGroupTag) {
        this(opCode, requestId, Locale.getDefault(), firstGroupTag);
    }

    public IppRequest(OpCode opCode, Locale locale, GroupTag firstGroupTag) {
        this(opCode, 1, locale, firstGroupTag);
    }

    public IppRequest(OpCode opCode, int requestId, Locale locale, GroupTag firstGroupTag) {
        super((short) opCode.getValue(), requestId);
        this.locale = validate(locale);
        newAttributeGroup(firstGroupTag);
    }

    protected IppRequest(short code, int requestId, Locale locale, GroupTag firstGroupTag) {
        super(code, requestId);
        this.locale = validate(locale);
        newAttributeGroup(firstGroupTag);
    }

    private Locale validate(Locale locale) throws NullPointerException, IllegalArgumentException {
        if (locale == null)
            throw new NullPointerException("locale");
        else if (locale.getLanguage().length() == 0)
            throw new IllegalArgumentException(resourceStrings.getString("LOCALE CANNOT HAVE AN EMPTY LANGUAGE FIELD."));
        return locale;
    }

    public OpCode getOpCode() {
        return OpCode.valueOf(getCode());
    }

    public Locale getLocale() {
        return locale;
    }
    
    public Doc getDoc() {
        return doc;
    }

    public void setDoc(Doc doc) throws PrintException, IOException {
        String rep = doc.getDocFlavor().getRepresentationClassName().intern();
        if (rep == "[C" || rep == "java.io.Reader" || rep == "java.lang.String")
            ddf = DocDataFlavor.READER;
        else if (rep == "[B" || rep == "java.io.InputStream")
            ddf = DocDataFlavor.STREAM;
        else
            // TODO: Add support for DocFlavor.URI
            throw new DocFlavorException(doc.getDocFlavor());
        
        addOperationAttribute(new GenericAttribute("document-format",
                doc.getDocFlavor().getMimeType(), ValueTag.MIME_MEDIA_TYPE));
        addAllAttributes(doc.getAttributes());
        this.doc = doc;
    }

    public DocDataFlavor getDocDataFlavor() {
        return ddf;
    }

//<editor-fold defaultstate="collapsed" desc="add attribute methods made public">
    @Override
    public boolean addAllAttributes(AttributeSet as) {
        return super.addAllAttributes(as);
    }
    
    @Override
    public boolean addAttribute(Attribute a) {
        return super.addAttribute(a);
    }
    
    @Override
    public boolean addOperationAttribute(Attribute a) {
        return super.addOperationAttribute(a);
    }
//</editor-fold>
}

class DocFlavorException extends PrintException implements FlavorException {
    private static final long serialVersionUID = -4729680667157678227L;
    private final DocFlavor df;

    public DocFlavorException(DocFlavor df) {
        super("Unsupported document represetation class: " + df.getRepresentationClassName());
        this.df = df;
    }

    public DocFlavor[] getUnsupportedFlavors() {
        return new DocFlavor[] { df };
    }
}

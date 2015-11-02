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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.SimpleDoc;

/**
 *
 * @author Opher Shachar
 * @param <T>
 */
public class IppResponse<T extends IppObject> extends BaseIppObject {
    private static Conformity defaultConformity = Conformity.LENIENT;

    /**
     * Sets the default {@link IppObject.Conformity Conformity} for subsequent IPP
     * responses.
     * 
     * @param defaultConformity the {@linkplain IppObject.Conformity Conformity} to
     *      use as the new default.
     */
    public static void setDefaultConformity(Conformity defaultConformity) {
        IppResponse.defaultConformity = defaultConformity;
    }
    
    final Conformity conformity = defaultConformity;
    private final T obj;
    private Locale locale;
    private Doc doc;
    
    public IppResponse(short version, short status, int requestId) {
        this(version, status, requestId, null);
    }
    
    @SuppressWarnings("unchecked")
    public IppResponse(short version, short status, int requestId, T obj) {
        super(version, status, requestId);
        if (obj != null)
            this.obj = obj;
        else
            this.obj = (T) this;
    }

    public short getResponseCode() {
        return getCode();
    }

    public T getObject() {
        if (obj != this)
            return obj;
        else
            return null;
    }
    
    // TODO: Delegate relevant methods to 'obj'

    public Locale getLocale() {
        return locale;
    }

    @Override
    void setLocale(Locale loc) {
        assert locale == null /* set only once */ &&
                loc != null && loc.getLanguage().length() > 0 :
                "this must have been already validated";
        locale = loc;
    }

    OutputStream getDocOutputStream() throws IOException {
        final File file = File.createTempFile("fil", null);
        return new FileOutputStream(file) {
            @Override
            public void close() throws IOException {
                super.close();
                doc = new SimpleDoc(new FileInputStream(file) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        file.delete();
                    }
                }, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            }
        };
    }
    
    public Doc getDoc() {
        return doc;
    }
}

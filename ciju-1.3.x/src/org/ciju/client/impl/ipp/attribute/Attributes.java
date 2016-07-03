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

package org.ciju.client.impl.ipp.attribute;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.Copies;
import org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public enum Attributes {
    COPIES(Copies.class, ValueTag.INTEGER)
    ;
    
    private final Class<? extends Attribute> attrClass;
    private final ValueTag vt;
    
    Attributes(Class<? extends Attribute> c, ValueTag s) {
        attrClass = c;
        vt = s;
    }
    
    public static Attribute create(String attr, ValueTag s, Object o) {
        Attribute a = createSpecific(attr, s, o);
        if (a == null)
            a = createGeneric(attr, s, o);
        return a;
    }
    
    private static Attribute createSpecific(String attr, ValueTag s, Object o) {
        Attributes as = null;
        Attribute  a  = null;
        try {
            as = Attributes.valueOf(attr.toUpperCase().replace('-', '_'));
            switch (s) {
                case INTEGER:
                case BOOLEAN:
                case ENUM:
                    a = as.attrClass.getConstructor(int.class).newInstance(o);
                    
            }
//            Copies((IntegerSyntax)a).
//        PageRanges pr = (PageRanges) a.attrClass.newInstance();
            return a;
        } catch (InstantiationException ex) {
            Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            if (as != null)
                Logger.getLogger(Attributes.class.getName()).log(Level.SEVERE, null, ex);
            // otherwise no such attribute known - OK
        }
        
        return null;
    }

    private static Attribute createGeneric(String attr, ValueTag s, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

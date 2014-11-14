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

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import org.ciju.ipp.IppEncoding.GroupTag;

/**
 *
 * @author Opher Shachar
 */
public abstract class IppObject {

    /**
     * Adds the given {@link Attribute} to the current attribute group.
     * 
     * @param a an attribute to add to the current attribute group.
     * @return <tt>true</tt> if the current attribute group changed as a result of
     *          the call, i.e., the given attribute value was not already a member 
     *          of the current attribute group.
     */
    protected abstract boolean addAttribute(Attribute a);
    
    /**
     * Adds the {@link Attribute}s from the given {@link AttributeSet} to the current
     * attribute group.
     * 
     * @param as a {@link AttributeSet Set of Attributes} to add to the current 
     *      attribute group.
     * @return <tt>true</tt> if the current attribute group changed as a result of
     *          the call, i.e., any attribute value in the given AttributeSet was not
     *          already a member of the current attribute group.
     */
    protected abstract boolean addAllAttributes(AttributeSet as);
    
    /**
     * Adds a new attribute group to this object's attributes list.
     * 
     * @param gt the {@link GroupTag GroupTag} for the new attribute group to add.
     * @return true.
     */
    protected abstract boolean newAttributeGroup(GroupTag gt);

    public enum Conformity {
        /** Raise an exception in face of a non-conformant situation */
        STRICT,
        /** Fix non-conformant issues (e.g. by truncation, sensible default etc.) */
        LENIENT,
        /** Let IPP Server or calling code deal with non-conformant situations */
        NONE
    }
}

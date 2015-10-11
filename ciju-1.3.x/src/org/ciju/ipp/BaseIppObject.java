/*
 * Copyright (C) 2012-2014 Opher Shachar
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import org.ciju.ipp.IppEncoding.GroupTag;
import org.ciju.ipp.IppEncoding.ValueTag;
import static org.ciju.ipp.IppTransport.resourceStrings;
import org.ciju.ipp.attribute.AttributeGroup;
import static org.ciju.ipp.attribute.GenericValue.deduceValueTag;

/**
 * Used as a base class containing common code between {@link IppRequest} and
 * {@link IppResponse}.
 * @author Opher Shachar
 */
abstract class BaseIppObject extends IppObject {
    private final IppHeader header;
    private final ArrayList<AttributeGroup> ags;

    protected BaseIppObject(short version, short code, int requestId) {
        this.header = new IppHeader(version, code, requestId);
    }
    
    protected BaseIppObject(short code, int requestId) {
        this.header = new IppHeader(code, requestId);
    }

    // Instance initializer for 'ags'
    {
        ags = new ArrayList<AttributeGroup>(2);
        ags.add(new AttributeGroup(GroupTag.OPERATION));
    }

    protected List<AttributeGroup> getAttributeGroups() {
        return Collections.unmodifiableList(ags);
    }

    public Attribute getAttribute(String name, GroupTag groupTag, ValueTag valueTag) {
        for (AttributeGroup attributeGroup : ags)
            if (attributeGroup.groupTag() == groupTag) {
                Attribute attribute = attributeGroup.get(name);
                if (attribute != null) {
                    ValueTag dvt = deduceValueTag(attribute, getLocale());
                    if (dvt == valueTag ||
                        (valueTag == ValueTag.NAME && 
                            (dvt == ValueTag.NAME_WITHOUT_LANGUAGE ||
                             dvt == ValueTag.NAME_WITH_LANGUAGE)) ||
                        (valueTag == ValueTag.TEXT && 
                            (dvt == ValueTag.TEXT_WITHOUT_LANGUAGE ||
                             dvt == ValueTag.TEXT_WITH_LANGUAGE)))
                        return attribute;
                }
            }
        return null;
    }
    
    /**
     * Add an Operational Attribute to this request or response object.
     * 
     * @param a the Operational Attribute to add
     * @return true if the given operational attribute value was not already added,
     *         false otherwise.
     */
    protected boolean addOperationAttribute(Attribute a) {
        return ags.get(0).add(a);
    }
    
    protected boolean addAttribute(Attribute a) {
        return ags.get(ags.size()-1).add(a);
    }

    protected boolean addAllAttributes(AttributeSet as) {
        return ags.get(ags.size()-1).addAll(as);
    }

    protected boolean newAttributeGroup(GroupTag gt) {
        if (gt == GroupTag.OPERATION)
            throw new IllegalStateException(resourceStrings.getString("OPERATION GROUP TAG ALREADY PRESENT."));
        else if (gt == GroupTag.END)
            // ignore, this is just an end marker.
            return true;
        else
            return ags.add(new AttributeGroup(gt));
    }

    public int getRequestId() {
        return header.getRequestId();
    }

    protected short getCode() {
        return header.getCode();
    }

    public short getVersion() {
        return header.getVersion();
    }

    public abstract Locale getLocale();

    /**
     * This default implementation does nothing.
     * @param loc the {@linkplain Locale} to set for this IPP Object.
     */
    void setLocale(Locale loc) { };
}

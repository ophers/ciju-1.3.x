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
import java.util.Locale;
import javax.print.attribute.Attribute;
import org.ciju.ipp.IppEncoding.GroupTag;
import org.ciju.ipp.IppEncoding.ValueTag;
import org.ciju.ipp.attribute.AttributeGroup;
import static org.ciju.ipp.attribute.GenericValue.deduceValueTag;

/**
 * Used as a base class containing common code between {@link IppRequest} and
 * {@link IppResponse}.
 * @author Opher Shachar
 */
abstract class BaseIppObject extends IppObject {
    private final IppHeader header;
    protected final ArrayList<AttributeGroup> ags;
    {
        ags = new ArrayList<AttributeGroup>(2);
        ags.add(new AttributeGroup(GroupTag.OPERATION));
    }

    protected BaseIppObject(short version, short code, int requestId) {
        this.header = new IppHeader(version, code, requestId);
    }
    
    protected BaseIppObject(short code, int requestId) {
        this.header = new IppHeader(code, requestId);
    }

    public Attribute getAttribute(String name, GroupTag groupTag, ValueTag valueTag) {
        for (AttributeGroup attributeGroup : ags) {
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
        }
        return null;
    }
    
    public boolean addAttribute(Attribute a) {
        return ags.get(ags.size()-1).add(a);
    }

    protected boolean newAttributeGroup(GroupTag gt) {
        if (gt == GroupTag.OPERATION)
            throw new IllegalStateException("Operation group tag already present.");
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
}

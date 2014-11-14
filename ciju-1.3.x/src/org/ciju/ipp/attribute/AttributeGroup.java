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

package org.ciju.ipp.attribute;

import javax.print.attribute.AttributeSet;
import static org.ciju.ipp.IppEncoding.GroupTag;

/**
 *
 * @author Opher Shachar
 */
public class AttributeGroup extends GenericAttributeSet {
    private static final long serialVersionUID = -8805114795791154621L;
    
    private final GroupTag gt;

    /**
     *
     * @param gt
     */
    public AttributeGroup(GroupTag gt) {
        this.gt = gt;
    }

    /**
     *
     * @param gt
     * @param attrs
     */
    public AttributeGroup(GroupTag gt, AttributeSet attrs) {
        super(attrs);
        this.gt = gt;
    }

    public GroupTag groupTag() {
        return gt;
    }
}

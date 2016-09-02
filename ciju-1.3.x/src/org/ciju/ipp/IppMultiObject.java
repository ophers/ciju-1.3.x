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

import java.util.List;
import java.util.ListIterator;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;

/**
 *
 * @param <T> a subclass of {@linkplain IppObject} that is the expected ipp response 
 *  entity.
 * @author Opher Shachar
 */
public final class IppMultiObject<T extends IppObject> extends IppObject {
    private final ListIterator<T> li;
    private final IppObjectFactory<T> fact;
    
    private T curr;
    
    public IppMultiObject(List<T> list, IppObjectFactory<T> fact) {
        if (list == null || fact == null)
            throw new NullPointerException();
        this.li = list.listIterator();
        this.fact = fact;
    }
    
    public IppMultiObject(ListIterator<T> li, IppObjectFactory<T> fact) {
        if (li == null || fact == null)
            throw new NullPointerException();
        this.li = li;
        this.fact = fact;
    }
    
    /* This will be called first before addAttribute or addAllAttributes so
       curr will not be null when those other methods are called */
    protected boolean newAttributeGroup(IppEncoding.GroupTag gt) {
        if (fact.canCreate(gt)) {
            curr = fact.create(gt);
            li.add(curr);
            return true;
        }
        return false;
    }
    
    protected boolean addAttribute(Attribute a) {
        return curr.addAttribute(a);
    }
    
    protected boolean addAllAttributes(AttributeSet as) {
        return curr.addAllAttributes(as);
    }
}

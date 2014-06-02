/*
 * Copyright (C) 2014 Opher Shachar
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

package org.ciju.client;

import java.util.Collection;
import java.util.ListIterator;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppObjectFactory;

/**
 *
 * @param <T>
 * @author Opher Shachar
 */
/* package */ class IppMultiObject<T extends IppObject> extends IppObject {
    private final Collection<T> col;
    private final ListIterator<T> li;
    private final IppObjectFactory<T> fact;
    
    private T currObj;
    
    /* package */ IppMultiObject(Collection<T> col, IppObjectFactory<T> fact) {
        this.col = col;
        this.fact = fact;
        this.li = null;
    }
    
    /* package */ IppMultiObject(ListIterator<T> li, IppObjectFactory<T> fact) {
        this.li = li;
        this.fact = fact;
        this.col = null;
    }
    
    private void add() {
        // Do something with currObj ...
        currObj = fact.create();
    }
}

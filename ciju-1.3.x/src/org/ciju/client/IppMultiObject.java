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

/**
 *
 * @param <T>
 * @author Opher Shachar
 */
/* package */ class IppMultiObject<T extends IppObject> extends IppObject {
    private final Collection<T> col;
    private final ListIterator<T> li;
    private final Class<T> type;
    
    private T currObj;
    
    private void init() throws InstantiationException, IllegalAccessException {
        currObj = type.newInstance();
    }
    
    public IppMultiObject(Collection<T> col, Class<T> type) throws InstantiationException, IllegalAccessException {
        this.col = col;
        this.type = type;
        this.li = null;
        init();
    }
    
    public IppMultiObject(ListIterator<T> li, Class<T> type) throws InstantiationException, IllegalAccessException {
        this.li = li;
        this.type = type;
        this.col = null;
        init();
    }
    
    private void add() {
        try {
            // Do something with currObj ...
            currObj = type.newInstance();
            
        } catch (InstantiationException ex) {
            /// Cannot happen
        } catch (IllegalAccessException ex) {
            /// Cannot happen
        }
    }
}

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

package org.ciju.ipp.attribute;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;

/**
 *
 * @author Opher Shachar
 */
public class GenericAttributeSet extends AbstractSet<Attribute> implements AttributeSet, Serializable {
    private static final long serialVersionUID = -5744845131975323409L;
    
    /**
     * A HashMap used by the implementation.
     * The serialised form doesn't include this instance variable.
     */
    private transient HashMap<String, Attribute> map;

    /**
     * Save the state of this <tt>GenericAttributeSetSet</tt> instance to a stream
     * (that is, serialize this set).
     *
     * @serialData The size of the set (the number of elements it contains) is
     *		   emitted, followed by all of its elements (each an Attribute) in
     *             no particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
	// Write out any hidden serialization magic
	s.defaultWriteObject();

        // Write out size
        s.writeInt(map.size());

	// Write out all Attributes.
	for (Attribute a : map.values())
            s.writeObject(a);
    }

    /**
     * Reconstitute this <tt>GenericAttributeSetuteSet</tt> instance from a stream
     * (that is, deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
	// Read in any hidden serialization magic
	s.defaultReadObject();

        // Read in HashMap size and create backing HashMap
        int size = s.readInt();
        map = new HashMap<String, Attribute>((int) (size/.75f) + 1);

	// Read in all Attributes.
	for (int i = 0; i < size; i++) {
            Attribute a = (Attribute) s.readObject();
            map.put(a.getName(), a);
        }
    }

    public GenericAttributeSet() {
        map = new HashMap<String, Attribute>();
    }

    public GenericAttributeSet(AttributeSet as) {
        map = new HashMap<String, Attribute>((int) (as.size()/.75f) + 1);
        for (Attribute attr : as.toArray())
            map.put(attr.getName(), attr);
    }
    
    @Override
    public Iterator<Attribute> iterator() {
        return map.values().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    public Attribute get(String attrName) {
        return map.get(attrName);
    }

    public boolean remove(String attrName) {
        return map.remove(attrName) != null;
    }

    public boolean remove(Attribute attribute) {
        return attribute != null &&
               map.remove(attribute.getName()) != null;
    }

    public boolean containsKey(String attrName) {
        return map.get(attrName) != null;
    }

    public boolean containsValue(Attribute attribute) {
        return attribute != null && attribute.equals(map.get(attribute.getName()));
    }

    public boolean addAll(AttributeSet attributes) {
	boolean modified = false;
        for (Attribute a : attributes.toArray())
	    modified |= add(a);
	return modified;
    }

    @Override
    public Attribute[] toArray() {
        return super.toArray(new Attribute[0]);
    }

    @Override
    public boolean add(Attribute attr) {
        Attribute old = map.put(attr.getName(), attr);
        return !attr.equals(old);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean contains(Object o) {
        return o != null &&
               o instanceof Attribute &&
               o.equals(map.get(((Attribute) o).getName()));
    }

    @Override
    public boolean remove(Object o) {
        return o != null &&
               o instanceof Attribute &&
               map.remove(((Attribute) o).getName()) != null;
    }

//<editor-fold defaultstate="collapsed" desc="Methods from AttributeSet that should not be used">
    @Deprecated
    public Attribute get(Class<?> category) {
        for (Attribute a : this)
            if (category.equals(a.getCategory()))
                return a;
        return null;
    }
    
    @Deprecated
    public boolean remove(Class<?> category) {
        if (category != null) {
            Iterator<Attribute> i = iterator();
            while (i.hasNext()) {
                Attribute a = i.next();
                if (category.equals(a.getCategory())) {
                    i.remove();
                    return true;
                }
            }
        }
        return false;
    }
    
    @Deprecated
    public boolean containsKey(Class<?> category) {
        for (Attribute a : this)
            if (a.getCategory().equals(category))
                return true;
        return false;
    }
//</editor-fold>
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;

/**
 *
 * @author Opher Shachar
 */
public class GenericAttribute implements Attribute, List<Object> {

    private final String name;

    private final List<Object> list;
    private final List<Object> unmodList;

    public GenericAttribute(String name) {
        this(name, 1);
    }

    public GenericAttribute(String name, int initCapacity) {
        this.name = name;
        this.list = new ArrayList<Object>(initCapacity);
        this.unmodList = Collections.unmodifiableList(list);
    }
    
    public Class<? extends Attribute> getCategory() {
        return GenericAttribute.class;
    }

    public String getName() {
        return name;
    }

    private void valivateSyntax(Object o) {
        if (o instanceof DateTimeSyntax ||
            o instanceof EnumSyntax ||
            o instanceof IntegerSyntax ||
            o instanceof ResolutionSyntax ||
            o instanceof SetOfIntegerSyntax ||
            o instanceof Size2DSyntax ||
            o instanceof TextSyntax ||
            o instanceof URISyntax ||
            o instanceof int[])
            return;

        throw new IllegalArgumentException("Argument does not implement a known Syntax.");
    }

    public boolean add(DateTimeSyntax o) {
        return list.add(o);
    }

    public boolean add(EnumSyntax o) {
        return list.add(o);
    }
    
    public boolean add(IntegerSyntax o) {
        return list.add(o);
    }
    
    public boolean add(ResolutionSyntax o) {
        return list.add(o);
    }
    
    public boolean add(SetOfIntegerSyntax o) {
        return list.add(o);
    }
    
    public boolean add(Size2DSyntax o) {
        return list.add(o);
    }
    
    public boolean add(TextSyntax o) {
        return list.add(o);
    }
    
    public boolean add(URISyntax o) {
        return list.add(o);
    }
    
    public boolean add(int[] o) {
        return list.add(o);
    }
    
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> This will be slower than the overloaded methods as
     * the object is validated to be acceptable.
     * @throws IllegalArgumentException if '<tt>o</tt>' does not implement a 
     * known Syntax
     */
    public boolean add(Object o) {
        valivateSyntax(o);
        return list.add(o);
    }
        
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> This will take time linear to <tt>c.size()</tt> as
     * each element is validated to be acceptable.
     * @throws IllegalArgumentException if any object in '<tt>c</tt>' does not 
     * implement a known Syntax
     */
    public boolean addAll(Collection<? extends Object> c) {
        for (Object o : c)
            valivateSyntax(o);
        return list.addAll(c);
    }
    
    /**
     * @throws IllegalArgumentException if '<tt>element</tt>' does not implement
     * a known Syntax
     */
    public void add(int index, Object element) {
        valivateSyntax(element);
        list.add(index, element);
    }
    
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> This will take time linear to <tt>c.size()</tt> as
     * each element is validated to be acceptable.
     * @throws IllegalArgumentException if any object in '<tt>c</tt>' does not 
     * implement a known Syntax
     */
    public boolean addAll(int index, Collection<? extends Object> c) {
        for (Object o : c)
            valivateSyntax(o);
        return list.addAll(index, c);
    }
    
    /**
     * @throws IllegalArgumentException if '<tt>element</tt>' does not implement
     * a known Syntax
     */
    public Object set(int index, Object element) {
        valivateSyntax(element);
        return list.set(index, element);
    }
    
//<editor-fold defaultstate="collapsed" desc="unmodList delegated methods">
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> The iterator returned does not allow mutating operations.
     */
    public ListIterator<Object> listIterator() {
        return unmodList.listIterator();
    }
    
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> The iterator returned does not allow mutating operations.
     */
    public ListIterator<Object> listIterator(int index) {
        return unmodList.listIterator(index);
    }
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="List delegated methods">
    public int size() {
        return list.size();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public boolean contains(Object o) {
        return list.contains(o);
    }
    
    public Iterator<Object> iterator() {
        return list.iterator();
    }
    
    public Object[] toArray() {
        return list.toArray();
    }
    
    /**
     * @throws ArrayStoreException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
    
    public boolean remove(Object o) {
        return list.remove(o);
    }
    
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }
    
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }
    
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }
    
    public void clear() {
        list.clear();
    }
    
    public boolean equals(Object o) {
        return list.equals(o);
    }
    
    public int hashCode() {
        return list.hashCode();
    }
    
    public Object get(int index) {
        return list.get(index);
    }
    
    public Object remove(int index) {
        return list.remove(index);
    }
    
    public int indexOf(Object o) {
        return list.indexOf(o);
    }
    
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }
    
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
//</editor-fold>
}

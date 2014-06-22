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
        if (name == null)
            throw new NullPointerException("name");
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
        if (o == null)
            throw new NullPointerException("element");
        
        if (o instanceof DateTimeSyntax ||
            o instanceof EnumSyntax ||
            o instanceof IntegerSyntax ||
            o instanceof ResolutionSyntax ||
            o instanceof SetOfIntegerSyntax ||
            o instanceof Size2DSyntax ||
            o instanceof TextSyntax ||
            o instanceof URISyntax)
            return;
        if (o instanceof int[])
            if (((int[]) o).length == 2)
                return;
            else
                throw new IllegalArgumentException("array must hold exactly two elements.");

        throw new ClassCastException("Argument does not implement a known Syntax.");
    }

    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(DateTimeSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }

    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(EnumSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(IntegerSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(ResolutionSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(SetOfIntegerSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(Size2DSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(TextSyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(URISyntax o) {
        if (o == null)
            throw new NullPointerException("element");
        return list.add(o);
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o a two element int array to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(int[] o) {
        if (o == null)
            throw new NullPointerException("element");
        else if (o.length != 2)
            throw new IllegalArgumentException("array must hold exactly two elements.");
        return list.add(o);
    }
    
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> This will be slower than the overloaded methods as
     * the object is validated to be acceptable.
     * @throws ClassCastException if '<tt>o</tt>' does not implement a known Syntax.
     * @throws NullPointerException if the specified element is null.
     * @throws IllegalArgumentException if argument type is int[] and its elements
     *           count is not two.
     */
    public boolean add(Object o) {
        valivateSyntax(o);
        return list.add(o);
    }
        
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> This will take time linear to <tt>c.size()</tt> as
     * each element is validated to be acceptable.
     * @throws ClassCastException if any element in '<tt>c</tt>' does not implement a
     *           known Syntax.
     * @throws NullPointerException if '<tt>c</tt>' or any of its elements is null.
     * @throws IllegalArgumentException if any element in '<tt>c</tt>' is of type
     *           int[] and its length is not two.
     */
    public boolean addAll(Collection<? extends Object> c) {
        for (Object o : c)
            valivateSyntax(o);
        return list.addAll(c);
    }
    
    /**
     * @throws ClassCastException if '<tt>element</tt>' does not implement a known
     *           Syntax.
     * @throws NullPointerException if the specified element is null.
     * @throws IllegalArgumentException if argument type is int[] and its elements
     *           count is not two.
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, Object element) {
        valivateSyntax(element);
        list.add(index, element);
    }
    
    /**
     * {@inheritDoc}
     * <p><b><u>Note</u>:</b> This will take time linear to <tt>c.size()</tt> as
     * each element is validated to be acceptable.
     * @throws ClassCastException if any element in '<tt>c</tt>' does not implement a
     *           known Syntax.
     * @throws NullPointerException if '<tt>c</tt>' or any of its elements is null.
     * @throws IllegalArgumentException if any element in '<tt>c</tt>' is of type
     *           int[] and its length is not two.
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public boolean addAll(int index, Collection<? extends Object> c) {
        for (Object o : c)
            valivateSyntax(o);
        return list.addAll(index, c);
    }
    
    /**
     * @throws ClassCastException if '<tt>element</tt>' does not implement a known
     *           Syntax.
     * @throws NullPointerException if the specified element is null.
     * @throws IllegalArgumentException if argument type is int[] and its elements
     *           count is not two.
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public Object set(int index, Object element) {
        valivateSyntax(element);
        return list.set(index, element);
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof GenericAttribute) && 
                this.name.equals(((GenericAttribute) o).name) &&
                list.equals(o);
    }
    
    @Override
    public int hashCode() {
        return 31*list.hashCode() + name.hashCode();
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

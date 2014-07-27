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
import org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public class GenericAttribute implements Attribute, List<Object> {

    private final String name;
    private final Class<? extends Attribute> category;

    private final List<Object> list;
    private final List<Object> unmodList;

    public GenericAttribute(String name) {
        this(name, GenericAttribute.class, 1);
    }

    public GenericAttribute(String name, int initCapacity) {
        this(name, GenericAttribute.class, initCapacity);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category) {
        this(name, category, 1);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, int initCapacity) {
        if (name == null || category == null)
            throw new NullPointerException();
        this.name = name;
        this.category = category;
        this.list = new ArrayList<Object>(initCapacity);
        this.unmodList = Collections.unmodifiableList(list);
    }

    public GenericAttribute(String name, Object value) {
        this(name, GenericAttribute.class, 1, value);
    }

    public GenericAttribute(String name, int initCapacity, Object value) {
        this(name, GenericAttribute.class, initCapacity, value);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, Object value) {
        this(name, category, 1, value);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, int initCapacity,
            Object value) {
        this(name, category, initCapacity);
        GenericValue.validateSyntax(value);
        list.add(value);
    }

    public GenericAttribute(String name, Object value, ValueTag vt) {
        this(name, GenericAttribute.class, 1, value, vt);
    }

    public GenericAttribute(String name, int initCapacity, Object value, ValueTag vt) {
        this(name, GenericAttribute.class, initCapacity, value, vt);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, Object value, ValueTag vt) {
        this(name, category, 1, value, vt);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, int initCapacity,
            Object value, ValueTag vt) {
        this(name, category, initCapacity);
        list.add(new GenericValue(vt, value));
    }
    
    public final Class<? extends Attribute> getCategory() {
        return category;
    }

    public final String getName() {
        return name;
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
     * View description for {@link #add(Object) add(Object)}.
     * @param lb the lower bound of the integer range
     * @param ub the upper bound of the integer range
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(int lb, int ub) {
        return add(new int[] {lb, ub});
    }
    
    /**
     * View description for {@link #add(Object) add(Object)}.
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of the
     *            <tt>Collection.add</tt> method).
     */
    public boolean add(int o) {
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
        GenericValue.validateSyntax(o);
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
            GenericValue.validateSyntax(o);
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
        GenericValue.validateSyntax(element);
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
            GenericValue.validateSyntax(o);
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
        GenericValue.validateSyntax(element);
        return list.set(index, element);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof GenericAttribute))
            return false;
        GenericAttribute other = (GenericAttribute) o;
        return name.equals(other.name) &&
                list.equals(other.list);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + name.hashCode();
        hash = 19 * hash + list.hashCode();
        return hash;
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

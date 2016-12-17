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

package org.ciju.ipp.attribute;

import java.net.ProtocolException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterLocation;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.PrinterMoreInfo;
import javax.print.attribute.standard.PrinterMoreInfoManufacturer;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterURI;
import org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public class GenericAttribute implements Attribute, List<Object> {
    private static final long serialVersionUID = 4440999212703310846L;

    // Logging facilities
    /* package */ static final ResourceBundle resourceStrings = ResourceBundle.getBundle("org/ciju/ResourceStrings");
    /* package */ static final Logger logger;
    static {
        String name = GenericAttribute.class.getName();
        String packageName = name.substring(0, name.lastIndexOf('.'));
        logger = Logger.getLogger(packageName, "org/ciju/ResourceStrings");
    }

    private final String name;
    private final Class<? extends Attribute> category;

    private final ArrayList<Object> list;
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
        validateSyntax(value);
        list.add(value);
    }

    public GenericAttribute(String name, Object value, ValueTag vt) {
        this(name, GenericAttribute.class, 1, value, vt);
    }

    public GenericAttribute(String name, int initCapacity, Object value, ValueTag vt) throws ProtocolException {
        this(name, GenericAttribute.class, initCapacity, value, vt);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, Object value, ValueTag vt) throws ProtocolException {
        this(name, category, 1, value, vt);
    }

    public GenericAttribute(String name, Class<? extends Attribute> category, int initCapacity,
            Object value, ValueTag vt) {
        this(name, category, initCapacity);
        list.add(new GenericValue(vt, value));
    }

    /**
     * Validate and make sure we support the type of the value given.
     * 
     * @param o a value for this attribute
     * @throws NullPointerException if <tt>o</tt> is null.
     * @throws IllegalArgumentException if <tt>o</tt> is of a type we don't handle.
     */
    private void validateSyntax(Object o) {
        if (o == null)
            throw new NullPointerException("element");
        
        // CIJU generic value
        if (o instanceof GenericValue)
            return;
        
        // JPS standard syntaxes
        if (o instanceof DateTimeSyntax ||
            o instanceof EnumSyntax ||
            o instanceof IntegerSyntax ||
            o instanceof ResolutionSyntax ||
            o instanceof SetOfIntegerSyntax ||
            o instanceof Size2DSyntax ||
            o instanceof TextSyntax ||
            o instanceof URISyntax)
            return;

        // Other standard objects
        if (o instanceof Integer /* used for syntaxes: enum/integer */ ||
            o instanceof String  /* used for syntaxes: name/text (w/o lang),
                                    keyword/uriScheme/charset/naturalLanguage
                                    mimeMediaType */ ||
            o instanceof URI     /* used for 'uri' syntax */ ||
            o instanceof Boolean /* used for 'boolean' syntax */ ||
            o instanceof Date    /* used for 'dateTime' syntax */ ||
            o instanceof byte[]  /* used for 'octetString' syntax */)
            return;
        if (o instanceof int[]) /* used for 'rangeOfInteger' syntax */
            if (((int[]) o).length == 2)
                return;
            else
                throw new IllegalArgumentException(resourceStrings.getString("ARRAY MUST HOLD EXACTLY TWO ELEMENTS."));
        
        throw new IllegalArgumentException(resourceStrings.getString("ARGUMENT DOES NOT IMPLEMENT A KNOWN SYNTAX."));
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
            throw new IllegalArgumentException(resourceStrings.getString("ARRAY MUST HOLD EXACTLY TWO ELEMENTS."));
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
        validateSyntax(o);
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
            validateSyntax(o);
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
        validateSyntax(element);
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
            validateSyntax(o);
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
        validateSyntax(element);
        return list.set(index, element);
    }
    
    /**
     *
     * @return
     */
    public Attribute subst() {
        // extract actual object of attribute's value
        Object o = get(0);
        if (o instanceof GenericValue)
            o = ((GenericValue)o).getValue();
        
        if ((name.equals("printer-uri") || name.equals("printer-uri-supported"))
                && o instanceof URI) {
            return new PrinterURI((URI) o);
        }
        else if (name.equals("printer-name") && o instanceof TextSyntax) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterName(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-location") && o instanceof TextSyntax) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterLocation(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-info") && o instanceof TextSyntax) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterInfo(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-more-info") && o instanceof URI) {
            return new PrinterMoreInfo((URI) o);
        }
        else if (name.equals("printer-make-and-model") && o instanceof TextSyntax) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterMakeAndModel(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-more-info-manufacturer") && o instanceof URI) {
            return new PrinterMoreInfoManufacturer((URI) o);
        }
        else if (name.equals("printer-state") && o instanceof Integer) {
            switch ((Integer) o) {
                case 3:
                    return PrinterState.IDLE;
                case 4:
                    return PrinterState.PROCESSING;
                case 5:
                    return PrinterState.STOPPED;
                default:
                    return PrinterState.UNKNOWN;
            }
        }
        else if (name.equals("printer-is-accepting-jobs") && o instanceof Boolean) {
            return (Boolean) o ? PrinterIsAcceptingJobs.ACCEPTING_JOBS
                    : PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
        }

        // if nothing matched return self
        return this;
    }
    
    @Override
    public String toString() {
        if (list.isEmpty())
            return "";
        
        Iterator<Object> it = list.iterator();
        StringBuilder sb = new StringBuilder(it.next().toString());
        while (it.hasNext()) {
            sb.append(", ").append(it.next().toString());
        }
        return sb.toString();
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

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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.DateTimeAtCompleted;
import javax.print.attribute.standard.DateTimeAtCreation;
import javax.print.attribute.standard.DateTimeAtProcessing;
import javax.print.attribute.standard.JobImpressions;
import javax.print.attribute.standard.JobImpressionsCompleted;
import javax.print.attribute.standard.JobImpressionsSupported;
import javax.print.attribute.standard.JobKOctets;
import javax.print.attribute.standard.JobKOctetsProcessed;
import javax.print.attribute.standard.JobKOctetsSupported;
import javax.print.attribute.standard.JobMediaSheets;
import javax.print.attribute.standard.JobMediaSheetsCompleted;
import javax.print.attribute.standard.JobMediaSheetsSupported;
import javax.print.attribute.standard.JobMessageFromOperator;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.JobState;
import javax.print.attribute.standard.JobStateReason;
import javax.print.attribute.standard.JobStateReasons;
import javax.print.attribute.standard.NumberOfDocuments;
import javax.print.attribute.standard.NumberOfInterveningJobs;
import javax.print.attribute.standard.OutputDeviceAssigned;
import javax.print.attribute.standard.PDLOverrideSupported;
import javax.print.attribute.standard.PagesPerMinute;
import javax.print.attribute.standard.PagesPerMinuteColor;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterLocation;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.PrinterMessageFromOperator;
import javax.print.attribute.standard.PrinterMoreInfo;
import javax.print.attribute.standard.PrinterMoreInfoManufacturer;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.PrinterURI;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.Severity;
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
        assert get(0) instanceof GenericValue :
                "This method should only be called on self constructed instances.";
        
        // extract actual object of attribute's value
        Object o = ((GenericValue) get(0)).getValue();
        
        /* Printer Description Attributes, https://tools.ietf.org/html/rfc2911#section-4.4 */
        if (name.equals("printer-uri") || name.equals("printer-uri-supported")) {
            return new PrinterURI((URI) o);
        }
        else if (name.equals("printer-name")) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterName(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-location")) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterLocation(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-info")) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterInfo(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-more-info")) {
            return new PrinterMoreInfo((URI) o);
        }
        else if (name.equals("printer-make-and-model")) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterMakeAndModel(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("printer-more-info-manufacturer")) {
            return new PrinterMoreInfoManufacturer((URI) o);
        }
        else if (name.equals("printer-state")) {
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
        else if (name.equals("printer-state-reasons")) {
            // This is a multivalued type of attribute
            return substPrinterStateReasons();
        }
        else if (name.equals("printer-is-accepting-jobs")) {
            return (Boolean) o ? PrinterIsAcceptingJobs.ACCEPTING_JOBS
                    : PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
        }
        else if (name.equals("queued-job-count")) {
            return new QueuedJobCount((Integer) o);
        }
        else if (name.equals("printer-message-from-operator")) {
            TextSyntax ts = (TextSyntax) o;
            return new PrinterMessageFromOperator(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("color-supported")) {
            return (Boolean) o ? ColorSupported.SUPPORTED
                    : ColorSupported.NOT_SUPPORTED;
        }
        else if (name.equals("reference-uri-schemes-supported")) {
            // FIXME: not quite sure how to handle this ...
        }
        else if (name.equals("pdl-override-supported")) {
            if ("attempted".equals(o))
                return PDLOverrideSupported.ATTEMPTED;
            else if ("not-attempted".equals(o))
                return PDLOverrideSupported.NOT_ATTEMPTED;
        }
        else if (name.equals("job-k-octets-supported")) {
            return new JobKOctetsSupported(((int[]) o)[0], ((int[]) o)[1]);
        }
        else if (name.equals("job-impressions-supported")) {
            return new JobImpressionsSupported(((int[]) o)[0], ((int[]) o)[1]);
        }
        else if (name.equals("job-media-sheets-supported")) {
            return new JobMediaSheetsSupported(((int[]) o)[0], ((int[]) o)[1]);
        }
        else if (name.equals("pages-per-minute")) {
            return new PagesPerMinute((Integer) o);
        }
        else if (name.equals("pages-per-minute-color")) {
            return new PagesPerMinuteColor((Integer) o);
        }

        /* Job Description Attributes, https://tools.ietf.org/html/rfc2911#section-4.3 */
        else if (name.equals("job-name")) {
            TextSyntax ts = (TextSyntax) o;
            return new JobName(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("job-originating-user-name")) {
            TextSyntax ts = (TextSyntax) o;
            return new JobOriginatingUserName(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("job-state")) {
            switch ((Integer) o) {
                case 3:
                    return JobState.PENDING;
                case 4:
                    return JobState.PENDING_HELD;
                case 5:
                    return JobState.PROCESSING;
                case 6:
                    return JobState.PROCESSING_STOPPED;
                case 7:
                    return JobState.CANCELED;
                case 8:
                    return JobState.ABORTED;
                case 9:
                    return JobState.COMPLETED;
                default:
                    return JobState.UNKNOWN;
            }
        }
        else if (name.equals("job-state-reasons")) {
            return substJobStateReasons();
        }
        else if (name.equals("number-of-documents")) {
            return new NumberOfDocuments((Integer) o);
        }
        else if (name.equals("output-device-assigned")) {
            TextSyntax ts = (TextSyntax) o;
            return new OutputDeviceAssigned(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("time-at-creation")) {
            return new DateTimeAtCreation(new Date(((Integer) o) * 1000));
        }
        else if (name.equals("time-at-processing")) {
            return new DateTimeAtProcessing(new Date(((Integer) o) * 1000));
        }
        else if (name.equals("time-at-completed")) {
            return new DateTimeAtCompleted(new Date(((Integer) o) * 1000));
        }
        else if (name.equals("date-time-at-creation")) {
            return new DateTimeAtCreation((Date) o);
        }
        else if (name.equals("date-time-at-processing")) {
            return new DateTimeAtProcessing((Date) o);
        }
        else if (name.equals("date-time-at-completed")) {
            return new DateTimeAtCompleted((Date) o);
        }
        else if (name.equals("number-of-intervening-jobs")) {
            return new NumberOfInterveningJobs((Integer) o);
        }
        else if (name.equals("job-message-from-operator")) {
            TextSyntax ts = (TextSyntax) o;
            return new JobMessageFromOperator(ts.getValue(), ts.getLocale());
        }
        else if (name.equals("job-k-octets")) {
            return new JobKOctets((Integer) o);
        }
        else if (name.equals("job-impressions")) {
            return new JobImpressions((Integer) o);
        }
        else if (name.equals("job-media-sheets")) {
            return new JobMediaSheets((Integer) o);
        }
        else if (name.equals("job-k-octets-processed")) {
            return new JobKOctetsProcessed((Integer) o);
        }
        else if (name.equals("job-impressions-completed")) {
            return new JobImpressionsCompleted((Integer) o);
        }
        else if (name.equals("job-media-sheets-completed")) {
            return new JobMediaSheetsCompleted((Integer) o);
        }

        // if nothing matched return self
        return this;
    }

    private Attribute substPrinterStateReasons() {
        // This is a multivalued type of attribute
        PrinterStateReasons psrs = new PrinterStateReasons(size() * 4/3 + 1);
        for (Object gv : this) {
            String s = (String) ((GenericValue) gv).getValue();
            // get severity
            int i = s.lastIndexOf('-');
            Severity sev = Severity.ERROR;
            if (i > 0 /* ss is a postfix */) {
                String ss = s.substring(i);
                if (ss.equals("-report"))
                    sev = Severity.REPORT;
                else if (ss.equals("-warning"))
                    sev = Severity.WARNING;
                s = s.substring(0, i);
            }
            PrinterStateReason psr = null;
            try {
                psr = (PrinterStateReason) PrinterStateReason.class
                        .getField(s.replace('-', '_').toUpperCase()).get(null);
            } catch (IllegalArgumentException ex) { /* irrelevant */
            } catch (IllegalAccessException ex) { /* irrelevant */
            } catch (NoSuchFieldException ex) {
            } catch (SecurityException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            if (psr == null)
                psr = new PrinterStateReasonValue(s);
            psrs.put(psr, sev);
        }
        return psrs;
    }

    private Attribute substJobStateReasons() {
        // This is a multivalued type of attribute
        JobStateReasons jsrs = new JobStateReasons(size() * 4/3 + 1);
        for (Object gv : this) {
            String s = (String) ((GenericValue) gv).getValue();
            JobStateReason jsr = null;
            try {
                jsr = (JobStateReason) JobStateReason.class
                        .getField(s.replace('-', '_').toUpperCase()).get(null);
            } catch (IllegalArgumentException ex) { /* irrelevant */
            } catch (IllegalAccessException ex) { /* irrelevant */
            } catch (NoSuchFieldException ex) {
            } catch (SecurityException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            if (jsr == null)
                jsr = new JobStateReasonValue(s);
            jsrs.add(jsr);
        }
        return jsrs;
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

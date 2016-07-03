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

package org.ciju.client.impl.ipp.attribute;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.standard.JobHoldUntil;
import org.ciju.ipp.attribute.GenericAttribute;

/**
 *
 * @author Opher
 */
public class CupsJobHoldUntil extends GenericAttribute implements PrintRequestAttribute {
    private static final long serialVersionUID = 7858038079695163322L;
    private static final ResourceBundle resourceStrings = ResourceBundle.getBundle("org/ciju/ResourceStrings");

    public CupsJobHoldUntil(JobHoldUntil o) {
        super(o.getName(), o.getCategory());
        Date date = o.getValue();
        long diff = date.getTime() - System.currentTimeMillis();
        if (diff < 0 || diff > 24*60*60*1000)
            throw new IllegalArgumentException(resourceStrings.getString("JOBHOLDUNTIL EITHER IN THE PAST OR MORE THAN A DAY AWAY."));
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        add(String.format("%TT", cal));
    }
}

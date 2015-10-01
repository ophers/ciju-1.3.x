/*
 * Copyright (C) 2012-2014 Opher Shachar
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

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.print.attribute.ResolutionSyntax;

/**
 *
 * @author Opher Shachar
 */
public class ResolutionValue extends ResolutionSyntax {
    private static final long serialVersionUID = 2909562989539601822L;
    private static final ResourceBundle resourceStrings = ResourceBundle.getBundle("org/ciju/ResourceStrings");

    public ResolutionValue(int crossFeedResolution, int feedResolution, int units) {
        super(crossFeedResolution, feedResolution, units);
    }
    
    public ResolutionValue(int crossFeedResolution, int feedResolution, byte units) {
        super(crossFeedResolution, feedResolution, convertUnits(units));
    }
    
    private static int convertUnits(byte units) {
        if (units == 3)
            return ResolutionSyntax.DPI;
        else if (units == 4)
            return ResolutionSyntax.DPCM;
        else
            throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("UNITS MAY BE EITHER 3 OR 4 NOT {0}"), units));
    }
}

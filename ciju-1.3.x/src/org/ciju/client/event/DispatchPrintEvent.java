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
package org.ciju.client.event;

import java.util.List;
import javax.print.event.PrintEvent;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

/**
 *
 * @author Opher Shachar
 */
public interface DispatchPrintEvent {

    public boolean dispatchPrintEvent(PrintEvent pe, List<?> listeners);

    public boolean dispatchPrintJobEvent(PrintJobEvent pje, List<? extends PrintJobListener> listeners);
    
}

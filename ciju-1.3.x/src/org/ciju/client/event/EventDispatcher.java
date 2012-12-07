/*
 * Copyright (C) 2012 Opher Shachar
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

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.event.PrintEvent;
import javax.print.event.PrintJobAttributeEvent;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import org.ciju.client.IppJob;
import org.ciju.client.IppPrinter;
import org.ciju.client.PrintServer;

/**
 *
 * @author Opher Shachar
 */
public class EventDispatcher implements Runnable {
    private static final Logger logger = Logger.getLogger(EventDispatcher.class.getName());
    private final BlockingQueue<PrintEvent> eventQueue = new LinkedBlockingQueue<PrintEvent>();

    public void run() {
        try {
            while (true) {
                final PrintEvent pe = eventQueue.take();
                if (pe instanceof PrintServiceAttributeEvent) {
                    for (PrintServiceAttributeListener psal : ((IppPrinter) pe.getSource()).getListeners())
                        psal.attributeUpdate((PrintServiceAttributeEvent) pe);
                }
                else if (pe instanceof PrintJobAttributeEvent) {
                    final PrintJobAttributeEvent pjae = (PrintJobAttributeEvent) pe;
                    for (PrintJobAttributeListener pjal : ((IppJob) pe.getSource()).getListeners(pjae))
                        pjal.attributeUpdate(pjae);
                }
                else if (pe instanceof PrintJobEvent) {
                    final PrintJobEvent pje = (PrintJobEvent) pe;
                    final Iterator<PrintJobListener> it = ((IppJob) pe.getSource()).listnerIterator(pje);
                    if (pje.getPrintEventType() == PrintJobEvent.DATA_TRANSFER_COMPLETE)
                        for (PrintJobListener pjl : ((IppJob) pe.getSource()).getListeners(pje))
                            pjl.printDataTransferCompleted(pje);
                    else if (pje.getPrintEventType() == PrintJobEvent.REQUIRES_ATTENTION)
                        for (; it.hasNext();) {
                            PrintJobListener psal = it.next();
                            psal.printJobRequiresAttention(pje);
                        }
                    else if (pje.getPrintEventType() == PrintJobEvent.JOB_CANCELED)
                        for (; it.hasNext();) {
                            PrintJobListener psal = it.next();
                            psal.printJobCanceled(pje);
                        }
                    else if (pje.getPrintEventType() == PrintJobEvent.JOB_FAILED)
                        for (; it.hasNext();) {
                            PrintJobListener psal = it.next();
                            psal.printJobFailed(pje);
                        }
                    else if (pje.getPrintEventType() == PrintJobEvent.JOB_COMPLETE)
                        for (; it.hasNext();) {
                            PrintJobListener psal = it.next();
                            psal.printJobCompleted(pje);
                        }
                    else if (pje.getPrintEventType() == PrintJobEvent.NO_MORE_EVENTS)
                        for (; it.hasNext();) {
                            PrintJobListener psal = it.next();
                            psal.printJobNoMoreEvents(pje);
                        }
                }
            }
        }
        catch (InterruptedException ie) {
            logger.log(Level.WARNING, "Event dispatcher thread interrupted. Exiting.", ie);
        }
    }    
}

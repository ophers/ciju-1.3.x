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

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.event.PrintEvent;
import javax.print.event.PrintJobAttributeEvent;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;
import org.ciju.client.IppJob;
import org.ciju.client.IppPrinter;

/**
 *
 * @author Opher Shachar
 */
public class EventDispatcher implements Runnable {
    private static final Logger logger = Logger.getLogger(EventDispatcher.class.getName());
    private final BlockingQueue<PrintEvent> eventQueue;

    public EventDispatcher(BlockingQueue<PrintEvent> eventQueue) {
        this.eventQueue = eventQueue;
    }

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
                    final List<PrintJobListener> pjll = ((IppJob) pe.getSource()).getListeners(pje);
                    dispatchPrintJobEvent(pje, pjll);
                }
            }
        }
        catch (InterruptedException ie) {
            logger.log(Level.WARNING, "Event dispatcher thread interrupted. Exiting.", ie);
        }
    }

    protected void dispatchPrintJobEvent(PrintJobEvent pje, List<PrintJobListener> pjll) {
        switch (pje.getPrintEventType()) {
            case PrintJobEvent.DATA_TRANSFER_COMPLETE:
                for (PrintJobListener pjl : pjll)
                    pjl.printDataTransferCompleted(pje);
                break;
            case PrintJobEvent.REQUIRES_ATTENTION:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobRequiresAttention(pje);
                break;
            case PrintJobEvent.JOB_CANCELED:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobCanceled(pje);
                break;
            case PrintJobEvent.JOB_FAILED:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobFailed(pje);
                break;
            case PrintJobEvent.JOB_COMPLETE:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobCompleted(pje);
                break;
            case PrintJobEvent.NO_MORE_EVENTS:
                for (PrintJobListener pjl : pjll)
                    pjl.printJobNoMoreEvents(pje);
                break;
            default:
                // As a library cannot throw AssertionError directly
                throw new IllegalArgumentException(new AssertionError(
                        "This PrintEventType " + pje.getPrintEventType() + " is unknown!"));
        }
    }
}

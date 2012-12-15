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

/**
 *
 * @author Opher Shachar
 */
public class EventDispatcher implements Runnable {
    private final BlockingQueue<PrintEventEntry> eventQueue;
    private final DispatchPrintEvent dispatchOther;

    // Logging facilities
    private static final Logger logger = Logger.getLogger(EventDispatcher.class.getName());

    public EventDispatcher(DispatchPrintEvent dispatchOther) {
        this.eventQueue = new LinkedBlockingQueue<PrintEventEntry>();
        this.dispatchOther = dispatchOther;
    }

    public EventDispatcher() {
        this(new CupsEventDispatcher());
    }

    public final void run() {
        while (true) {
            try {
                final PrintEventEntry pee = eventQueue.take();
                dispatchPrintEvent(pee);
            } catch (RuntimeException re) {
                logger.log(Level.INFO, "An event listener threw an exception. Some listeners may not have been invoked.", re);
            } catch (InterruptedException ie) {
                logger.log(Level.WARNING, "Event dispatcher thread interrupted. Exiting.", ie);
                return;
            }
        }
    }

    private void dispatchPrintEvent(final PrintEventEntry pee) {
        final PrintEvent pe = pee.event;
        if (pe instanceof PrintServiceAttributeEvent) {
            for (PrintServiceAttributeListener psal : pee.getListeners(PrintServiceAttributeListener.class)) {
                psal.attributeUpdate((PrintServiceAttributeEvent) pe);
            }
        } else if (pe instanceof PrintJobAttributeEvent) {
            final PrintJobAttributeEvent pjae = (PrintJobAttributeEvent) pe;
            for (PrintJobAttributeListener pjal : pee.getListeners(PrintJobAttributeListener.class)) {
                pjal.attributeUpdate(pjae);
            }
        } else if (pe instanceof PrintJobEvent) {
            final PrintJobEvent pje = (PrintJobEvent) pe;
            final List<PrintJobListener> pjll = pee.getListeners(PrintJobListener.class);
            dispatchPrintJobEvent(pje, pjll);
        } else {
            if (dispatchOther == null || !dispatchOther.dispatchPrintEvent(pe, pee.listeners)) {
                final String message = "This PrintEvent " + pe + " is unknown!";
                logger.log(Level.SEVERE, message);
                // As a library cannot throw AssertionError directly
                throw new IllegalArgumentException(new AssertionError(message));
            }
        }
    }

    private void dispatchPrintJobEvent(PrintJobEvent pje, List<PrintJobListener> pjll) {
        switch (pje.getPrintEventType()) {
            case PrintJobEvent.DATA_TRANSFER_COMPLETE:
                for (PrintJobListener pjl : pjll) {
                    pjl.printDataTransferCompleted(pje);
                }
                break;
            case PrintJobEvent.REQUIRES_ATTENTION:
                for (PrintJobListener pjl : pjll) {
                    pjl.printJobRequiresAttention(pje);
                }
                break;
            case PrintJobEvent.JOB_CANCELED:
                for (PrintJobListener pjl : pjll) {
                    pjl.printJobCanceled(pje);
                }
                break;
            case PrintJobEvent.JOB_FAILED:
                for (PrintJobListener pjl : pjll) {
                    pjl.printJobFailed(pje);
                }
                break;
            case PrintJobEvent.JOB_COMPLETE:
                for (PrintJobListener pjl : pjll) {
                    pjl.printJobCompleted(pje);
                }
                break;
            case PrintJobEvent.NO_MORE_EVENTS:
                for (PrintJobListener pjl : pjll) {
                    pjl.printJobNoMoreEvents(pje);
                }
                break;
            default:
                if (dispatchOther == null || !dispatchOther.dispatchPrintJobEvent(pje, pjll)) {
                    final String message = "This PrintEventType " + pje.getPrintEventType() + " is unknown!";
                    logger.log(Level.SEVERE, message);
                    // As a library cannot throw AssertionError directly
                    throw new IllegalArgumentException(new AssertionError(message));
                }
        }
    }

    /**
     *
     * @param event the value of printEvent
     * @param listeners the value of listeners
     */
    public final void enqueuePrintEvent(PrintEvent event, List<?> listeners) {
        eventQueue.add(new PrintEventEntry(event, listeners));
    }

    private static class PrintEventEntry {

        private final PrintEvent event;
        private final List<?> listeners;

        private PrintEventEntry(PrintEvent event, List<?> listeners) {
            this.event = event;
            this.listeners = listeners;
        }

        @SuppressWarnings(value = "unchecked")
        private <T> List<T> getListeners(Class<T> aClass) {
            return (List<T>) listeners;
        }
    }

}

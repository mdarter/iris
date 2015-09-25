/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.server.comm.e6;

import java.io.IOException;
import us.mn.state.dot.tms.server.TagReaderImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to query status of an E6.
 *
 * @author Douglas Lau
 */
public class OpQueryStatus extends OpE6 {

	/** Create a new "query status" operation */
	public OpQueryStatus(TagReaderImpl tr, E6Poller ep) {
		super(PriorityLevel.DEVICE_DATA, tr, ep);
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase<E6Property> phaseTwo() {
		return new QueryDiagStatus();
	}

	/** Phase to query the diagnostic status */
	private class QueryDiagStatus extends Phase<E6Property> {

		/** Query diag status */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			DiagStatusProp stat = new DiagStatusProp();
			poller.sendQuery(stat);
			mess.logQuery(stat);
			return new QueryBufferingMode();
		}
	}

	/** Phase to query the buffering mode */
	private class QueryBufferingMode extends Phase<E6Property> {

		/** Query the buffering mode */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			BufferingModeProp mode = new BufferingModeProp();
			poller.sendQuery(mode);
			mess.logQuery(mode);
			if (mode.isEnabled())
				return new StoreBufferingMode();
			else
				return new QueryBufferedCount();
		}
	}

	/** Phase to store the buffering mode */
	private class StoreBufferingMode extends Phase<E6Property> {

		/** Store the buffering mode */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			BufferingModeProp mode = new BufferingModeProp(false);
			mess.logStore(mode);
			poller.sendStore(mode);
			return new QueryBufferedCount();
		}
	}

	/** Phase to query the buffered tag transaction count */
	private class QueryBufferedCount extends Phase<E6Property> {

		/** Query the buffered tag transaction count */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			BufferedCountProp count = new BufferedCountProp();
			poller.sendQuery(count);
			mess.logQuery(count);
			return new QueryBufferedTransactions(count.getCount());
		}
	}

	/** Phase to query the buffered tag transactions */
	private class QueryBufferedTransactions extends Phase<E6Property> {
		private final int n_count;
		private int n_curr;
		private QueryBufferedTransactions(int n) {
			n_count = n;
			n_curr = 1;
		}

		/** Query the buffered tag transactions */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			BufferedTransactionProp trans =
				new BufferedTransactionProp(n_curr);
			poller.sendQuery(trans);
			mess.logQuery(trans);
			if (n_curr < n_count) {
				n_curr++;
				return this;
			} else
				return new ClearBufferedCount();
		}
	}

	/** Phase to clear the buffered tag transaction count */
	private class ClearBufferedCount extends Phase<E6Property> {

		/** Clear the buffered tag transaction count */
		protected Phase<E6Property> poll(CommMessage<E6Property> mess)
			throws IOException
		{
			BufferedCountProp count = new BufferedCountProp();
			mess.logStore(count);
			poller.sendStore(count);
			return null;
		}
	}
}

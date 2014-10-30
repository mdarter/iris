/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.mndot;

import java.io.IOException;
import us.mn.state.dot.tms.server.BeaconImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Query the state of a beacon
 *
 * @author Douglas Lau
 */
public class OpQueryBeaconState extends Op170Device {

	/** Beacon device */
	private final BeaconImpl beacon;

	/** Create a new query beacon state operation */
	public OpQueryBeaconState(BeaconImpl b) {
		super(PriorityLevel.DATA_30_SEC, b);
		beacon = b;
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase phaseTwo() {
		return new QueryStatus();
	}

	/** Phase to query the beacon state */
	protected class QueryStatus extends Phase {

		/** Query the beacon state */
		protected Phase poll(CommMessage mess) throws IOException {
			byte[] b = new byte[1];
			MemoryProperty prop = new MemoryProperty(
				Address.RAMP_METER_DATA, b);
			mess.add(new MemoryProperty(Address.RAMP_METER_DATA,b));
			mess.queryProps();
			beacon.setFlashingNotify(b[Address.OFF_STATUS] !=
				MeterStatus.FLASH);
			return null;
		}
	}
}

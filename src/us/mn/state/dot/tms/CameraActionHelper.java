/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2019  Minnesota Department of Transportation
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
package us.mn.state.dot.tms;

import java.util.Iterator;

/**
 * Helper class for camera actions.
 *
 * @author Douglas Lau
 */
public class CameraActionHelper extends BaseHelper {

	/** Don't allow instances to be created */
	private CameraActionHelper() {
		assert false;
	}

	/** Lookup the camera action with the specified name */
	static public CameraAction lookup(String name) {
		return (CameraAction) namespace.lookupObject(
			CameraAction.SONAR_TYPE, name);
	}

	/** Get a camera action iterator */
	static public Iterator<CameraAction> iterator() {
		return new IteratorWrapper<CameraAction>(namespace.iterator(
			CameraAction.SONAR_TYPE));
	}
}

/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2008  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm.ntcip;

import java.io.IOException;
import us.mn.state.dot.tms.TMSObjectImpl;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSImpl;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SystemAttributeHelper;
import us.mn.state.dot.tms.comm.AddressedMessage;

/**
 * Operation to download default values to a DMS
 *
 * @author Douglas Lau
 */
public class DMSDefaultDownload extends DMSOperation {

	/** Create a new DMS default download object */
	public DMSDefaultDownload(DMSImpl d) {
		super(DOWNLOAD, d);
		controller.setError("OK");
	}

	/** Create the first real phase of the operation */
	protected Phase phaseOne() {
		return new QueryBrightness();
	}

	/** Phase to query static brightness values */
	protected class QueryBrightness extends Phase {

		/** Query static brightness values */
		protected Phase poll(AddressedMessage mess) throws IOException {
			DmsIllumMaxPhotocellLevel level =
				new DmsIllumMaxPhotocellLevel();
			mess.add(level);
			DmsIllumNumBrightLevels levels =
				new DmsIllumNumBrightLevels();
			mess.add(levels);
			mess.getRequest();
			dms.setMaxPhotocellLevel(level.getInteger());
			dms.setBrightnessLevels(levels.getInteger());
			return new BrightnessTable();
		}
	}

	/** Phase to get the brightness table */
	protected class BrightnessTable extends Phase {

		/** Get the brightness table */
		protected Phase poll(AddressedMessage mess) throws IOException {
			int[] table = dms.getBrightnessTable();
			DmsIllumBrightnessValues brightness =
				new DmsIllumBrightnessValues(table);
			mess.add(brightness);
			DmsIllumControl control = new DmsIllumControl(
				DmsIllumControl.PHOTOCELL);
			mess.add(control);
			mess.getRequest();
			dms.setBrightnessTable(brightness.getTable());
			DMS_LOG.log(dms.getId() + ": " + brightness);
			return new CommLoss();
		}
	}

	/** Phase to set the comm loss action */
	protected class CommLoss extends Phase {

		/** Set the comm loss action */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new DmsTimeCommLoss(10));
			mess.add(new DmsCommunicationsLossMessage(
				DmsMessageMemoryType.BLANK, 1, 0));
			mess.add(new DmsEndDurationMessage(
				DmsMessageMemoryType.BLANK, 1, 0));
			mess.setRequest();
			return new PowerLoss();
		}
	}

	/** Phase to set the power loss action */
	protected class PowerLoss extends Phase {

		/** Set the comm loss action */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new DmsShortPowerLossTime(0));
			mess.add(new DmsLongPowerRecoveryMessage(
				DmsMessageMemoryType.BLANK, 1, 0));
//			mess.add(new DmsPowerLossMessage(
//				DmsMessageMemoryType.BLANK, 1, 0));
			mess.setRequest();
			return new PixelService();
		}
	}

	/** Phase to set the pixel service schedule */
	protected class PixelService extends Phase {

		/** Set the pixel service schedule */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new VmsPixelServiceDuration(10));
			mess.add(new VmsPixelServiceFrequency(1440));
			mess.add(new VmsPixelServiceTime(180));
			mess.setRequest();
			return new MessageDefaults();
		}
	}

	/** Phase to set the message defaults */
	protected class MessageDefaults extends Phase {

		/** Set the message defaults */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new DefaultJustificationLine(
				MultiString.JustificationLine.CENTER));
			mess.add(new DefaultJustificationPage(
				DefaultJustificationPage.TOP));
			mess.add(new DefaultPageOnTime(Math.round(10 *
				SystemAttributeHelper.getDmsPageOnSecs())));
			mess.add(new DefaultPageOffTime(Math.round(10 *
				SystemAttributeHelper.getDmsPageOffSecs())));
			mess.setRequest();
			return new LedstarDefaults();
		}
	}

	/** Phase to set Ledstar-specific object defaults */
	protected class LedstarDefaults extends Phase {

		/** Set Ledstar-specific object defaults */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new LedHighTempCutoff(DMS.HIGH_TEMP_CUTOFF));
			mess.add(new LedSignErrorOverride());
			mess.add(new LedBadPixelLimit(DMS.BAD_PIXEL_LIMIT));
			try { mess.setRequest(); }
			catch(SNMP.Message.NoSuchName e) {
				// Must not be a Ledstar sign
				return new SkylineDefaults();
			}
			return null;
		}
	}

	/** Phase to set Skyline-specific object defaults */
	protected class SkylineDefaults extends Phase {

		/** Set Skyline-specific object defaults */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new DmsTempCritical(DMS.HIGH_TEMP_CUTOFF));
			mess.add(new DynBrightDayNight(32));
			mess.add(new DynBrightDayRate(1));
			mess.add(new DynBrightNightRate(15));
			mess.add(new DynBrightMaxNightManLvl(20));
			try { mess.setRequest(); }
			catch(SNMP.Message.NoSuchName e) {
				// Must not be a Skyline sign
				return new AddcoDefaults();
			}
			return null;
		}
	}

	/** Phase to set Addco-specific object defaults */
	protected class AddcoDefaults extends Phase {

		/** Set Addco-specific object defaults */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new DmsHorizontalBorder(50));
			mess.add(new DmsVerticalBorder(69));
			mess.add(new VmsVerticalPitch(69));
			mess.add(new VmsHorizontalPitch(69));
			mess.setRequest("administrator");
			return null;
		}
	}

	/** Cleanup the operation */
	public void cleanup() {
		if(success)
			dms.notifyUpdate();
		else
			controller.setError(null);
		super.cleanup();
	}
}

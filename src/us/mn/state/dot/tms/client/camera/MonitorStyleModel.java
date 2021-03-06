/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2017-2018  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.camera;

import java.util.ArrayList;
import us.mn.state.dot.tms.MonitorStyle;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyDescriptor;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for monitor styles.
 *
 * @author Douglas Lau
 */
public class MonitorStyleModel extends ProxyTableModel<MonitorStyle> {

	/** Create a proxy descriptor */
	static public ProxyDescriptor<MonitorStyle> descriptor(Session s) {
		return new ProxyDescriptor<MonitorStyle>(
			s.getSonarState().getCamCache().getMonitorStyles(),
			false
		);
	}

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<MonitorStyle>> createColumns() {
		ArrayList<ProxyColumn<MonitorStyle>> cols =
			new ArrayList<ProxyColumn<MonitorStyle>>(8);
		cols.add(new ProxyColumn<MonitorStyle>("monitor.style",
			120)
		{
			public Object getValueAt(MonitorStyle et) {
				return et.getName();
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.force.aspect",
			120, Boolean.class)
		{
			public Object getValueAt(MonitorStyle ms) {
				return ms.getForceAspect();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "forceAspect");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				if (value instanceof Boolean)
					ms.setForceAspect((Boolean) value);
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.accent", 120) {
			public Object getValueAt(MonitorStyle ms) {
				return ms.getAccent();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "accent");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				ms.setAccent(value.toString());
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.font.sz", 90,
			Integer.class)
		{
			public Object getValueAt(MonitorStyle ms) {
				return ms.getFontSz();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "fontSz");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				if (value instanceof Integer)
					ms.setFontSz((Integer) value);
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.title.bar",
			120, Boolean.class)
		{
			public Object getValueAt(MonitorStyle ms) {
				return ms.getTitleBar();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "titleBar");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				if (value instanceof Boolean)
					ms.setTitleBar((Boolean) value);
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.auto.expand",
			120, Boolean.class)
		{
			public Object getValueAt(MonitorStyle ms) {
				return ms.getAutoExpand();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "autoExpand");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				if (value instanceof Boolean)
					ms.setAutoExpand((Boolean) value);
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.hgap", 90,
			Integer.class)
		{
			public Object getValueAt(MonitorStyle ms) {
				return ms.getHGap();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "hGap");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				if (value instanceof Integer)
					ms.setHGap((Integer) value);
			}
		});
		cols.add(new ProxyColumn<MonitorStyle>("monitor.vgap", 90,
			Integer.class)
		{
			public Object getValueAt(MonitorStyle ms) {
				return ms.getVGap();
			}
			public boolean isEditable(MonitorStyle ms) {
				return canWrite(ms, "vGap");
			}
			public void setValueAt(MonitorStyle ms, Object value) {
				if (value instanceof Integer)
					ms.setVGap((Integer) value);
			}
		});
		return cols;
	}

	/** Create a new monitor style table model */
	public MonitorStyleModel(Session s) {
		super(s, descriptor(s), 12);
	}
}

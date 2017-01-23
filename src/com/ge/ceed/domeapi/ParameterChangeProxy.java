package com.ge.ceed.domeapi;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import com.ge.ceed.domeapi.web.runModel;

import mit.cadlab.dome3.objectmodel.dataobject.interfaces.DomeFile;
//import mit.cadlab.dome3.api.ParameterValueChangeEvent;
import mit.cadlab.dome3.objectmodel.modelobject.parameter.InterfaceParameterClient;
import mit.cadlab.dome3.objectmodel.util.id.Id;

/**
 * Catches event data during a run.
 * See {@link runModel}
 */
public class ParameterChangeProxy {

	public Object event = null;
	public Object param = null;
	public Id id = null;
	public Object old_val = null;
	public Object new_val = null;
	public Object occur = null;
	
	public ParameterChangeProxy(PropertyChangeEvent evt, int cnt) {
		//ParameterValueChangeEvent pev = (ParameterValueChangeEvent)evt;
		if (evt.getSource() instanceof String) {
			event = "solving thread finished";
			param = cnt;
			id = new Id("end_of_run");
			old_val = evt.getOldValue();
			new_val = evt.getNewValue();
		}
		else { 
			InterfaceParameterClient ipc = (InterfaceParameterClient)evt.getSource();
			param = ipc.getName();
			id = ipc.getId();
			if ("File".equals(ipc.getCurrentType())) {
				Properties props = new Properties();
				try {
					props.load(DomeProxy.class.getClassLoader().getResourceAsStream("config/config.properties"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String rPath = null;
				rPath = props.getProperty("result.url", rPath);
				DomeFile domeFile = (DomeFile) ipc.getDataObjectForType("File");
				String fPath = domeFile.getFilePath();
				new_val = rPath + (new File(fPath)).getName();
				old_val = fPath;
			}
			else {
				old_val = evt.getOldValue();
				new_val = evt.getNewValue();
			}
		}
//		if ("File".equalsIgnoreCase(resultParameter.getDataType())) {
//			DomeFile domeFile = (DomeFile) ipc.getDataObjectForType("File");			
//		}
//		else {
//			old_val = evt.getOldValue();
//			new_val = evt.getNewValue();
//		}
		event = evt.getClass().toString();
		occur = Calendar.getInstance().getTimeInMillis();
	}
}

//ParameterValueChangeEvent[ param = WheelRadius, id = b59f3482-cf07-1004-85fb-21dabaafca77, change = [16.0] -> [16.0]]
//ParameterStatusChangeEvent[ param = Acceleration, id = b59f348b-cf07-1004-85fb-21dabaafca77, change = INCONSISTENT -> WAITING_VALIDATION]

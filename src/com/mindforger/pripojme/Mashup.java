package com.mindforger.pripojme;

import java.util.HashMap;
import java.util.Map;

public class Mashup {
	
	private int sensorCount;
	private Sensor jtSensor, lukySensor;
	private VirtualDataGenerator generator;
	private Map<String,Sensor> sensors;
	
	public Mashup(int sensorCount) {
		this.sensorCount = sensorCount;
		this.sensors = new HashMap<>();
		this.generator = new VirtualDataGenerator();
		
		generateSensors();
	}

	// https://api.pripoj.me/message/get/4786E6ED00350042?limit=1&token=XdFzHfh47YmoydrtQWsZKPadWkp4Hpke
	public boolean isOurSensor(String id) {
		if(jtSensor.devEUI.equals(id) || lukySensor.devEUI.equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	private String extractDevEUI(String url) {
		try {
			int begin=url.indexOf("get/")+4;
			int end=begin+"0004A30B001A82A8".length();
			return url.substring(begin,end);			
		} catch(Exception e) {
			// fallback
			return "0004A30B001A82A8";
		}
	}

	public boolean isVirtualSensor(String id) {
		if(id!=null && sensors.get(id)!=null) {
			return true;
		} else {
			return false;
		}
		
	}
	
	// example:  0004A30B001A82A8
	// template: A16EEEEEEEE.....
	private static final String EUD_PREFIX = "A16EEEEEEEE";
	private void generateSensors() {
		jtSensor = new Sensor("0004A30B001A82A8", "JT:F00");
		sensors.put(jtSensor.devEUI,jtSensor);
		lukySensor = new Sensor("0004A30B001A1CC7", "LUKY:F00");
		sensors.put(lukySensor.devEUI,lukySensor);
		
		for(int i=1; i<=sensorCount-2; i++) {
			String eud=EUD_PREFIX+String.format("%1$05d", i);
			String hexdata="F00";
			sensors.put(eud,new Sensor(eud, hexdata));
		}
	}
	
	private static final String PRJ_LIST_MARKER="records\":[";
	private static final String PROJECT_NAME = "CistyVzduch";
	private static final String PROJECT_DESCRIPTION = "Monitorovani kvality ovzdusi.";
	
	public String getProjectListMashup(String message) {
		String result = message;
		if(message!=null && message.contains(PRJ_LIST_MARKER)) {
			String ourProject=
					"{\"projectId\":\""
					+ PROJECT_NAME
					+ "\",\"description\":\""
					+ PROJECT_DESCRIPTION
					+ "\"},";
			// IMPROVE count
			return message.replace(
					PRJ_LIST_MARKER, 
					PRJ_LIST_MARKER+ourProject);
		}
		return result;
	}
	
	public String getProjectsMashup(String message) {
		// generate our message
		StringBuffer sb=new StringBuffer();
		sb.append("{\"_meta\":{\"status\":\"SUCCESS\",\"count\":");
		sb.append(sensorCount);
		sb.append("},\"records\":[");
		Sensor s;
		for(String k:sensors.keySet()) {
			s=sensors.get(k);
			String entry=
			"{\"devEUI\":\""+
		    s.devEUI+
		    "\",\"projectId\":\""+
		    PROJECT_NAME+
		    "\",\"description\":\"CistyVzduch Cidlo\",\"model\":\"MQ135\",\"vendor\":\"Olimex\"},";
			sb.append(entry);
		}
		sb.setLength(sb.length()-1);
		sb.append("]}");
		return sb.toString();
	}

	// https://api.pripoj.me/message/get/4786E6ED00350042?limit=1&token=XdFzHfh47YmoydrtQWsZKPadWkp4Hpke
	public String getSensorMashup(String url, String message) {
		String eui = extractDevEUI(url);
		if(isOurSensor(eui)) {
			return message;
		} else {
			if(isVirtualSensor(eui)) {
				return
						"{\"_meta\":{\"status\":\"SUCCESS\",\"count\":1},\"records\":[{\"devEUI\":\""
						+ eui
						+ "\",\"fPort\":8,\"fCntUp\":24362,\"aDRbit\":1,\"fCntDn\":1921,\"payloadHex\":\""
						+ generator.getHexPackage()
						+ "\",\"micHex\":\"\",\"lrrRSSI\":-107,\"lrrSNR\":-7.75,\"spFact\":12,\"subBand\":\"G1\",\"channel\":\"LC2\",\"devLrrCnt\":2,\"lrrid\":\"080500A9\",\"lrrLAT\":49.198814,\"lrrLON\":16.579626,\"lrrs\":[{\"Lrrid\":\"080500A9\",\"LrrRSSI\":-107,\"LrrSNR\":-7.75,\"LrrESP\":-115.423981},{\"Lrrid\":\"290000DE\",\"LrrRSSI\":-117,\"LrrSNR\":-12.25,\"LrrESP\":-129.501282}],\"createdAt\":\"2016-11-25T13:00:53+0000\"}]}";				
			} else {
				return message;
			}
		}
	}
}

package com.mindforger.pripojme;

import java.util.Random;

public class VirtualDataGenerator {

	private Random rand;
	
	public VirtualDataGenerator() {
	    rand = new Random();
	}

	public String getHexPackage() {
		byte[] b = new byte[8];

		int pollution=70+randomInteger(1,170);
		b[0]=(byte)(((pollution>>8)&0xFF)); // pollution 70-170: 120+/-50
		b[1]=(byte)(pollution&0xff); // LOW
		int temperature=50+randomInteger(1,50);
		b[2]=(byte)(((temperature>>8)&0xFF)); // temperature 50-100: 75+/-25;
		b[3]=(byte)(temperature&0xff); // LOW
		int humidity=500+randomInteger(1,300);
		b[4]=(byte)(((humidity>>8)&0xFF)); // humidity 500-800: 650+/-150
		b[5]=(byte)(humidity&0xff); // LOW
		b[6]=(byte)((3500>>8)&0xFF); // battery 3500 D
		b[7]=(byte)(3500&0xFF); // LOW AC
		
		return bytesToHex(b);
	}

	public int randomInteger(int min, int max) {
	    // nextInt excludes the top value so we have to add 1 to include the top value
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}	
}

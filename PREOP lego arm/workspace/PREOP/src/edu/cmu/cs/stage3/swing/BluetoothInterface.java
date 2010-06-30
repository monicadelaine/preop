package edu.cmu.cs.stage3.swing;

import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public class BluetoothInterface implements DiscoveryListener{
	Vector macAddresses = new Vector();
	String searchFor = new String();
	String specificAddress = new String();
	boolean deviceSearchDone = false;
	boolean specificSearch = false;
	boolean quitSearch = false;
	public static LocalDevice localDevice;
	
	public void getLocalDevice()throws Exception{
		if(localDevice==null){
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
			authoringTool.printReactingHack().stopReactingToPrint();
			localDevice = LocalDevice.getLocalDevice();
			authoringTool.printReactingHack().startReactingToPrint();
		}
	}
	
    public Vector searchForBamModules()throws Exception{
    	getLocalDevice();
	    DiscoveryAgent agent;
		agent = localDevice.getDiscoveryAgent();
		agent.startInquiry(DiscoveryAgent.GIAC, this);
		while(!deviceSearchDone)
			Thread.sleep(500);
        return macAddresses;
    }
    
    public String findBamModuleEndingWith(String lastFourDigits) throws Exception{
    	getLocalDevice();
    	searchFor = lastFourDigits;
    	specificSearch = true;
	    DiscoveryAgent agent;
		agent = localDevice.getDiscoveryAgent();
		agent.startInquiry(DiscoveryAgent.GIAC, this);

		long startTime = System.currentTimeMillis();
		while(!deviceSearchDone){Thread.sleep(500);
			if(quitSearch || (System.currentTimeMillis()-startTime) > 20*1000)
				agent.cancelInquiry(this);
		}
		quitSearch = false;
		specificSearch = false;
        return specificAddress;
    }
    
    public void deviceDiscovered(RemoteDevice device, DeviceClass devClass){
		try {
			if(device.getFriendlyName(false).equals("Element Serial") ||
			   device.getFriendlyName(false).equals("NXT")){
				if(specificSearch){
					if(device.getBluetoothAddress().endsWith(searchFor)){
						specificAddress = (device.getBluetoothAddress());
						quitSearch = true;
					}
				}
				else{
					macAddresses.add(device.getBluetoothAddress());
				}
			}
		} catch (IOException e) {
			//macAddresses.add("Error: " + e);
		}
	}

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
	} 

	public void serviceSearchCompleted(int transID, int responseCode) {
	}

	public void inquiryCompleted(int discType) {
		deviceSearchDone=true;
	}
}
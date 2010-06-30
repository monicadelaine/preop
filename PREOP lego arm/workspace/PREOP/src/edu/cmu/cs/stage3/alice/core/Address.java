package edu.cmu.cs.stage3.alice.core;

public class Address {
	public static Address NONE = new Address();
	protected String macAddress;
	public Address(String macAddress){
		if(macAddress!=null && macAddress.indexOf("Error")==-1)
			this.macAddress = macAddress;
		else
			this.macAddress = "none";
	}
	public static Address valueOf(String macAddress){
		if(macAddress!=null && macAddress.length()==12)
			return new Address(macAddress);
		else
			return NONE;
	}
	public Address(){
		macAddress = "none";
	}
	public String toString(){
		return macAddress;
	}
	public boolean equals(Object obj){
		if(obj instanceof Address && ((Address)obj).macAddress.equalsIgnoreCase(this.macAddress))
			return true;
		return false;
	}
}

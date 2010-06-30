package edu.cmu.cs.stage3.alice.core.irobot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Address;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.core.property.LEDBooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.LEDColorProperty;
import edu.cmu.cs.stage3.alice.core.property.AddressProperty;
import edu.cmu.cs.stage3.alice.core.Note;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.util.HowMuch;


public class Create extends Model implements Runnable{
	public final LEDColorProperty powerLEDColor = new LEDColorProperty( this, "powerLEDColor", edu.cmu.cs.stage3.alice.scenegraph.Color.RED );
	public final LEDBooleanProperty isAdvanceLEDOn = new LEDBooleanProperty( this, "isAdvanceLEDOn", Boolean.FALSE );
	public final LEDBooleanProperty isPlayLEDOn = new LEDBooleanProperty( this, "isPlayLEDOn", Boolean.FALSE );
	public final AddressProperty macAddress = new AddressProperty( this, "connectedTo", Address.NONE);
	
	private StreamConnection connection;
	private OutputStream outputStream;
	private InputStream inputStream;
	private LinkedList noteQueue= new LinkedList();
	
	private long timeOfLastPlay = 0;
	
	private static final double RADIANS_360_DEGREES = 6.28318531;
	private static final double METERS_TO_AU = 1.3282505611858621010267376837967;

	private double virtualForwardVelocity = 0;
	private double virtualTurnVelocity = 0;
	private double actualForwardVelocity = 0;
	private double actualTurnVelocity = 0;
	
	private double xPos = 0;
	private double yPos = 0;
	private double theta = 0;

	private Color powerLED = Color.RED;

	private boolean isBumpLeftDepressed = false;
	private boolean isBumpRightDepressed = false;
	private boolean isRightWheelDropped = false;
	private boolean isLeftWheelDropped = false;
	private boolean isCasterWheelDropped = false;
	private boolean isWallDetected = false;
	private boolean isPlayPressed = false;
	private boolean isAdvancePressed = false;
	private boolean playLED = false;
	private boolean advanceLED = false;
	private boolean saving = false;
	private boolean running=false;
	private boolean connected=false;
	private boolean aborting = false;
	
	private String currentMacAddress = "none";

	private static int FULL_MODE = 1;
	private static int SAFE_MODE = 2;
	private static int FORWARD = 1;
	private static int TURN = 2;
	private int allowedRefresh = 1;
	private int refreshCount = 0;
	
	private Thread input_t;
	
	public void move(double distance, double duration){
		moveAtSpeed(distance/duration,duration);
	}

	public void moveAtSpeed(double speed, double duration){
		TerminalCommand command = new TerminalCommand(FORWARD,speed,duration);
		Thread t = new Thread(command);
		t.start();
	}

	public void turn(double distance, double duration){
		turnAtSpeed(distance/duration,duration);
	}

	public void turnAtSpeed(double speed, double duration){
		TerminalCommand command = new TerminalCommand(TURN,speed,duration);
		Thread t = new Thread(command);
		t.start();
	}

	public void playNote(Note note){
		noteQueue.add(note);
	}

	public void setAdvanceLED(boolean value){
		advanceLED = value;
		refreshLEDs();
	}

	public void setPlayLED(boolean value){
		playLED = value;
		refreshLEDs();
	}

	public void setPowerLED(Color value){
		powerLED = value;
		refreshLEDs();
	}

	public boolean isBumpLeftDepressed(){
		return isBumpLeftDepressed;
	}

	public boolean isBumpRightDepressed(){
		return isBumpRightDepressed;
	}

	public boolean isRightWheelDropped(){
		return isRightWheelDropped;
	}

	public boolean isLeftWheelDropped(){
		return isLeftWheelDropped;
	}

	public boolean isCasterWheelDropped(){
		return isCasterWheelDropped;
	}

	public boolean isWallDetected(){
		return isWallDetected;
	}

	public boolean isPlayPressed(){
		return isPlayPressed;
	}

	public boolean isAdvancePressed(){
		return isAdvancePressed;
	}
	
	public double getXPos(){
		try{
			Thread.sleep(10);
		}
		catch(InterruptedException e){}
		return xPos;
	}
	
	public double getYPos(){
		try{
			Thread.sleep(10);
		}
		catch(InterruptedException e){}
		return yPos;
	}
	
	public double getTheta(){
		try{
			Thread.sleep(10);
		}
		catch(InterruptedException e){}
		return theta;
	}
	
	private static double Round(double Rval, int Rpl) {
		  double p = (double)Math.pow(10,Rpl);
		  Rval = Rval * p;
		  double tmp = Math.round(Rval);
		  return (double)tmp/p;
	}
	
	public void run() {
		//MARE-HACK #2
		//TODO: Remove this line, it is another hack.
		//TODO: After achieving the correct default color this line should be able to be removed.
		//TODO: Related to MARE-Hack #1
		setPowerLED(Color.RED);
		int badPacketCount = 0;
		while(running){
			byte[] packet = new byte[56];
			int bufferByte = 0;
			int tempDistance = 0;
			int tempTheta = 0;
			while(bufferByte!=-1){
				try{
					bufferByte = inputStream.read();
					if(bufferByte==-1){
						badPacketCount++;
						if(badPacketCount>4000){
							throw new IOException("The connection to " + currentMacAddress + " has time out after 4000 bad reads.");
						}
					}
					else{
						badPacketCount=0;
					}
					if(bufferByte==19){
						packet[0] = 19; //Used as checksum
						for(int i=1; i<56;i++){
							bufferByte = inputStream.read();
							packet[0] += packet[i] = (byte)(bufferByte & 0x00FF);
						}
						if(packet[0]==0){ //If our data packet is good
							isBumpRightDepressed = (packet[3] & 0x01) == 0x01 ? true : false;
							isBumpLeftDepressed = (packet[3] & 0x02) == 0x02 ? true : false;
							isRightWheelDropped = (packet[3] & 0x04) == 0x04 ? true : false;
							isLeftWheelDropped = (packet[3] & 0x08) == 0x08 ? true : false;
							isCasterWheelDropped = (packet[3] & 0x10) == 0x10 ? true : false;
							isWallDetected = packet[4] > 0 ? true : false;
							isPlayPressed = (packet[14] & 0x01) == 0x01 ? true : false;
							isAdvancePressed = (packet[14] & 0x04) == 0x04 ? true : false;
							tempDistance = packet[15];
							tempDistance = tempDistance<<8 | packet[16];
							tempTheta = packet[17];
							tempTheta = tempTheta<<8 | packet[18];
							theta = theta + ((tempTheta)*(Math.PI/180));
							xPos = xPos + ((Math.cos(theta)*tempDistance)/1000);
							xPos = Round(xPos,2);
							yPos = yPos + ((Math.sin(theta)*tempDistance)/1000);
							yPos = Round(yPos,2);
							if(packet[45]==0) //If song is not playing
								playNoteFromQueue();
						}
						bufferByte=-1;
					}
				}catch(IOException e){
					if(!aborting){
						disconnect();
						//System.out.println("IOException: Refresh #" + Integer.toString(refreshCount+1) + " of " + Integer.toString(allowedRefresh));
						//if(allowedRefresh==refreshCount){
						macAddress.set(Address.valueOf("none"));
						AuthoringTool.showErrorDialog("The connection to " + currentMacAddress + " has been dropped or timed out.", e);
						//}else{
						//	System.out.println("Refreshing");
						//	refreshCount++;
						//	macAddress.set(Address.valueOf(currentMacAddress));
						//}
						return;
					}
				}
			}
		}
	}
	public void zeroXYTheta(){
		xPos=yPos=theta=0;
	}
	public boolean isConnected(){
		return connected;
	}

	private void connect(final String macAddress, final int mode){
	//		Thread t = new Thread(new Runnable(){
	//			public void run(){
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
					authoringTool.printReactingHack().stopReactingToPrint();
					try{
						connection = (StreamConnection) Connector.open("btspp://" + macAddress + ":1;authenticate=false;encrypt=false;master=true");
						outputStream = connection.openOutputStream();
						inputStream = connection.openInputStream();
						if(mode==FULL_MODE)
							sendByteCommands((byte)128,(byte)132);
						if(mode==SAFE_MODE)
							sendByteCommands((byte)128,(byte)131);
						sendByteCommands((byte)148,(byte)1,(byte)6);
						
						
						//Set LEDs to their initial values
						//MARE-Hack #1
						//TODO: Fix this next line, it is a hack. The light should default to RED.
						//TODO: Because that is the way it's constructor is called above. Related to MARE-Hack #2
						powerLEDColor.set(edu.cmu.cs.stage3.alice.scenegraph.Color.RED);
						isPlayLEDOn.set(isPlayLEDOn.getValue());
						isAdvanceLEDOn.set(isAdvanceLEDOn.getValue());
						
						running = true;
						input_t = new Thread(this);
						input_t.start();
						
						if(!edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().connectedDevices.containsKey(macAddress)){
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().connectedDevices.put(macAddress, this);
						}
						connected = true;
						currentMacAddress = macAddress;
					}catch(IOException e){
						//connectionProgressPane.setVisible(false);
						this.macAddress.set(Address.NONE);
						//AuthoringTool.showErrorDialog( "Unable to connect.", e );
					}
					authoringTool.printReactingHack().startReactingToPrint();
	//			}
	//		});
	//		t.start();
		}

	private void disconnect(){
		if(connected){
			//This color change is a courtesy to the user.
			//It is simply to reset the robot to it's default ON state
			//This makes it easier for the user to identify when it connects to a new Robot.
			setPowerLED(Color.GREEN);
			connected=false;
			running=false;
			try{
				inputStream.close();
				outputStream.close();
				connection.close();
			}catch(IOException e){
				//System.out.print("Error closing connection. " + e.toString());
				//Do nothing
			}
		}
	}

	//1.3 does not support "byte...byteArray" syntax so sendByteCommmands is overloaded several times
	private boolean sendByteCommands(byte b1, byte b2){
		byte[] bArray =  {b1 ,b2};
		try{ outputStream.write(bArray);
		} catch (IOException e){ return false;}
		return true;
	}

	private boolean sendByteCommands(byte b1, byte b2, byte b3){
		byte[] bArray =  {b1 ,b2, b3};
		try{ outputStream.write(bArray);
		} catch (IOException e){ return false;}
		return true;
	}

	private boolean sendByteCommands(byte b1, byte b2, byte b3, byte b4){
		byte[] bArray =  {b1 ,b2, b3 , b4};
		try{ outputStream.write(bArray);
		} catch (IOException e){ return false;}
		return true;
	}

	private boolean sendByteCommands(byte b1, byte b2, byte b3, byte b4, byte b5){
		byte[] bArray =  {b1 ,b2, b3, b4, b5};
		try{ outputStream.write(bArray);
		} catch (IOException e){ return false;}
		return true;
	}

	private void playNoteCommand(int note, double durationInSeconds){
		int duration = (int)(durationInSeconds*64);
		if(duration<0)
			duration=0;
		if(duration>255)
			duration=255;
		if(note<31)
			note=31;
		if(note>127)
			note=127;
		sendByteCommands((byte)140,(byte)0,(byte)1,(byte)note,(byte)duration);
		sendByteCommands((byte)141,(byte)0);
	}
	
	private void playNoteFromQueue(){
		if(noteQueue.size()>0 && System.currentTimeMillis()>timeOfLastPlay){
			Note note = (Note) noteQueue.removeFirst();
			timeOfLastPlay = (long)(System.currentTimeMillis() + 750 * note.getDuration());
			playNoteCommand(note.getMidi(),note.getDuration());
		}
	}

	private void refreshLEDs(){
		setLEDs(playLED, advanceLED, powerLED.red/(powerLED.green+powerLED.red), Math.sqrt(Math.pow(powerLED.red, 2)+Math.pow(powerLED.green, 2)+Math.pow(powerLED.blue, 2)));
	}

	private void setLEDs(boolean setPlayLit, boolean setAdvanceLit, double powerColor, double powerIntensity){
		int color = (int)(powerColor*255);
		int intensity = (int)(powerIntensity*255);
		if(color<0)
			color=0;
		if(color>255)
			color=255;
		if(intensity<0)
			intensity=0;
		if(intensity>255)
			intensity=255;
		int otherLEDs = 0xF0;
		if(setPlayLit)
			otherLEDs = (otherLEDs | 0xF2);
		if(setAdvanceLit)
			otherLEDs = (otherLEDs | 0xF8);
		sendByteCommands((byte)139,(byte)otherLEDs,(byte)color,(byte)intensity);
	}

	private class TerminalCommand implements Runnable{
		private int type;
		private double speed, duration;

		public TerminalCommand(int type, double speed, double duration){
			this.type = type;
			this.speed = speed;
			this.duration = duration;
		}

		public void run(){
			if(type==FORWARD)
				virtualForwardVelocity += speed;
			else if(type==TURN)
				virtualTurnVelocity += speed;
			refreshMovement(5);
			try{Thread.sleep((long)(duration*1000));}catch(Exception e){}
			if(type==FORWARD)
				virtualForwardVelocity -= speed;
			else if(type==TURN)
				virtualTurnVelocity -= speed;
			refreshMovement(30);
		}

		public void refreshMovement(int buffer){
			//System.out.println("Refreshing Movement");
			double changeTestForwardVelocity = virtualForwardVelocity;
			double changeTestTurnVelocity = virtualTurnVelocity;
			try{Thread.sleep(buffer);}catch(Exception e){}
			if(changeTestForwardVelocity != virtualForwardVelocity || changeTestTurnVelocity != virtualTurnVelocity)
				return;
			if(virtualForwardVelocity != actualForwardVelocity || virtualTurnVelocity != actualTurnVelocity)
				translateVelocity(virtualForwardVelocity,virtualTurnVelocity);
		}
	}

	private void translateVelocity(double forwardVelocity, double turnVelocity){
		actualForwardVelocity = forwardVelocity;
		actualTurnVelocity = turnVelocity;
		double precalc1 = .258 * turnVelocity *2*Math.PI;
		double precalc2 =  2 * forwardVelocity;
		directDrive((int)(((precalc1+precalc2)/2)*1000),(int)(((precalc2-precalc1)/2)*1000));
	}

	private void directDrive(int right, int left){
		if(right>500)
			right=500;
		if(right<-500)
			right=-500;
		if(left>500)
			left=500;
		if(left<-500)
			left=-500;
		short rightTwoByte = (short) right;
		short leftTwoByte = (short) left;
		byte upperByteRight = (byte) (rightTwoByte >> 8);
		byte lowerByteRight = (byte) (rightTwoByte & 0x00FF);
		byte upperByteLeft = (byte) (leftTwoByte >> 8);
		byte lowerByteLeft = (byte) (leftTwoByte & 0x00FF);
		sendByteCommands((byte)145,(byte)upperByteRight,(byte)lowerByteRight,(byte)upperByteLeft,(byte)lowerByteLeft);
	}

	protected void internalRelease( int pass ) {
	    switch( pass ) {
	    case 1:
	    	disconnect();
	        break;
	    case 2:
	        break;
	    }
	    super.internalRelease(pass);
	}

	protected int internalStore( javax.xml.parsers.DocumentBuilder builder, DirectoryTreeStorer storer, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver, HowMuch howMuch, ReferenceGenerator referenceGenerator, int count ) throws java.io.IOException, edu.cmu.cs.stage3.progress.ProgressCancelException {
		Object tempAddress =  macAddress.getValue();
		saving = true;
		macAddress.set(Address.NONE);
		int returnValue =  super.internalStore(builder, storer, progressObserver, howMuch,referenceGenerator, count);
		macAddress.set(tempAddress);
		saving = false;
		return returnValue;
	}

	protected void propertyChanged( edu.cmu.cs.stage3.alice.core.Property property, Object value ) {
		if(property==macAddress && !saving){
			if(property.getValue().toString().equals("none")){
				aborting = true;
				disconnect();
				aborting = false;
			}
			else{
				aborting = true;
				if(!property.getValue().toString().equals(currentMacAddress)){
					refreshCount = 0;
				}
				disconnect();
				connect(property.getValue().toString(), Create.FULL_MODE);
				aborting = false;
			}
		}
		super.propertyChanged(property, value);
	}
}

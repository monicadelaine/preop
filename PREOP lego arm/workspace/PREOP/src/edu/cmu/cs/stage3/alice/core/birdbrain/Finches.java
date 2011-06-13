package edu.cmu.cs.stage3.alice.core.birdbrain;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Note;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.core.Robot;
import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.LEDColorProperty;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.util.HowMuch;
import edu.cmu.cs.stage3.alice.scenegraph.Color;
import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.util.concurrent.locks.ReentrantLock;


public class Finches extends Robot implements Runnable {

	public final LEDColorProperty beakLEDColor = new LEDColorProperty( this, "beakLEDColor", edu.cmu.cs.stage3.alice.scenegraph.Color.RED);
	public final BooleanProperty connected = new BooleanProperty( this, "connected", Boolean.FALSE );	
	
	private LinkedList noteQueue= new LinkedList();
	
	private long timeOfLastPlay = 0;
	
	private static final double RADIANS_360_DEGREES = 6.28318531;
	private static final double METERS_TO_AU = 1.3282505611858621010267376837967;

	private int virtualLeftVelocity = 0;
	private int virtualRightVelocity = 0;
	private int actualLeftVelocity = 0;
	private int actualRightVelocity = 0;
	private long currentMovementPID = 0;
	
	edu.cmu.ri.createlab.terk.robot.finch.Finch robot=null;
	
	private final ReentrantLock finchAccess = new ReentrantLock();

	
	private java.awt.Color beakColor = java.awt.Color.RED;

	private boolean isLeftObstacle = false;
	private boolean isRightObstacle = false;
	private boolean hasBeenShaken = false;
	private boolean hasBeenTapped = false;
	private double temperature = 0.0;
	private double xAcceleration = 0.0;
	private double yAcceleration = 0.0;
	private double zAcceleration = 0.0;
	private int leftLightSensor = 0;
	private int rightLightSensor = 0;
	private boolean saving = false;
	private boolean running=false;
	private boolean isConnected=false;
	private boolean aborting = false;
	
	private static int FORWARD = 1;
	private static int TURN_LEFT = 2;
	private static int INDIVIDUAL=3;
	private static int TURN_RIGHT=4;
	private static int BACKWARD=5;
	private final static int MAX_SPEED=255;
	private final static int MIN_SPEED=-255;
	private final static int SPEED=200;
	
	
	
	
	private Thread input_t;
	
	public void move(int left, int right, double duration){
		TerminalCommand command = new TerminalCommand(this.INDIVIDUAL, left,right,duration);
		Thread t = new Thread(command);
		t.start();	
	}

	public void moveForward(double duration){
		TerminalCommand command = new TerminalCommand(this.FORWARD,0,0,duration);
		Thread t = new Thread(command);
		t.start();	
	}
	public void moveBackward(double duration){
		TerminalCommand command = new TerminalCommand(this.BACKWARD,0,0,duration);
		Thread t = new Thread(command);
		t.start();	
	}
	public void turnLeft(double duration){
		TerminalCommand command = new TerminalCommand(this.TURN_LEFT,0,0,duration);
		Thread t = new Thread(command);
		t.start();	
	}
	public void turnRight(double duration){
		TerminalCommand command = new TerminalCommand(this.TURN_RIGHT,0,0,duration);
		Thread t = new Thread(command);
		t.start();	
	}


	public boolean isLeftObstacle() {
		return isLeftObstacle;
	}


	public boolean isRightObstacle() {
		return isRightObstacle;
	}

	public boolean hasBeenShaken() {
		return hasBeenShaken;
	}


	public boolean hasBeenTapped() {
		return hasBeenTapped;
	}

	public double getTemperature() {
		return temperature;
	}

	public double getXAcceleration() {
		return xAcceleration;
	}

	public double getYAcceleration() {
		return yAcceleration;
	}

	public double getZAcceleration() {
		return zAcceleration;
	}

	public int getLeftLightSensor() {
		return leftLightSensor;
	}

	public int getRightLightSensor() {
		return rightLightSensor;
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
		setBeakLED(Color.RED);
		while(running){
			//thread to run once robot is connected to get data, does not mean the animation is currently running
				try{
//					System.out.println("running");
					if (! this.isConnected) throw new IOException("not connected for some reason");
					try {
						this.rightLightSensor= robot.getRightLightSensor();
						this.leftLightSensor= robot.getLeftLightSensor();
						this.isLeftObstacle=robot.isObstacleLeftSide();
						this.isRightObstacle=robot.isObstacleRightSide();
						//System.out.println("\n"+this.isRightObstacle);
						this.xAcceleration=robot.getXAcceleration();
						this.yAcceleration=robot.getYAcceleration();
						this.zAcceleration=robot.getZAcceleration();
						this.temperature=robot.getTemperature();
						this.hasBeenShaken=robot.isShaken();
						this.hasBeenTapped=robot.isTapped();
						finchAccess.lock();
						robot.setWheelVelocities(virtualLeftVelocity,virtualRightVelocity);
						actualLeftVelocity=virtualLeftVelocity;
						actualRightVelocity=virtualRightVelocity;
					} finally {
						finchAccess.unlock();
					}
//					System.out.println("\n"+this.rightLightSensor);
//					System.out.println("\n"+this.leftLightSensor);
				}catch(IOException e){
					if(!aborting){
						disconnect();
						AuthoringTool.showErrorDialog("The connection to finch has been dropped or timed out.", e);
						return;
					}
				}		
		}
	}
	public boolean isConnected(){
		return isConnected;
	}

	private void connect(){
	//		Thread t = new Thread(new Runnable(){
	//			public void run(){
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
					authoringTool.printReactingHack().stopReactingToPrint();
					try{
						robot = new Finch();

						running = true;
						input_t = new Thread(this);
						input_t.start();
						isConnected = true;
					}catch(Exception e){
						System.out.println("error"+e.getMessage()+"\n");
						e.printStackTrace();
					}
					authoringTool.printReactingHack().startReactingToPrint();
		}

	private void disconnect(){
		if(isConnected){
			//This color change is a courtesy to the user.
			//It is simply to reset the robot to it's default ON state
			//This makes it easier for the user to identify when it connects to a new Robot.
			setBeakLED(Color.GREEN);
			isConnected=false;
			running=false;
			try{
				robot.quit();
			}catch(Exception e){
				System.out.print("Error closing connection. " + e.toString());
				//Do nothing
			}
		}
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
//		sendByteCommands((byte)140,(byte)0,(byte)1,(byte)note,(byte)duration);
//		sendByteCommands((byte)141,(byte)0);
	}
	
	private void playNoteFromQueue(){
		if(noteQueue.size()>0 && System.currentTimeMillis()>timeOfLastPlay){
			Note note = (Note) noteQueue.removeFirst();
			timeOfLastPlay = (long)(System.currentTimeMillis() + 750 * note.getDuration());
			playNoteCommand(note.getMidi(),note.getDuration());
		}
	}

	private void refreshLEDs(){
		robot.setLED(beakColor);
	}

	public void setBeakLED(Color value){
		beakColor = value.createAWTColor();
//		super.propertyChanged(beakLEDColor, value);
		refreshLEDs();
	}

	private class TerminalCommand implements Runnable{
		private int left, right, type;
		private double duration;
		private long myID;

		public TerminalCommand(int type, int left, int right, double duration){
			if(right>MAX_SPEED)
				right=MAX_SPEED;
			if(right<-MIN_SPEED)
				right=-MIN_SPEED;
			if(left>MAX_SPEED)
				left=MAX_SPEED;
			if(left<-MIN_SPEED)
				left=-MIN_SPEED;
			this.type=type;
			this.left = left;
			this.right = right;
			this.duration = duration;
			myID=Thread.currentThread().getId();
		}

		public void run(){
			if(type==FORWARD) {
				left = SPEED;
				right= SPEED;
			} else if(type==TURN_LEFT) {
				left = -SPEED;
				right= SPEED;
			} else if(type==TURN_RIGHT) {
				left = SPEED;
				right= -SPEED;
			} else if(type==BACKWARD) {
				left = -SPEED;
				right= -SPEED;
			}
			finchAccess.lock();
			virtualLeftVelocity=left;
			virtualRightVelocity=right;
			currentMovementPID=myID;
			finchAccess.unlock();
			try{Thread.sleep((long)(duration*1000));}catch(Exception e){}
			finchAccess.lock();
			if (currentMovementPID==myID) {
				virtualLeftVelocity=0;
				virtualRightVelocity=0;
			}
			finchAccess.unlock();
		}

	}
	private void directDrive(int right, int left){
		directDrive(right, left, 0);
	
	}

	private void directDrive(int right, int left, int duration){
//		finchAccess.lock();
		robot.setWheelVelocities(left, right,duration);
//		finchAccess.unlock();
	
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
//		Object tempAddress =  portAddress.getValue();
		saving = true;
//		portAddress.set(Port.NONE);
		int returnValue =  super.internalStore(builder, storer, progressObserver, howMuch,referenceGenerator, count);
//		portAddress.set(tempAddress);
		saving = false;
		return returnValue;
	}

	protected void propertyChanged( edu.cmu.cs.stage3.alice.core.Property property, Object value ) {
		if(property==this.connected && !saving){
			if(property.getValue().toString().equals("false")){
				aborting = true;
				disconnect();
				aborting = false;
			}
			else{
				aborting = true;
				if(!property.getValue().toString().equals("true")){
				}
				disconnect();
				connect();
				aborting = false;
			}
		}
		super.propertyChanged(property, value);
	}
}

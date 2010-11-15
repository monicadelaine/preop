/**
 * The class for the NXTArm, which will eventually be any kind of robot.
 * A robot consists of up to 3 motors and up to 4 sensors. This class is used 
 * to interact with these motors and sensors. 
 * 
 * @author Trey Davis, Jeff Byrd
 */
package edu.cmu.cs.stage3.alice.core.lego;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.cmu.cs.stage3.alice.core.Address;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.ReferenceGenerator;
import edu.cmu.cs.stage3.alice.core.property.AddressProperty;
import edu.cmu.cs.stage3.io.DirectoryTreeStorer;
import edu.cmu.cs.stage3.util.HowMuch;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class NXTRobot extends Model {
	private static NXTCommand nxtCommand = new NXTCommand();
	private NXTComm nxtComm;
	private RemoteMotor[] connectedMotors = new RemoteMotor[3];
	private int[] gearFactors = new int[3];
	private int position[] = new int[connectedMotors.length];
	private final int MAX_MOTOR_SPEED = 99999;
	
	//public  static SensorPort port;
	private SensorPort[] connectedSensors = new SensorPort[5];
	public TouchSensor touch = new TouchSensor(SensorPort.S1);
	public SoundSensor sound = new SoundSensor(SensorPort.S2);
	public LightSensor light = new LightSensor(SensorPort.S3);
	public UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);

	public NXTRobot() {
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException e) {
		}
	}

	private boolean connected = false;
	public void connect(String macAddressPostfix) {
		try {
			NXTInfo nxtInfo = findNXTArmWithPostfix(macAddressPostfix);
			connectToNXTArm(nxtInfo);
		} catch (Exception e) {
			// this.macAddress.set(Address.NONE);
			e.printStackTrace();
		}
		initializeMotors();
		initializeGearFactors();
		initializePose();
		initializeSensors();
		connected = true;
	}

	private final int MACADDRESS_POSTFIX_START_INDEX = 8;
	private NXTInfo findNXTArmWithPostfix(String macAddressPostfix)
			throws NXTCommException, Exception {
		NXTInfo[] nxtInfo = nxtComm.search("NXT", NXTCommFactory.BLUETOOTH);
		if (nxtInfo == null)
			throw new Exception("Couldn't find an NXT.");

		for (int i = 0; i < nxtInfo.length; i++)
			if (nxtInfo[i].deviceAddress.substring(
					MACADDRESS_POSTFIX_START_INDEX).equals(macAddressPostfix))
				return nxtInfo[i];

		throw new Exception("NXT of particular address not found");
	}

	private void connectToNXTArm(NXTInfo nxtInfo) throws NXTCommException,
			Exception {
		if (!nxtComm.open(nxtInfo))
			throw new Exception("Couldn't establish connection to NXT.");
		nxtCommand.setNXTComm(nxtComm);
	}


	
	/**
	 * Set up the motors and set their speeds to the max speed. 
	 */
	private void initializeMotors() {
		for(int i = 0; i<connectedMotors.length;++i)
		{
			connectedMotors[i] = new RemoteMotor(nxtCommand, i);
			connectedMotors[i].setSpeed(MAX_MOTOR_SPEED);
		}
	}
	
	/**
	 * Set up the sensors to read from the correct port. Assumes a touch sensor
	 * in port 1, a sound sensor in 2, a light sensor in 3, and an ultrasonic 
	 * sensor in 4. This cannot be changed except by modifying member variables above. 
	 */
	private void initializeSensors()
	{
	
			connectedSensors[1] = SensorPort.S1;
			connectedSensors[2] = SensorPort.S2;
			connectedSensors[3] = SensorPort.S3;
			connectedSensors[4] = SensorPort.S4;
	}
	
	/**
	 * Hard coded for now. Motor A (0) gets 165, B gets 71, and C gets 1.
	 * To derive this number, simply multiply all gear ratios of attached gears. 
	 * This must be changed for a different type of robot.
	 */
	private void initializeGearFactors()
	{
		gearFactors[0]=165;
		gearFactors[1]=72;
		gearFactors[2]=1;
	}
	
	/**
	 * Set the gear factor associated with the gears attached to a specified 
	 * motor.
	 * @param motorNum: Changes the gear factor of gears associated with this motor 
	 * @param factor: The new gear factor
	 */
	public void setGearFactor(int motorNum, int factor)
	{
		gearFactors[motorNum] = factor;
	}
	
	/**
	 * Set positions to 0.
	 */
	private void initializePose()
	{
		for(int i = 0; i < position.length; ++i)
		{
			position[i] = 0;
		}
	}

	
	/**
	 * Disconnect from the robot
	 */
	protected void disconnect() {
		if(connected) {
			try {
				nxtComm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Move a motor a specified amount.
	 * This move can be interrupted by another movement command.
	 * @param motorNum: specify which motor you want to rotate by number.
	 * @param angle: give the angle you want to move the motor. 
	 * 	Positive angles move the motor clockwise and negative angles move 
	 * counterclockwise
	 */
	public void move(int motorNum, double angle)
	{
			RemoteMotor motor = connectedMotors[motorNum];
			motor.rotate((int) angle * gearFactors[motorNum], true); 
		
	}
	
	/**
	 * Move a motor to a specified angle. This moveTo can be interrupted by 
	 * another command.
	 * @param motorNum: specifies which motor to move
	 * @param angle: Specifies the number of degrees to move the system associated 
	 * with the motor
	 */
	public void moveTo(int motorNum, double angle)
	{
		RemoteMotor motor = connectedMotors[motorNum];
		motor.rotateTo((int)angle*gearFactors[motorNum], true);
	}
	
	/**
	 * Move a motor by a specified amount. 
	 * This move cannot be interrupted by another movement command. Movement will 
	 * finish before the next command is executed.
	 * @param motorNum: specifies which motor to move
	 * @param angle: Specifies the number of degrees to move the system
	 * associated with the motor
	 */
	public void absoluteMove(int motorNum, double angle)
	{
		RemoteMotor motor = connectedMotors[motorNum];
		motor.rotate((int) angle * gearFactors[motorNum], false); 
	}
	
	/**
	 * Move a motor to a specified angle. This moveTo cannot be interrupted by 
	 * another command. 
	 * @param motorNum: specifies which motor to move
	 * @param angle: number of degrees to move the system associated with the motor
	 */
	public void absoluteMoveTo(int motorNum, double angle)
	{
		RemoteMotor motor = connectedMotors[motorNum];
		motor.rotateTo((int)angle*gearFactors[motorNum], false);
	}
	
		
	/**
	 * Gets the raw value of a specified sensor. 
	 * @param sensorNum: the number of the sensor to read, between 1 and 4.
	 * @return the raw value from the sensor
	 */
	public int getRawSensorValue(int sensorNum)
	{
		return connectedSensors[sensorNum].readRawValue();
	}
	
	/**
	 * Gets the value of the specified sensor. 
	 * @param sensorNum: the number of the sensor being read, according to the 
	 * label of the port
	 * @return the value of the specified sensor
	 */
	public int getSensorValue(int sensorNum)
	{
		return connectedSensors[sensorNum].readValue();
	}
	
	/**
	 * Gets the normalized value of the sensor [0-1023]. 
	 * @param sensorNum: the number of the sensor being read according to the
	 * label on the port.
	 * @return The normalized value of the specified sensor; [0 - 1023]
	 */
	public int getNormalizedValue(int sensorNum)
	{
		return connectedSensors[sensorNum].readNormalizedValue();
	}
	
	/**
	 * Returns the Boolean value from a sensor. For touch sensor, returns true
	 * if pressed or false if not pressed.
	 * @param sensorNum: number of the sensor to read
	 * @return: boolean from the sensor
	 */
	public boolean getBooleanValue(int sensorNum)
	{
		return connectedSensors[sensorNum].readBooleanValue();
	}
	/**
	 * Remember the positions of the motors. 
	 * The revertToRecordedPose() function returns to this pose.
	 */
	public void recordPose() {
		for(int i = 0; i<position.length; ++i)
		{
			position[i] = connectedMotors[i].getTachoCount();
			System.out.println(position[i]);
		}
	}


	/**
	 * Return the robot to the pose that was recorded by recordPose()
	 * If recordPose() was never used, then it will return to 0 positions.
	 */
	public void revertToRecordedPose() {
		// The following 'if' statements are a hack. The rotateTo method
		// is defective and cannot take a value that is exactly its current
		// position

		for(int i = 0; i<position.length; ++i)
		{
			if(connectedMotors[i].getTachoCount() != position[i])
			{
				connectedMotors[i].rotateTo(position[i], false);
			}
		}
	}
	/**
	 * Return the robot to the position it was in when it was powered on.
	 */
	public void revertToOriginalPose()
	{
		for(int i = 0; i<position.length; ++i)
		{
			if(connectedMotors[i].getTachoCount() != position[i])
			{
				connectedMotors[i].rotateTo(0, false);
			}
		}
	}

	protected void internalRelease(int pass) {
		switch (pass) {
		case 1:
			disconnect();
			break;
		case 2:
			break;
		}
		super.internalRelease(pass);
	}

	private boolean saving = false;
	public final AddressProperty macAddress = new AddressProperty( this, "connectedTo", Address.NONE);
	protected int internalStore(javax.xml.parsers.DocumentBuilder builder,
			DirectoryTreeStorer storer,
			edu.cmu.cs.stage3.progress.ProgressObserver progressObserver,
			HowMuch howMuch, ReferenceGenerator referenceGenerator, int count)
			throws java.io.IOException,
			edu.cmu.cs.stage3.progress.ProgressCancelException {
		Object tempAddress = macAddress.getValue();
		saving = true;
		macAddress.set(Address.NONE);
		int returnValue = super.internalStore(builder, storer,
				progressObserver, howMuch, referenceGenerator, count);
		macAddress.set(tempAddress);
		saving = false;
		return returnValue;
	}

	protected void propertyChanged(
			edu.cmu.cs.stage3.alice.core.Property property, Object value) {
		if (property == macAddress && !saving) {
			if (property.getValue().toString().equals("none")) {
				disconnect();
			} else {
				disconnect();
				connect(property.getValue().toString());
			}
		}
		super.propertyChanged(property, value);
	}

	public static void main(String[] args) {
		try {
			Class.forName("edu.cmu.cs.stage3.alice.core.lego.NXTRobot");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			NXTRobot arm = new NXTRobot();
			arm.connect("6831");
			/*arm.recordPose();
			System.out.println("Press Enter for Motor 1:");
			br.readLine();
			arm.move(0, -9.0);
			System.out.println("Press Enter for Motor 1 moveTo:");
			br.readLine();
			arm.moveTo(0, 0.0);
			
			System.out.println("Press Enter to open claw");
			br.readLine();
			arm.move(2, -55);
			
			System.out.println("Press Enter for Motor 2:");
			br.readLine();
			arm.move(1, -75.0);
			System.out.println("Press Enter for Motor 3:");
			br.readLine();
			arm.move(2, 55.0);
			System.out.println("Press Enter to revert to original position:");
			br.readLine();
			arm.move(0,55);
			br.readLine();
			arm.move(2, -55);
			arm.revertToRecordedPose();
			*/
			System.out.println("Press Enter Sensor 1:");
			//arm.move(0, -9.0); //to prove there is a connection
			br.readLine();
			System.out.println("Raw 1: "+arm.getRawSensorValue(1));
			System.out.println("Scaled 1:"+arm.getNormalizedValue(1));
			System.out.println("Scaled 1:"+arm.getSensorValue(1));
			System.out.println("Press the touch sensor to break the loop!");
			while(true)
			{
				if(arm.getSensorValue(1) == 1)
					break;
			}
			System.out.println("Press Enter Sensor 2:");
			br.readLine();
			System.out.println("Raw 2: "+arm.getRawSensorValue(2));
			System.out.println("Scaled 2:"+arm.getNormalizedValue(2));
			System.out.println("Scaled 2:"+arm.getSensorValue(2));
			
			System.out.println("Press Enter Sensor 3:");
			br.readLine();
			System.out.println("Raw 3: "+arm.getRawSensorValue(3));
			System.out.println("Scaled 3:"+arm.getNormalizedValue(3));
			System.out.println("Scaled 3:"+arm.getSensorValue(3));
			
			System.out.println("Press Enter Sensor 4:");
			br.readLine();
			System.out.println("Raw 4: "+arm.getRawSensorValue(4));
			System.out.println("Scaled 4:"+arm.getNormalizedValue(4));
			System.out.println("Scaled 4:"+arm.getSensorValue(4));
			
			arm.disconnect();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
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

import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.InputValues;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class NXTArm extends Model {
	private static NXTCommand nxtCommand = new NXTCommand();
	private NXTComm nxtComm;
	private RemoteMotor[] connectedMotors = new RemoteMotor[3];
	private int[] gearFactors = new int[3];
	private int position[] = new int[connectedMotors.length];
	private final int MAX_MOTOR_SPEED = 99999;
	
	public InputValues[] connectedSensors = new InputValues[5];

	public NXTArm() {
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
	 * Hard coded for now. Motor A (0) gets 165, B gets 27, and C gets 37.
	 * What is a good way of input?
	 */
	private void initializeGearFactors()
	{
		//TODO: un-hard-code these
		gearFactors[0]=165;
		gearFactors[1]=72;
		gearFactors[2]=1;
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
	 * Sets up the sensors 1 through 4. It also asks for each sensor's scaledValue
	 * because, according to the documentation, getting the scaled value "starts working
	 * after the first call to sensor."
	 */
	private void initializeSensors()
	{
		for(int i = 1; i< connectedSensors.length;++i)
		{
			connectedSensors[i] = new InputValues();
			connectedSensors[i].inputPort = i; //i-1?
			int j =connectedSensors[i].scaledValue;
			
		}
	}
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
	 * 
	 * @param motorNum: specify which motor you want to rotate by number.
	 * @param angle: give the angle you want to move the motor. 
	 * 	Positive angles move the motor clockwise and negative angles move 
	 * counterclockwise
	 */
	public void move(int motorNum, double angle)
	{
			RemoteMotor motor = connectedMotors[motorNum];
			motor.rotate((int) angle * gearFactors[motorNum], true); 
			//TODO: why * 360?
	}
	
	public void moveTo(int motorNum, double angle)
	{
		RemoteMotor motor = connectedMotors[motorNum];
		motor.rotateTo((int)angle*gearFactors[motorNum], true);
	}
	/**
	 * Gets the scaled value of a specified sensor. For touch sensor, 0 is off and 
	 * 1 is on. 
	 * @param sensorNum: the number of the sensor being read. Ports are labeled 1 through 4,
	 * so this number must be between 1 and 4.
	 * @return the scaled value of the specified sensor
	 */
	public short getScaledSensorValue(int sensorNum)
	{
		return connectedSensors[sensorNum].scaledValue;
	}
	
	/**
	 * Gets the raw value of a specified sensor. 
	 * @param sensorNum: the number of the sensor to read, between 1 and 4.
	 * @return the raw value from the sensor
	 */
	public int getRawSensorValue(int sensorNum)
	{
		return connectedSensors[sensorNum].rawADValue;
	}
	
	/**
	 * Gets the normalized value of the specified sensor. The documentation says 
	 * this number will be between 0 and 1023, but it is unclear. 
	 * @param sensorNum: the number of the sensor being read, according to the 
	 * label of the port
	 * @return the normalized value of the specified sensor
	 */
	public int getNormalizedSensorValue(int sensorNum)
	{
		return connectedSensors[sensorNum].normalizedADValue;
	}
	
	/**
	 * Remember the pose of the motors. The revertToRecordedPose() function returns to this pose.
	 */
	public void recordPose() {
		for(int i = 0; i<position.length; ++i)
		{
			position[i] = connectedMotors[i].getTachoCount();
		}
	}

	//TODO: Write Javadoc for this. Figure out why claw won't revert.
	public void revertToRecordedPose() {
		// The following 'if' statements are a hack. The rotateTo method
		// is defective and cannot take a value that is exactly its current
		// position

		for(int i = 0; i<position.length; ++i)
		{
			if(connectedMotors[i].getTachoCount() != position[i])
			{
				connectedMotors[i].rotateTo(position[i], true);
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
			Class.forName("edu.cmu.cs.stage3.alice.core.lego.NXTArm");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			NXTArm arm = new NXTArm();
			arm.connect("6831");
			arm.recordPose();
			System.out.println("Press Enter for Motor 1:");
			br.readLine();
			arm.move(0, -9.0);
			System.out.println("Press Enter for Motor 1 moveTo:");
			br.readLine();
			arm.moveTo(0, 0.0);
			System.out.println("Press Enter for Motor 2:");
			br.readLine();
			arm.move(1, -75.0);
			System.out.println("Press Enter for Motor 3:");
			br.readLine();
			arm.move(2, 90.0);
			System.out.println("Press Enter to revert to original positon:");
			br.readLine();
			//br.readLine();
			arm.revertToRecordedPose();
			System.out.println("Press Enter Sensor 1:");
			br.readLine();
			System.out.println("Raw 1: "+arm.getRawSensorValue(1));
			System.out.println("Scaled 1:"+arm.getScaledSensorValue(1));
			System.out.println("Scaled 1:"+arm.getNormalizedSensorValue(1));
			System.out.println("should be 1: "+arm.connectedSensors[1].inputPort);
			/*while(true)
			{
				if(arm.getScaledSensorValue(1) == 1)
					break;
			}*/
			System.out.println("Press Enter Sensor 2:");
			br.readLine();
			System.out.println("Raw 2: "+arm.getRawSensorValue(2));
			System.out.println("Scaled 2:"+arm.getScaledSensorValue(2));
			System.out.println("Scaled 1:"+arm.getNormalizedSensorValue(2));
			System.out.println("should be 2: "+arm.connectedSensors[2].inputPort);
			System.out.println("Press Enter Sensor 3:");
			br.readLine();
			System.out.println("Raw 3: "+arm.getRawSensorValue(3));
			System.out.println("Scaled 3:"+arm.getScaledSensorValue(3));
			System.out.println("Scaled 1:"+arm.getNormalizedSensorValue(3));
			System.out.println("should be 3: "+arm.connectedSensors[3].inputPort);
			arm.disconnect();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
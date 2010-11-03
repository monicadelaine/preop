/**
 * The class for the NXTArm, which will eventually be any kind of robot.
 */
package edu.cmu.cs.stage3.alice.core.lego;

/*
 * This class uses the LEJOS Java library to move the Lego NXT arm. "Swivel" 
 * here refers to the motion that rotates the entire arm. "Reach" refers to the
 * motion of the arm moving forward and lowering the claw. "Claw" refers to the 
 * appendage that constitutes the "grip" or "hand" of the arm.
 * 
 * @author Trey Davis
 */
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
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class NXTArm extends Model {
	private static NXTCommand nxtCommand = new NXTCommand();
	private NXTComm nxtComm;
	//private RemoteMotor swivelMotor, reachMotor, clawMotor;
	private RemoteMotor[] connectedMotors = new RemoteMotor[3];
	private int[] gearFactors = new int[3];
	private int position[] = new int[connectedMotors.length];
	private final int MAX_MOTOR_SPEED = 99999;

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
	 * Hard coded for now. Motor A (0) gets 167, B gets 27, and C gets 37.
	 */
	private void initializeGearFactors()
	{
		gearFactors[0]=167;
		gearFactors[1]=27;
		gearFactors[2]=37;
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
	 * 	Positive angles move left and negative angles move right
	 */
	public void move(int motorNum, double angle)
	{
			RemoteMotor motor = connectedMotors[motorNum];
			motor.rotate((int) angle * 360 * gearFactors[motorNum], true); 
	}
	/*private final int SWIVEL_GEAR_FACTOR = 167;
	public void swivelLeft(double angle) {
		swivelMotor.rotate((int) (angle * 360 * SWIVEL_GEAR_FACTOR), true);
	}

	public void swivelRight(double angle) {
		swivelMotor.rotate((int) (-angle * 360 * SWIVEL_GEAR_FACTOR), true);
	}

	private final int REACH_GEAR_FACTOR = 27;
	public void reachForward(double reachAmount) {
		reachMotor.rotate((int) (-reachAmount * 360 * REACH_GEAR_FACTOR), true);
	}

	public void reachBackward(double reachAmount) {
		reachMotor.rotate((int) (reachAmount * 360 * REACH_GEAR_FACTOR), true);
	}

	private final int CLAW_MOTOR_ROTATION_AMOUNT = 37;
	public void openClaw() {
		clawMotor.rotate(-CLAW_MOTOR_ROTATION_AMOUNT, true);
	}

	public void closeClaw() {
		clawMotor.rotate(CLAW_MOTOR_ROTATION_AMOUNT, true);
	}*/

	//private int firstMotorPosition, secondMotorPosition, thirdMotorPosition;
	
	/**
	 * Remember the pose of the motors. The revertToRecordedPose() function returns to this pose.
	 */
	public void recordPose() {
		for(int i = 0; i<position.length; ++i)
		{
			position[i] = connectedMotors[i].getTachoCount();
		}
	}

	public void revertToRecordedPose() {
		// TODO: The following 'if' statements are a hack. The rotateTo method
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
			//arm.openClaw();
			br.readLine();
			//arm.closeClaw();
			arm.revertToRecordedPose();
			br.readLine();
			arm.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
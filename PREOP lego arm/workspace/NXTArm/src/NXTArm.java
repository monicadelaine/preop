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

import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class NXTArm {
	private static NXTCommand nxtCommand = new NXTCommand();
	private NXTComm nxtComm;
	private RemoteMotor swivelMotor, reachMotor, clawMotor;
	
	public NXTArm() {
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException e) {}
	}
	
	public void connect(String macAddressPostfix) {
		try {
			NXTInfo nxtInfo = findNXTArmWithPostfix(macAddressPostfix);
			connectToNXTArm(nxtInfo);
		} catch (Exception e) {
			// this.macAddress.set(Address.NONE);
			e.printStackTrace();
		}
		initializeMotors();
	}
	private final int MACADDRESS_POSTFIX_START_INDEX = 8;
	private NXTInfo findNXTArmWithPostfix(String macAddressPostfix) 
					throws NXTCommException, Exception {
		NXTInfo[] nxtInfo = nxtComm.search("NXT", NXTCommFactory.BLUETOOTH);
		if (nxtInfo == null)
			throw new Exception("Couldn't find an NXT.");
		
		for(int i=0; i < nxtInfo.length; i++)
			if(nxtInfo[i].deviceAddress.substring(MACADDRESS_POSTFIX_START_INDEX)
					.equals(macAddressPostfix))
				return nxtInfo[i];
		
		throw new Exception("NXT of particular address not found");
	}
	private void connectToNXTArm(NXTInfo nxtInfo) 
				 throws NXTCommException, Exception {
		if (!nxtComm.open(nxtInfo))
			throw new Exception("Couldn't establish connection to NXT.");
		nxtCommand.setNXTComm(nxtComm);
	}
	private final int SWIVEL_MOTOR_ID = 0, 
	  				  REACH_MOTOR_ID = 1,
	  				  CLAW_MOTOR_ID = 2;
	private final int MAX_MOTOR_SPEED = 99999;
	private void initializeMotors() {
		swivelMotor = new RemoteMotor(nxtCommand, SWIVEL_MOTOR_ID);
		swivelMotor.setSpeed(MAX_MOTOR_SPEED);
		reachMotor = new RemoteMotor(nxtCommand, REACH_MOTOR_ID);
		reachMotor.setSpeed(MAX_MOTOR_SPEED);
		clawMotor = new RemoteMotor(nxtCommand, CLAW_MOTOR_ID);
		clawMotor.setSpeed(MAX_MOTOR_SPEED);
	}

	protected void disconnect() {
		try {
			nxtComm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final int SWIVEL_GEAR_FACTOR = 167;
	public void swivelLeft(double angle) {
		swivelMotor.rotate((int)(angle*360*SWIVEL_GEAR_FACTOR), true);
	}
	public void swivelRight(double angle) {
		swivelMotor.rotate((int)(-angle*360*SWIVEL_GEAR_FACTOR), true);
	}
	
	private final int REACH_GEAR_FACTOR = 27;
	public void reachForward(double reachAmount) {
		reachMotor.rotate((int)(-reachAmount*360*REACH_GEAR_FACTOR), true);
	}
	public void reachBackward(double reachAmount) {
		reachMotor.rotate((int)(reachAmount*360*REACH_GEAR_FACTOR), true);
	}
	
	private final int CLAW_MOTOR_ROTATION_AMOUNT = 37;
	public void openClaw() {
		clawMotor.rotate(-CLAW_MOTOR_ROTATION_AMOUNT, true);
	}
	public void closeClaw() {
		clawMotor.rotate(CLAW_MOTOR_ROTATION_AMOUNT, true);
	}
	
	private int swivelMotorPosition, reachMotorPosition, clawMotorPosition;
	public void recordPose() {
		swivelMotorPosition = swivelMotor.getTachoCount();
		reachMotorPosition = reachMotor.getTachoCount();
		clawMotorPosition = clawMotor.getTachoCount();
	}
	public void revertToRecordedPose() {
		//TODO: The following 'if' statements are a hack. The rotateTo method is
		//defective and cannot take a value that is exactly its current position
		if(swivelMotor.getTachoCount() != swivelMotorPosition)
			swivelMotor.rotateTo(swivelMotorPosition, true);
		if(reachMotor.getTachoCount() != reachMotorPosition)
			reachMotor.rotateTo(reachMotorPosition, true);
		if(clawMotor.getTachoCount() != clawMotorPosition)
			clawMotor.rotateTo(clawMotorPosition, true);
	}
	
	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			NXTArm arm = new NXTArm();
			arm.connect("6831");
			arm.recordPose();
			arm.openClaw();
			br.readLine();
			arm.closeClaw();
			arm.revertToRecordedPose();
			br.readLine();
			arm.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
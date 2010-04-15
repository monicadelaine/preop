import java.io.IOException;
import lejos.nxt.remote.FileInfo;
import lejos.nxt.remote.FirmwareInfo;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class NXTArm {
	private static NXTCommand nxtCommand = new NXTCommand();
	NXTComm nxtComm = null;
	public RemoteMotor swivelMotor = null; // Swivel
	public RemoteMotor rm2 = null; // Arm
	public RemoteMotor rm3 = null; // Claw
	public static void main(String[] args) throws NXTCommException {
		try {
			NXTArm arm = new NXTArm();
			arm.swivelMotor.setSpeed(100);
			arm.swivelMotor.forward();
			Thread.sleep(20000);
			arm.swivelMotor.stop();
			arm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public NXTArm() throws Exception{
		nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		NXTInfo[] nxtInfo = nxtComm.search("NXT", NXTCommFactory.BLUETOOTH);
		if (nxtInfo == null)
			throw new Exception("Couldn't find the NXT.");
		if (!nxtComm.open(nxtInfo[0]))
			throw new Exception("Couldn't establish connection to NXT.");
		nxtCommand.setNXTComm(nxtComm);
		swivelMotor = new RemoteMotor(nxtCommand, 1);
		rm2 = new RemoteMotor(nxtCommand, 2);
		rm3 = new RemoteMotor(nxtCommand, 0);
	}
	protected void close() {
		try {
			nxtComm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void swivelLeft(double speed, double duration) {
		swivelMotor.setSpeed(100);
		swivelMotor.backward();
		try{Thread.sleep((long)(duration*1000));}catch(Exception e){}
		swivelMotor.stop();
	}
	
}
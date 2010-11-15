package edu.cmu.cs.stage3.alice.core.response.lego;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.NXTRobotProperty;

public class SwivelAnimation extends Response {
	public final NXTRobotProperty subject = new NXTRobotProperty( this, "subject", null );
	
}

package edu.cmu.cs.stage3.alice.core.response.lego;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.NXTArmProperty;

public class CloseClawAnimation extends Response {
	public final NXTArmProperty subject = new NXTArmProperty( this, "subject", null );

}

package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;

public class TurnAwayFromAnimation extends Response{
	public final ReferenceFrameProperty subject = new ReferenceFrameProperty( this, "subject", null );
	//TODO: Change type
	public final ReferenceFrameProperty note = new ReferenceFrameProperty( this, "target", null );
}

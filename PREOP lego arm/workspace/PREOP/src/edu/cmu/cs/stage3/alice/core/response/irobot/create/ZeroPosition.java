package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import edu.cmu.cs.stage3.alice.core.Note;
import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.property.CreateProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class ZeroPosition extends Response{
	public final CreateProperty subject = new CreateProperty( this, "subject", null );
	//public final NumberProperty duration = new NumberProperty( this, "duration", new Double(.01));

	public ZeroPosition(){
		super();
		this.duration.set(new Double(.01));
	}
	public class RuntimeZeroPosition extends RuntimeResponse {
		public void prologue( double t ) {
			super.prologue( t );
			((Create)subject.getValue()).zeroXYTheta();
		}
		public void update(double t){
			super.update(t);
		}
	}
}

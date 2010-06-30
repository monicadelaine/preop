package edu.cmu.cs.stage3.alice.core.question.irobot.create;

import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

public class GetXPos extends NumberQuestion {
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public Object getValue() {
		return new Double(((Create)subject.getValue()).getXPos());
	}
}

package edu.cmu.cs.stage3.alice.core.question.birdbrain.finch;

import edu.cmu.cs.stage3.alice.core.birdbrain.Finches;
import edu.cmu.cs.stage3.alice.core.question.NumberQuestion;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;

public class GetXAcceleration extends NumberQuestion {
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public Object getValue() {
		return new Double(((Finches)subject.getValue()).getXAcceleration());
	}
}

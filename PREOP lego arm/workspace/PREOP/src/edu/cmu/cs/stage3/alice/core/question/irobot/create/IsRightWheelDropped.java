package edu.cmu.cs.stage3.alice.core.question.irobot.create;

import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;

public class IsRightWheelDropped extends BooleanQuestion{
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public Object getValue() {
		if(((Create)subject.getValue()).isRightWheelDropped())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
}
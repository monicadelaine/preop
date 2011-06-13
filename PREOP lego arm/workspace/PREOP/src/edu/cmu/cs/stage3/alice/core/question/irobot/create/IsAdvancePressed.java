package edu.cmu.cs.stage3.alice.core.question.irobot.create;

import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.property.RobotProperty;

public class IsAdvancePressed extends BooleanQuestion{
	public final RobotProperty subject = new RobotProperty( this, "subject", null );
	public Object getValue() {
		if(((Create)subject.getValue()).isAdvancePressed())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
}
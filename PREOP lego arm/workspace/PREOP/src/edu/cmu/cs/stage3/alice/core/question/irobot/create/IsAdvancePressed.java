package edu.cmu.cs.stage3.alice.core.question.irobot.create;

import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.property.CreateProperty;

public class IsAdvancePressed extends BooleanQuestion{
	public final CreateProperty subject = new CreateProperty( this, "subject", null );
	public Object getValue() {
		if(((Create)subject.getValue()).isAdvancePressed())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
}
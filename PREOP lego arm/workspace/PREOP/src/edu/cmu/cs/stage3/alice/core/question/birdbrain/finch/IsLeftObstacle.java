package edu.cmu.cs.stage3.alice.core.question.birdbrain.finch;

import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;
import edu.cmu.cs.stage3.alice.core.birdbrain.Finches;
import edu.cmu.cs.stage3.alice.core.property.RobotProperty;

public class IsLeftObstacle extends BooleanQuestion{
	public final RobotProperty subject = new RobotProperty( this, "subject", null );
	public Object getValue() {
		if(((Finches)subject.getValue()).isLeftObstacle())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
}
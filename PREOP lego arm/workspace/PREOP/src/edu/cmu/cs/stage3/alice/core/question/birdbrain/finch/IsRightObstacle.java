package edu.cmu.cs.stage3.alice.core.question.birdbrain.finch;

import edu.cmu.cs.stage3.alice.core.question.BooleanQuestion;
import edu.cmu.cs.stage3.alice.core.birdbrain.Finches;
import edu.cmu.cs.stage3.alice.core.property.RobotProperty;

public class IsRightObstacle extends BooleanQuestion{
	public final RobotProperty subject = new RobotProperty( this, "subject", null );
	public Object getValue() {
		if(((Finches)subject.getValue()).isRightObstacle())
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
}
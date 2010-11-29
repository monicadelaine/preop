package edu.cmu.cs.stage3.alice.core.question.lego.NXTArm;

import edu.cmu.cs.stage3.alice.core.lego.NXTArm;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInBooleanQuestion;

public class GetBooleanSensorValue extends UnaryNumberResultingInBooleanQuestion{
	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public final NumberProperty sensorNum = new NumberProperty( this, "sensorNum", new Integer( -1 ) );

	public boolean getValue(double sensorNum) {
		return (((NXTArm)subject.getValue()).getBooleanValue((int)sensorNum));
	}

}
package edu.cmu.cs.stage3.alice.core.question.lego;

import edu.cmu.cs.stage3.alice.core.lego.NXTArm;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInBooleanQuestion;

public class GetBooleanSensorValue extends UnaryNumberResultingInBooleanQuestion{

	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public boolean getValue(double sensorNum) {
		return (((NXTArm)subject.getValue()).getBooleanValue((int)sensorNum));
	}
}
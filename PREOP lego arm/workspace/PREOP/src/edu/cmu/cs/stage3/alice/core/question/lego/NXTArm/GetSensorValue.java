package edu.cmu.cs.stage3.alice.core.question.lego.NXTArm;

import edu.cmu.cs.stage3.alice.core.lego.NXTArm;
import edu.cmu.cs.stage3.alice.core.property.TransformableProperty;
import edu.cmu.cs.stage3.alice.core.question.UnaryNumberResultingInNumberQuestion;

public class GetSensorValue extends UnaryNumberResultingInNumberQuestion{

	public final TransformableProperty subject = new TransformableProperty( this, "subject", null );
	public double getValue(double sensorNum) {
		return (((NXTArm)subject.getValue()).getSensorValue((int)sensorNum));
	}
}

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.lego.NXTRobot;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class NXTRobotProperty extends TransformableProperty {
	protected NXTRobotProperty( Element owner, String name, Transformable defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public NXTRobotProperty( Element owner, String name, Transformable defaultValue ) {
		this( owner, name, defaultValue, NXTRobot.class );
	}
	public Transformable getTransformableValue() {
		return (Transformable)getReferenceFrameValue();
	}
}
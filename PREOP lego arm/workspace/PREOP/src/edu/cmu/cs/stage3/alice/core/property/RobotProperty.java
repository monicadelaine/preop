package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Robot;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class RobotProperty extends TransformableProperty{
	protected RobotProperty( Element owner, String name, Transformable defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public RobotProperty( Element owner, String name, Transformable defaultValue ) {
		this( owner, name, defaultValue, Robot.class );
	}
	public Transformable getTransformableValue() {
		return (Transformable)getReferenceFrameValue();
	}
}
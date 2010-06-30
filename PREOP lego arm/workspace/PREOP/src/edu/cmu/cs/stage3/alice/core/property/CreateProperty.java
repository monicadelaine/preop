package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class CreateProperty extends TransformableProperty{
	protected CreateProperty( Element owner, String name, Transformable defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public CreateProperty( Element owner, String name, Transformable defaultValue ) {
		this( owner, name, defaultValue, Create.class );
	}
	public Transformable getTransformableValue() {
		return (Transformable)getReferenceFrameValue();
	}
}
package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.lego.NXTArm;
import edu.cmu.cs.stage3.alice.core.Transformable;

public class NXTArmProperty extends TransformableProperty {
	protected NXTArmProperty( Element owner, String name, Transformable defaultValue, Class valueClass ) {
		super( owner, name, defaultValue, valueClass );
	}
	public NXTArmProperty( Element owner, String name, Transformable defaultValue ) {
		this( owner, name, defaultValue, NXTArm.class );
	}
	public Transformable getTransformableValue() {
		return (Transformable)getReferenceFrameValue();
	}
}
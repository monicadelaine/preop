package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.response.RollAnimation;
import edu.cmu.cs.stage3.alice.core.response.RotateAnimation;
import edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle;

public class TurnAnimation extends RotateAnimation {
	public TurnAnimation(){
		super();
		this.asSeenBy.deprecate();
		this.style.deprecate();
		this.subject.setValueClass(Create.class); //TODO: change subject to a CreatePropery and make setValueClass protected
	}
	private static Class[] s_supportedCoercionClasses = { RollAnimation.class };
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	protected boolean acceptsDirection( Direction direction ) {
		return direction.getTurnAxis()!=null;
	}
	public class RuntimeTurnAnimation extends RuntimeRotateAnimation {
		protected javax.vecmath.Vector3d getAxis( Direction direction ) {
			if( direction != null ) {
				return direction.getTurnAxis();
			} else {
				return null;
			}
		}
		public void prologue( double t ) {
			TurnAnimation.this.style.set(TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
			super.prologue( t );
			if(((Create)TurnAnimation.this.subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
				if(TurnAnimation.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT))
					((Create)TurnAnimation.this.subject.getValue()).turn(((Double)TurnAnimation.this.amount.getValue()).doubleValue(), ((Double)TurnAnimation.this.duration.getValue()).doubleValue());
				if(TurnAnimation.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT))
					((Create)TurnAnimation.this.subject.getValue()).turn(-((Double)TurnAnimation.this.amount.getValue()).doubleValue(), ((Double)TurnAnimation.this.duration.getValue()).doubleValue());
			}
		}
	}
}

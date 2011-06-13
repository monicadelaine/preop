package edu.cmu.cs.stage3.alice.core.response.birdbrain.finch;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.birdbrain.Finches;
import edu.cmu.cs.stage3.alice.core.response.RollAnimation;
import edu.cmu.cs.stage3.alice.core.response.RotateAnimation;
import edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle;

public class TurnAnimation extends RotateAnimation {
	public TurnAnimation(){
		super();
		this.asSeenBy.deprecate();
		this.style.deprecate();
		this.subject.setValueClass(Finches.class); //TODO: change subject to a CreatePropery and make setValueClass protected
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
			if(((Finches)TurnAnimation.this.subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
				if(TurnAnimation.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.LEFT))
					((Finches)TurnAnimation.this.subject.getValue()).turnLeft(((Double)TurnAnimation.this.duration.getValue()).doubleValue());
				if(TurnAnimation.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.RIGHT))
					((Finches)TurnAnimation.this.subject.getValue()).turnRight(((Double)TurnAnimation.this.duration.getValue()).doubleValue());
			}
		}
	}
}

package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation;
import edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle;
import edu.cmu.cs.stage3.alice.core.irobot.Create;

public class MoveAnimation extends DirectionAmountTransformAnimation {
	public MoveAnimation(){
		super();
		this.asSeenBy.deprecate();
		this.style.deprecate();
		this.subject.setValueClass(Create.class); //TODO: change subject to a CreatePropery and make setValueClass protected
	}
	protected Direction getDefaultDirection() {
		return Direction.FORWARD;
	}
	protected boolean acceptsDirection( edu.cmu.cs.stage3.alice.core.Direction direction ) {
		return direction.getMoveAxis()!=null;
	}
	public class RuntimeMoveAnimation extends RuntimeDirectionAmountTransformAnimation {
		private javax.vecmath.Vector3d m_vector;
		private javax.vecmath.Vector3d m_vectorPrev;
		protected javax.vecmath.Vector3d getVector() {
			Direction directionValue = MoveAnimation.this.direction.getDirectionValue();
			double amountValue = MoveAnimation.this.amount.doubleValue();
			if( directionValue!=null && !Double.isNaN( amountValue ) ) {
				javax.vecmath.Vector3d v = edu.cmu.cs.stage3.math.MathUtilities.multiply( directionValue.getMoveAxis(), amountValue );
				return v;
			} else {
				return new javax.vecmath.Vector3d();
			}
		}
		public void prologue( double t ) { 
			MoveAnimation.this.style.set(TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
			if(((Create)MoveAnimation.this.subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
				if(MoveAnimation.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD))
					((Create)MoveAnimation.this.subject.getValue()).move(((Double)MoveAnimation.this.amount.getValue()).doubleValue(), ((Double)MoveAnimation.this.duration.getValue()).doubleValue());
				if(MoveAnimation.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD))
					((Create)MoveAnimation.this.subject.getValue()).move(-((Double)MoveAnimation.this.amount.getValue()).doubleValue(), ((Double)MoveAnimation.this.duration.getValue()).doubleValue());
			}
			super.prologue( t );
			m_vectorPrev = new javax.vecmath.Vector3d();
			m_vector = getVector();
		}
		public void update( double t ) {
			super.update( t );
			javax.vecmath.Vector3d delta = edu.cmu.cs.stage3.math.MathUtilities.subtract( edu.cmu.cs.stage3.math.MathUtilities.multiply( m_vector, getPortion( t ) ), m_vectorPrev );
			m_subject.moveRightNow( delta, m_asSeenBy );
			m_vectorPrev.add( delta );
		}
	}
}

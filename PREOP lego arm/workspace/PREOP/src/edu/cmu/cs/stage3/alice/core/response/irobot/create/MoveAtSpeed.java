package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.response.DirectionSpeedTransformResponse;

public class MoveAtSpeed extends DirectionSpeedTransformResponse {
	public MoveAtSpeed(){
		super();
		this.asSeenBy.deprecate();
		this.subject.setValueClass(Create.class); //TODO: change subject to a CreatePropery and make setValueClass protected
	}
	protected Direction getDefaultDirection() {
		return Direction.FORWARD;
	}
	protected boolean acceptsDirection( Direction direction ) {
		return direction.getMoveAxis()!=null;
	}
	public class RuntimeMoveAtSpeed extends RuntimeDirectionSpeedTransformResponse {
		private javax.vecmath.Vector3d m_directionVector;
		public void prologue( double t ) {
			super.prologue( t );
			if(((Create)MoveAtSpeed.this.subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
				if(MoveAtSpeed.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.FORWARD))
					((Create)MoveAtSpeed.this.subject.getValue()).moveAtSpeed(((Double)MoveAtSpeed.this.speed.getValue()).doubleValue(), ((Double)MoveAtSpeed.this.duration.getValue()).doubleValue());
				if(MoveAtSpeed.this.direction.getValue().equals(edu.cmu.cs.stage3.alice.core.Direction.BACKWARD))
					((Create)MoveAtSpeed.this.subject.getValue()).moveAtSpeed(-((Double)MoveAtSpeed.this.speed.getValue()).doubleValue(), ((Double)MoveAtSpeed.this.duration.getValue()).doubleValue());
			}
			Direction directionValue = MoveAtSpeed.this.direction.getDirectionValue();
			if( directionValue!=null ) {
				m_directionVector = directionValue.getMoveAxis();
			} else {
				m_directionVector = new javax.vecmath.Vector3d();
			}
		}
		public void update( double t ) {
			super.update( t );
            double delta = getDT()*getSpeed();
			m_subject.moveRightNow( edu.cmu.cs.stage3.math.MathUtilities.multiply( m_directionVector, delta ), m_asSeenBy );
		}
	}
}

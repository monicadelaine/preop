package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.response.RollAtSpeed;
import edu.cmu.cs.stage3.alice.core.response.RotateAtSpeed;

public class TurnAtSpeed extends RotateAtSpeed {
	public TurnAtSpeed(){
		super();
		this.asSeenBy.deprecate();
		this.subject.setValueClass(Create.class); //TODO: change subject to a CreatePropery and make setValueClass protected
	}
	private static Class[] s_supportedCoercionClasses = { RollAtSpeed.class };
	public Class[] getSupportedCoercionClasses() {
		return s_supportedCoercionClasses;
	}
	protected boolean acceptsDirection( Direction direction ) {
		return direction.getTurnAxis()!=null;
	}
	public class RuntimeTurnAtSpeed extends RuntimeRotateAtSpeed {
		protected javax.vecmath.Vector3d getAxis( Direction direction ) {
			if( direction!=null ) {
				return direction.getTurnAxis();
			} else {
				return null;
			}
		}
		public void prologue( double t ) {
			super.prologue( t );
			if(((Create)TurnAtSpeed.this.subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
				if(TurnAtSpeed.this.direction.getValue()==edu.cmu.cs.stage3.alice.core.Direction.LEFT)
					((Create)TurnAtSpeed.this.subject.getValue()).turnAtSpeed(((Double)TurnAtSpeed.this.speed.getValue()).doubleValue(), ((Double)TurnAtSpeed.this.duration.getValue()).doubleValue());
				if(TurnAtSpeed.this.direction.getValue()==edu.cmu.cs.stage3.alice.core.Direction.RIGHT)
					((Create)TurnAtSpeed.this.subject.getValue()).turnAtSpeed(-((Double)TurnAtSpeed.this.speed.getValue()).doubleValue(), ((Double)TurnAtSpeed.this.duration.getValue()).doubleValue());
			}
		}
	}
}

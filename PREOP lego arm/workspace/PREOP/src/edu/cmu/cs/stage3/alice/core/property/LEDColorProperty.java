package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Robot;
import edu.cmu.cs.stage3.alice.core.birdbrain.Finches;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.response.irobot.create.MoveAnimation;

public class LEDColorProperty extends ColorProperty{
	public LEDColorProperty( Element owner, String name, edu.cmu.cs.stage3.alice.scenegraph.Color defaultValue ){
		super( owner, name, defaultValue);
		//System.out.println("passing up: " + defaultValue.toString());
	}
	public void set( Object value ) throws IllegalArgumentException {
		super.set(value);
		//System.out.println("setting: " + value.toString());
		if(((Robot)this.getOwner()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
			if(this.getName().equals("powerLEDColor")){
				if(value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)
					((Create)this.getOwner()).setPowerLED((edu.cmu.cs.stage3.alice.scenegraph.Color)value);
			}
		}
	}
	public void set( Object value, edu.cmu.cs.stage3.util.HowMuch howMuch ) throws IllegalArgumentException {
		super.set(value,howMuch);
		if(((Robot)this.getOwner()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
			if(this.getName().equals("powerLEDColor") && this.getOwner() instanceof Create){
				if(value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)
					((Create)this.getOwner()).setPowerLED((edu.cmu.cs.stage3.alice.scenegraph.Color)value);
			}
			if(this.getName().equals("beakLEDColor") && this.getOwner() instanceof Finches){
				if(value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)
					((Finches)this.getOwner()).setBeakLED((edu.cmu.cs.stage3.alice.scenegraph.Color)value);
			}
		}
	}
}

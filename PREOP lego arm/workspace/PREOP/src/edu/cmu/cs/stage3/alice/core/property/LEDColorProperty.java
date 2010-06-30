package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
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
		if(((Create)this.getOwner()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
			if(this.getName().equals("powerLEDColor")){
				if(value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)
					((Create)this.getOwner()).setPowerLED((edu.cmu.cs.stage3.alice.scenegraph.Color)value);
			}
		}
	}
	public void set( Object value, edu.cmu.cs.stage3.util.HowMuch howMuch ) throws IllegalArgumentException {
		super.set(value,howMuch);
		if(((Create)this.getOwner()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
			if(this.getName().equals("powerLEDColor")){
				if(value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color)
					((Create)this.getOwner()).setPowerLED((edu.cmu.cs.stage3.alice.scenegraph.Color)value);
			}
		}
	}
}

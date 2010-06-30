package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.response.irobot.create.MoveAnimation;

public class LEDBooleanProperty extends BooleanProperty{
	public LEDBooleanProperty( Element owner, String name, Boolean defaultValue ){
		super( owner, name, defaultValue);
		//setIsAcceptingOfHowMuch(false);
	}
	public void set( Object value ) throws IllegalArgumentException {
		super.set(value);
		if(((Create)this.getOwner()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
			if(this.getName().equals("isAdvanceLEDOn")){
				if(value instanceof Boolean)
					((Create)this.getOwner()).setAdvanceLED(((Boolean)value).booleanValue());
			}
			else if(this.getName().equals("isPlayLEDOn")){
				if(value instanceof Boolean)
					((Create)this.getOwner()).setPlayLED(((Boolean)value).booleanValue());
			}
		}
	}
	public void set( Object value, edu.cmu.cs.stage3.util.HowMuch howMuch ) throws IllegalArgumentException {
		super.set(value,howMuch);
		if(((Create)this.getOwner()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning()))){
			if(this.getName().equals("isAdvanceLEDOn")){
				if(value instanceof Boolean)
					((Create)this.getOwner()).setAdvanceLED(((Boolean)value).booleanValue());
			}
			else if(this.getName().equals("isPlayLEDOn")){
				if(value instanceof Boolean)
					((Create)this.getOwner()).setPlayLED(((Boolean)value).booleanValue());
			}
		}
	}
}

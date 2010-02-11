package edu.cmu.cs.stage3.alice.core.irobot;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.Address;
import edu.cmu.cs.stage3.alice.core.property.LEDBooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ColorProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;
import edu.cmu.cs.stage3.alice.core.property.AddressProperty;

public class Create extends Model{
	public final ColorProperty powerLEDColor = new ColorProperty( this, "powerLEDColor", edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN );
	public final NumberProperty powerLEDBrightness = new NumberProperty( this, "powerLEDBrightness", new Double( 0 ) );
	public final LEDBooleanProperty isAdvanceLEDOn = new LEDBooleanProperty( this, "isAdvanceLEDOn", Boolean.FALSE );
	public final LEDBooleanProperty isPlayLEDOn = new LEDBooleanProperty( this, "isPlayLEDOn", Boolean.FALSE );
	public final AddressProperty macAddress = new AddressProperty( this, "macAddress", new Address());
	
}

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class NoteProperty extends ObjectProperty {
	public NoteProperty( Element owner, String name, edu.cmu.cs.stage3.alice.core.Address defaultValue ) {
		super( owner, name, defaultValue, edu.cmu.cs.stage3.alice.core.Note.class );
	}
	/*
    protected void decodeObject( org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, java.util.Vector referencesToBeResolved, double version ) throws java.io.IOException {
        set( getNodeText( node ) );
    }
    protected void encodeObject( org.w3c.dom.Document document, org.w3c.dom.Element node, edu.cmu.cs.stage3.io.DirectoryTreeStorer storer, edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator ) throws java.io.IOException {
        node.appendChild( createNodeForString( document, getAddressValue() ) );
    }
    */
}

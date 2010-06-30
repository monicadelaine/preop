package edu.cmu.cs.stage3.alice.authoringtool.viewcontroller;

import edu.cmu.cs.stage3.alice.core.Note;

public class NotePropertyViewController  extends TextFieldEditablePropertyViewController{
	protected javax.swing.JLabel numberLabel = new javax.swing.JLabel();

	public void set( edu.cmu.cs.stage3.alice.core.Property property, boolean includeDefaults, boolean allowExpressions, boolean omitPropertyName, final edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory ) {
		super.set( property, includeDefaults, allowExpressions, true, omitPropertyName, factory );
		this.allowEasyEditWithClick = false;
		refreshGUI();
	}

	protected void setValueFromString( String valueString ) {

	}

	protected java.awt.Component getNativeComponent() {
		return numberLabel;
	}
	
	protected java.awt.event.MouseListener getMouseListener() {
		return new java.awt.event.MouseAdapter() {
			public void mouseReleased( java.awt.event.MouseEvent ev ) {
				if( (ev.getX() >= 0) && (ev.getX() < ev.getComponent().getWidth()) && (ev.getY() >= 0) && (ev.getY() < ev.getComponent().getHeight()) ) {
					if( isEnabled() ) {
						NotePropertyViewController.this.popupButton.doClick();
					}
				}
			}
		};
	}

	protected Class getNativeClass() {
		return Note.class;
	}

	protected void updateNativeComponent() {
		numberLabel.setText( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( property.get(), property, property.getOwner().data ) );
	}

	protected void refreshGUI() {
		if( this.isAncestorOf( textField ) ) {
			remove( textField );
		}
		super.refreshGUI();
	}
}

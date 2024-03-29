/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 * 
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.authoringtool;

// autogenerated imports
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

import edu.cmu.cs.stage3.alice.authoringtool.util.Configuration;

/**
 * @author Jason Pratt
 */
public class JAliceFrame extends javax.swing.JFrame {
	protected AuthoringTool authoringTool;

	// modes
	public static int SCENE_EDITOR_SMALL_MODE = 1;
	public static int SCENE_EDITOR_LARGE_MODE = 2;
	protected int guiMode = SCENE_EDITOR_SMALL_MODE;

	// custom components
	WorldTreeComponent worldTreeComponent;
	DragFromComponent dragFromComponent;
	//EditorComponent editorComponent;
	TabbedEditorComponent tabbedEditorComponent;
	edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor sceneEditor;
	edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BehaviorGroupsEditor behaviorGroupsEditor;
	edu.cmu.cs.stage3.alice.authoringtool.util.TrashComponent trashComponent;
	protected ComponentImageFactory componentImageFactory = new ComponentImageFactory();
	StatusBar statusBar;

	// preferences
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( getClass().getPackage() );

	// recent worlds
	protected int recentWorldsPosition = -1;
	protected java.util.ArrayList recentWorlds = new java.util.ArrayList();
	protected int maxRecentWorlds;
	protected java.awt.event.ActionListener recentWorldsListener;

	////////////////
	// Constructor
	////////////////
	public JAliceFrame( AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled( false );
		jbInit();
		guiInit();
		recentWorldsInit();
		helpInit();
		setGuiMode( SCENE_EDITOR_SMALL_MODE );

		setIconImage( AuthoringToolResources.getAliceSystemIconImage() );
	}

	protected class ComponentImageFactory extends javax.swing.JPanel {
		public ComponentImageFactory() {
			setPreferredSize( new java.awt.Dimension( 0, 0 ) );
		}
		public java.awt.image.BufferedImage manufactureImage( javax.swing.JComponent component ) {
			boolean isShowing = component.isShowing();
			java.awt.Container parent = component.getParent();
			if( isShowing ) {
				//pass
			} else {
				if( parent != null ) {
					parent.remove( component );
				}
				add( component );
			}
			doLayout();
			java.awt.Dimension d = component.getPreferredSize();
			java.awt.image.BufferedImage image = new java.awt.image.BufferedImage( d.width, d.height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
			java.awt.Graphics2D g = image.createGraphics();
			component.paintAll( g );
			if( isShowing ) {
				//pass
			} else {
				remove( component );
				if( parent != null ) {
					parent.add( component );				
				}
			}
			return image;
		}
	}

	public java.awt.Image getImageForComponent(javax.swing.JComponent c){
		java.awt.image.BufferedImage image = componentImageFactory.manufactureImage( c );
		return image;
	}
	
	public void HACK_goToRedAlert() {
		getContentPane().setBackground( new java.awt.Color( 192, 0, 0 ) );
	}
	public void HACK_standDownFromRedAlert() {
		getContentPane().setBackground( edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf( authoringToolConfig.getValue( "backgroundColor" ) ).createAWTColor() );
	}
	
	private void guiInit() {
		setDefaultCloseOperation( javax.swing.JFrame.DO_NOTHING_ON_CLOSE );

		if( authoringToolConfig.getValue( "useSingleFileLoadStore" ).equalsIgnoreCase( "true" ) ) {
			fileMenu.remove( JAliceFrame.this.openZippedWorldItem );
		}

		getContentPane().setBackground( edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf( authoringToolConfig.getValue( "backgroundColor" ) ).createAWTColor() );
		Configuration.addConfigurationListener(
			new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener() {
				public void changing( edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev ) {}
				public void changed( edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev ) {
					if( ev.getKeyName().equals( "edu.cmu.cs.stage3.alice.authoringtool.backgroundColor" ) ) {
						JAliceFrame.this.getContentPane().setBackground( edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf( authoringToolConfig.getValue( "backgroundColor" ) ).createAWTColor() );
					} else if( ev.getKeyName().equals( "edu.cmu.cs.stage3.alice.authoringtool.useSingleFileLoadStore" ) ) {
						if( ev.getNewValue().equalsIgnoreCase( "true" ) ) {
							fileMenu.remove( JAliceFrame.this.openZippedWorldItem );
						} else {
							if( ! fileMenu.isMenuComponent( JAliceFrame.this.openZippedWorldItem ) ) {
								fileMenu.add( JAliceFrame.this.openZippedWorldItem, 2 );
							}
						}
					}
				}
			}
		);

		// create custom components
		worldTreeComponent = new WorldTreeComponent( authoringTool );
		dragFromComponent = new DragFromComponent( authoringTool );
		//editorComponent = new EditorComponent( authoringTool );
		tabbedEditorComponent = new TabbedEditorComponent( authoringTool );
		behaviorGroupsEditor = new edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor.BehaviorGroupsEditor();
		sceneEditor = new edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor();
		sceneEditor.setAuthoringTool( authoringTool );
		trashComponent = new edu.cmu.cs.stage3.alice.authoringtool.util.TrashComponent( authoringTool );
		statusBar = new StatusBar( authoringTool );

		// add custom components
		worldTreePanel.add( worldTreeComponent, java.awt.BorderLayout.CENTER );
		dragFromPanel.add( dragFromComponent, java.awt.BorderLayout.CENTER );
		//editorPanel.add( editorComponent, java.awt.BorderLayout.CENTER );
		editorPanel.add( tabbedEditorComponent, java.awt.BorderLayout.CENTER );
		scenePanel.add( sceneEditor, java.awt.BorderLayout.CENTER );
		behaviorPanel.add( behaviorGroupsEditor, java.awt.BorderLayout.CENTER );
//		outputPanel.add( outputComponent, java.awt.BorderLayout.CENTER );
		trashPanel.add( trashComponent, java.awt.BorderLayout.SOUTH );
		if( authoringToolConfig.getValue( "showWorldStats" ).equalsIgnoreCase( "true" ) ) {
			getContentPane().add( statusBar, java.awt.BorderLayout.SOUTH );
		}

		// resize the window
		int screenWidth = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight = (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();

		String boundsString = authoringToolConfig.getValue( "mainWindowBounds" );
		java.util.StringTokenizer st = new java.util.StringTokenizer( boundsString, " \t," );
		// if size isn't set in config, fill the screen
		int x = 0;
		int y = 0;
		int width = screenWidth;
		int height = screenHeight - 28;
		if( st.countTokens() == 4 ) {
			try {
				x = Integer.parseInt( st.nextToken() );
				y = Integer.parseInt( st.nextToken() );
				width = Integer.parseInt( st.nextToken() );
				height = Integer.parseInt( st.nextToken() );
			} catch( NumberFormatException e ) {
				AuthoringTool.showErrorDialog( "Parse error in config value: mainWindowBounds", null );
			}
		} else {
			AuthoringTool.showErrorDialog( "Incorrect number of tokens in config value: mainWindowBounds", null );
		}
		this.setBounds( x, y, width, height );

		// set divider locations
//		authoringOutputSplitPane.setDividerLocation( height );
//		authoringOutputSplitPane.setResizeWeight( 1.0 );
		leftRightSplitPane.setDividerLocation( 230 );
		leftRightSplitPane.setResizeWeight( 0.0 );
		worldTreeDragFromSplitPane.setDividerLocation( (int)(.32 * height) );
		worldTreeDragFromSplitPane.setResizeWeight( 0.0 );
		editorBehaviorSplitPane.setDividerLocation( (int)(.275 * height) );
		editorBehaviorSplitPane.setResizeWeight( 0.0 );
		smallSceneBehaviorSplitPane.setDividerLocation( (int)((4.0/3.0)*(editorBehaviorSplitPane.getDividerLocation()-36)) );
		smallSceneBehaviorSplitPane.setResizeWeight( 0.0 );

		// make dividers invisible
//		authoringOutputSplitPane.setUI( new edu.cmu.cs.stage3.alice.authoringtool.util.InvisibleSplitPaneUI() );
		leftRightSplitPane.setUI( new edu.cmu.cs.stage3.alice.authoringtool.util.InvisibleSplitPaneUI() );
		worldTreeDragFromSplitPane.setUI( new edu.cmu.cs.stage3.alice.authoringtool.util.InvisibleSplitPaneUI() );
		editorBehaviorSplitPane.setUI( new edu.cmu.cs.stage3.alice.authoringtool.util.InvisibleSplitPaneUI() );
		smallSceneBehaviorSplitPane.setUI( new edu.cmu.cs.stage3.alice.authoringtool.util.InvisibleSplitPaneUI() );

//		authoringOutputSplitPane.setBorder( null );
		leftRightSplitPane.setBorder( null );
		worldTreeDragFromSplitPane.setBorder( null );
		editorBehaviorSplitPane.setBorder( null );
		smallSceneBehaviorSplitPane.setBorder( null );

		// allow dividers to close all/most of the way
		worldTreePanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
		dragFromPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
		editorPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
		smallSceneBehaviorPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
		scenePanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
		behaviorPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
		rightPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );
//		outputPanel.setMinimumSize( new java.awt.Dimension( 0, 0 ) );

		// hook up dragFromComponent to listen for selection changes
		authoringTool.addElementSelectionListener( dragFromComponent );

		// clipboards...
		clipboardPanel.setLayout( new javax.swing.BoxLayout( clipboardPanel, javax.swing.BoxLayout.X_AXIS ) );
		updateClipboards();

		worldTreeComponent.startListening( authoringTool );

		// popup output component when necessary
//		final edu.cmu.cs.stage3.alice.authoringtool.util.StyledStreamTextPane textPane = outputComponent.getTextPane();
//		textPane.getDocument().addDocumentListener(
//			new javax.swing.event.DocumentListener() {
//				public void insertUpdate( javax.swing.event.DocumentEvent ev ) { update(); }
//				public void removeUpdate( javax.swing.event.DocumentEvent ev ) { update(); }
//				public void changedUpdate( javax.swing.event.DocumentEvent ev ) { update(); }
//
//				private long myLastTime = 0;
//				private void update() {
//					javax.swing.SwingUtilities.invokeLater(
//						new Runnable() {
//							public void run() {
//								if( ! textPane.getText().equals( "" ) ) {
//									java.awt.Dimension size = outputComponent.getSize();
//									if( size.height < 32 ) {
//										authoringOutputSplitPane.setDividerLocation( authoringOutputSplitPane.getMaximumDividerLocation() - 128 );
//									}
//								}
//							}
//						}
//					);
//				}
//			}
//		);

		// on screen help
		if( AuthoringToolResources.areExperimentalFeaturesEnabled() ) {
			helpMenu.add( onScreenHelpItem, 1 );
			buttonPanel.add( teachMeButton, new GridBagConstraints( 4, 0, 1, 1, 0.0, 0.0 ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 16, 0, 0), 0, 0 ) );
		}
		this.getContentPane().add(componentImageFactory, BorderLayout.WEST);
	}

	private void recentWorldsInit() {
		recentWorldsListener = new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent ev ) {
				final javax.swing.JMenuItem item = (javax.swing.JMenuItem)ev.getSource();
				if( JAliceFrame.this.authoringTool.loadWorld( item.getText(), true ) == Constants.FAILED ) {
					JAliceFrame.this.recentWorlds.remove( item.getText() );
					JAliceFrame.this.updateRecentWorlds();
				}
//				final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
//					public Object construct() {
//						if( JAliceFrame.this.authoringTool.loadWorld( item.getText(), true ) == Constants.FAILED ) {
//							JAliceFrame.this.recentWorlds.remove( item.getText() );
//							JAliceFrame.this.updateRecentWorlds();
//						}
//						return null;
//					}
//				};
//				worker.start();
			}
		};

		String[] recentWorldsStrings = authoringToolConfig.getValueList( "recentWorlds.worlds" );
		for( int i = 0; i < recentWorldsStrings.length; i++ ) {
			recentWorlds.add( recentWorldsStrings[i] );
		}

		String max = authoringToolConfig.getValue( "recentWorlds.maxWorlds" );
		maxRecentWorlds = Integer.parseInt( max );

		recentWorldsPosition = fileMenu.getMenuComponentCount() - 2;  // assumes (separator, exit) at end of menu
		fileMenu.insertSeparator( recentWorldsPosition++ );

		updateRecentWorlds();
	}

	private void helpInit() {
		/*
		try {
			java.net.URL hsURL = javax.help.HelpSet.findHelpSet( null, hsFile );
			if( hsURL != null ) {
				helpSet = new javax.help.HelpSet( null, hsURL );
				helpBroker = helpSet.createHelpBroker();
			} else {
				AuthoringTool.showErrorDialog( "HelpSet not found: " + hsFile, null );
				authoringTool.getActions().helpAction.setEnabled( false );
			}
		} catch( Throwable t ) {
			AuthoringTool.showErrorDialog( "Error initializing help system.", t );
			authoringTool.getActions().helpAction.setEnabled( false );
		}
		*/
	}

	public void actionInit( Actions actions ) {
		newWorldItem.setAction( actions.newWorldAction );
		openWorldItem.setAction( actions.openWorldAction );
		saveWorldItem.setAction( actions.saveWorldAction );
		saveWorldAsItem.setAction( actions.saveWorldAsAction );
		saveForWebItem.setAction( actions.saveForWebAction );
		addCharacterItem.setAction( actions.addCharacterAction );
		add3DTextItem.setAction( actions.add3DTextAction );
		exportMovieItem.setAction( actions.exportMovieAction );
		importItem.setAction( actions.importObjectAction );
		makeBillboardItem.setAction( actions.makeBillboardAction );
		exitItem.setAction( actions.quitAction );
		preferencesItem.setAction( actions.preferencesAction );
		realToggleItem.setAction( actions.realToggleAction );
		worldInfoItem.setAction( actions.showWorldInfoAction );
		exampleWorldsItem.setAction( actions.openExampleWorldAction );
		aboutItem.setAction( actions.aboutAction );
		onScreenHelpItem.setAction( actions.onScreenHelpAction );
		playButton.setAction( actions.playAction );
		undoButton.setAction( actions.undoAction );
		redoButton.setAction( actions.redoAction );
		addObjectButton.setAction( actions.addCharacterAction );
		teachMeButton.setAction( actions.launchTutorialAction );
		selectTutorialMenuItem.setAction( actions.launchTutorialFileAction );
		showStdOutItem.setAction( actions.showStdOutDialogAction );
		showStdErrItem.setAction( actions.showStdErrDialogAction );
		printItem.setAction( actions.showPrintDialogAction );
	}


	///////////////////
	// public methods
	///////////////////

	public void setWorld( edu.cmu.cs.stage3.alice.core.World world ) {
//		System.out.println("set world: "+world);
		worldTreeComponent.setWorld( world );
		behaviorGroupsEditor.setObject( world );
		sceneEditor.setObject( world );
		tabbedEditorComponent.setWorld( world );
	}

	public void setGuiMode( int guiMode ) {
		this.guiMode = guiMode;
		int dividerLocation1 = leftRightSplitPane.getDividerLocation();
		int dividerLocation2 = smallSceneBehaviorSplitPane.getDividerLocation();
		rightPanel.removeAll();
		scenePanel.removeAll();
		if( guiMode == SCENE_EDITOR_SMALL_MODE ) {
			scenePanel.add( sceneEditor, java.awt.BorderLayout.CENTER );
			rightPanel.add( editorBehaviorSplitPane, BorderLayout.CENTER );
			sceneEditor.setGuiMode( edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.SMALL_MODE );
		} else if( guiMode == SCENE_EDITOR_LARGE_MODE ) {
			rightPanel.add( sceneEditor, java.awt.BorderLayout.CENTER );
			sceneEditor.setGuiMode( edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.LARGE_MODE );
		}
		leftRightSplitPane.setDividerLocation( dividerLocation1 ); // shouldn't have to do this??
		smallSceneBehaviorSplitPane.setDividerLocation( dividerLocation2 ); // shouldn't have to do this??

//		int dividerLocation1 = leftRightSplitPane.getDividerLocation();
//		int dividerLocation2 = smallSceneBehaviorSplitPane.getDividerLocation();
//		if( guiMode == SCENE_EDITOR_SMALL_MODE ) {
//			leftRightSplitPane.setRightComponent( rightPanel );
//			smallSceneBehaviorSplitPane.setLeftComponent( scenePanel );
//			sceneEditor.setGuiMode( edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.SMALL_MODE );
//		} else if( guiMode == SCENE_EDITOR_LARGE_MODE ) {
//			smallSceneBehaviorSplitPane.setLeftComponent( null );
//			leftRightSplitPane.setRightComponent( scenePanel );
//			sceneEditor.setGuiMode( edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.SceneEditor.LARGE_MODE );
//		}
//		leftRightSplitPane.setDividerLocation( dividerLocation1 ); // shouldn't have to do this??
//		smallSceneBehaviorSplitPane.setDividerLocation( dividerLocation2 ); // shouldn't have to do this??
	}

	public int getGuiMode() {
		return guiMode;
	}

	public void showStatusPanel( boolean b ) {
		if( b ) {
			getContentPane().add( statusBar, BorderLayout.SOUTH );
		} else {
			getContentPane().remove( statusBar );
		}
		getContentPane().validate();
		getContentPane().repaint();
	}

	public void registerKeyboardAction( java.awt.event.ActionListener actionListener, String command, javax.swing.KeyStroke keyStroke, int condition ) {
		mainPanel.registerKeyboardAction( actionListener, command, keyStroke, condition );
	}

	public void updateRecentWorlds( String pathname ) {
		int pos = recentWorlds.indexOf( pathname );
		if( pos > -1 ) {
			recentWorlds.remove( pos );
		}
		recentWorlds.add( 0, pathname );
		updateRecentWorlds();
	}

	public void updateRecentWorlds() {
		// trim recentWorlds to max
		int numExtra = recentWorlds.size() - maxRecentWorlds;
		for( int i = 0; i < numExtra; i++ ) {
			recentWorlds.remove( maxRecentWorlds );
		}

		// remove existing menu items
		int recentWorldsEndPosition = fileMenu.getMenuComponentCount() - 2;
		int numEntries = recentWorldsEndPosition - recentWorldsPosition;
		for( int i = 0; i < numEntries; i++ ) {
			fileMenu.remove( recentWorldsPosition );
		}

		// add everything back in
		if( recentWorlds.size() > 0 ) {
			int i;
			java.util.Iterator iter;
			for( i = 0, iter = recentWorlds.iterator(); iter.hasNext(); i++ ) {
				String pathname = (String)iter.next();
				javax.swing.JMenuItem item = new javax.swing.JMenuItem( pathname );
				item.addActionListener( recentWorldsListener );
				fileMenu.insert( item, recentWorldsPosition + i );
			}
		} else {
			javax.swing.JMenuItem item = new javax.swing.JMenuItem( "<No Recent Worlds>" );
			item.setEnabled( false );
			fileMenu.insert( item, recentWorldsPosition );
		}

		// keep the configuration updated
		authoringToolConfig.setValueList( "recentWorlds.worlds", (String[])recentWorlds.toArray( new String[0] ) );
	}

	public void updateClipboards() {
		try {
			int numClipboards = Integer.parseInt( authoringToolConfig.getValue( "numberOfClipboards" ) );
			int current = clipboardPanel.getComponentCount();
			if( numClipboards > current ) {
				for( int i = current; i < numClipboards; i++ ) {
					clipboardPanel.add( new edu.cmu.cs.stage3.alice.authoringtool.util.DnDClipboard() );
				}
			} else if( (numClipboards < current) && (numClipboards >= 0) ) {
				for( int i = numClipboards; i < current; i++ ) {
					clipboardPanel.remove( 0 );
				}
			}
			clipboardPanel.revalidate();
			clipboardPanel.repaint();
		} catch( NumberFormatException e ) {
			AuthoringTool.showErrorDialog( "illegal number of clipboards: " + authoringToolConfig.getValue( "numberOfClipboards" ), null );
		}
	}

	public WorldTreeComponent getWorldTreeComponent() {
		return worldTreeComponent;
	}

	public TabbedEditorComponent getTabbedEditorComponent() {
		return tabbedEditorComponent;
	}

	///////////////////
	// Autogenerated
	///////////////////

	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu();
	JMenuItem newWorldItem = new JMenuItem();
	JMenuItem openWorldItem = new JMenuItem();
	JMenuItem openZippedWorldItem = new JMenuItem();
	JMenuItem saveWorldItem = new JMenuItem();
	JMenuItem saveWorldAsItem = new JMenuItem();
	JMenuItem saveForWebItem = new JMenuItem();
	JMenuItem addCharacterItem = new JMenuItem();
	JMenuItem importItem = new JMenuItem();
	JMenuItem makeBillboardItem = new JMenuItem();
	JMenuItem exitItem = new JMenuItem();
	JMenu editMenu = new JMenu();
	JMenuItem preferencesItem = new JMenuItem();
	JMenuItem realToggleItem = new JMenuItem();
	JMenu helpMenu = new JMenu();
	JMenuItem exampleWorldsItem = new JMenuItem();
	JMenuItem aboutItem = new JMenuItem();
	JMenuItem worldInfoItem = new JMenuItem();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel toolBarPanel = new JPanel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JPanel buttonPanel = new JPanel();
	JButton playButton = new JButton();
	JPanel clipboardPanel = new JPanel();
	JPanel trashPanel = new JPanel();
	JPanel mainPanel = new JPanel();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	BorderLayout borderLayout2 = new BorderLayout();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JButton undoButton = new JButton();
	JButton redoButton = new JButton();
//	JSplitPane authoringOutputSplitPane = new JSplitPane();
	JPanel authoringPanel = new JPanel();
//	JPanel outputPanel = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	BorderLayout borderLayout4 = new BorderLayout();
	JSplitPane leftRightSplitPane = new JSplitPane();
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JPanel scenePanel = new JPanel();
	JSplitPane worldTreeDragFromSplitPane = new JSplitPane();
	JPanel smallSceneBehaviorPanel = new JPanel();
	JSplitPane smallSceneBehaviorSplitPane = new JSplitPane();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel worldTreePanel = new JPanel();
	JPanel dragFromPanel = new JPanel();
	BorderLayout borderLayout6 = new BorderLayout();
	BorderLayout borderLayout7 = new BorderLayout();
	BorderLayout borderLayout8 = new BorderLayout();
	JSplitPane editorBehaviorSplitPane = new JSplitPane();
	JPanel editorPanel = new JPanel();
	JPanel behaviorPanel = new JPanel();
	BorderLayout borderLayout9 = new BorderLayout();
	BorderLayout borderLayout10 = new BorderLayout();
	BorderLayout borderLayout11 = new BorderLayout();
	Component glue;
	JMenuItem onScreenHelpItem = new JMenuItem();
	JMenu toolsMenu = new JMenu();
	BorderLayout borderLayout12 = new BorderLayout();
	BorderLayout borderLayout13 = new BorderLayout();
	Border border1;
	Border border2;
	Border border3;
	Border border4;
	Border border5;
	Border border6;
	Border border7;
	Border border8;
	JButton addObjectButton = new JButton();
	JMenuItem add3DTextItem = new JMenuItem();
	JMenuItem exportMovieItem = new JMenuItem();
	JButton teachMeButton = new JButton();
	JMenuItem selectTutorialMenuItem = new JMenuItem();
	JMenuItem showStdOutItem = new JMenuItem();
	JMenuItem showStdErrItem = new JMenuItem();
	JMenuItem printItem = new JMenuItem();

	private void jbInit() {
		glue = Box.createGlue();
		border1 = BorderFactory.createEmptyBorder(10,10,0,10);
		border2 = BorderFactory.createEmptyBorder(0,10,10,10);
		border3 = BorderFactory.createEmptyBorder(2,10,10,10);
		border4 = BorderFactory.createLineBorder(Color.black,1);
		border5 = BorderFactory.createLineBorder(Color.black,1);
		border6 = BorderFactory.createLineBorder(Color.black,1);
		border7 = BorderFactory.createLineBorder(Color.black,1);
		border8 = BorderFactory.createLineBorder(Color.black,1);
		fileMenu.setMnemonic('F');
		fileMenu.setText("File");
		newWorldItem.setText("New World");
		openWorldItem.setText("Open World");
		openZippedWorldItem.setText("Open Single File World");
		saveWorldItem.setText("Save World");
		saveWorldAsItem.setText("Save World As...");
		saveForWebItem.setText("Export As A Web Page...");
		addCharacterItem.setText("Add Character");
		importItem.setText("Import...");
		makeBillboardItem.setText("Make Billboard");
		exitItem.setText("Exit");
		editMenu.setMnemonic('E');
		editMenu.setText("Edit");
		preferencesItem.setText("Preferences...");
		realToggleItem.setText("Use Real Mode");
		helpMenu.setMnemonic('H');
		helpMenu.setText("Help");
		exampleWorldsItem.setText("Example Worlds...");
		aboutItem.setText("About PREOP");
		this.getContentPane().setLayout(borderLayout1);
		toolBarPanel.setLayout(gridBagLayout1);
		playButton.setText("Play");
		trashPanel.setAlignmentY((float) 0.0);
		trashPanel.setOpaque(false);
		trashPanel.setLayout(borderLayout11);
		clipboardPanel.setLayout(gridBagLayout2);
		mainPanel.setLayout(borderLayout2);
		buttonPanel.setLayout(gridBagLayout4);
		undoButton.setText("Undo");
		redoButton.setText("Redo");
//		authoringOutputSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
//		authoringOutputSplitPane.setDoubleBuffered(true);
//		authoringOutputSplitPane.setOpaque(false);
		authoringPanel.setLayout(borderLayout3);
//		outputPanel.setLayout(borderLayout4);
		leftPanel.setLayout(borderLayout5);
		worldTreeDragFromSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		worldTreeDragFromSplitPane.setOpaque(false);
		rightPanel.setLayout(borderLayout6);
		worldTreePanel.setLayout(borderLayout7);
		dragFromPanel.setLayout(borderLayout8);
		editorBehaviorSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		editorBehaviorSplitPane.setOpaque(false);
		behaviorPanel.setLayout(borderLayout9);
		editorPanel.setLayout(borderLayout10);
		leftRightSplitPane.setDoubleBuffered(true);
		leftRightSplitPane.setOpaque(false);
//		this.getContentPane().setBackground(new Color(255, 230, 180));
		this.setJMenuBar(menuBar);
		onScreenHelpItem.setToolTipText("");
		onScreenHelpItem.setActionCommand("OnScreenHelp");
		onScreenHelpItem.setText("On-Screen Help");
		toolsMenu.setMnemonic('T');
		toolsMenu.setText("Tools");
		worldInfoItem.setActionCommand("worldInfo");
		worldInfoItem.setText("World Statistics...");
		smallSceneBehaviorPanel.setLayout(borderLayout12);
		scenePanel.setLayout(borderLayout13);
		worldTreePanel.setBorder(border4);
		worldTreePanel.setOpaque(false);
		dragFromPanel.setOpaque(false);
		scenePanel.setBorder(border6);
		scenePanel.setOpaque(false);
		behaviorPanel.setBorder(border7);
		behaviorPanel.setOpaque(false);
		editorPanel.setOpaque(false);
		toolBarPanel.setBorder(border3);
		toolBarPanel.setOpaque(false);
		buttonPanel.setOpaque(false);
		clipboardPanel.setOpaque(false);
		mainPanel.setBorder(border2);
		mainPanel.setOpaque(false);
		authoringPanel.setOpaque(false);
		leftPanel.setOpaque(false);
		rightPanel.setOpaque(false);
		smallSceneBehaviorPanel.setOpaque(false);
//		outputPanel.setOpaque(false);
		smallSceneBehaviorSplitPane.setOpaque(false);
		addObjectButton.setText("Add Object");
		add3DTextItem.setText("Add 3D Text");
		exportMovieItem.setText("Export Movie");
		teachMeButton.setText("Teach Me");
		selectTutorialMenuItem.setText("Select a Tutorial");
		showStdOutItem.setText("Text Output...");
		showStdErrItem.setText("Standard Error...");
		printItem.setText("Print...");
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);
		fileMenu.add(newWorldItem);
		fileMenu.add(openWorldItem);
		fileMenu.add(openZippedWorldItem);
		fileMenu.add(saveWorldItem);
		fileMenu.add(saveWorldAsItem);
		fileMenu.add(saveForWebItem);
		fileMenu.add(exportMovieItem);
		fileMenu.add(printItem);
//		fileMenu.add(addCharacterItem);
		fileMenu.add(importItem);
		fileMenu.add(add3DTextItem);
		fileMenu.add(makeBillboardItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		editMenu.add(preferencesItem);
		editMenu.add(realToggleItem);
		toolsMenu.add(worldInfoItem);
		toolsMenu.add(showStdOutItem);
		toolsMenu.add(showStdErrItem);
		helpMenu.add(exampleWorldsItem);
		helpMenu.add(selectTutorialMenuItem);
		helpMenu.add(aboutItem);
		this.getContentPane().add(toolBarPanel, BorderLayout.NORTH);
		toolBarPanel.add(buttonPanel,   new GridBagConstraints(0, 0, 1, 2, 0.0, 0.0
			,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonPanel.add(playButton,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonPanel.add(undoButton,        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 16, 0, 0), 0, 0));
		buttonPanel.add(redoButton,       new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
//		buttonPanel.add(addObjectButton,       new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
//			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 16, 0, 0), 0, 0));
//		buttonPanel.add(teachMeButton,    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
//			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 16, 0, 0), 0, 0));
		toolBarPanel.add(clipboardPanel,   new GridBagConstraints(4, 0, 1, 2, 0.0, 0.0
			,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		toolBarPanel.add(trashPanel,     new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0
			,GridBagConstraints.SOUTHWEST, GridBagConstraints.VERTICAL, new Insets(0, 14, 0, 0), 0, 0));
		toolBarPanel.add(glue,   new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(authoringPanel, BorderLayout.CENTER);
//		mainPanel.add(authoringOutputSplitPane, BorderLayout.CENTER);
//		authoringOutputSplitPane.add(authoringPanel, JSplitPane.TOP);
		authoringPanel.add(leftRightSplitPane, BorderLayout.CENTER);
		leftRightSplitPane.add(leftPanel, JSplitPane.LEFT);
		leftPanel.add(worldTreeDragFromSplitPane, BorderLayout.CENTER);
		worldTreeDragFromSplitPane.add(worldTreePanel, JSplitPane.TOP);
		worldTreeDragFromSplitPane.add(dragFromPanel, JSplitPane.BOTTOM);
		leftRightSplitPane.add(rightPanel, JSplitPane.RIGHT);
		rightPanel.add(editorBehaviorSplitPane, BorderLayout.CENTER);
		editorBehaviorSplitPane.add(smallSceneBehaviorPanel, JSplitPane.TOP);
		editorBehaviorSplitPane.add(editorPanel, JSplitPane.BOTTOM);
		smallSceneBehaviorPanel.add( smallSceneBehaviorSplitPane, BorderLayout.CENTER );
		smallSceneBehaviorSplitPane.add( scenePanel, JSplitPane.LEFT );
		smallSceneBehaviorSplitPane.add( behaviorPanel, JSplitPane.RIGHT );
//		authoringOutputSplitPane.add(outputPanel, JSplitPane.BOTTOM);
//		authoringOutputSplitPane.setDividerLocation(450);
		worldTreeDragFromSplitPane.setDividerLocation(120);
		editorBehaviorSplitPane.setDividerLocation(150);
		leftRightSplitPane.setDividerLocation(100);
		smallSceneBehaviorSplitPane.setDividerLocation(100);
		//this.setTitle("PREOP-Providing robotic experiences through object-based programming");
	}
}
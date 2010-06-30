package edu.cmu.cs.stage3.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import edu.cmu.cs.stage3.alice.core.Note;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NotePicker extends edu.cmu.cs.stage3.swing.ContentPane{

	javax.swing.JSlider noteLetter = new javax.swing.JSlider(javax.swing.JSlider.VERTICAL, 1, 12 , 1);
	javax.swing.JSlider octave = new javax.swing.JSlider(javax.swing.JSlider.VERTICAL, 2, 8, 5 );
	javax.swing.JSlider beatsPerMin = new javax.swing.JSlider(javax.swing.JSlider.HORIZONTAL, 60, 240, bpm );
	javax.swing.JSlider numBeats = new javax.swing.JSlider(javax.swing.JSlider.HORIZONTAL, 1, 6, 4 );
	javax.swing.JSlider numDots = new javax.swing.JSlider(javax.swing.JSlider.HORIZONTAL, 0, 3, 0 );
	javax.swing.JTextField totalTimeLabel = new javax.swing.JTextField();
	javax.swing.JPanel pitchPanel = new javax.swing.JPanel();
	javax.swing.JPanel bpmLabelPanel = new javax.swing.JPanel();
	javax.swing.JPanel beatsLabelPanel = new javax.swing.JPanel();
	javax.swing.JPanel dotsLabelPanel = new javax.swing.JPanel();
	javax.swing.JPanel durationPanel = new javax.swing.JPanel();
	javax.swing.JPanel noteLabelPanel = new javax.swing.JPanel();
	javax.swing.JPanel octaveLabelPanel = new javax.swing.JPanel();
	javax.swing.JPanel anotherPanel = new javax.swing.JPanel();
	javax.swing.JButton okayButton = new javax.swing.JButton("OK");
	javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");
	
	private static int bpm = 120;

	public static void main(String args[]){
		NotePicker a = new NotePicker();
		javax.swing.JFrame frame = new javax.swing.JFrame(a.getTitle());
		frame.setContentPane(a);
		frame.pack();
		frame.setVisible(true);
	}
	public double getDuration(){
		double timePerBeat = 1/(((double)beatsPerMin.getValue())/60);
		double numberOfBeats = 0;
		switch(numBeats.getValue()){
			case 1:
				numberOfBeats = 0.125;
				break;
			case 2:
				numberOfBeats = 0.25;
				break;
			case 3:
				numberOfBeats = .5;
				break;
			case 4:
				numberOfBeats = 1;
				break;
			case 5:
				numberOfBeats = 2;
				break;
			case 6:
				numberOfBeats = 4;
				break;
		}
		double duration = (timePerBeat*numberOfBeats);
		switch(numDots.getValue()){
			case 0:
				break;
			case 1:
				duration += .5*duration;
				break;
			case 2:
				duration += .5*duration + .25*duration;
				break;
			case 3:
				duration += .5*duration + .25*duration + .125;
				break;
		}
		return duration;
	}
	public NotePicker(){
		ChangeListener calculateTotalTime = new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				bpm = beatsPerMin.getValue();
				totalTimeLabel.setText(String.valueOf(getDuration()));
			}
		};
		beatsPerMin.addChangeListener(calculateTotalTime);
		numBeats.addChangeListener(calculateTotalTime);
		numDots.addChangeListener(calculateTotalTime);
		totalTimeLabel.setText(String.valueOf(getDuration()));
		
		pitchPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Pitch"),javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
		durationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Duration"),javax.swing.BorderFactory.createEmptyBorder(5,5,5,5)));
		
		javax.swing.border.TitledBorder title = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(),"Note");
		noteLabelPanel.setBorder(title);
		javax.swing.border.TitledBorder title2 = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(),"Octave");
		octaveLabelPanel.setBorder(title2);
		javax.swing.border.TitledBorder title3 = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(),"Beats per minute");
		bpmLabelPanel.setBorder(title3);
		javax.swing.border.TitledBorder title4 = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(),"Length of note (in beats)");
		beatsLabelPanel.setBorder(title4);
		javax.swing.border.TitledBorder title5 = javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(),"Plus");
		dotsLabelPanel.setBorder(title5);
		
		noteLetter.setMajorTickSpacing(1);
		noteLetter.setPaintTicks(true);
		noteLetter.setSnapToTicks(true);
		
		octave.setMajorTickSpacing(1);
		octave.setPaintLabels(true);
		octave.setPaintTicks(true);
		octave.setSnapToTicks(true);
//Create the label table
		java.util.Hashtable labelTable = new java.util.Hashtable();
		labelTable.put( new Integer( 1 ), new javax.swing.JLabel("C"));
		labelTable.put( new Integer( 2 ), new javax.swing.JLabel("C#"));
		labelTable.put( new Integer( 3 ), new javax.swing.JLabel("D"));
		labelTable.put( new Integer( 4 ), new javax.swing.JLabel("D#"));
		labelTable.put( new Integer( 5 ), new javax.swing.JLabel("E"));
		labelTable.put( new Integer( 6 ), new javax.swing.JLabel("F"));
		labelTable.put( new Integer( 7 ), new javax.swing.JLabel("F#"));
		labelTable.put( new Integer( 8 ), new javax.swing.JLabel("G"));
		labelTable.put( new Integer( 9 ), new javax.swing.JLabel("G#"));
		labelTable.put( new Integer( 10 ), new javax.swing.JLabel("A"));
		labelTable.put( new Integer( 11 ), new javax.swing.JLabel("A#"));
		labelTable.put( new Integer( 12 ), new javax.swing.JLabel("B"));
		noteLetter.setLabelTable( labelTable );

		noteLetter.setPaintLabels(true);
		noteLabelPanel.add(noteLetter);
		pitchPanel.add(noteLabelPanel);
		octaveLabelPanel.add(octave);
		pitchPanel.add(octaveLabelPanel);
		add(pitchPanel);
		
		beatsPerMin.setMajorTickSpacing(30);
		beatsPerMin.setPaintTicks(true);
		beatsPerMin.setPaintLabels(true);
		
		numBeats.setMajorTickSpacing(1);
		numBeats.setPaintTicks(true);
		numBeats.setSnapToTicks(true);
		numBeats.setPaintLabels(true);
		java.util.Hashtable labelTable2 = new java.util.Hashtable();
		labelTable2.put( new Integer( 1 ), new javax.swing.JLabel("1/8"));
		labelTable2.put( new Integer( 2 ), new javax.swing.JLabel("1/4"));
		labelTable2.put( new Integer( 3 ), new javax.swing.JLabel("1/2"));
		labelTable2.put( new Integer( 4 ), new javax.swing.JLabel("1"));
		labelTable2.put( new Integer( 5 ), new javax.swing.JLabel("2"));
		labelTable2.put( new Integer( 6 ), new javax.swing.JLabel("4"));
		numBeats.setLabelTable( labelTable2 );
		
		numDots.setMajorTickSpacing(1);
		numDots.setPaintTicks(true);
		numDots.setSnapToTicks(true);
		numDots.setPaintLabels(true);
		java.util.Hashtable labelTable3 = new java.util.Hashtable();
		labelTable3.put( new Integer( 0 ), new javax.swing.JLabel("0"));
		labelTable3.put( new Integer( 1 ), new javax.swing.JLabel("1/2"));
		labelTable3.put( new Integer( 2 ), new javax.swing.JLabel("3/4"));
		labelTable3.put( new Integer( 3 ), new javax.swing.JLabel("7/8"));
		numDots.setLabelTable( labelTable3 );
		
		durationPanel.setLayout( new javax.swing.BoxLayout(durationPanel , javax.swing.BoxLayout.Y_AXIS));
		bpmLabelPanel.add(beatsPerMin);
		durationPanel.add(bpmLabelPanel);
		beatsLabelPanel.add(numBeats);
		dotsLabelPanel.add(numDots);
		durationPanel.add(beatsLabelPanel);
		durationPanel.add(dotsLabelPanel);
		durationPanel.add(totalTimeLabel);
		anotherPanel.setLayout( new javax.swing.BoxLayout(anotherPanel , javax.swing.BoxLayout.Y_AXIS));
		anotherPanel.add(durationPanel);
		javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
		buttonsPanel.add(okayButton);
		buttonsPanel.add(cancelButton);
		anotherPanel.add(buttonsPanel);
		add(anotherPanel);
		
	}
	public Note getSelectedNote(){
		return new Note((octave.getValue()+1)*12+noteLetter.getValue()-1,Double.parseDouble((totalTimeLabel.getText())));
	}
	public String getTitle() {
		return "Create note";
	}
	public void addOKActionListener(java.awt.event.ActionListener l) {
		okayButton.addActionListener(l);
	}
	public void removeOKActionListener(java.awt.event.ActionListener l) {
		okayButton.removeActionListener(l);
	}
	public void addCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.addActionListener(l);
	}
	public void removeCancelActionListener(java.awt.event.ActionListener l) {
		cancelButton.removeActionListener(l);
	}


}


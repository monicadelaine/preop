package edu.cmu.cs.stage3.alice.core;

public class Note {
	protected int midi;
	protected double duration;
	public Note(int midi, double duration){
		this.midi = midi;
		this.duration = duration;
	}
	public Note(){
		midi = 127;
		duration = 1;
	}
	public int getMidi(){
		return midi;
	}
	public double getDuration(){
		return duration;
	}
	public String toString(){
		int quotient = midi/12;
		int remainder = midi-quotient*12;
		
		String noteLetter = "";
		switch(remainder){
			case 0:
				noteLetter = "C";
				break;
			case 1:
				noteLetter = "C#";
				break;
			case 2:
				noteLetter = "D";
				break;
			case 3:
				noteLetter = "D#";
				break;
			case 4:
				noteLetter = "E";
				break;
			case 5:
				noteLetter = "F";
				break;
			case 6:
				noteLetter = "F#";
				break;
			case 7:
				noteLetter = "G";
				break;
			case 8:
				noteLetter = "G#";
				break;
			case 9:
				noteLetter = "A";
				break;
			case 10:
				noteLetter = "A#";
				break;
			case 11:
				noteLetter = "B";
				break;
		}
		noteLetter+=String.valueOf(quotient-1);
		
		return new String(noteLetter + " for " + String.valueOf(Math.round(duration*100)/100.00) + " s");
	}
	
	public static Note valueOf(String noteString){
		String pitchPart = noteString.substring(0, noteString.indexOf(" for"));
		int octave = Integer.valueOf(pitchPart.substring(pitchPart.length()-1)).intValue();
		String noteLetter = "";
		if(pitchPart.length()==2)
			noteLetter = pitchPart.substring(0, 1);
		if(pitchPart.length()==3)
			noteLetter = pitchPart.substring(0, 2);
		
		int midi = 0;
		if(noteLetter.equals("C"))
			midi = 0;
		else if(noteLetter.equals("C#"))
			midi = 1;
		else if(noteLetter.equals("D"))
			midi = 2;
		else if(noteLetter.equals("D#"))
			midi = 3;
		else if(noteLetter.equals("E"))
			midi = 4;
		else if(noteLetter.equals("F"))
			midi = 5;
		else if(noteLetter.equals("F#"))
			midi = 6;
		else if(noteLetter.equals("G"))
			midi = 7;
		else if(noteLetter.equals("G#"))
			midi = 8;
		else if(noteLetter.equals("A"))
			midi = 9;
		else if(noteLetter.equals("A#"))
			midi = 10;
		else if(noteLetter.equals("B"))
			midi = 11;
		
		midi += (octave+1)*12;
		
		double duration = Double.valueOf(noteString.substring(noteString.indexOf(" for")+5, noteString.indexOf(" s"))).doubleValue();
		
		return new Note(midi,duration);
	}
}
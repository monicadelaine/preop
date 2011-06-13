package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.property.RobotProperty;
import edu.cmu.cs.stage3.alice.core.property.ListProperty;
import edu.cmu.cs.stage3.alice.core.Note;

public class PlaySong extends Response{
	public static void main(String args[]){
//		PlaySong n = new PlaySong();
//		Runnable r = n.new playNote(new Note(72,1));
//		Thread thread = new Thread(r);
//		thread.start();
//		PlaySong n = new PlaySong();
//		playNote p = n.new playNote(new Note());
//		p.play(new Note(72,1));
//		p.close();
	}
	public final RobotProperty subject = new RobotProperty( this, "subject", null );
	public final ListProperty note = new ListProperty( this, "list", null );
	
	public PlaySong(){
		super();
		this.duration.deprecate();
	}
	public class RuntimePlayNote extends RuntimeResponse {
		private int index = 0;
		private double timeUntilNextNote = 0;
		private playNote player = new playNote();
		
		public void prologue( double t ) {
			super.prologue( t );
			double totalDuration = 0;
//			for(int i=0; i<note.getListValue().size(); i++)
//				if(note.getListValue().itemAtIndex(i) instanceof Note)
//					totalDuration += ((Note)note.getListValue().itemAtIndex(i)).getDuration();
//			setDuration(totalDuration);
//			PlaySong.this.duration.set(Double.valueOf(String.valueOf(totalDuration)));
			if(((Create)subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning())))
				for(int i=0; i<note.getListValue().size(); i++)
					if(note.getListValue().itemAtIndex(i) instanceof Note)
							((Create)subject.getValue()).playNote((Note)note.getListValue().itemAtIndex(i));
		}
		public void update( double t){
			super.update(t);
//			if( timeUntilNextNote<=0 ){
//				if(note.getListValue().itemAtIndex(index) instanceof Note){
//					Note currentNote = (Note) note.getListValue().itemAtIndex(index);
//					timeUntilNextNote = currentNote.getDuration();
//					player.play(currentNote);
//				}
//				index++;
//			}
//			timeUntilNextNote -= getDT();
		}
		public void epilogue( double t ){
			super.epilogue(t);
//			player.close();
		}
	}
	public class playNote implements Runnable{
		private int midi;
		private double duration;
		private AudioFormat af;
		private DataLine.Info info;
		private SourceDataLine source;
		public playNote(){
			try{
				af = new AudioFormat((float)8000, 8, 1, true, true);
				info = new DataLine.Info (SourceDataLine.class, af);
				source = (SourceDataLine)AudioSystem.getLine(info);
				source.open(af);
			}catch(Exception e){}
			source.start();
		}
		public void play(Note note){
			this.midi = note.getMidi();
			this.duration = note.getDuration();
			Thread thread = new Thread(this);
			thread.start();
		}
		public void close(){
			source.stop();
			source.close();
		}
		public void run(){
			try {
				byte[] buf = new byte[(int)(8000*duration)];
				double preCalc =  2.0 * Math.PI * 440*Math.pow(2,((midi-69.0)/12.0)) / 8000;
				for ( int i=0; i<buf.length; i++ )
					buf[i] = (byte)(Math.sin(preCalc*i)*127.0);

				source.write( buf, 0, buf.length );
				//source.drain();
			}catch ( Exception e ){}
		}
		
	}
}

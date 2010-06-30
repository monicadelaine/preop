package edu.cmu.cs.stage3.alice.core.response.irobot.create;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import edu.cmu.cs.stage3.alice.core.Response;
import edu.cmu.cs.stage3.alice.core.irobot.Create;
import edu.cmu.cs.stage3.alice.core.property.CreateProperty;
import edu.cmu.cs.stage3.alice.core.property.NoteProperty;
import edu.cmu.cs.stage3.alice.core.Note;


public class PlayNote extends Response{
	public final CreateProperty subject = new CreateProperty( this, "subject", null );
	public final NoteProperty note = new NoteProperty( this, "note", null );

	public PlayNote(){
		super();
		this.duration.deprecate();
	}
	public class RuntimePlayNote extends RuntimeResponse {
		public void prologue( double t ) {
			super.prologue( t );
			if(((Create)subject.getValue()).isConnected() && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.realPlay || !(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld().isRunning())))
				((Create)subject.getValue()).playNote((Note)note.getValue());
//			setDuration(((Note)note.getValue()).getDuration());
//			Runnable r = new playNote((Note)note.getValue());
//			Thread thread = new Thread(r);
//			thread.start();
		}
		public void update(double t){
			super.update(t);
		}
	}
	public class playNote implements Runnable{
		private int midi;
		private double duration;

		public playNote(Note note){
			this.midi = note.getMidi();
			this.duration = note.getDuration();
		}

		public void run(){
			try {
				AudioFormat af = new AudioFormat((float)8000, 8, 1, true, true);
				DataLine.Info info = new DataLine.Info (SourceDataLine.class, af);
				SourceDataLine source = (SourceDataLine)AudioSystem.getLine(info);
				source.open(af);
				source.start();

				byte[] buf = new byte[(int)(8000*duration)];
				double preCalc =  2.0 * Math.PI * 440*Math.pow(2,((midi-69.0)/12.0)) / 8000;
				for ( int i=0; i<buf.length; i++ )
					buf[i] = (byte)(Math.sin(preCalc*i)*127.0);

				source.write( buf, 0, buf.length );
				source.drain();
				source.stop();
				source.close();
			}catch ( Exception e ){}
		}
	}

}

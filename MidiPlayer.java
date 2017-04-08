import javax.sound.midi.*;
import java.io.File;
import java.io.File;
import java.io.IOException;

class MidiPlayer
{
	public MidiPlayer(String fileName)
	{
		try
		{
			sequence= MidiSystem.getSequence(new File(fileName) );
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			Synthesizer sintetizador= MidiSystem.getSynthesizer();
			sintetizador.open();
			sequencer.getTransmitter().setReceiver(sintetizador.getReceiver() );
			sequencer.start();
		}
		catch (InvalidMidiDataException a)
		{
			System.out.println("Could not load midi file\n");
		}
		catch (MidiUnavailableException a)
		{
			System.out.println("Could not get sequencer\n");
		}
/*		catch (InvalidMidiDataException a)
		{
			System.out.println("Could not get sequence\n");
		}*/
		catch(IOException a)
		{
			System.out.println("Could not load file\n");
		}
	}
	public static void main(String args[])
	{
		MidiPlayer midiPlayer= new MidiPlayer(args[0]);
	}
	private Sequence sequence;
	private Sequencer sequencer;
}

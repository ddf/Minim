package ddf.minim;

import java.util.ArrayList;
import java.util.Iterator;

import ddf.minim.ugens.Instrument;

public class NoteManager
{
	// we use this do our timing, basically
	private AudioOutput out;
	private ArrayList<NoteEvent> events;
	
	private class NoteEvent
	{
	
		Instrument instrument;
		int samplesUntilNoteOn;
		int samplesUntilNoteOff;
		
		NoteEvent(Instrument i, int son, int soff)
		{
			instrument = i;
			samplesUntilNoteOn = son;
			samplesUntilNoteOff = soff;
		}
	}
	
	NoteManager(AudioOutput parent)
	{
		out = parent;
		events = new ArrayList<NoteEvent>();
	}
	
	synchronized void addEvent(float startTime, float duration, Instrument instrument)
	{
		int son = (int)(out.sampleRate() * startTime);
		int soff = (int)(out.sampleRate() * duration);
		events.add( new NoteEvent(instrument, son, soff) );
	}
	
	synchronized public void tick()
	{
		Iterator<NoteEvent> iter = events.iterator();
		while ( iter.hasNext() )
		{
			NoteEvent event = iter.next();
			if ( event.samplesUntilNoteOn == 0 )
			{
				event.instrument.noteOn();
			}
			if ( event.samplesUntilNoteOff == 0 )
			{
				event.instrument.noteOff();
				iter.remove();
			}
			if ( event.samplesUntilNoteOn < 0 )
			{
				event.samplesUntilNoteOff--;
			}
			
			event.samplesUntilNoteOn--;
		}
	}
}

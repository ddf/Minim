package ddf.minim;

import java.util.ArrayList;
import java.util.Iterator;

import ddf.minim.ugens.Instrument;

public class NoteManager
{
	// we use this do our timing, basically
	private AudioOutput out;
	private ArrayList<NoteEvent> events;
	private float tempo;
	private float noteOffset;
	
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
		tempo = 60f;
		noteOffset = 0.0f;
	}
	
	synchronized void addEvent(float startTime, float duration, Instrument instrument)
	{
		int son = (int)(out.sampleRate() * ( startTime + noteOffset ) * 60f/tempo);
		int soff = (int)(out.sampleRate() * duration * 60f/tempo);
		events.add( new NoteEvent(instrument, son, soff) );
	}
	public void setTempo(float tempo)
	{
		this.tempo = tempo;
	}
	public void setNoteOffset(float noteOffset)
	{
		this.noteOffset = noteOffset;
	}
	
	synchronized public void tick()
	{
		Iterator<NoteEvent> iter = events.iterator();
		while ( iter.hasNext() )
		{
			NoteEvent event = iter.next();
			if ( event.samplesUntilNoteOn > -1 )
			{
				event.samplesUntilNoteOn--;
				if ( event.samplesUntilNoteOn == -1 )
				{
					event.instrument.noteOn( (float)event.samplesUntilNoteOff/out.sampleRate() );
				}
			}
			else
			{
				event.samplesUntilNoteOff--;
				if ( event.samplesUntilNoteOff == -1 )
				{
					event.instrument.noteOff();
					iter.remove();
				}
			}
		}
	}
}

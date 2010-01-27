package ddf.minim.ugens;

import ddf.minim.Minim;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Iterator;

public class Frequency
{
	static float HZA4=440.0f;
	static float MIDIA4=69.0f;
	static float MIDIOCTAVE=12.0f;
	
	private static HashMap< String, Integer > noteNameOffsets = initializeNoteNameOffsets();
	private static HashMap< String, Integer > initializeNoteNameOffsets()
	{
		HashMap< String, Integer > initNNO = new HashMap< String, Integer >();
		initNNO.put( "A", new Integer( 9 ) );
		initNNO.put( "B", new Integer( 11 ) );
		initNNO.put( "C", new Integer( 0 ) );
		initNNO.put( "D", new Integer( 2 ) );
		initNNO.put( "E", new Integer( 4 ) );
		initNNO.put( "F", new Integer( 5 ) );
		initNNO.put( "G", new Integer( 7 ) );
		initNNO.put( "La", new Integer( 9 ) );
		initNNO.put( "Si", new Integer( 11 ) );
		initNNO.put( "Do", new Integer( 0 ) );
		initNNO.put( "Re", new Integer( 2 ) );
		initNNO.put( "Mi", new Integer( 4 ) );
		initNNO.put( "Fa", new Integer( 5 ) );
		initNNO.put( "Sol", new Integer( 7 ) );
		return initNNO;
	}


	float freq;
	
	Frequency(float hz)
	{
		freq = hz;
	}

	public float asHz()
	{
		return freq;
	}
	
	public float asMidiNote()
	{
		float midiNote = MIDIA4 + MIDIOCTAVE*(float)Math.log( freq/HZA4 )/(float)Math.log( 2.0 );
		return midiNote;
	}
	
	public String asPitch()
	{
		// TODO convert to something like "A4"
		return "";
	}
	
	void useFrequency(UGen freqGen)
	{
		
	}
	
	public static Frequency ofHertz(float hz)
	{
		return new Frequency(hz);
	}
	
	public static Frequency ofMidiNote( float midiNote )
	{
		float hz = HZA4*(float)Math.pow( 2.0, ( midiNote - MIDIA4 )/MIDIOCTAVE );
		return new Frequency(hz);
	}
	
	public static Frequency ofPitch(String pitchName)
	{
		// TODO return a frequency object that has the frequency of pitchName
		float midiNote = 0.0f;
		pitchName.trim();
	
		// check to see if this is a note
		//String noteName = "([A-G]|(Do|Re|Mi|Fa|Sol|La_|Si))";
		String noteName = "(";
		Iterator<String> iterator = noteNameOffsets.keySet().iterator();
		while( iterator.hasNext() )
		{
			noteName += iterator.next() + "|";
			//System.out.println( iterator.next() );
		}
		noteName = noteName.substring( 0, noteName.length() - 1 );
		noteName += ")";
		Minim.debug( "noteName = " + noteName );
		String noteNaturalness = "[#b]";
		String noteOctave = "(-1|10|[0-9])";
		String pitchRegex = "^" + noteName + "?" + noteNaturalness +"*" + noteOctave +"?$";
			// "^([A-G]|(Do|Re|Mi|Fa|Sol|La_|Si))?[#b]*(-1|10|[0-9])?$";
		if ( pitchName.matches( pitchRegex ) )
		{
			Minim.debug(pitchName + " matches.");

			// get octave0
			Pattern pattern = Pattern.compile( noteOctave );
			Matcher matcher = pattern.matcher( pitchName );
			
			String s;
			float f;
			if ( matcher.find() )
			{
				s = pitchName.substring(matcher.start(), matcher.end() );
				f = Float.valueOf(s.trim()).floatValue();
			} else
			{
				f = 4.0f;
			}
			midiNote = f*12.0f + 12.0f;
			Minim.debug("midiNote based on octave = " + midiNote );

			// get naturalness			
			pattern = Pattern.compile( noteNaturalness );
			matcher = pattern.matcher( pitchName );
			
			while( matcher.find() )
			{
				
				s = pitchName.substring(matcher.start(), matcher.end() );
				if ( s.equals("#") )
				{
					midiNote += 1.0f;
				} else
				{
					midiNote -= 1.0f;
				}
			}
			Minim.debug("midiNote based on naturalness = " + midiNote );
	
			// get note
			pattern = Pattern.compile( noteName );
			matcher = pattern.matcher( pitchName );
			
			if ( matcher.find() )
			{	
				s = pitchName.substring(matcher.start(), matcher.end() );
				float noteOffset = (float) noteNameOffsets.get( s );
				midiNote += noteOffset;
			}
			Minim.debug("midiNote based on noteName = " + midiNote );
			return new Frequency( ofMidiNote( midiNote ).asHz() );
					
		} else
		{
			Minim.debug(pitchName + " DOES NOT MATCH.");			
			return new Frequency( 0.0f );
		}
	}
}

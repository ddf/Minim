package ddf.minim.ugens;

/**
 * A library of waveforms.
 * 
 *  @author Nicolas Brix, Anderson Mills
 */
public class Waves 
{
	/**
	 * standard size for a Wavetabel from Waves
	 */
	private static int tableSize = 8192;
	private static int tSby2 = tableSize/2;
	private static int tSby4 = tableSize/4;

	// Perfect waveforms
	/** 
	 * A pure sine wave.
	 */
	public final static Wavetable SINE = 
		WavetableGenerator.gen10(tableSize, new float[] { 1 });
	/**
	 * A perfect sawtooth wave.
	 */	
	public final static Wavetable SAW = 
		WavetableGenerator.gen7(tableSize, new float[] {0,-1,1,0}, new int[] {tSby2,0,tableSize - tSby2});
	/**
	 * A perfect phasor wave going from 0 to 1.
	 */	
	public final static Wavetable PHASOR = 
		WavetableGenerator.gen7(tableSize, new float[] {0,1}, new int[] {tableSize});
	/**
	 * A perfect square wave with a 50% duty cycle.
	 */
	public final static Wavetable SQUARE = 
		WavetableGenerator.gen7(tableSize, new float[] {-1,-1,1,1}, new int[] {tSby2,0,tableSize - tSby2});
	 /**
	  * A perfect triangle wave.
	  */

	public final static Wavetable TRIANGLE = 
		WavetableGenerator.gen7(tableSize, new float[] {0,1,-1,0}, new int[] {tSby4,tSby2,tableSize - tSby2 - tSby4});
	/**
	 * A perfect square wave with a 25% duty cycle.
	 */
	public final static Wavetable QUARTERPULSE = 
		WavetableGenerator.gen7(tableSize, new float[] {-1,-1,1,1}, new int[] {tSby4,0, tableSize - tSby4});
	
	/**
	 * Builds a sawtooth wave from the first numberofHarms harmonics. 
	 * @param numberOfHarms
	 * @return Wavetable
	 */
	public static Wavetable saw(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms];
		for(int i=0;i<numberOfHarms;i++)
		{
			content[i]=(float) ((-2)/((i+1)*Math.PI)*Math.pow(-1,i+1));
		}
		return WavetableGenerator.gen10(tableSize, content);
	}

	/**
	 * Builds a square wave from the first numberofHarms harmonics. 
	 * @param numberOfHarms
	 * @return Wavetable
	 */
	public static Wavetable square(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]= (float)1 /(i+1);
			content[i+1]=0;
		}
		return WavetableGenerator.gen10(tableSize, content);
	}
	
	/**
	 * Builds a triangle wave from the first numberofHarms harmonics. 
	 * @param numberOfHarms
	 * @return Wavetable
	 */
	public static Wavetable triangle(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]= (float)(Math.pow(-1,i/2)*8/Math.PI/Math.PI/Math.pow(i+1,2));
			content[i+1]=0;
		}
		return WavetableGenerator.gen10(tableSize, content);
	}
	
	/**	
	 * Constructs a square wave with specficed duty cycle.
	 * @param dutyCycle
	 * @return Wavetable
	 */
	public static Wavetable pulse(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		return WavetableGenerator.gen7(tableSize, new float[] {1,1,-1,-1}, 
				new int[] {(int)(dutyCycle*tableSize),0,tableSize-(int)(dutyCycle*tableSize)});
	}
	
	/**	
	 * Constructs a triangle wave with specficed duty cycle.
	 * @param dutyCycle
	 * @return Wavetable
	 */
	public static Wavetable triangle(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		int a=(int)(tableSize*dutyCycle*0.5);
		return WavetableGenerator.gen7(tableSize, new float[] {0,1,0,-1,0}, 
				new int[] {a,a,tSby2-a,tableSize-tSby2-a});
	}
	
	/**	
	 * Constructs a sawtooth wave with specficed duty cycle.
	 * @param dutyCycle
	 * @return Wavetable
	 */
	public static Wavetable saw(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		int a=(int)(tableSize*dutyCycle);
		return WavetableGenerator.gen7(tableSize, new float[] {1,0,-1}, new int[] {a,tableSize-a});
	}

	/**	
	 * Constructs a square wave with specficed duty cycle.
	 * @param dutyCycle
	 * @return Wavetable
	 */
	public static Wavetable square(float dutyCycle)
	{//same as pulse
		return pulse(dutyCycle);
	}
	
	//TODO a dutycycled sine wavetable : i think a new warp() method in Wavetable would be the best

	/**
	 * Constructs a wave from the first numberofHarms harmonics given random amplitudes.
	 * @param numberOfHarms
	 * @return Wavetable
	 */
	public static Wavetable randomNHarms(int numberOfHarms)
	{
		float[] harmAmps = new float[numberOfHarms];
		for(int i=0;i<numberOfHarms;i++)
		{
			harmAmps[i]=(float)Math.random()*2 - 1;
		}
		Wavetable builtWave=WavetableGenerator.gen10(tableSize, harmAmps);
		builtWave.normalize();
		return builtWave;
	}
	
	/**
	 * Constructs a wave from the numberOfHarms even harmonics given random amplitudes.
	 * @param numberOfHarms
	 * @return Wavetable
	 */
	public static Wavetable randomNOddHarms(int numberOfHarms)
	{
		float[] harmAmps = new float[numberOfHarms*2];
		for(int i=0;i<numberOfHarms;i+=1)
		{
			harmAmps[i*2]=(float)Math.random()*2 - 1;
			harmAmps[i*2+1]=0.0f;
		}
		Wavetable builtWave=WavetableGenerator.gen10(tableSize, harmAmps);
		builtWave.normalize();
		return builtWave;
	}
	
	/**
	 * Constructs a wavetable of noise
	 * @return Wavetable
	 */
	public static Wavetable randomNoise()
	{
		float[] builtArray = new float[tableSize];
		for(int i=0;i<builtArray.length;i++)
		{
			builtArray[i]=(float)Math.random()*2 - 1;
		}
		Wavetable builtWave = new Wavetable(builtArray);
		builtWave.normalize();
		return builtWave;
	}
	
	// TODO rewrite RandomPulses to be more comprehensible
	//random impulses, proba being proportional to the number of impulses
	//values for proba : 1 to 100
	/*
	public static Wavetable RandomPulses(float proba)
	{
		float[] a = new float[tableSize];
		if(proba<1 || proba > 100) proba=50;//could be changed for more freedom
		proba = 1 - proba/10000;
		for(int i=0;i<a.length;i++)
		{
			if(Math.random()>proba) a[i]=(float)Math.random()*2-1;
		}
		Wavetable rand = new Wavetable(a);
		rand.normalize();
		return rand;
	}
	*/
	
	/**
	 * Adds any number of Wavetables, each with their own amplitude
	 * @param amps
	 * @param waves
	 * @return Wavetable
	 */
	public static Wavetable add( float[] amps, Wavetable ... waves )
	{
		if( amps.length != waves.length ) 
		{
			System.out.println("add() : amplitude array size must match the number of waveforms...");
			System.out.println("...returning the first waveform ");
			return waves[ 0 ];
		}
		float[] accumulate = new float[ tableSize ];
		for( int i=0; i<waves.length; i++ )
		{
			waves[ i ].scale( amps[ i ] );
			// TODO Wavetable needs an add method and we should use it here.
			for( int j=0; j<tableSize; j++ )
			{
				accumulate[ j ] += waves[ i ].get( j );
			}
		}
		return new Wavetable( accumulate );
	}
}

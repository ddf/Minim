package ddf.minim.ugens;

/**
 * A waveforms library
 * 
 *  @author Nicolas Brix
 */
public class Waves 
{
	private static int tableSize = 8192;
	private static int tSby2 = tableSize/2;
	private static int tSby4 = tableSize/4;

	//perfect waveforms
	public final static Wavetable Sine = 
		WavetableGenerator.gen10(tableSize, new float[] { 1 });
	public final static Wavetable Saw = 
		WavetableGenerator.gen7(tableSize, new float[] {0,-1,1,0}, new int[] {tSby2,0,tableSize - tSby2});
	public final static Wavetable Square = 
		WavetableGenerator.gen7(tableSize, new float[] {-1,-1,1,1}, new int[] {tSby2,0,tableSize - tSby2});
	public final static Wavetable Triangle = 
		WavetableGenerator.gen7(tableSize, new float[] {0,1,-1,0}, new int[] {tSby4,tSby2,tableSize - tSby2 - tSby4});
	//shortcut for a 0.25 Pulse
	public final static Wavetable QuarterPulse = 
		WavetableGenerator.gen7(tableSize, new float[] {-1,-1,1,1}, new int[] {tSby4,0, tableSize - tSby4});
	
	//methods to build waveforms with numbers of harmonics
	public static Wavetable Saw(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms];
		for(int i=0;i<numberOfHarms;i++)
		{
			content[i]=(float) ((-2)/((i+1)*Math.PI)*Math.pow(-1,i+1));
		}
		return WavetableGenerator.gen10(tableSize, content);
	}

	public static Wavetable Square(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]= (float)1 /(i+1);
			content[i+1]=0;
		}
		return WavetableGenerator.gen10(tableSize, content);
	}
	
	public static Wavetable Triangle(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]= (float)(Math.pow(-1,i/2)*8/Math.PI/Math.PI/Math.pow(i+1,2));
			content[i+1]=0;
		}
		return WavetableGenerator.gen10(tableSize, content);
	}
	
	// methods to specify waveforms by duty cycle
	public static Wavetable Pulse(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		return WavetableGenerator.gen7(tableSize, new float[] {1,1,-1,-1}, 
				new int[] {(int)(dutyCycle*tableSize),0,tableSize-(int)(dutyCycle*tableSize)});
	}
	
	public static Wavetable Triangle(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		int a=(int)(tableSize*dutyCycle*0.5);
		return WavetableGenerator.gen7(tableSize, new float[] {0,1,0,-1,0}, 
				new int[] {a,a,tSby2-a,tableSize-tSby2-a});
	}
	
	public static Wavetable Saw(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		int a=(int)(tableSize*dutyCycle);
		return WavetableGenerator.gen7(tableSize, new float[] {1,0,-1}, new int[] {a,tableSize-a});
	}

	public static Wavetable Square(float dutyCycle)
	{//same as Pulse
		return Pulse(dutyCycle);
	}
	
	//TODO a dutycycled sine wavetable : i think a new warp() method in Wavetable would be the best

	//Other waveforms
	public static Wavetable RandomSaw(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms];
		for(int i=0;i<numberOfHarms;i++)
		{
			content[i]=(float)Math.random()*2 - 1;
		}
		Wavetable rand=WavetableGenerator.gen10(tableSize, content);
		rand.normalize();
		return rand;
	}
	
	public static Wavetable RandomSquare(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]=(float)Math.random()*2 - 1;
			content[i+1]=0;
		}
		Wavetable rand=WavetableGenerator.gen10(tableSize, content);
		rand.normalize();
		return rand;
	}
	
	// looped noise
	public static Wavetable RandomNoise()
	{
		float[] a = new float[tableSize];
		for(int i=0;i<a.length;i++)
		{
			a[i]=(float)Math.random()*2 - 1;
		}
		Wavetable rand = new Wavetable(a);
		rand.normalize();
		return rand;
	}
	
	//random impulses, proba being proportional to the number of impulses
	//values for proba : 1 to 100
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
	
	//advanced user functions
	public static Wavetable Custom(float[] amplitudes)
	{
		return WavetableGenerator.gen10(tableSize, amplitudes);
	}
	
	
	//method for adding any number of wavetables, each with their own amplitude
	public static Wavetable add(float [] amps, Wavetable ... waves)
	{
		if(amps.length != waves.length) 
		{
			System.out.println("add() : amplitude array size must match the number of waveforms...");
			System.out.println("...returning the first waveform ");
			return waves[0];
		}
		float[] acc= new float[tableSize];
		for(int i=0;i<waves.length;i++)
		{
			waves[i].scale(amps[i]);
			for(int j=0;j<tableSize;j++)
			{
				acc[j] += waves[i].get(j);
			}
		}
		return new Wavetable(acc);
	}
}

package ddf.minim.ugens;

public class Waves 
{
	/**
	 * A waveforms library
	 * 
	 *  @author nb
	 */
	
	//perfect waveforms
	public final static Wavetable Sine = WavetableGenerator.gen10(8192, new float[] { 1 });
	public final static Wavetable Saw = WavetableGenerator.gen7(8192, new float[] {1,-1}, new int[] {8192});
	public final static Wavetable Square = WavetableGenerator.gen7(8192, new float[] {-1,-1,1,1}, new int[] {4096,0,4096});
	public final static Wavetable Triangle = WavetableGenerator.gen7(8192, new float[] {-1,1,-1}, new int[] {4096,4096});
	//shortcut for a 0.25 Pulse
	public final static Wavetable Pulse = WavetableGenerator.gen7(8192, new float[] {-1,-1,1,1}, new int[] {2048,0,6144});
	
	
	//methods to harmonically build waveforms
	public static Wavetable Saw(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms];
		
		for(int i=0;i<numberOfHarms;i++)
		{
			content[i]=(float) ((-2)/((i+1)*Math.PI)*Math.pow(-1,i+1));
		}
		
		return WavetableGenerator.gen10(8192, content);
	}

	public static Wavetable Square(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]= (float)1 /(i+1);

			content[i+1]=0;
		}
		
		return WavetableGenerator.gen10(8192, content);
	}
	
	public static Wavetable Triangle(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms+1];
		
		for(int i=0;i<numberOfHarms;i+=2)
		{
			content[i]= (float)(Math.pow(-1,i/2)*8/Math.PI/Math.PI/Math.pow(i+1,2));
			
			content[i+1]=0;
		}
		
		return WavetableGenerator.gen10(8192, content);
	}
	
	
	
	
	//duty cycle stuff
	
	
	public static Wavetable Pulse(float dutyCycle)
	{
		//TODO exception for floats higher than 1

		return WavetableGenerator.gen7(8192, new float[] {1,1,-1,-1}, new int[] {(int)(dutyCycle*8192),0,8192-(int)(dutyCycle*8192)});
	}
	
	
	
	
	public static Wavetable Triangle(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		int a=(int)(8192*dutyCycle*0.5);
		return WavetableGenerator.gen7(8192, new float[] {0,1,0,-1,0}, new int[] {a,a,4096-a,4096-a});
	}

	
	
	public static Wavetable Saw(float dutyCycle)
	{
		//TODO exception for floats higher than 1
		int a=(int)(8192*dutyCycle);
		return WavetableGenerator.gen7(8192, new float[] {1,0,-1}, new int[] {a,8192-a});
	}


	public static Wavetable Square(float dutyCycle)
	{//same as Pulse
		return Pulse(dutyCycle);
	}
	
	
	
	//TODO a dutycycled sine wavetable : i think a new warp() fct in Wavetable would be the best

	
	
	
	
	
	
	
	
	
	//Other waveforms
	
	
	public static Wavetable RandomSaw(int numberOfHarms)
	{
		float[] content = new float[numberOfHarms];
		
		for(int i=0;i<numberOfHarms;i++)
		{
			content[i]=(float)Math.random()*2 - 1;
		}
		Wavetable rand=WavetableGenerator.gen10(8192, content);
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
		Wavetable rand=WavetableGenerator.gen10(8192, content);
		rand.normalize();
		return rand;
	}
	
	public static Wavetable RandomNoise()
	{
		float[] a = new float[8192];
		for(int i=0;i<a.length;i++)
		{
			a[i]=(float)Math.random()*2 - 1;
		}
		Wavetable rand = new Wavetable(a);
		rand.normalize();
		return rand;
	}
	
	
	
	
	
	
}

package ddf.minim.ugens;

public class Harmonics {

	/*I created this class to deal with "spectrums" of waveforms that require 
	 * a special formula to compute the amplitude of their harmonics
	 * (such a sawtooth, square, triangle)
	 * 
	 * It might not be so useful so perhaps we'll have to erase it at some point.
	 * 
	 * Nicolas Brix
	 * */
	 
	
	//array where the amplitudes of the harmonics will be stored
	public float[] content;
	
	
	

	
	public Harmonics(String wavetype, int numberOfHarms)
	{
		content = new float[numberOfHarms+1];
		/*We might have a discussion about what the user should should expect when typing e.g. 5
		 * as the number of harmonics of a square wave.
		 * For now, there will be 3 non-zero harmonics (1 3 5), but maybe it should be changed,
		 * so that the wave contains 1 3 5 7 9, i.e. 5 non-zero harmonics.
		 * This is also true for the triangle wave.
		 * 
		 * */
		
		
		if(wavetype=="Saw")
		{
			for(int i=0;i<numberOfHarms;i++)
			{
				content[i]=(float) ((-2)/((i+1)*Math.PI)*Math.pow(-1,i+1));
			}
			return;
		}
		
		
		
		if(wavetype=="Square")
		{
			for(int i=0;i<numberOfHarms;i+=2)
			{
				content[i]= (float)1 /(i+1);
	
				content[i+1]=0;
			}
			return;
		}
		
		if(wavetype=="Triangle")
		{
			for(int i=0;i<numberOfHarms;i+=2)
			{
				content[i]= (float)(Math.pow(-1,i/2)*8/Math.PI/Math.PI/Math.pow(i+1,2));
				
				content[i+1]=0;
			}
			return;
		}
		
	}
}


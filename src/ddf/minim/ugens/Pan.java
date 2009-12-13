package ddf.minim.ugens;

import ddf.minim.ugens.UGen.InputType;
import ddf.minim.ugens.UGen.UGenInput;

public class Pan extends UGen{
	
	/** A UGen for balance and stereo width
	 * 
	 * Width :
	 * (formula is from Michael Gruhn on www.musicdsp.com)
	 * 'width' is the stretch factor of the stereo field:
	 *	width < 1: decrease in stereo width
	 *	width = 1: no change
	 *	width > 1: increase in stereo width
	 *	width = 0: mono
	 * 
	 * 
	 * Balance :
	 * should be between -1 and +1
	 * 
	 * 
	 * 
	 * @author nb
	 */
	
	
	
	
	public UGenInput audio;
	public UGenInput balance;
	private float currentBalance;
	private float currentWidth;
	public UGenInput width;

	private boolean widthActivated=false;
	private boolean balActivated=false;
	
	
	
	
	public Pan(float bala, float wid)
	{
		super();
		currentBalance = bala;
		currentWidth = wid;
		audio = new UGenInput(InputType.AUDIO);
		balance = new UGenInput(InputType.CONTROL);
		width = new UGenInput(InputType.CONTROL);
		
		if(bala != 0 ) balActivated = true;
		if(wid != 0) widthActivated = true;
		
	}
	



	
	protected void uGenerate(float[] channels) 
	{
		if ((balance != null) && (balance.isPatched()))
		{
			currentBalance = balance.getLastValues()[0];
		}
		
		if ((width != null) && (width.isPatched()))
		{
			currentWidth = width.getLastValues()[0];

		}
		
		for(int i=0;i<channels.length;i++)
		{

			channels[i]= audio.getLastValues()[i];
		}

		
		if(balActivated)
		{
			/*//i dont like that code
			float angle = (float)(-currentBalance/2*Math.PI);
			float cos_coef = (float)Math.cos(angle);
			float sin_coef = (float)Math.sin(angle);
			float tmp = channels[0];
			channels[0]  = tmp * cos_coef - channels[1] * sin_coef;
			channels[1] = tmp * sin_coef + channels[1] * cos_coef;
			*/
			
			if(currentBalance < 0)
			{
				channels[0] = channels[0]  + (-1)*currentBalance* channels[1];
				channels[1] = (1 + currentBalance)* channels[1];
			}
			if(currentBalance > 0)
			{
				channels[1] = currentBalance * channels[0] + channels[1];
				channels[0] = (1-currentBalance) * channels [0];
			}
			
			
		}
		
		
		if(widthActivated)
		{
			float tmp = 1/Math.max(1 + currentWidth , 2);
			float coef_M = 1 * tmp;
			float coef_S = currentWidth * tmp;
			float m = (channels[0] + channels[1])*coef_M;
			float s = (channels[0] - channels[1])*coef_S;
			channels[0] = m-s;
			channels[1] = m+s;
		}
		
		
		
	}
	

}

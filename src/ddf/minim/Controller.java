/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package ddf.minim;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;

import processing.core.PApplet;

/**
 * <code>Controller</code> is the base class of all Minim classes that deal
 * with audio I/O. It provides control over the underlying <code>DataLine</code>,
 * which is a low-level JavaSound class that talks directly to the audio
 * hardware of the computer. This means that you can make changes to the audio
 * without having to manipulate the samples directly. The downside to this is
 * that when outputting sound to the system (such as with an
 * <code>AudioOutput</code>), these changes will not be present in the
 * samples made available to your program.
 * <p>
 * The {@link #volume()}, {@link #gain()}, {@link #pan()}, and
 * {@link #balance()} methods return objects of type <code>FloatControl</code>,
 * which is a class defined by the JavaSound API. A <code>FloatControl</code>
 * represents a control of a line that holds a <code>float</code> value. This
 * value has an associated maximum and minimum value (such as between -1 and 1
 * for pan), and also a unit type (such as dB for gain). You should refer to the
 * <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/sampled/FloatControl.html">FloatControl
 * Javadoc</a> for the full description of the methods available.
 * <p>
 * Not all controls are available on all objects. Before calling the methods
 * mentioned above, you should call
 * {@link #hasControl(javax.sound.sampled.Control.Type)} with the control type
 * you want to use. Alternatively, you can use the <code>get</code> and
 * <code>set</code> methods, which will simply do nothing if the control you
 * are trying to manipulate is not available.
 * 
 * @author Damien Di Fede
 * 
 */
public class Controller
{
  /**
   * The volume control type.
   */
  public static FloatControl.Type VOLUME = FloatControl.Type.VOLUME;

  /**
   * The gain control type.
   */
  public static FloatControl.Type GAIN = FloatControl.Type.MASTER_GAIN;

  /**
   * The balance control type.
   */
  public static FloatControl.Type BALANCE = FloatControl.Type.BALANCE;

  /**
   * The pan control type.
   */
  public static FloatControl.Type PAN = FloatControl.Type.PAN;

  /**
   * The sample rate control type.
   */
  public static FloatControl.Type SAMPLE_RATE = FloatControl.Type.SAMPLE_RATE;

  /**
   * The mute control type.
   */
  public static BooleanControl.Type MUTE = BooleanControl.Type.MUTE;
  
  private Control[] controls;
  // the starting value for shifting
  private ValueShifter vshifter, gshifter, bshifter, pshifter;
  private boolean vshift, gshift, bshift, pshift;

  /**
   * Constructs a <code>Controller</code> for the given <code>Line</code>.
   * 
   * @param cntrls
   *          an array of Controls that this Controller will manipulate
   */
  public Controller(Control[] cntrls)
  {
    controls = cntrls;
    vshift = gshift = bshift = pshift = false;
  }
  
  // for line reading/writing classes to alert the controller 
  // that a new buffer has been read/written
  void update()
  {
    if ( vshift )
    {
      setVolume( vshifter.value() );
      if ( vshifter.done() ) vshift = false;
    }

    if ( gshift )
    {
      setGain( gshifter.value() );
      if ( gshifter.done() ) gshift = false;
    }
    
    if ( bshift )
    {
      setBalance( bshifter.value() );
      if ( bshifter.done() ) bshift = false;
    }
    
    if ( pshift )
    {
      setPan( pshifter.value() );
      if ( pshifter.done() ) pshift = false;
    }
  }
  
  // a small class to interpolate a value over time
  class ValueShifter
  {
    private float tstart, tend, vstart, vend;
    
    public ValueShifter(float vs, float ve, int t)
    {
      tstart = (int)System.currentTimeMillis();
      tend = tstart + t;
      vstart = vs;
      vend = ve;
    }
    
    public float value()
    {
      int millis = (int)System.currentTimeMillis();
      return PApplet.map(millis, tstart, tend, vstart, vend);
    }
    
    public boolean done()
    {
      return (int)System.currentTimeMillis() > tend;
    }
  }

  /**
   * Prints the available controls and their ranges to the console. Not all
   * lines have all of the controls available on them so this is a way to find
   * out what is available.
   * 
   */
  public void printControls()
  {
    if (controls.length > 0)
    {
      PApplet.println("Available controls are:");
      for (int i = 0; i < controls.length; i++)
      {
        Control.Type type = controls[i].getType();
        PApplet.print("  " + type.toString());
        if (type == VOLUME || type == GAIN || type == BALANCE || type == PAN)
        {
          FloatControl fc = (FloatControl) controls[i];
          String shiftSupported = "does";
          if (fc.getUpdatePeriod() == -1)
          {
            shiftSupported = "doesn't";
          }
          PApplet.println(", which has a range of " + fc.getMaximum() + " to "
              + fc.getMinimum() + " and " + shiftSupported
              + " support shifting.");
        }
        else
        {
          PApplet.println("");
        }
      }
    }
    else
    {
      PApplet.println("There are no controls available for this line.");
    }
  }

  /**
   * Returns whether or not the particular control type is supported by the Line
   * being controlled.
   * 
   * @see #VOLUME
   * @see #GAIN
   * @see #BALANCE
   * @see #PAN
   * @see #SAMPLE_RATE
   * @see #MUTE
   * 
   * @return true if the control is available
   */
  public boolean hasControl(Control.Type type)
  {
    for(int i = 0; i < controls.length; i++)
    {
      if ( controls[i].getType().equals(type) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an array of all the available <code>Control</code>s for the
   * <code>DataLine</code> being controlled. You can use this if you want to
   * access the controls directly, rather than using the convenience methods
   * provided by this class.
   * 
   * @return an array of all available controls
   */
  public Control[] getControls()
  {
    return controls;
  }
  
  public Control getControl(Control.Type type)
  {
    for(int i = 0; i < controls.length; i++)
    {
      if ( controls[i].getType().equals(type) )
      {
        return controls[i];
      }
    }
    return null;
  }

  /**
   * Gets the volume control for the <code>Line</code>, if it exists. You
   * should check for the availability of a volume control by using
   * {@link #hasControl(javax.sound.sampled.Control.Type)} before calling this
   * method.
   * 
   * @return the volume control
   */
  public FloatControl volume()
  {
    return (FloatControl)getControl(VOLUME);
  }

  /**
   * Gets the gain control for the <code>Line</code>, if it exists. You
   * should check for the availability of a gain control by using
   * {@link #hasControl(javax.sound.sampled.Control.Type)} before calling this
   * method.
   * 
   * @return the gain control
   */
  public FloatControl gain()
  {
    return (FloatControl) getControl(GAIN);
  }

  /**
   * Gets the balance control for the <code>Line</code>, if it exists. You
   * should check for the availability of a balance control by using
   * {@link #hasControl(javax.sound.sampled.Control.Type)} before calling this
   * method.
   * 
   * @return the balance control
   */
  public FloatControl balance()
  {
    return (FloatControl) getControl(BALANCE);
  }

  /**
   * Gets the pan control for the <code>Line</code>, if it exists. You should
   * check for the availability of a pan control by using
   * {@link #hasControl(javax.sound.sampled.Control.Type)} before calling this
   * method.
   * 
   * @return the pan control
   */
  public FloatControl pan()
  {
    return (FloatControl) getControl(PAN);
  }

  /**
   * Mutes the line.
   * 
   */
  public void mute()
  {
    setValue(MUTE, true);
  }

  /**
   * Unmutes the line.
   * 
   */
  public void unmute()
  {
    setValue(MUTE, false);
  }

  /**
   * Returns true if the line is muted.
   * 
   * @return the current mute state
   */
  public boolean isMuted()
  {
    return getValue(MUTE);
  }

  private boolean getValue(BooleanControl.Type type)
  {
    boolean v = false;
    if (hasControl(type))
    {
      BooleanControl c = (BooleanControl) getControl(type);
      v = c.getValue();
    }
    else
    {
      Minim.error(type.toString() + " is not supported.");
    }
    return v;
  }

  private void setValue(BooleanControl.Type type, boolean v)
  {
    if (hasControl(type))
    {
      BooleanControl c = (BooleanControl) getControl(type);
      c.setValue(v);
    }
    else
    {
      Minim.error(type.toString() + " is not supported.");
    }
  }

  private float getValue(FloatControl.Type type)
  {
    float v = 0;
    if (hasControl(type))
    {
      FloatControl c = (FloatControl) getControl(type);
      v = c.getValue();
    }
    else
    {
      Minim.error(type.toString() + " is not supported.");
    }
    return v;
  }

  private void setValue(FloatControl.Type type, float v)
  {
    if (hasControl(type))
    {
      FloatControl c = (FloatControl) getControl(type);
      if (v > c.getMaximum())
        v = c.getMaximum();
      else if (v < c.getMinimum()) v = c.getMinimum();
      c.setValue(v);
    }
    else
    {
      Minim.error(type.toString() + " is not supported.");
    }
  }

  /**
   * Returns the current volume. If a volume control is not available, this
   * returns 0. Note that the volume is not the same thing as the
   * <code>level()</code> of an AudioBuffer!
   * 
   * @return the current volume or zero if a volume control is unavailable
   */
  public float getVolume()
  {
    return getValue(VOLUME);
  }

  /**
   * Sets the volume to <code>v</code>. If a volume control is not available,
   * this does nothing.
   * 
   * @param v
   *          the new value for the volume
   */
  public void setVolume(float v)
  {
    setValue(VOLUME, v);
  }

  /**
   * Shifts the value of the volume from <code>from</code> to <code>to</code>
   * in the space of <code>millis</code> milliseconds.
   * 
   * @param from
   *          the starting volume
   * @param to
   *          the ending volume
   * @param millis
   *          the length of the transition
   */
  public void shiftVolume(float from, float to, int millis)
  {
    if ( hasControl(VOLUME) )
    {
      setVolume(from);
      vshifter = new ValueShifter(from, to, millis);
      vshift = true;
    }
  }

  /**
   * Returns the current gain. If a gain control is not available, this returns
   * 0. Note that the gain is not the same thing as the <code>level()</code>
   * of an AudioBuffer!
   * 
   * @return the current gain or zero if a gain control is unavailable
   */
  public float getGain()
  {
    return getValue(GAIN);
  }

  /**
   * Sets the gain to <code>v</code>. If a gain control is not available,
   * this does nothing.
   * 
   * @param v
   *          the new value for the gain
   */
  public void setGain(float v)
  {
    setValue(GAIN, v);
  }

  /**
   * Shifts the value of the gain from <code>from</code> to <code>to</code>
   * in the space of <code>millis</code>
   * 
   * @param from
   *          the starting volume
   * @param to
   *          the ending volume
   * @param millis
   *          the length of the transition
   */
  public void shiftGain(float from, float to, int millis)
  {
    if ( hasControl(GAIN) )
    {
      setGain(from);
      gshifter = new ValueShifter(from, to, millis);
      gshift = true;
    }
  }

  /**
   * Returns the current balance of the line. This will be in the range [-1, 1].
   * If a balance control is not available, this will do nothing.
   * 
   * @return the current balance or zero if a balance control is unavailable
   */
  public float getBalance()
  {
    return getValue(BALANCE);
  }

  /**
   * Sets the balance of the line to <code>v</code>. The provided value
   * should be in the range [-1, 1]. If a balance control is not available, this
   * will do nothing.
   * 
   * @param v
   *          the new value for the balance
   */
  public void setBalance(float v)
  {
    setValue(BALANCE, v);
  }

  /**
   * Shifts the value of the balance from <code>from</code> to <code>to</code>
   * in the space of <code>millis</code> milliseconds.
   * 
   * @param from
   *          the starting volume
   * @param to
   *          the ending volume
   * @param millis
   *          the length of the transition
   */
  public void shiftBalance(float from, float to, int millis)
  {
    if ( hasControl(BALANCE) )
    {
      setBalance(from);
      bshifter = new ValueShifter(from, to, millis);
      bshift = true;
    }
  }

  /**
   * Returns the current pan value. This will be in the range [-1, 1]. If the
   * pan control is not available
   * 
   * @return the current pan or zero if a pan control is unavailable
   */
  public float getPan()
  {
    return getValue(PAN);
  }

  /**
   * Sets the pan of the line to <code>v</code>. The provided value should be
   * in the range [-1, 1].
   * 
   * @param v
   *          the new value for the pan
   */
  public void setPan(float v)
  {
    setValue(PAN, v);
  }

  /**
   * Shifts the value of the pan from <code>from</code> to <code>to</code>
   * in the space of <code>millis</code> milliseconds.
   * 
   * @param from
   *          the starting pan
   * @param to
   *          the ending pan
   * @param millis
   *          the length of the transition
   */
  public void shiftPan(float from, float to, int millis)
  {
    if ( hasControl(PAN) )
    {
      setPan(from);
      pshifter = new ValueShifter(from, to, millis);
      pshift = true;
    }
  }
}

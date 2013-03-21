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

/**
 * AudioMetaData provides information commonly found in ID3 tags. 
 * However, other audio formats, such as Ogg, can contain
 * similar information. So rather than refer to this information
 * as ID3Tags or similar, we simply call it metadata. This base 
 * class returns the empty string or -1 from all methods and
 * derived classes are expected to simply override the methods
 * that they have information for. This is a little less brittle
 * than using an interface because later on new properties can 
 * be added without breaking existing code.
 * 
 * @example Basics/GetMetaData
 */

public abstract class AudioMetaData
{
	/**
	 * The length of the recording in milliseconds.
	 */
	public int length()
	{
		return -1;
	}
	
	/**
	 * 
	 * How many sample frames are in this recording.
	 */
	public int sampleFrameCount()
	{
		return -1;
	}
	
	/**
	 * The name of the file / URL of the recording.
	 */
	public String fileName()
	{
		return ""; 
	}
	
	/**
	 * The title of the recording.
	 */
	public String title()
	{
		return "";
	}
	
	/**
	 * The author or the recording.
	 */
	public String author()
	{
		return "";
	}
	
	/**
	 * The album the recording came from.
	 */
	public String album()
	{
		return "";
	}
	
	/**
	 * The date the recording was made.
	 */
	public String date()
	{
		return "";
	}
	
	/**
	 * The comment field in the file.
	 */
	public String comment()
	{
		return "";
	}
	
	/**
	 * The track number of the recording.
	 * This will sometimes be in the form 3/10,
	 * giving you both the track number and total
	 * tracks on the album this track came from.
	 */
	public String track()
	{
		return "";
	}
	
	/**
	 * The genre of the recording.
	 */
	public String genre()
	{
		return "";
	}
	
	/**
	 * The copyright of the recording.
	 */
	public String copyright()
	{
		return "";
	}
	
	/**
	 * The disc number of the recording.
	 */
	public String disc()
	{
		return "";
	}
	
	/**
	 * The composer of the recording.
	 */
	public String composer()
	{
		return "";
	}
	
	/**
	 * The orchestra that performed the recording.
	 */
	public String orchestra()
	{
		return "";
	}
	
	/** 
	 * The publisher of the recording.
	 */
	public String publisher()
	{
		return "";
	}
	
	/**
	 * The software the recording was encoded with.
	 */
	public String encoded()
	{
		return "";
	}
}

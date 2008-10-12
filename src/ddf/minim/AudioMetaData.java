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
 * as ID3Tags or similar, I simply call it metadata. This base 
 * class returns the empty string or -1 from all methods and
 * derived classes are expected to simply override the methods
 * that they have information for. This is a little less brittle
 * than using an interface because later on new properties can 
 * be added without breaking existing code.
 */

public abstract class AudioMetaData
{
	/**
	 * @return the length of the recording in milliseconds.
	 */
	public int length()
	{
		return -1;
	}
	
	/**
	 * @return the name of the file / URL of the recording.
	 */
	public String fileName()
	{
		return ""; 
	}
	
	/**
	 * @return the title of the recording
	 */
	public String title()
	{
		return "";
	}
	
	/**
	 * @return the author or the recording
	 */
	public String author()
	{
		return "";
	}
	
	public String album()
	{
		return "";
	}
	
	public String date()
	{
		return "";
	}
	
	public String comment()
	{
		return "";
	}
	
	public int track()
	{
		return -1;
	}
	
	public String genre()
	{
		return "";
	}
	
	public String copyright()
	{
		return "";
	}
	
	public String disc()
	{
		return "";
	}
	
	public String composer()
	{
		return "";
	}
	
	public String orchestra()
	{
		return "";
	}
	
	public String publisher()
	{
		return "";
	}
	
	public String encoded()
	{
		return "";
	}
}

package ddf.mimin.javasound;

import java.util.Map;

class MP3MetaData extends BasicMetaData
{
	private Map mTags;
	
	MP3MetaData(String filename, long length, Map tags)
	{
		super(filename, length);
		mTags = tags;
	}
	
	private String getTag(String tag)
	{
		if ( mTags.containsKey(tag) )
		{
			return (String)mTags.get(tag);
		}
		return "";
	}
	
	public String title()
	{
		return getTag("title");
	}
	
	public String author()
	{
		return getTag("author");
	}
	
	public String album()
	{
		return getTag("album");
	}
	
	public String date()
	{
		return getTag("date");
	}
	
	public String comment()
	{
		return getTag("comment");
	}
	
	public int track()
	{
		String t = getTag("mp3.id3tag.track");
		if ( t == "" )
		{
			return -1;
		}
		return Integer.parseInt(t);
	}
	
	public String genre()
	{
		return getTag("mp3.id3tag.genre");
	}
	
	public String copyright()
	{
		return getTag("copyright");
	}
	
	public String disc()
	{
		return getTag("mp3.id3tag.disc");
	}
	
	public String composer()
	{
		return getTag("mp3.id3tag.composer");
	}
	
	public String orchestra()
	{
		return getTag("mp3.id3tag.orchestra");
	}
	
	public String publisher()
	{
		return getTag("mp3.id3tag.publisher");
	}
	
	public String encoded()
	{
		return getTag("mp3.id3tag.encoded");
	}
}

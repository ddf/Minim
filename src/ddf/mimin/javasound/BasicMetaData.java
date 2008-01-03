package ddf.mimin.javasound;

import ddf.minim.AudioMetaData;

class BasicMetaData extends AudioMetaData
{
	private String mFileName;
	private long mLength;
	BasicMetaData(String filename, long length)
	{
		mFileName = filename;
		mLength = length;
	}
	
	public int length()
	{
		return (int)mLength;
	}
	
	public String fileName()
	{
		return mFileName;
	}

}

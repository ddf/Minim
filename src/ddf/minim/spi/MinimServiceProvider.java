package ddf.minim.spi;

import processing.core.PApplet;
import ddf.minim.AudioSample;
import ddf.minim.Recordable;


public interface MinimServiceProvider
{
  void start(PApplet parent);
  
  void stop();
  
  void debugOn();
  
  void debugOff();
  
  AudioRecording getAudioRecording(String filename);
  
  AudioRecordingStream getAudioRecordingStream(String filename, int bufferSize);
  
  AudioStream getAudioStream(int type, int bufferSize, float sampleRate, int bitDepth);
  
  AudioSynthesizer getAudioSynthesizer(int type, int bufferSize, float sampleRate, int bitDepth);
  
  AudioSample getAudioSample(String filename, int bufferSize);
  
  SampleRecorder getSampleRecorder(Recordable source, String saveTo, boolean buffered);
}

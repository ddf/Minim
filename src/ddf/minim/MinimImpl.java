package ddf.minim;


public interface MinimImpl
{
  void start();
  void stop();
  AudioRecording newAudioRecording(String filename);
  AudioRecordingStream newAudioRecordingStream(String filename, int bufferSize);
  AudioStream newAudioStream(int type, int bufferSize, float sampleRate, int bitDepth);
  AudioSynthesizer newAudioSythesizer(int type, int bufferSize, float sampleRate, int bitDepth);
  AudioSampleImpl newAudioSample(String filename, int bufferSize);
  SampleRecorder newSampleRecorder(Recordable source, String saveTo, boolean buffered);
}

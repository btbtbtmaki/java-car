package car.audio;

import org.lwjgl.openal.AL10;

public class Source {
private int sourceId;

public Source(){
	sourceId = AL10.alGenSources();
	AL10.alSourcef(sourceId, AL10.AL_GAIN, 1);
	AL10.alSourcef(sourceId, AL10.AL_PITCH, 1);
	AL10.alSource3f(sourceId, AL10.AL_POSITION, 0,0,0);

	
}
public void pause(){
	AL10.alSourcePause(sourceId);
}
public void continuePlaying(){
	AL10.alSourcePlay(sourceId);
}

public void stop(){
	AL10.alSourceStop(sourceId);
}


public void setLooping(boolean loop){
	AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
}

public boolean isPlaying(){
	return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
}
public void play(int buffers){
	AL10.alSourcei(sourceId, AL10.AL_BUFFER	, buffers);
	AL10.alSourcePlay(sourceId);
	continuePlaying();
}

public void delete(){
	stop();
	AL10.alDeleteSources(sourceId);
}

}

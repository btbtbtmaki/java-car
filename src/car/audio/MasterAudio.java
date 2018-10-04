package car.audio;

import java.util.ArrayList;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALContext;
import org.lwjgl.openal.ALDevice;

public class MasterAudio {

	private static ArrayList<Integer> buffers = new ArrayList<Integer>();
	private static ALContext context;
	
	public static void init(){

			context = ALContext.create();
	}
	
	public static void setListenerData(){
		AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);

	}
	
	
	public static int loadSound(String file){
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(file);
	AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
	waveFile.dispose();
	return buffer;
	}
	
	public static void cleanUp(){
		for(int buffer : buffers){
			AL10.alDeleteBuffers(buffer);
		}
		ALDevice device = context.getDevice();
		context.destroy();
		device.destroy();
			
	}
	
	
	
	
}

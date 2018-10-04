 package car;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;



//import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWVidMode;
//import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL;
//import org.lwjgl.opengl.GLContext;

import car.audio.MasterAudio;
import car.audio.Source;
import car.graphics.Shader;
import car.input.Input;
import car.level.Level;
import car.maths.Matrix4f;

public class Main implements Runnable {
	//the size of the game window is as follow
	private int width = 1280;
	private int height = 720;
	///////////////////////////////////////////
	private Input incrl = new Input();
	private Thread thread;
	private boolean running = false;
	
	private long window;
	
	private Level level;
	
	public void start() {
		running = true;
		thread = new Thread(this, "Game");
		thread.start();
		
	}
	
	private void init() {
		if (glfwInit() != GL_TRUE) {
			System.err.println("Could not initialize GLFW!");
			return;
		}
		
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		window = glfwCreateWindow(width, height, "DRIVE", NULL, NULL );
		if (window == NULL) {
			System.err.println("Could not create GLFW window!");
			return;
		}
 
		
		
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		

		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		
	
		//init input 
		
		glfwSetKeyCallback(window, incrl);
		//
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities();
		
 		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		Shader.loadAll();
		
		//this pr_matrix determines the coordinate of the game viewing window
		//in left right bot top near far order.
		Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
		//////////////////////////////////////////////
		
		Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.BG.setUniform1i("tex", 1);
		
		Shader.PLAYER.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.PLAYER.setUniform1i("tex", 1);
		
		Shader.BLOCK.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.BLOCK.setUniform1i("tex", 1);
		
		level = new Level();
	}
	public void playBGMusic(){

	

		

	}
	public void run() {
		
		//setting up the audio variables
		MasterAudio.init();
		MasterAudio.setListenerData();
		
		int buffer = MasterAudio.loadSound("car/audio/Traffic.wav");
		Source source = new Source();


		int buffer2 = MasterAudio.loadSound("car/audio/Crash.wav");
		Source source2 = new Source();

	
		
		//setting up the game variables
		init();

		

		boolean crash = false;
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		
		
		while (running) {
//plays the background music
			if(!source.isPlaying() && !level.collosionDetection()){
				source.setLooping(true);
				source.play(buffer);
				
			}
			
			if(level.collosionDetection()){
				source.stop();
				
					if(!source2.isPlaying() && !crash){
						crash = true;
					System.out.println("crashed");
				source2.play(buffer2);
				}
			}
			//checks to see if new game is set
			if (Input.isKeyDown(GLFW_KEY_SPACE)){
				crash = false;
			}
			
			
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1.0) {
				update();
				updates++;
				delta--;
			}
			render();
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}

			if (glfwWindowShouldClose(window) == GL_TRUE ){

				running = false;
			}

		}

		source.delete();
		source2.delete();
		 MasterAudio.cleanUp();
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	
	private void update() {
		glfwPollEvents();
		level.update();
		if (level.isGameOver()) {
			level = new Level();
		
		}
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		level.render();
		
		int error = glGetError();
		if (error != GL_NO_ERROR)
			System.out.println(error);
		
		glfwSwapBuffers(window);
	}

	public static void main(String[] args) {
		new Main().start();
	}

}

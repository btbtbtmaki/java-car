package car.level;

import static org.lwjgl.glfw.GLFW.*;

import car.graphics.Shader;
import car.graphics.Texture;
import car.graphics.VertexArray;
import car.input.Input;
import car.maths.Matrix4f;
import car.maths.Vector3f;

public class Player {

	private static float width = 1.5f, height = 3.0f;
	private VertexArray mesh;
	private Texture texture;
	
	private Vector3f position = new Vector3f();
	private float rot;
	private float delta = -0.05f;
	
	public Player() {
		//drawing the car with following dimension *2.0f
		float[] vertices = new float[] {
				
				0.0f, 0.0f -4, 0.1f,
				0.0f, height -4, 0.1f,
				width, height -4, 0.1f,
				width, 0.0f -4, 0.1f
				/*
			-SIZE / 2.0f, -SIZE / 2.0f - 3.0f, 0.2f,
			-SIZE / 2.0f,  SIZE / 2.0f *2.0f - 3.0f, 0.2f,
			 SIZE / 2.0f,  SIZE / 2.0f *2.0f - 3.0f, 0.2f,
			 SIZE / 2.0f, -SIZE / 2.0f - 3.0f, 0.2f
			 */
		};
		  	 
		byte[] indices = new byte[] {
			0, 1, 2,
			2, 3, 0
		};
		
		float[] tcs = new float[] {
			0, 1,
			0, 0,
			1, 0,
			1, 1
		};
		
		mesh = new VertexArray(vertices, indices, tcs);
		texture = new Texture("res/car.png");
	}
	
	public void update() {
		//up down  control, so be left right control
		//position.x -= delta;
		if (Input.isKeyDown(GLFW_KEY_A)) {
			position.x -= 0.10f;
			rot = -delta * 100.0f; //greater f greater angle : rotation
			
			
		}
		else if (Input.isKeyDown(GLFW_KEY_D)) {
			position.x += 0.10f;
			rot = delta * 100.0f;
			
		}
		else 
			rot = delta;//return car to neutral position
		
	}
	
	public void render() {
		Shader.PLAYER.enable();
		Shader.PLAYER.setUniformMat4f("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
		texture.bind();
		mesh.render();
		Shader.PLAYER.disable();
	}

	public float getX() {
		return position.x;
	}
	
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}

	
}

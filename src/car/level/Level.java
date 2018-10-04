package car.level;

import java.util.Random;

import car.graphics.Shader;
import car.graphics.Texture;
import car.graphics.VertexArray;
import car.input.Input;
import car.maths.Matrix4f;
import car.maths.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Level {

	private VertexArray background, fade,endfade;// bg and the fade when the game reset
	private Texture bgTexture;
	
	private int yScroll = 0;
	private int map = 0;
	
	private Player player;
	
	private Block[] blocks = new Block[10*5];
	private int index = 0;

	private boolean control = true, reset = false;
	private float verlocation = 8f;
	
	private Random random = new Random(); 
	
	
	private float time = 0.0f;
	private float crashtime = 0.0f;
	
	public Level() {
		
		
		//vertices. bg position
		float[] vertices = new float[] {
				
				
				-10.0f, -10.0f * 9.0f / 16.0f, 0.0f, //bot left p
				-10.0f,  10.0f * 9.0f / 16.0f/1.25f, 0.0f, //top left p //2.0f
				 10.0f,  10.0f * 9.0f / 16.0f/1.25f, 0.0f, //top right p //2.0f
				 10.0f, -10.0f * 9.0f / 16.0f, 0.0f //bot right p 
				
		};        
		
		//indice
		byte[] indices = new byte[] {
			0, 1, 2,
			2, 3, 0
		};
		
		//textureCoordinates 
		float[] tcs = new float[] {
			0, 1,
			0, 0,
			1, 0,
			1, 1
		};
		
		fade = new VertexArray(6);
		endfade = new VertexArray(6);
		background = new VertexArray(vertices, indices, tcs);
		bgTexture = new Texture("res/bg2.png");
		player = new Player();
		
		createBlock();
	}
	private float randomlocation() { //this is a little random generator to generate position of blocks
		int x = random.nextInt(7);
		if(x==0) return -6.5f;
		else if (x==1) return -2.5f;
		else if (x==2) return 1f;
		else if (x==3) return 2.85f;
		else if (x==4) return -0.8f;
		else if (x==5) return -4.35f;
		else return 4.5f;
		/* another generator
		int x = random.nextInt(4);
		int y = random.nextInt(3);
		float re = 0f;
		
		
		if(x==0) { 
			if (y == 0) return -6f;
			else if (y==1) return -6.5f;
			else return -7;
		}
		else if (x==1){
			if (y == 0) return -2;
			else if (y==1) return -2.5f;
			else return -3;
			
		} 
		else if (x==2){
			if (y == 0) return 1f;
			else if (y==1) return -0.5f;
			else return 1.5f;
			
		} 
		
		else {
			if (y == 0) return 4.5f;
			else if (y==1) return 5f;
			else return 4;
			
		}
		*/
		
		
	}
	private void createBlock() {//actually creating an array of blocks for rendering
		Block.create();
	
		//hor, ver
		//the blocks are created row by row, there are 10 rows and 5 blocks on each row
		//verlocation determines the distance between each row
		//index for tracking the location of a block in the array
		for (int i = 0; i < 10 * 5; i += 5) {
			blocks[i] = new Block(randomlocation(), verlocation);//+(random.nextFloat()*2 -1)*0.5f);
			//blocks[i] = new Block(-4.35f, verlocation+(random.nextFloat()*2 -1)*0.5f); testing
			blocks[i + 1] = new Block(randomlocation(), verlocation);//+(random.nextFloat()*2 -1)*0.5f);
			blocks[i + 2] = new Block(randomlocation(), verlocation);//+(random.nextFloat()*2 -1)*0.5f);
			blocks[i + 3] = new Block(randomlocation(), verlocation);//+(random.nextFloat()*2 -1)*0.5f);
			blocks[i + 4] = new Block(randomlocation(), verlocation);//+(random.nextFloat()*2 -1)*0.5f);
			verlocation += 8;
			index += 5;
			

			
		}
		
	}
	
	private void updateBlocks() {//update the position of a row of block to render, reuse the old array of blocks
		
		blocks[index % 50] = new Block(randomlocation(), verlocation);
		blocks[(index + 1) % 50] = new Block(randomlocation(), verlocation);
		blocks[(index + 2) % 50] = new Block(randomlocation(), verlocation);
		blocks[(index + 3) % 50] = new Block(randomlocation(), verlocation);
		blocks[(index + 4) % 50] = new Block(randomlocation(), verlocation);
		
	
		verlocation += 8;// how far apart the rows of cop cars are from top to bottom
		index += 5; // how far apart the cop cars are
	}
	
	public void update() {//update the whole level
		
		if (control) {
			/*//gradual speed gain did not work because the background or the cars would go out of sync
			if(yScroll > -500){
			yScroll-=1; // 2.9 is the max speed we can use, after that the background will go out of sync
			}
			else if(yScroll > -5000 && yScroll <= -500){
				yScroll-=1.5;
			}
			else if (yScroll > -10000 && yScroll <= -5000){
				yScroll -=2.0;
			}
			else{
				yScroll -= 2.5;
			}
			*/
			yScroll -= 2.0;
			if (-yScroll % 350 == 0) map+=1; //335//dont touch
			if (-yScroll > 350 && -yScroll % 160 == 0) //160//this controls the speed which the blocks r updated//dont touch
				updateBlocks();
				player.update();
		}
		
		
	if (yScroll < -20000){
		yScroll = -10000;
	}
		
		
		
		if (control && collision()) {
			
			
			System.out.print(crashtime);
			//take away player control when in fail state
			control = false;
			



		}
		
		
		
		if (!control && Input.isKeyDown(GLFW_KEY_SPACE))	//use space to restart the game
			reset = true;
		
		time += 0.01f;
		
	}
	
	private void renderBlocks() {//rendering of the blocks
		Shader.BLOCK.enable();
		Shader.BLOCK.setUniform2f("player", 0, player.getX());
		Shader.BLOCK.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(0.0f, yScroll * 0.05f, 0.0f))); //0.1//the speed of the blocks//dont touch
		Block.getTexture().bind();//choosing the layer to work on it
		Block.getMesh().bind();
		
		for (int i = 0; i < 10 * 5; i++) {
			Shader.BLOCK.setUniformMat4f("ml_matrix", blocks[i].getModelMatrix());
		
			Block.getMesh().draw();
		}
		Block.getMesh().unbind();//unable write to the layer
		Block.getTexture().unbind();
	}
	public boolean collosionDetection(){
		return collision();
	}
	private boolean collision() {

		for (int i = 0; i < 10 * 5; i++) {
			float by = -yScroll * 0.05f -4;
			float bx = player.getX();
			float px = blocks[i].getX();
			float py = blocks[i].getY();
			//collision box for player with adjusted hit box to give a feeling of better handling. 
			float bx0 = bx ; //left side
			float bx1 = bx + player.getWidth()-0.35f; //right side
			float by0 = by +0.5f; //bottom
			float by1 = by + player.getHeight() -0.25f; //top
			//collision box for block
			float px0 = px;
			float px1 = px + Block.getWidth()-0.35f;
			float py0 = py +0.08f;
			float py1 = py + Block.getHeight()-.40f;
			//checking collision
			if (bx1 > px0 && bx0 < px1) {
				if (by1 > py0 && by0 < py1) {
					
					return true;
				}
			}
			//check hitting sides of the road
			else if (bx0 < -7.4 || bx1 > 7.4){

				return true;
			}
		}


		return false;
		
		
		
	}
	
	public boolean isGameOver() {//reset level

		
		return reset;
	}
	
	public void render() {
		bgTexture.bind();
		//lighting
		Shader.BG.enable();
		Shader.BG.setUniform2f("player", player.getX()+0.8f,0-0.1f); //setting light position hor, ver
		background.bind();
		//the speed which the background is drew
		for (int i = map; i < map + 5; i++) {
			Shader.BG.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(0.0f, i * 10 + yScroll * 0.03f, 0.0f)));
			background.draw();
		}
		Shader.BG.disable();
		bgTexture.unbind();
		
		renderBlocks();// block on and off
		player.render();//player on and off
		//endscreen
		if(control==false){
			crashtime += 0.0002f;
		Shader.ENDFADE.enable();
		Shader.ENDFADE.setUniform1f("time", crashtime);//
		//System.out.print(crashtime);
		endfade.render();//endfade on and off
		Shader.ENDFADE.disable();
		}
		
	
		
		Shader.FADE.enable();
		Shader.FADE.setUniform1f("time", time);
		fade.render();//fade on and off
		Shader.FADE.disable();
		
		
		
	}
	
}

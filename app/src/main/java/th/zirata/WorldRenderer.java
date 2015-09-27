package th.zirata;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Vector2;

public class WorldRenderer {

	static final float FRUSTUM_WIDTH = 320;
	static final float FRUSTUM_HEIGHT = 480;
	GLGraphics glGraphics;
	World world;
	Camera2D cam;
	SpriteBatcher batcher;
	BlockRenderer blockRenderer;
	
	public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world){
		this.glGraphics = glGraphics;
		this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.batcher = batcher;
		setTurretDirections();
		blockRenderer = new BlockRenderer();
	}
	
	public  void render(){
		cam.setViewportAndMatrices();
		renderBackground();
		renderObjects();
	}

	public void renderBackground(){
		batcher.beginBatch(Assets.backgroundTextures);
		for(int i = 0; i < world.backgrounds.size(); i++){
			Background currBackground = world.backgrounds.get(i);
			batcher.drawSprite(currBackground.position.x, currBackground.position.y, currBackground.bounds.width, currBackground.bounds.height, world.worldAngle, Assets.textureRegions.get("Background"));
			batcher.drawSprite(currBackground.position.x, currBackground.position.y, currBackground.bounds.width, currBackground.bounds.height, world.worldAngle, Assets.textureRegions.get("NearStarBG"));
			batcher.drawSprite(currBackground.position.x, currBackground.position.y, currBackground.bounds.width, currBackground.bounds.height, world.worldAngle, Assets.textureRegions.get("StarBG"));
		}
		batcher.endBatch();
	}
	
	public void renderObjects(){
		GL10 gl = glGraphics.getGL();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		renderPlayer();
		renderEnemies();
		renderEnemyBullets();
		renderText();
	}

	public void setTurretDirections(){
		ArrayList<Block> tBlocks = getBlocksFromType(TurretBlock.class);
		for(Block currBlock : tBlocks){
			TurretBlock tBlock = (TurretBlock)currBlock;
			tBlock.setBeginningLastTouch();
		}
	}

	public ArrayList<Block> getBlocksFromType(Class clazz){
		ArrayList<Block> blocksRequested = new ArrayList<Block>();
		for(int i = 0; i < PlayerSave.bankedBlocks.size(); i++){
			Block currBlock = PlayerSave.bankedBlocks.get(i);
			if(currBlock.getClass() == clazz){
				blocksRequested.add(currBlock);
			}
		}
		return blocksRequested;
	}

	private void renderPlayer(){

		if(world.player.playerBlocks.size() > 0){
			batcher.beginBatch(Assets.blockTextures);

			for(int i = 0; i < world.player.playerBlocks.size(); i++){
				Block currBlock = world.player.playerBlocks.get(i);
				blockRenderer.renderGameBlock(currBlock, batcher);
			}
			Bullet b;
			for(int i = 0; i < World.PLAYER_BULLETS.size(); i++){
				b = World.PLAYER_BULLETS.get(i);
				batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));
			}
		}
	    batcher.endBatch();
	}

	private void renderEnemies(){
		if(world.enemies.size() > 0){

			try{
				for(int i = 0; i < world.enemies.size(); i++){
					for(int j = 0; j < world.enemies.get(i).enemyBlocks.size(); j++){
						Block currBlock = world.enemies.get(i).enemyBlocks.get(j);
						if(currBlock.getClass().equals(EnemyTurretBlock.class)){
							batcher.beginBatch(Assets.factionTextures);
							EnemyTurretBlock currEnemy = (EnemyTurretBlock) currBlock;
							Vector2 rotate = new Vector2(160,240);
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.textureRegions.get("greenTurretBase"));
							//TODO Make turrets actually point at the right spot
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 36, 36, rotate.sub(currEnemy.position.x, currEnemy.position.y).angle(), Assets.textureRegions.get("greenTurretTop"));
							batcher.endBatch();
							EnemyTurretBlock tBlock = (EnemyTurretBlock)currBlock;
							Bullet b;
							for(int k = 0; k < tBlock.bullets.size(); k++){
								batcher.beginBatch(Assets.blockTextures);
								b = tBlock.bullets.get(k);
								batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));
								batcher.endBatch();
							}

						}

						else if(currBlock.getClass().equals(ArmorBlock.class)){

							batcher.beginBatch(Assets.blockTextures);
							if(currBlock.health <= currBlock.maxHealth && currBlock.health > currBlock.maxHealth*.7){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("FullHealthArmorBlock"));
							}
							else if(currBlock.health <= currBlock.maxHealth*.7 && currBlock.health > currBlock.maxHealth*.3){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("MidHealthArmorBlock"));
							}
							else if(currBlock.health <= currBlock.maxHealth*.3 && currBlock.health > 0){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("LowHealthArmorBlock"));
							}
							batcher.endBatch();
						}

						else if(currBlock.getClass().equals(EnergyBlock.class)){
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyBlock"));
						}

						else{
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.textureRegions.get("BaseBlock"));
						}
					}
				}

			}
			catch(Exception e){
				//Enemy dies while trying to draw
			}
		}
	}

	private void renderEnemyBullets(){
		if(world.enemyBullets.size() > 0){
			batcher.beginBatch(Assets.blockTextures);
			Bullet b;
			for(int i = 0; i < world.enemyBullets.size(); i++){
				b = world.enemyBullets.get(i);
				batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));

			}
			batcher.endBatch();
		}
	}

	private void renderText(){
		batcher.beginBatch(Assets.mainMenuTextures);
		Assets.font.drawText(batcher, "Energy: " + world.player.energy + " ", 16, 480 - 20);

		batcher.drawSprite(285, 30, 60, 60, Assets.textureRegions.get("PowerButton"));
		batcher.endBatch();
		batcher.beginBatch(Assets.blockTextures);
		Vector2 rotate = new Vector2();
		if (world.moveLeft){
			rotate.set(1,1);
			batcher.drawSprite(85, 45, 15, 130, rotate.sub(new Vector2(0,0)).angle()-90, Assets.textureRegions.get("GreenBullet"));
		}
		else if(world.moveRight){
			rotate.set(-1,1);
			batcher.drawSprite(35, 45, 15, 130, rotate.sub(new Vector2(0,0)).angle()-90, Assets.textureRegions.get("GreenBullet"));
		}
		else{
			batcher.drawSprite(60, 55, 15, 130, Assets.textureRegions.get("GreenBullet"));
		}

		batcher.drawSprite(280, 450, 5, 35, Assets.textureRegions.get("BaseBlock"));
		batcher.drawSprite(290, 450, 5, 35, Assets.textureRegions.get("BaseBlock"));

		batcher.endBatch();
	}
}

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
	
	public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world){
		this.glGraphics = glGraphics;
		this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.batcher = batcher;
		setTurretDirections();
	}
	
	public  void render(){
		cam.setViewportAndMatrices();
		renderBackground();
		renderObjects();
	}

	public void renderBackground(){
		batcher.beginBatch(Assets.backgroundTextures);
		batcher.drawSprite(160, 240, 320, 480, world.worldAngle, Assets.textureRegions.get("Background"));
		batcher.drawSprite(160, 240, 320, 480, world.worldAngle, Assets.textureRegions.get("NearStarBG"));
		batcher.drawSprite(160, 240, 320, 480, world.worldAngle, Assets.textureRegions.get("StarBG"));
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
				if(currBlock.getClass().equals(TurretBlock.class)){
					TurretBlock tBlock = (TurretBlock)currBlock;
					Vector2 rotate = new Vector2(currBlock.lastTouch);
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.textureRegions.get("TurretBase"));
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, rotate.sub(currBlock.position).angle()-90, Assets.textureRegions.get("TurretTop"));

					if(tBlock.active){
						batcher.drawSprite(currBlock.position.x - 8 + 3, currBlock.position.y - 8, 5, 5, Assets.textureRegions.get("GreenBullet"));
						//There is a bug here with drawing the fire arcs. They need to respect the upgraded range. Added a temporary fix because we can't figure out the real problem
						batcher.drawSprite(tBlock.coneX1, tBlock.coneY1, tBlock.fireRange + tBlock.getAttributeLevel(3)*3.5f, 1, (tBlock.fireAngle + tBlock.fireArcAngle), Assets.textureRegions.get("Bullet"));
						batcher.drawSprite(tBlock.coneX2, tBlock.coneY2, tBlock.fireRange + tBlock.getAttributeLevel(3)*3.5f, 1, (tBlock.fireAngle - tBlock.fireArcAngle), Assets.textureRegions.get("Bullet"));
					}
					else{
						batcher.drawSprite(currBlock.position.x - 8 + 3, currBlock.position.y - 8, 5, 5, Assets.textureRegions.get("Bullet"));
					}

				}

				else if(currBlock.getClass().equals(ArmorBlock.class)){
					batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("ArmorBlock"));
				}
				
				else if(currBlock.getClass().equals(MultiplierBlock.class)){
					MultiplierBlock mBlock = (MultiplierBlock)currBlock;
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.textureRegions.get("MultiplierBlock"));
					if(mBlock.state == MultiplierBlock.MULTIPLIER_READY){
						batcher.drawSprite(currBlock.position.x , currBlock.position.y + 2, 10, 10, Assets.textureRegions.get("Bullet"));
					}
					else if(mBlock.state == MultiplierBlock.MULTIPLIER_MULTIPLYING){
						batcher.drawSprite(currBlock.position.x, currBlock.position.y +2, 10, 10, Assets.textureRegions.get("GreenBullet"));
					}
					else if(mBlock.state == MultiplierBlock.MULTIPLIER_COOLING){
						batcher.drawSprite(currBlock.position.x, currBlock.position.y + 2, 10, 10, Assets.textureRegions.get("YellowBullet"));
					}

				}

				else if(currBlock.getClass().equals(EnergyBlock.class)){
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyBlock"));
				}

				else{
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.textureRegions.get("BaseBlock"));
				}
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
				batcher.beginBatch(Assets.blockTextures);
				for(int i = 0; i < world.enemies.size(); i++){
					for(int j = 0; j < world.enemies.get(i).enemyBlocks.size(); j++){
						Block currBlock = world.enemies.get(i).enemyBlocks.get(j);
						if(currBlock.getClass().equals(EnemyTurretBlock.class)){
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.textureRegions.get("TurretBase"));
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.textureRegions.get("TurretTop"));
							EnemyTurretBlock tBlock = (EnemyTurretBlock)currBlock;
							Bullet b;
							for(int k = 0; k < tBlock.bullets.size(); k++){
								b = tBlock.bullets.get(k);
								batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));
							}
						}

						else if(currBlock.getClass().equals(ArmorBlock.class)){
							if(currBlock.health <= currBlock.maxHealth && currBlock.health > currBlock.maxHealth*.7){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("FullHealthArmorBlock"));
							}
							else if(currBlock.health <= currBlock.maxHealth*.7 && currBlock.health > currBlock.maxHealth*.3){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("MidHealthArmorBlock"));
							}
							else if(currBlock.health <= currBlock.maxHealth*.3 && currBlock.health > 0){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.textureRegions.get("LowHealthArmorBlock"));
							}
						}

						else if(currBlock.getClass().equals(EnergyBlock.class)){
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.textureRegions.get("EnergyBlock"));
						}

						else{
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.textureRegions.get("BaseBlock"));
						}
					}
				}
				batcher.endBatch();
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

		//batcher.drawSprite(30, 30, -60, 60, Assets.textureRegions.get("Arrow"));
		//batcher.drawSprite(60, 30, 60, 60, Assets.textureRegions.get("Arrow"));
		batcher.drawSprite(285, 30, 60, 60, Assets.textureRegions.get("PowerButton"));
		batcher.endBatch();
		batcher.beginBatch(Assets.blockTextures);
		Vector2 rotate = new Vector2();
		if (world.moveLeft){
			rotate.set(1,1);
			batcher.drawSprite(60, 30, 15, 145, rotate.sub(new Vector2(0,0)).angle()-90, Assets.textureRegions.get("GreenBullet"));
		}
		else if(world.moveRight){
			rotate.set(-1,1);
			batcher.drawSprite(30, 30, 15, 145, rotate.sub(new Vector2(0,0)).angle()-90, Assets.textureRegions.get("GreenBullet"));
		}
		else{
			batcher.drawSprite(45, 30, 15, 145, Assets.textureRegions.get("GreenBullet"));
		}

		batcher.endBatch();
	}
}

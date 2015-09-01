package th.zirata;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.AndroidGraphics;
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
	}
	
	public  void render(){
		cam.setViewportAndMatrices();
		renderBackground();
		renderObjects();
	}

	public void renderBackground(){
		batcher.beginBatch(Assets.backgroundTextures);
		batcher.drawSprite(160, 240, 320, 480, world.worldAngle, Assets.backgroundRegion);
		batcher.drawSprite(160, 240, 320, 480, world.worldAngle, Assets.nearStarRegion);
		batcher.drawSprite(160, 240, 320, 480, world.worldAngle, Assets.farStarRegion);
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
	
	private void renderPlayer(){

		if(world.player.playerBlocks.size() > 0){
			batcher.beginBatch(Assets.blockTextures);

			for(int i = 0; i < world.player.playerBlocks.size(); i++){
				Block currBlock = world.player.playerBlocks.get(i);
				if(currBlock.getClass().equals(TurretBlock.class)){
					TurretBlock tBlock = (TurretBlock)currBlock;
					Vector2 rotate = new Vector2(currBlock.lastTouch);
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.turretBaseRegion);
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, rotate.sub(currBlock.position).angle()-90, Assets.turretTopRegion);

					if(tBlock.active){
						batcher.drawSprite(currBlock.position.x - 8 + 3, currBlock.position.y - 8, 5, 5, Assets.greenBulletRegion);

						batcher.drawSprite(tBlock.coneX1, tBlock.coneY1, tBlock.fireRange, 1, (tBlock.fireAngle + tBlock.fireArcAngle), Assets.bulletRegion);
						batcher.drawSprite(tBlock.coneX2, tBlock.coneY2, tBlock.fireRange, 1, (tBlock.fireAngle - tBlock.fireArcAngle), Assets.bulletRegion);

					}
					else{
						batcher.drawSprite(currBlock.position.x - 8 + 3, currBlock.position.y - 8, 5, 5, Assets.bulletRegion);
					}

				}

				else if(currBlock.getClass().equals(ArmorBlock.class)){
					batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.armorBlockRegion);
				}
				
				else if(currBlock.getClass().equals(MachineGunBlock.class)){
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, currBlock.lastTouch.sub(currBlock.position).angle(), Assets.machineGunBlockRegion);
				}

				else if(currBlock.getClass().equals(EnergyBlock.class)){
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.energyBlockRegion);
				}

				else{
					batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.baseBlockRegion);
				}
			}
			Bullet b;
			for(int i = 0; i < world.PLAYER_BULLETS.size(); i++){
				b = world.PLAYER_BULLETS.get(i);
				batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.bulletRegion);
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
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.turretBaseRegion);
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets. turretTopRegion);
							EnemyTurretBlock tBlock = (EnemyTurretBlock)currBlock;
							Bullet b;
							for(int k = 0; k < tBlock.bullets.size(); k++){
								b = tBlock.bullets.get(k);
								batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.bulletRegion);
							}
						}

						else if(currBlock.getClass().equals(ArmorBlock.class)){
							if(currBlock.health <= currBlock.maxHealth && currBlock.health > currBlock.maxHealth*.7){
								//batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.armorBlockRegion);
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.fullArmorBlockRegion);
							}
							if(currBlock.health <= currBlock.maxHealth*.7 && currBlock.health > currBlock.maxHealth*.3){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.midArmorBlockRegion);
							}
							if(currBlock.health <= currBlock.maxHealth*.3 && currBlock.health > 0){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.lowArmorBlockRegion);
							}
						}

						else if(currBlock.getClass().equals(EnergyBlock.class)){
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.energyBlockRegion);
						}

						else{
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.baseBlockRegion);
						}
					}
				}
				batcher.endBatch();
			}
			catch(Exception e){

			}
		}
	}

	private void renderEnemyBullets(){
		if(world.enemyBullets.size() > 0){
			batcher.beginBatch(Assets.blockTextures);
			Bullet b;
			for(int i = 0; i < world.enemyBullets.size(); i++){
				b = world.enemyBullets.get(i);
				batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.bulletRegion);

			}
			batcher.endBatch();
		}
	}

	private void renderText(){
		batcher.beginBatch(Assets.mainMenuTextures);
		Assets.font.drawText(batcher, "Energy: " + world.player.energy + " ", 16, 480 - 20);
		batcher.endBatch();
	}
}

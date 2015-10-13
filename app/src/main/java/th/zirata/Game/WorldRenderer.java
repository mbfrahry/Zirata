package th.zirata.Game;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.EnemyShips.Spinner;
import th.zirata.Menus.PopupManager;
import th.zirata.Settings.Assets;
import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.BlockRenderer;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Blocks.EnergyBlock;
import th.zirata.Blocks.TurretBlock;
import th.zirata.Blocks.Bullet;
import th.zirata.Settings.PlayerSave;

public class WorldRenderer {

	static final float FRUSTUM_WIDTH = 320;
	static final float FRUSTUM_HEIGHT = 480;
	GLGraphics glGraphics;
	World world;
	Camera2D cam;
	SpriteBatcher batcher;
	BlockRenderer blockRenderer;
    PopupManager popupManager;
    Rectangle worldBounds;
	
	public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world, PopupManager popManager){
		this.glGraphics = glGraphics;
		this.world = world;
        this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.batcher = batcher;
		setTurretDirections();
		blockRenderer = new BlockRenderer();
        this.popupManager = popManager;
        worldBounds = new Rectangle(0,0, 320, 480);
	}
	
	public  void render(){
		cam.setViewportAndMatrices();

		GL10 gl = glGraphics.getGL();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batcher.beginBatch(Assets.imageTextures);
		renderBackground();
		renderObjects();
        popupManager.drawUIExtras();
		batcher.endBatch();

		gl.glDisable(GL10.GL_BLEND);
	}

	public void renderBackground(){
		for(int i = 0; i < world.backgrounds.size(); i++){
			Background currBackground = world.backgrounds.get(i);
			batcher.drawSprite(currBackground.position.x, currBackground.position.y, currBackground.bounds.width, currBackground.bounds.height, currBackground.bounds.rotationAngle.angle(), Assets.textureRegions.get("background"));
		}
		for (int i = 0; i < world.farBackgrounds.size(); i++){
			Background currBackground = world.farBackgrounds.get(i);
			batcher.drawSprite(currBackground.position.x, currBackground.position.y, currBackground.bounds.width, currBackground.bounds.height, currBackground.bounds.rotationAngle.angle(), Assets.textureRegions.get("StarBG"));
		}
		for (int i = 0; i < world.nearBackgrounds.size(); i++){
			Background currBackground = world.nearBackgrounds.get(i);
			batcher.drawSprite(currBackground.position.x, currBackground.position.y, currBackground.bounds.width, currBackground.bounds.height, currBackground.bounds.rotationAngle.angle(), Assets.textureRegions.get("NearStarBG"));
		}
	}
	
	public void renderObjects(){
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

			for(int i = 0; i < world.player.playerBlocks.size(); i++){
				Block currBlock = world.player.playerBlocks.get(i);
				blockRenderer.renderGameBlock(currBlock, batcher);
			}
			Bullet b;
			for(int i = 0; i < World.playerBullets.size(); i++){
				b = World.playerBullets.get(i);
				batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));
                //for (Vector2 v : b.bounds.vertices) {
				//	batcher.drawSprite(v.x, v.y, 2, 2, Assets.textureRegions.get("Bullet"));
				//}
			}
		}
	}

	private void renderEnemies(){
		if(world.enemyManager.enemies.size() > 0){

			try{
				for(int i = 0; i < world.enemyManager.enemies.size(); i++){
					for(int j = 0; j < world.enemyManager.enemies.get(i).enemyBlocks.size(); j++){
						Block currBlock = world.enemyManager.enemies.get(i).enemyBlocks.get(j);
                        if(!OverlapTester.pointInRectangle(worldBounds, currBlock.position)){
                            Vector2 ePosition = currBlock.position;
                            if(ePosition.x < 0){
                                batcher.drawSprite(0, ePosition.y, 5, 50, Assets.textureRegions.get("Bullet"));
                            }
                            else if(ePosition.x > 320){
                                batcher.drawSprite(315, ePosition.y, 5, 50, Assets.textureRegions.get("Bullet"));
                            }
                            else if(ePosition.y < 0){
                                batcher.drawSprite(ePosition.x, 0, 50, 7, Assets.textureRegions.get("Bullet"));
                            }
                            else{
                                batcher.drawSprite(ePosition.x, 475, 50, 5, Assets.textureRegions.get("Bullet"));
                            }

                        }

    					//for (Vector2 v : currBlock.bounds.vertices) {

							//batcher.drawSprite(v.x, v.y, 8, 8, Assets.textureRegions.get("Bullet"));

						//}
						if(currBlock.getClass().equals(EnemyTurretBlock.class)){
							EnemyTurretBlock currEnemy = (EnemyTurretBlock) currBlock;
							Vector2 rotate = new Vector2(160,240);
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, currEnemy.bounds.rotationAngle.angle() - 90, Assets.textureRegions.get("PurpleBase3"));
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, rotate.sub(currEnemy.position.x, currEnemy.position.y).angle() - 90, Assets.textureRegions.get("PurpleTurret3"));
//							batcher.drawSprite(currEnemy.bounds.lowerLeft.x  , currEnemy.bounds.lowerLeft.y, 8, 8, Assets.textureRegions.get("Bullet"));

							EnemyTurretBlock tBlock = (EnemyTurretBlock)currBlock;
							Bullet b;
							for(int k = 0; k < tBlock.bullets.size(); k++){
								b = tBlock.bullets.get(k);
								batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));
							}

						}

						else if(currBlock.getClass().equals(ArmorBlock.class)){
//							batcher.drawSprite(currBlock.bounds.lowerLeft.x  , currBlock.bounds.lowerLeft.y, 8, 8, Assets.textureRegions.get("Bullet"));
							if(currBlock.health <= currBlock.maxHealth && currBlock.health > currBlock.maxHealth*.7){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, currBlock.bounds.rotationAngle.angle() - 90, Assets.textureRegions.get("PurpleBase3"));
							}
							else if(currBlock.health <= currBlock.maxHealth*.7 && currBlock.health > currBlock.maxHealth*.3){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, currBlock.bounds.rotationAngle.angle() - 90, Assets.textureRegions.get("PurpleBase2"));
							}
							else if(currBlock.health <= currBlock.maxHealth*.3 && currBlock.health > 0){
								batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, currBlock.bounds.rotationAngle.angle() - 90, Assets.textureRegions.get("PurpleBase1"));
							}
						}

						else if(currBlock.getClass().equals(EnergyBlock.class)){
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, currBlock.bounds.rotationAngle.angle() - 90, Assets.textureRegions.get("EnergyBlock"));
//							batcher.drawSprite(currBlock.bounds.lowerLeft.x  , currBlock.bounds.lowerLeft.y, 8, 8, Assets.textureRegions.get("Bullet"));
						}

						else{
							batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, currBlock.bounds.rotationAngle.angle() - 90, Assets.textureRegions.get("BaseBlock"));
//							batcher.drawSprite(currBlock.bounds.lowerLeft.x  , currBlock.bounds.lowerLeft.y, 8, 8, Assets.textureRegions.get("Bullet"));
						}
//						batcher.drawSprite(currBlock.position.x, currBlock.position.y, 8, 8, Assets.textureRegions.get("Bullet"));
//						if(world.enemyManager.enemies.get(i).getClass().equals(Spinner.class)){
//							Spinner currEnemy = (Spinner)world.enemyManager.enemies.get(i);
//							batcher.drawSprite(currEnemy.shipMidPoint.x, currEnemy.shipMidPoint.y, 8, 8, Assets.textureRegions.get("GreenBullet"));
//						}
					}
				}

			}
			catch(Exception e){
				//Enemy dies while trying to draw
			}
		}
	}

	private void renderEnemyBullets(){
		if(world.enemyManager.enemyBullets.size() > 0){
			Bullet b;
			for(int i = 0; i < world.enemyManager.enemyBullets.size(); i++){
				b = world.enemyManager.enemyBullets.get(i);
				batcher.drawSprite(b.position.x, b.position.y, 5, 5, Assets.textureRegions.get("Bullet"));

			}
		}
	}

	private void renderText(){
		Assets.font.drawText(batcher, "Energy: " + world.player.energy + " ", 16, 480 - 20);

		batcher.drawSprite(285, 30, 60, 60, Assets.textureRegions.get("PowerButton"));
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

	}
}

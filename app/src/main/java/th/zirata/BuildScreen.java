package th.zirata;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class BuildScreen extends GLScreen{

	Camera2D guiCam;
	SpriteBatcher batcher;
	Rectangle backBounds;
	Rectangle forwardBounds;
	Rectangle blockMenuBounds;
	Vector2 touchPoint;
	ArrayList<Block> potentialBlocks;

	boolean showBlockMenu;
	
	public BuildScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        backBounds = new Rectangle(0, 0, 64, 64);
        forwardBounds = new Rectangle(320-64, 0, 64, 64);
		blockMenuBounds = new Rectangle(130, 0, 64, 64);
		touchPoint = new Vector2();
		batcher = new SpriteBatcher(glGraphics, 500);
		potentialBlocks = new ArrayList<Block>();
		getPotentialBlocks();
		Settings.spaceBucks = 100;
		showBlockMenu = false;
    }

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for(int i = 0; i < len; i++){
			TouchEvent event = touchEvents.get(i);
			touchPoint.set(event.x, event.y);
			guiCam.touchToWorld(touchPoint);
			
			if(event.type == TouchEvent.TOUCH_UP){
				if(OverlapTester.pointInRectangle(backBounds, touchPoint)){
					game.setScreen(new MainMenuScreen(game));
					return;
				}
				
				if(OverlapTester.pointInRectangle(forwardBounds, touchPoint)){
					Settings.save(game.getFileIO());
					PlayerSave.save(game.getFileIO());
					game.setScreen(new GameScreen(game));
					return;
				}

				if(OverlapTester.pointInRectangle(blockMenuBounds, touchPoint)){
					showBlockMenu = true;
					return;
				}
				
				Rectangle pBlockBounds;
				for(int j = 0; j < potentialBlocks.size(); j++){
					Block currBlock = potentialBlocks.get(j);
					pBlockBounds = new Rectangle(currBlock.position.x -12, currBlock.position.y - 12, 25, 25);
					if(OverlapTester.pointInRectangle(pBlockBounds, touchPoint)){
						if(Settings.spaceBucks >= Settings.nextBlockCost){
							PlayerSave.playerBlocks.add(currBlock);
							PlayerSave.save(game.getFileIO());
							potentialBlocks.remove(j);
							getPotentialBlocks();
							Settings.spaceBucks -= Settings.nextBlockCost;
							Settings.nextBlockCost = PlayerSave.playerBlocks.size();
							Settings.save(game.getFileIO());
						}
						return;
					}
				}
				
				for(int j = 0; j < PlayerSave.playerBlocks.size(); j++){
					Block currBlock = PlayerSave.playerBlocks.get(j);
					pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
					if(OverlapTester.pointInRectangle(pBlockBounds, touchPoint)){
						game.setScreen(new BlockUpgradeScreen(game, j));
						return;
					}
				}
			}
		}
		
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		
		batcher.beginBatch(Assets.backgroundTextures);
		batcher.drawSprite(160, 240, 320, 480, Assets.backgroundRegion);
		batcher.drawSprite(160, 240, 320, 480, Assets.nearStarRegion);
		batcher.drawSprite(160, 240, 320, 480, Assets.farStarRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		
		batcher.beginBatch(Assets.blockTextures);
		for(int i = 0; i < PlayerSave.playerBlocks.size(); i++){
			Block currBlock = PlayerSave.playerBlocks.get(i);
			if(currBlock.getClass().equals(TurretBlock.class)){
				batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets.turretBaseRegion);
				batcher.drawSprite(currBlock.position.x  , currBlock.position.y, 24, 24, Assets. turretTopRegion);
			}
			
			else if(currBlock.getClass().equals(ArmorBlock.class)){
				batcher.drawSprite(currBlock.position.x , currBlock.position.y, 24 , 24, Assets.armorBlockRegion);
			}
			else if (currBlock.getClass().equals(MultiplierBlock.class)){
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.multiplierBlockRegion);
			}
			else if (currBlock.getClass().equals(EnergyBlock.class)){
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.energyBlockRegion);
			}
			else{
				batcher.drawSprite(currBlock.position.x  , currBlock.position.y , 24, 24, Assets.baseBlockRegion);
			}
		}
		
		for(int i = 0; i < potentialBlocks.size(); i++){
			Block currBlock = potentialBlocks.get(i);
			batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24 , 24 , Assets.potentialBlockRegion);
		}


		batcher.endBatch();
		
		batcher.beginBatch(Assets.mainMenuTextures);
		batcher.drawSprite(290, 30, 60, 60, Assets.arrowRegion);
		batcher.drawSprite(30, 30, -60, 60, Assets.arrowRegion);
		Assets.font.drawText(batcher, "SpaceBucks: " + Settings.spaceBucks + " ", 16, 480 - 20);
		Assets.font.drawText(batcher, "Next Block Cost: " + Settings.nextBlockCost, 16, 480-40);


		batcher.drawSprite(160, 30, 180, 45, Assets.rectangleRegion);
		Assets.font.drawText(batcher, "Block Bank", 85, 30);

		batcher.endBatch();
		
		gl.glDisable(GL10.GL_BLEND);
		
	}
	
	public void getPotentialBlocks(){
		ArrayList<Integer> emptyBlock = new ArrayList<Integer>();
		for(int i = 0; i < PlayerSave.playerBlocks.size(); i++){
			Block currBlock = PlayerSave.playerBlocks.get(i);
			
			emptyBlock = checkAdjacentBlocks(currBlock);
			for(int j = 0; j < emptyBlock.size(); j++){
				Block newBlock = new Block(0, 0, 10, 0);
				if(emptyBlock.get(j) == 0){
					newBlock.position.x = currBlock.position.x;
					newBlock.position.y = currBlock.position.y + 25;
					addPotentialBlock(newBlock);
				}
				if(emptyBlock.get(j) == 1){
					newBlock.position.x = currBlock.position.x + 25;
					newBlock.position.y = currBlock.position.y;
					addPotentialBlock(newBlock);
				}
				if(emptyBlock.get(j) == 2){
					newBlock.position.x = currBlock.position.x;
					newBlock.position.y = currBlock.position.y - 25;
					addPotentialBlock(newBlock);
				}
				if(emptyBlock.get(j) == 3){
					newBlock.position.x = currBlock.position.x - 25;
					newBlock.position.y = currBlock.position.y;
					addPotentialBlock(newBlock);
				}
			}
			
		}
	}

	public ArrayList<Integer> checkAdjacentBlocks(Block block){
		ArrayList<Integer> emptyBlock = new ArrayList<Integer>(){{
			add(0);
			add(1);
			add(2);
			add(3);
		}};
		
		for(int i = 0; i < PlayerSave.playerBlocks.size(); i++){
			if(emptyBlock.size() == 0){
				continue;
			}
			Block currBlock = PlayerSave.playerBlocks.get(i);
			if(block.position.y + 25 == currBlock.position.y && block.position.x == currBlock.position.x){
				emptyBlock.remove(new Integer(0));
			}
			
			if(block.position.x + 25 == currBlock.position.x && block.position.y == currBlock.position.y){
				emptyBlock.remove(new Integer(1));
			}
			
			if(block.position.y - 25 == currBlock.position.y && block.position.x == currBlock.position.x){
				emptyBlock.remove(new Integer(2));
			}
			
			if(block.position.x - 25 == currBlock.position.x && block.position.y == currBlock.position.y){
				emptyBlock.remove(new Integer(3));
			}


		}
		return emptyBlock;
	}
	
	public void addPotentialBlock(Block newBlock){
		if(newBlock.position.x < 20 || newBlock.position.x + 25 > 320){
			return;
		}
		if(newBlock.position.y < 0 || newBlock.position.y +25 > 480)
			return;
		
		if(newBlock.position.x < 64 && newBlock.position.y < 75)
			return;
		if(newBlock.position.x > 250 && newBlock.position.y < 75)
			return;
		for(int i = 0; i < potentialBlocks.size(); i++){
			Block currBlock = potentialBlocks.get(i);
			if(newBlock.position.x == currBlock.position.x && newBlock.position.y == currBlock.position.y){
				return;
			}
		}
		potentialBlocks.add(newBlock);
	}
	
	@Override
	public void pause() {
		Settings.save(game.getFileIO());
		PlayerSave.save(game.getFileIO());
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}

package th.zirata;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

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
	Rectangle closeBlockMenuBounds;
	Rectangle blockBankTurretBounds;
	Rectangle blockBankArmorBounds;
	Rectangle blockBankEnergyBounds;
	Rectangle blockBankMultiplierBounds;
	Vector2 touchPoint;
	ArrayList<Block> potentialBlocks;
	ArrayList<Block> ownedBlocksByType;
	Block selectedBankBlock;
	Block selectedActiveBlock;

	boolean showBlockBank;
	int blockBankOption;
	public static final int BLOCK_BANK_TURRET = 0;
	public static final int BLOCK_BANK_ARMOR = 1;
	public static final int BLOCK_BANK_ENERGY = 2;
	public static final int BLOCK_BANK_MULTIPLIER = 3;

	boolean draggingBankBlock;

	public BuildScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        backBounds = new Rectangle(0, 0, 64, 64);
        forwardBounds = new Rectangle(320-64, 0, 64, 64);
		blockMenuBounds = new Rectangle(130, 0, 64, 64);
		closeBlockMenuBounds = new Rectangle(260, 200, 64, 64);
		blockBankTurretBounds  = new Rectangle(0, 160, 80, 50);
		blockBankArmorBounds  = new Rectangle(80, 160, 80, 50);
		blockBankEnergyBounds  = new Rectangle(160, 160, 80, 50);
		blockBankMultiplierBounds  = new Rectangle(240, 160, 80, 50);

		selectedBankBlock = null;
		selectedActiveBlock = null;

		touchPoint = new Vector2();
		batcher = new SpriteBatcher(glGraphics, 500);

		potentialBlocks = new ArrayList<Block>();
		getPotentialBlocks();
		Settings.spaceBucks = 100;
		showBlockBank = false;
		blockBankOption = BLOCK_BANK_TURRET;
		ownedBlocksByType = getBlocksFromType(TurretBlock.class);
		draggingBankBlock = false;
    }

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		if(len > 0){
			for(int i = 0; i < len; i++) {
				TouchEvent event = touchEvents.get(i);
				touchPoint.set(event.x, event.y);
				guiCam.touchToWorld(touchPoint);

				if (event.type == TouchEvent.TOUCH_UP) {


					if (selectedBankBlock != null && selectedActiveBlock == null) {
						selectedBankBlock = null;
						showBlockBank = true;
					}

					if (!showBlockBank) {
						if (OverlapTester.pointInRectangle(backBounds, touchPoint)) {
							game.setScreen(new MainMenuScreen(game));
							return;
						}

						if (OverlapTester.pointInRectangle(forwardBounds, touchPoint)) {
							Settings.save(game.getFileIO());
							PlayerSave.save(game.getFileIO());
							game.setScreen(new GameScreen(game));
							return;
						}

						if (OverlapTester.pointInRectangle(blockMenuBounds, touchPoint)) {
							showBlockBank = true;
							return;
						}

						Rectangle pBlockBounds;
						for (int j = 0; j < potentialBlocks.size(); j++) {
							Block currBlock = potentialBlocks.get(j);
							pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
							if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
								if (Settings.spaceBucks >= Settings.nextBlockCost) {
									PlayerSave.activeBlocks.add(currBlock);
									PlayerSave.save(game.getFileIO());
									potentialBlocks.remove(j);
									getPotentialBlocks();
									Settings.spaceBucks -= Settings.nextBlockCost;
									Settings.nextBlockCost = PlayerSave.activeBlocks.size();
									Settings.save(game.getFileIO());
								}
								return;
							}
						}
						/*
						for (int j = 0; j < PlayerSave.activeBlocks.size(); j++) {
							Block currBlock = PlayerSave.activeBlocks.get(j);
							pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
							if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
								game.setScreen(new BlockUpgradeScreen(game, j));
								return;
							}
						}*/
					} else {
						checkBankBlocks(touchPoint);
					}
				}

				if (event.type == TouchEvent.TOUCH_DOWN) {
					if (showBlockBank == true) {
						Rectangle bankBlockBounds;
						for (int j = 0; j < ownedBlocksByType.size(); j++) {
							Block currBlock = ownedBlocksByType.get(j);
							bankBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
							if (OverlapTester.pointInRectangle(bankBlockBounds, touchPoint)) {
								selectedBankBlock = currBlock;
							}
						}

					}

				}

				if (event.type == TouchEvent.TOUCH_DRAGGED) {
					if (selectedBankBlock != null) {
						if (showBlockBank == true) {
							showBlockBank = false;
						}
						selectedBankBlock.position.x = touchPoint.x;
						selectedBankBlock.position.y = touchPoint.y;
						Rectangle pBlockBounds;
						for (int j = 0; j < PlayerSave.activeBlocks.size(); j++) {
							Block currBlock = PlayerSave.activeBlocks.get(j);
							pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
							if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
								selectedActiveBlock = currBlock;
								return;
							}
							draggingBankBlock = true;
						}
					}
				}
			}

		}else if (draggingBankBlock){
			draggingBankBlock = false;
			if(selectedActiveBlock != null && selectedBankBlock != null){
				PlayerSave.bankedBlocks.remove(selectedBankBlock);
				PlayerSave.activeBlocks.remove(selectedActiveBlock);
				selectedBankBlock.position.x = selectedActiveBlock.position.x;
				selectedBankBlock.position.y = selectedActiveBlock.position.y;
				PlayerSave.bankedBlocks.add(selectedActiveBlock);
				PlayerSave.activeBlocks.add(selectedBankBlock);
				resetBlockBank();
				if(selectedBankBlock.getClass() == TurretBlock.class){
					game.setScreen(new BlockDirectionScreen(game, selectedBankBlock.position, PlayerSave.activeBlocks.indexOf(selectedBankBlock)));
				}
				else {
					selectedActiveBlock = null;
					selectedBankBlock = null;
				}

				return;
			}
		}
	}


	private void checkBankBlocks(Vector2 touchPoint){
		if (OverlapTester.pointInRectangle(closeBlockMenuBounds, touchPoint)) {
			showBlockBank = false;
			return;
		}
		else if (OverlapTester.pointInRectangle(blockBankTurretBounds, touchPoint) && blockBankOption != BLOCK_BANK_TURRET) {
			blockBankOption = BLOCK_BANK_TURRET;
			ownedBlocksByType = getBlocksFromType(TurretBlock.class);
			return;
		}
		else if (OverlapTester.pointInRectangle(blockBankArmorBounds, touchPoint) && blockBankOption != BLOCK_BANK_ARMOR) {
			blockBankOption = BLOCK_BANK_ARMOR;
			ownedBlocksByType = getBlocksFromType(ArmorBlock.class);
			return;
		}
		else if (OverlapTester.pointInRectangle(blockBankEnergyBounds, touchPoint) && blockBankOption != BLOCK_BANK_ENERGY) {
			blockBankOption = BLOCK_BANK_ENERGY;
			ownedBlocksByType = getBlocksFromType(EnergyBlock.class);
			return;
		}
		else if (OverlapTester.pointInRectangle(blockBankMultiplierBounds, touchPoint) && blockBankOption != BLOCK_BANK_MULTIPLIER) {
			blockBankOption = BLOCK_BANK_MULTIPLIER;
			ownedBlocksByType = getBlocksFromType(MultiplierBlock.class);
			return;
		}

		Rectangle bankBlockBounds = null;
		for(int j = 0; j < ownedBlocksByType.size(); j++){
			Block currBlock = ownedBlocksByType.get(j);
			bankBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
			if(OverlapTester.pointInRectangle(bankBlockBounds, touchPoint)){

			}
		}
		//Checks if add block was pressed
		if(bankBlockBounds == null){
			bankBlockBounds = new Rectangle(26-12, 130-12, 25, 25);
		}
		else{
			bankBlockBounds = new Rectangle(ownedBlocksByType.get(ownedBlocksByType.size() - 1).position.x -12, ownedBlocksByType.get(ownedBlocksByType.size() - 1).position.y -12, 25, 25);
			bankBlockBounds.lowerLeft.x +=30;
			if(bankBlockBounds.lowerLeft.x >  300) {
				bankBlockBounds.lowerLeft.x = 18;
				bankBlockBounds.lowerLeft.y -= 30;
			}

		}
		if (bankBlockBounds.lowerLeft.y > 15 && OverlapTester.pointInRectangle(bankBlockBounds, touchPoint)){
			Block block = null;
			if(blockBankOption  == BLOCK_BANK_TURRET){
				block = new TurretBlock(-100, -100, 10, 3, 0);
			}
			if(blockBankOption  == BLOCK_BANK_ARMOR){
				block = new ArmorBlock(-100, -100, 20);
			}
			if(blockBankOption  == BLOCK_BANK_ENERGY){
				block = new EnergyBlock(-100, -100, 10, 0, 10);
			}
			if(blockBankOption  == BLOCK_BANK_MULTIPLIER){
				block = new MultiplierBlock(-100, -100, 10, 0, 1.5f, 5, 10);
			}
			PlayerSave.bankedBlocks.add(block);
			ownedBlocksByType = getBlocksFromType(block.getClass());
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
		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			Block currBlock = PlayerSave.activeBlocks.get(i);
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
		if(selectedBankBlock != null) {
			Block currBlock = selectedBankBlock;
			if (currBlock.getClass().equals(TurretBlock.class)) {
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.turretBaseRegion);
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.turretTopRegion);
			} else if (currBlock.getClass().equals(ArmorBlock.class)) {
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.armorBlockRegion);
			} else if (currBlock.getClass().equals(MultiplierBlock.class)) {
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.multiplierBlockRegion);
			} else if (currBlock.getClass().equals(EnergyBlock.class)) {
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.energyBlockRegion);
			} else {
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.baseBlockRegion);
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
		Assets.font.drawText(batcher, "Next Block Cost: " + Settings.nextBlockCost, 16, 480 - 40);

		batcher.endBatch();

		if(showBlockBank){
			drawBlockBank();
		} else {
			batcher.beginBatch(Assets.mainMenuTextures);
			batcher.drawSprite(160, 30, 180, 45, Assets.rectangleRegion);
			Assets.font.drawText(batcher, "Block Bank", 85, 30);
			batcher.endBatch();
		}
		gl.glDisable(GL10.GL_BLEND);
		
	}

	private void drawBlockBank(){
		batcher.beginBatch(Assets.mainMenuTextures);
		batcher.drawSprite(290, 225, -60, 60, 90, Assets.arrowRegion);
		Assets.font.drawText(batcher, "Close", 245, 265);

		batcher.drawSprite(160, 100, 320, 200, Assets.rectangleRegion);

		batcher.drawSprite(40, 175, 80, 50, Assets.rectangleRegion);
		batcher.drawSprite(120, 175, 80, 50, Assets.rectangleRegion);
		batcher.drawSprite(200, 175, 80, 50, Assets.rectangleRegion);
		batcher.drawSprite(280, 175, 80, 50, Assets.rectangleRegion);
		if(blockBankOption == BLOCK_BANK_TURRET){
			batcher.drawSprite(40, 175, 80, 50, Assets.darkGrayRectangleRegion);
		}
		else if (blockBankOption == BLOCK_BANK_ARMOR){
			batcher.drawSprite(120, 175, 80, 50, Assets.darkGrayRectangleRegion);
		}
		else if (blockBankOption == BLOCK_BANK_ENERGY){
			batcher.drawSprite(200, 175, 80, 50, Assets.darkGrayRectangleRegion);
		}
		else if (blockBankOption == BLOCK_BANK_MULTIPLIER){
			batcher.drawSprite(280, 175, 80, 50, Assets.darkGrayRectangleRegion);
		}


		batcher.endBatch();

		batcher.beginBatch(Assets.blockTextures);
		batcher.drawSprite(40, 175, 24, 24, Assets.turretBaseRegion);
		batcher.drawSprite(40, 175, 24, 24, Assets.turretTopRegion);
		batcher.drawSprite(120, 175, 24, 24, Assets.armorBlockRegion);
		batcher.drawSprite(200, 175, 24, 24, Assets.energyBlockRegion);
		batcher.drawSprite(280, 175, 24, 24, Assets.multiplierBlockRegion);

		int storeX = 26;
		int storeY = 130;
		for(int i = 0; i < ownedBlocksByType.size(); i++){
			Block currBlock = ownedBlocksByType.get(i);
			currBlock.position.x = storeX;
			currBlock.position.y = storeY;
			if(blockBankOption == BLOCK_BANK_TURRET) {
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.turretBaseRegion);
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.turretTopRegion);

			}
			else if(blockBankOption == BLOCK_BANK_ARMOR){
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.armorBlockRegion);
			}
			else if(blockBankOption == BLOCK_BANK_ENERGY){
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.energyBlockRegion);
			}
			else if(blockBankOption == BLOCK_BANK_MULTIPLIER){
				batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.multiplierBlockRegion);
			}

			storeX += 30;
			if(storeX >  300){
				storeX = 26;
				storeY -= 30;
			}
		}

		batcher.endBatch();

		if(storeY > 15) {
			batcher.beginBatch(Assets.mainMenuTextures);
			batcher.drawSprite(storeX, storeY, 24, 24, Assets.addIcon);
			batcher.endBatch();
		}

	}
	
	public void getPotentialBlocks(){
		ArrayList<Integer> emptyBlock = new ArrayList<Integer>();
		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			Block currBlock = PlayerSave.activeBlocks.get(i);
			
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
		
		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			if(emptyBlock.size() == 0){
				continue;
			}
			Block currBlock = PlayerSave.activeBlocks.get(i);
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

	public void resetBlockBank(){
		if ( blockBankOption == BLOCK_BANK_TURRET) {
			ownedBlocksByType = getBlocksFromType(TurretBlock.class);
			return;
		}
		else if (blockBankOption == BLOCK_BANK_ARMOR) {
			ownedBlocksByType = getBlocksFromType(ArmorBlock.class);
			return;
		}
		else if ( blockBankOption == BLOCK_BANK_ENERGY) {
			ownedBlocksByType = getBlocksFromType(EnergyBlock.class);
			return;
		}
		else if ( blockBankOption == BLOCK_BANK_MULTIPLIER) {
			ownedBlocksByType = getBlocksFromType(MultiplierBlock.class);
			return;
		}
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

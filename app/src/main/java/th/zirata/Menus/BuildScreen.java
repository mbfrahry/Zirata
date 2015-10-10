package th.zirata.Menus;

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

import th.zirata.Blocks.ArmorBlock;
import th.zirata.Help.GameScreenHelp;
import th.zirata.Settings.Assets;
import th.zirata.Blocks.BlankBlock;
import th.zirata.Blocks.Block;
import th.zirata.Blocks.BlockRenderer;
import th.zirata.Blocks.EnergyBlock;
import th.zirata.Blocks.MultiplierBlock;
import th.zirata.Game.GameScreen;
import th.zirata.Settings.PlayerSave;
import th.zirata.Settings.Settings;
import th.zirata.Blocks.TurretBlock;

public class BuildScreen extends GLScreen{

	public Camera2D guiCam;
	boolean isPanning = false;

	public SpriteBatcher batcher;
	BlockRenderer blockRenderer;
	Rectangle backBounds;
	public Rectangle forwardBounds;
	Rectangle blockBankTurretBounds;
	Rectangle blockBankArmorBounds;
	Rectangle blockBankEnergyBounds;
	Rectangle blockBankMultiplierBounds;
	Rectangle trashBounds;
	public Rectangle closeBankBounds;
	public Vector2 touchPoint;
	public ArrayList<Block> potentialBlocks;
	public ArrayList<Block> ownedBlocksByType;
	public Block selectedBankBlock;
	public Block selectedActiveBlock;

	public boolean showSubmenu;
	public boolean showUpgrades;
	public boolean showFuse;

	public boolean showBlockBank;
	public int blockBankOption;
	public static final int BLOCK_BANK_TURRET = 0;
	public static final int BLOCK_BANK_ARMOR = 1;
	public static final int BLOCK_BANK_ENERGY = 2;
	public static final int BLOCK_BANK_MULTIPLIER = 3;

	public float minHeldTime;
	public float heldTime;

    public PopupManager popupManager;

	boolean devMode;

	public BuildScreen(Game game) {
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
		isPanning = false;

        backBounds = new Rectangle(0, 0, 64, 64);
        forwardBounds = new Rectangle(320-120, 0, 120, 60);
		blockBankTurretBounds  = new Rectangle(0, 160, 80, 40);
		blockBankArmorBounds  = new Rectangle(80, 160, 80, 40);
		blockBankEnergyBounds = new Rectangle(160, 160, 80, 40);
		blockBankMultiplierBounds  = new Rectangle(240, 160, 80, 40);
		closeBankBounds = new Rectangle(120, 200, 360, 360);
		trashBounds = new Rectangle(200, 200, 60, 30);
		selectedBankBlock = null;
		selectedActiveBlock = null;

		touchPoint = new Vector2();
		batcher = new SpriteBatcher(glGraphics, 500);
		blockRenderer = new BlockRenderer();

		potentialBlocks = new ArrayList<Block>();
		getPotentialBlocks();
		showBlockBank = false;
		showSubmenu = true;
		showUpgrades = false;
		showFuse = false;
		blockBankOption = BLOCK_BANK_TURRET;
		ownedBlocksByType = getBlocksFromType(TurretBlock.class);

		minHeldTime = 0.5f;
		heldTime = 0;
		devMode = true;
		popupManager = new PopupManager(batcher, guiCam);
    }

	public BuildScreen(Game game, boolean showBlockBank, Block activeBlock){
		this(game);
		this.showBlockBank = showBlockBank;
		this.selectedActiveBlock = activeBlock;
		blockRenderer = new BlockRenderer();
		if(testShowFuse()){
			showFuse = true;
			showUpgrades = false;
		}
		else{
			showUpgrades = true;
			showFuse = false;
		}
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


					if (devMode){
						if (OverlapTester.pointInRectangle(new Rectangle(0, 400, 64, 64), touchPoint)) {
							Settings.spaceBucks += 100;
						}
					}
					if (selectedBankBlock != null && selectedActiveBlock == null) {
						selectedBankBlock = null;
						showBlockBank = false;
					}

					if (!showBlockBank) {
						if (OverlapTester.pointInRectangle(backBounds, touchPoint)) {
							game.setScreen(new MapScreen(game));
							return;
						}

						if (OverlapTester.pointInRectangle(forwardBounds, touchPoint)) {
							if(checkForBlankBlocks()){
								popupManager.createSpriteExtra("sprite", "Rectangle", 160f, 260f, 260f, 50f, 1f, 0f);
								popupManager.createTextExtra("text", "Can't launch with blank blocks!", 160f, 260f, 8f, 8f, 1f, "white", "center");
							}
							else{
								Settings.save(game.getFileIO());
								PlayerSave.save(game.getFileIO());
								setTurretDirections();
								game.setScreen(new GameScreen (game));
								return;
							}

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
									showBlockBank = true;
									selectedActiveBlock = currBlock;
									guiCam.panToPosition(selectedActiveBlock.position.x, selectedActiveBlock.position.y, .5f);
									isPanning = true;
									resetBlockBankBounds();
									showUpgrades = true;
									showFuse = false;
								}
								else{
									popupManager.createTextExtra("text", "Bank", 217f, 395f, 12f, 12f, .25f, "red", "right");
								}
								return;
							}
						}

						for (int j = 0; j < PlayerSave.activeBlocks.size(); j++) {
							Block currBlock = PlayerSave.activeBlocks.get(j);
							pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
							if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint) ){//&& currBlock.getClass() != BlankBlock.class) {
								selectedActiveBlock = currBlock;
								guiCam.panToPosition(selectedActiveBlock.position.x, selectedActiveBlock.position.y, .5f);
								isPanning = true;
								//resetBlockBankBounds();
								if(testShowFuse()){
									showFuse = true;
									showUpgrades = false;
								}
								else{
									showUpgrades = true;
									showFuse = false;
								}

								showBlockBank = true;
								if (selectedActiveBlock.getClass() == TurretBlock.class || selectedActiveBlock.getClass() == BlankBlock.class) {
									blockBankOption = BLOCK_BANK_TURRET;
									ownedBlocksByType = getBlocksFromType(TurretBlock.class);
									return;
								}
								else if (selectedActiveBlock.getClass() == ArmorBlock.class) {
									blockBankOption = BLOCK_BANK_ARMOR;
									ownedBlocksByType = getBlocksFromType(ArmorBlock.class);
									return;
								}
								else if (selectedActiveBlock.getClass() == EnergyBlock.class) {
									blockBankOption = BLOCK_BANK_ENERGY;
									ownedBlocksByType = getBlocksFromType(EnergyBlock.class);
									return;
								}
								else if (selectedActiveBlock.getClass() == MultiplierBlock.class) {
									blockBankOption = BLOCK_BANK_MULTIPLIER;
									ownedBlocksByType = getBlocksFromType(MultiplierBlock.class);
									return;
								}
								return;
							}
						}
					} else {
						if(OverlapTester.pointInRectangle(trashBounds, touchPoint)){
							//popupManager.generatePopup("wow", 160, 240, 1f);
							PlayerSave.activeBlocks.remove(selectedActiveBlock);
							selectedActiveBlock = null;
							getPotentialBlocks();
							showFuse = false;
							showUpgrades = false;
							showBlockBank = false;
							Settings.spaceBucks += (int)Math.ceil((Settings.nextBlockCost-1)*.6);
							guiCam.panToPosition(160, 240, 1);
							isPanning = true;

							PlayerSave.save(game.getFileIO());
							return;
						}
						checkBankBlocks(touchPoint);
						if(showUpgrades && showSubmenu){
							checkUpgradeBounds(touchPoint);
						}
						else if(showFuse && showSubmenu){
							checkFuseBounds();
						}
						if(OverlapTester.pointInRectangle(closeBankBounds, touchPoint)){
							guiCam.panToPosition(160, 240, 1);
							isPanning = true;
							showBlockBank = false;
							return;
						}
					}


				}

			}

		}

		guiCam.update(deltaTime);
		if(showBlockBank && isPanning && guiCam.zoom == guiCam.finalZoom){
			resetBlockBankBounds();
			isPanning = false;
		}
	}



	public void checkBankBlocks(Vector2 touchPoint){
		if (OverlapTester.pointInRectangle(blockBankTurretBounds, touchPoint) && blockBankOption != BLOCK_BANK_TURRET) {
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

		Rectangle bankBlockBounds;
		for(int j = 0; j < ownedBlocksByType.size(); j++){

			Block currBlock = ownedBlocksByType.get(j);

			int xval =(26 + j*30)%240;
			int yval = 130 - 30*((26 + j*30)/240);

			bankBlockBounds = new Rectangle(guiCam, xval - 12, yval - 12, 25, 25);
			if (OverlapTester.pointInRectangle(bankBlockBounds, touchPoint)) {
				selectedBankBlock = currBlock;
				if (selectedActiveBlock == selectedBankBlock) {
					return;
				}
				if (!PlayerSave.activeBlocks.contains(selectedBankBlock)) {
					PlayerSave.activeBlocks.remove(selectedActiveBlock);
					selectedBankBlock.position.x = selectedActiveBlock.position.x;
					selectedBankBlock.position.y = selectedActiveBlock.position.y;
					PlayerSave.activeBlocks.add(selectedBankBlock);

				} else {
					float tempX = selectedActiveBlock.position.x;
					float tempY = selectedActiveBlock.position.y;
					selectedActiveBlock.position.x = selectedBankBlock.position.x;
					selectedActiveBlock.position.y = selectedBankBlock.position.y;
					selectedBankBlock.position.x = tempX;
					selectedBankBlock.position.y = tempY;
				}

				resetBlockBank();
				selectedActiveBlock = selectedBankBlock;
				//TODO Do this mo betta
				if(!Settings.firstTime) {
					guiCam.panToPosition(selectedActiveBlock.position.x, selectedActiveBlock.position.y, .5f);
					isPanning = true;
				}
				selectedBankBlock = null;
				showBlockBank = true;
				if(testShowFuse()){
					showFuse = true;
					showUpgrades = false;
				}
				else{
					showUpgrades = true;
					showFuse = false;
				}
			}
		}
        Rectangle addBlockBounds = new Rectangle(guiCam, 260, 5, 50, 150);
		if (ownedBlocksByType.size() < 32 && OverlapTester.pointInRectangle(addBlockBounds, touchPoint)){
			Block block;
			if(blockBankOption  == BLOCK_BANK_TURRET){
				block = new TurretBlock(-100, -100, 10, 3, 1, 90);
			}
			else if(blockBankOption  == BLOCK_BANK_ARMOR){
				block = new ArmorBlock(-100, -100, 20, 1);
			}
			else if(blockBankOption  == BLOCK_BANK_ENERGY){
				block = new EnergyBlock(-100, -100, 10, 0, 1, 10);
			}
			else{
				block = new MultiplierBlock(-100, -100, 10, 0, 1, 1.5f, 5, 10);
			}
			PlayerSave.activeBlocks.remove(selectedActiveBlock);
			block.position.x = selectedActiveBlock.position.x;
			block.position.y = selectedActiveBlock.position.y;
			selectedActiveBlock = block;
			//TODO Do this mo betta
			if(!Settings.firstTime) {
				guiCam.panToPosition(selectedActiveBlock.position.x, selectedActiveBlock.position.y, .5f);
				isPanning = true;
			}
			PlayerSave.activeBlocks.add(block);
			PlayerSave.bankedBlocks.add(block);
			showUpgrades = true;
			showFuse = false;
			ownedBlocksByType = getBlocksFromType(block.getClass());

		}

	}

	public void checkUpgradeBounds(Vector2 touchPoint){
		String[] upgradeableAttributes = selectedActiveBlock.getUpgradableAttributes();
		float[] upgradeValues = selectedActiveBlock.getUpgradeValues();
		Rectangle attrBlockBounds;
		int yStart = 190;
		if (selectedActiveBlock.getClass().equals(TurretBlock.class)){
			yStart += 34;
			attrBlockBounds =  new Rectangle(guiCam, 0, 190, 110, 37);
			if(OverlapTester.pointInRectangle(attrBlockBounds, touchPoint)){
				TurretBlock tBlock = (TurretBlock)selectedActiveBlock;
				tBlock.fireAngle += 270;
				tBlock.fireAngle %= 360;
				PlayerSave.save(game.getFileIO());
			}
		}
		for(int i = 0; i < upgradeableAttributes.length; i++) {
			attrBlockBounds =  new Rectangle(guiCam, 0, yStart + i * 40, 110, 40);
			if (OverlapTester.pointInRectangle(attrBlockBounds, touchPoint)) {
				if(!selectedActiveBlock.checkMaxAttributeLevel(i)) {
					if (Settings.spaceBucks >= selectedActiveBlock.getAttributeLevel(i) + 1) {
						Settings.spaceBucks -= selectedActiveBlock.getAttributeLevel(i) + 1;
						selectedActiveBlock.updateAttribute(i, upgradeValues[i]);
						if(testShowFuse()){
							showFuse = true;
							showUpgrades = false;
						}
						PlayerSave.save(game.getFileIO());
					}
					else{
						popupManager.createTextExtra("text", "Bank", 217f, 395f, 12f, 12f, .25f, "red", "right");
//						HashMap newExtra = createTextExtra("text", "Bank", 217f, 395f, 12f, 12f, .25f, "red", "right");
//						UIExtras.add(newExtra);
					}
				}
			}
		}
	}

	public void resetBlockBankBounds(){
		blockBankTurretBounds  = new Rectangle(guiCam, 0, 160, 80, 40);
		blockBankArmorBounds  = new Rectangle(guiCam, 80, 160, 80, 40);
		blockBankEnergyBounds = new Rectangle(guiCam, 160, 160, 80, 40);
		blockBankMultiplierBounds  = new Rectangle(guiCam, 240, 160, 80, 40);
		closeBankBounds = new Rectangle(guiCam, 120, 200, 360, 240);
		trashBounds = new Rectangle(guiCam, 200, 200, 60, 30);
	}

	public void checkFuseBounds(){
		ArrayList<Block> compatibleFusionBlocks = getCompatibleFusionBlocks();
		int fuseX = 26;
		int fuseY = 285;
		if (selectedActiveBlock.getClass().equals(TurretBlock.class)) {
			fuseY += 34;
			Rectangle rotateBounds = new Rectangle(guiCam, 0, 190, 110, 37);
			if (OverlapTester.pointInRectangle(rotateBounds, touchPoint)) {
				TurretBlock tBlock = (TurretBlock) selectedActiveBlock;
				tBlock.fireAngle += 270;
				tBlock.fireAngle %= 360;
				PlayerSave.save(game.getFileIO());
			}
		}
		int maxFuseBlocks = compatibleFusionBlocks.size() > 9 ? 9 : compatibleFusionBlocks.size();
		for (int i = 0; i < maxFuseBlocks; i++){
			Rectangle currCoords = new Rectangle(guiCam, fuseX-12, fuseY-12, 25, 25);
			Block currBlock = compatibleFusionBlocks.get(i);
			if(OverlapTester.pointInRectangle(currCoords, touchPoint)){
				//Fuse blocks together and delete selected block
				if(PlayerSave.activeBlocks.contains(currBlock)){
					popupManager.createSpriteExtra("sprite", "Rectangle", 160f, 260f, 295f, 50f, 1f, 0f);
					popupManager.createTextExtra("text", "Can't fuse with blocks on the ship!", 160f, 260f, 8f, 8f, 1f, "white", "center");
				}
				else{
					selectedActiveBlock.fuseWith(currBlock);
					PlayerSave.bankedBlocks.remove(currBlock);
					Class c;
					if(blockBankOption == BLOCK_BANK_TURRET){
						c = TurretBlock.class;
					}
					else if (blockBankOption == BLOCK_BANK_ARMOR){
						c = ArmorBlock.class;
					}
					else if (blockBankOption == BLOCK_BANK_ENERGY){
						c = EnergyBlock.class;
					}
					else{
						c = MultiplierBlock.class;
					}
					ownedBlocksByType = getBlocksFromType(c);
					showUpgrades = true;
					showFuse = false;
				}

				return;
			}
			fuseX += 30;
			if(fuseX > 90){
				fuseX = 26;
				fuseY -= 30;
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		batcher.beginBatch(Assets.imageTextures);
		batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("background"));
		batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("NearStarBG"));
		batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("StarBG"));

		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			Block currBlock = PlayerSave.activeBlocks.get(i);
			blockRenderer.renderSimpleBlock(currBlock, batcher, currBlock.position.x , currBlock.position.y, 24, 24);
		}

		for(int i = 0; i < potentialBlocks.size(); i++){
			Block currBlock = potentialBlocks.get(i);
			batcher.drawSprite(currBlock.position.x, currBlock.position.y, 24, 24, Assets.textureRegions.get("PotentialBlock"));
		}

		batcher.drawUISprite(guiCam, 245, 385, 150, 40, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 260, 30, 120, 60, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 40, 30, 80, 60, Assets.textureRegions.get("Rectangle"));
		Assets.font.drawUITextCentered(guiCam, batcher, "Map", 40, 30, 15, 15);
		Assets.font.drawUITextCentered(guiCam, batcher, "Launch", 260, 30, 15, 15);
		Assets.font.drawTextCentered(batcher, "Prepare Your", 160, 460, 20, 23);
		Assets.font.drawTextCentered(batcher, "Ship For", 160, 438, 20, 23);
		Assets.font.drawTextCentered(batcher, "Level " + Settings.currLevel, 160, 416, 16, 16);

		Assets.font.drawUITextRightJustified(guiCam, batcher, "Bank:", 229, 395, 12, 12);
		Assets.font.drawUITextRightJustified(guiCam, batcher, Settings.spaceBucks + " ", 300, 395, 12, 12);
		Assets.font.drawUITextRightJustified(guiCam, batcher, ":", 229, 375, 12, 12);
		String cost = Settings.nextBlockCost + " ";
		if(Settings.nextBlockCost > 0){
			cost = "-".concat(cost);
		}
		Assets.font.drawUITextRightJustified(guiCam, batcher, cost, 300, 375, 12, 12);


		batcher.drawUISprite(guiCam, 305, 395, 12, 12, Assets.textureRegions.get("BaseBlock"));
		batcher.drawUISprite(guiCam, 305, 375, 12, 12, Assets.textureRegions.get("BaseBlock"));
		batcher.drawUISprite(guiCam, 216, 375, 12, 12, Assets.textureRegions.get("PotentialBlock"));

		if(showBlockBank){
			drawBlockBank();
			if(selectedActiveBlock != null && selectedActiveBlock.getClass() != BlankBlock.class){
				if(showFuse && showSubmenu){
					drawFuseMenu();
				}
				else if (showUpgrades && showSubmenu) {
					drawBlockUpgrades();
				}

			}
		}
		popupManager.drawUIExtras(deltaTime);

		batcher.endBatch();
		gl.glDisable(GL10.GL_BLEND);

	}


	private void drawBlockBank(){

		batcher.drawUISprite(guiCam, 290, 215, 60, 30, Assets.textureRegions.get("DarkGrayRectangle"));
		Assets.font.drawUIText(guiCam, batcher, "X", 290, 215, 24, 24);

		batcher.drawUISprite(guiCam, 230, 215, 60, 30, Assets.textureRegions.get("DarkGrayRectangle"));
		Assets.font.drawUIText(guiCam, batcher, "T", 230, 215, 24, 24);


		batcher.drawUISprite(guiCam, 160, 100, 320, 200, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 40, 175, 80, 50, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 120, 175, 80, 50, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 200, 175, 80, 50, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 280, 175, 80, 50, Assets.textureRegions.get("Rectangle"));
		if(blockBankOption == BLOCK_BANK_TURRET){
			batcher.drawUISprite(guiCam, 40, 175, 80, 50, Assets.textureRegions.get("DarkGrayRectangle"));
		}
		else if (blockBankOption == BLOCK_BANK_ARMOR){
			batcher.drawUISprite(guiCam, 120, 175, 80, 50, Assets.textureRegions.get("DarkGrayRectangle"));
		}
		else if (blockBankOption == BLOCK_BANK_ENERGY){
			batcher.drawUISprite(guiCam, 200, 175, 80, 50, Assets.textureRegions.get("DarkGrayRectangle"));
		}
		else if (blockBankOption == BLOCK_BANK_MULTIPLIER){
			batcher.drawUISprite(guiCam, 280, 175, 80, 50, Assets.textureRegions.get("DarkGrayRectangle"));
		}


		batcher.drawUISprite(guiCam, 40, 175, 24, 24, Assets.textureRegions.get("TurretBase"));
		batcher.drawUISprite(guiCam, 40, 175, 24, 24, Assets.textureRegions.get("TurretTop"));
		batcher.drawUISprite(guiCam, 120, 175, 24, 24, Assets.textureRegions.get("ArmorBlock"));
		batcher.drawUISprite(guiCam, 200, 175, 24, 24, Assets.textureRegions.get("EnergyBlock"));
		batcher.drawUISprite(guiCam, 280, 175, 24, 24, Assets.textureRegions.get("MultiplierBlock"));

		int storeX = 26;
		int storeY = 130;
		for(int i = 0; i < ownedBlocksByType.size(); i++){
			Block currBlock = ownedBlocksByType.get(i);
			if(!PlayerSave.activeBlocks.contains(currBlock)){
				currBlock.position.x = storeX;
				currBlock.position.y = storeY;
				blockRenderer.renderSimpleUIBlock(guiCam, currBlock, batcher, currBlock.position.x , currBlock.position.y, 24, 24);
			}
			else{
				if (currBlock == selectedActiveBlock){
					batcher.drawUISprite(guiCam, storeX, storeY, 50, 50, Assets.textureRegions.get("GreenBullet"));
				}
				else {
					batcher.drawUISprite(guiCam, storeX, storeY, 50, 50, Assets.textureRegions.get("Bullet"));
				}
				blockRenderer.renderSimpleUIBlock(guiCam, currBlock, batcher, storeX, storeY, 24, 24);
			}


			storeX += 30;
			if(storeX >  240){
				storeX = 26;
				storeY -= 30;
			}
		}


		batcher.drawUISprite(guiCam, 285, 80, 50, 85, Assets.textureRegions.get("addIcon"));

	}

	public void drawBlockUpgrades(){
		float x = 55;
		float y = 220;

		String[] upgradeableAttributes = selectedActiveBlock.getUpgradableAttributes();
		//float[] attributeValues = selectedActiveBlock.getAttributeVals();
		//float[] upgradeValues = selectedActiveBlock.getUpgradeValues();
		if (selectedActiveBlock.getClass().equals(TurretBlock.class)){

			batcher.drawUISprite(guiCam, x, 217, 110, 35, Assets.textureRegions.get("Rectangle"));
			Assets.font.drawUITextCentered(guiCam, batcher, "Rotate", x, 217, 10, 10);

			y+=34;
		}
		for (int i = 0; i < upgradeableAttributes.length; i++) {
			//float nextVal = attributeValues[i] + upgradeValues[i];
			batcher.drawUISprite(guiCam, x, y + i * 40, 110, 40, Assets.textureRegions.get("Rectangle"));
			Assets.font.drawUIText(guiCam, batcher, upgradeableAttributes[i], x - 45, y + 10 + i * 40, 10, 10);
			//UpgradeCost
			if(!selectedActiveBlock.checkMaxAttributeLevel(i)) {
				Assets.font.drawUITextRightJustified(guiCam, batcher, selectedActiveBlock.getAttributeLevel(i) + 1 + "", x + 37, y - 5 + i * 40, 10, 10);
				Assets.font.drawUIText(guiCam, batcher, "Lvl " + selectedActiveBlock.getAttributeLevel(i), x - 45, y - 5 + i * 40, 8, 8);

				batcher.drawUISprite(guiCam, x + 48, y + 10 + i * 40, 10, 10, Assets.textureRegions.get("addIcon"));
			}
		}

		batcher.drawUISprite(guiCam, x, y + 40 * upgradeableAttributes.length - 10, 110, 25, Assets.textureRegions.get("Rectangle"));
		String text;
		if (showUpgrades){
			float percentage = selectedActiveBlock.getExperienceLevel(upgradeableAttributes.length)/(float)selectedActiveBlock.getMaxAttributeNum();
			batcher.drawUISprite(guiCam, x * percentage, y + 40 * upgradeableAttributes.length - 10, 176 * percentage, 38, Assets.textureRegions.get("GreenBullet"));
			text = "Upgrades";
		}
		else{
			batcher.drawUISprite(guiCam, x, y + 40 * upgradeableAttributes.length - 10, 176, 38, Assets.textureRegions.get("Bullet"));
			text = "Fuse!";
		}

		Assets.font.drawUIText(guiCam, batcher, text, x - 35, y - 10 + upgradeableAttributes.length * 40, 10, 10);
		x = 55;
		y = 220;
		if(selectedActiveBlock.getClass().equals(TurretBlock.class)){
			y+=34;
		}
		for (int i = 0; i < upgradeableAttributes.length; i++) {
			if (!selectedActiveBlock.checkMaxAttributeLevel(i)) {
				batcher.drawUISprite(guiCam, x + 48, y - 5 + i * 40, 10, 10, Assets.textureRegions.get("BaseBlock"));
			}
			else{
				Assets.font.drawUIText(guiCam, batcher, "MAX LEVEL!", x - 45, y - 5 + i * 40, 10, 10);
			}
		}
	}

	public void drawFuseMenu(){
		int yAdd = 0;
		int fuseX = 26;
		int fuseY = 285;
		if(selectedActiveBlock.getClass().equals(TurretBlock.class)){

			batcher.drawUISprite(guiCam, 55, 217, 110, 35, Assets.textureRegions.get("Rectangle"));
			Assets.font.drawUITextCentered(guiCam, batcher, "Rotate", 55, 217, 10, 10);

			yAdd +=34;
			fuseY += 34;
		}
		batcher.drawUISprite(guiCam, 55, 318 + yAdd, 110, 25, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 55, 253 + yAdd, 110, 110, Assets.textureRegions.get("Rectangle"));
		batcher.drawUISprite(guiCam, 55, 318 + yAdd, 176, 38, Assets.textureRegions.get("Bullet"));
		Assets.font.drawUITextCentered(guiCam, batcher, "Fuse!", 55, 320 + yAdd, 10, 10);

		ArrayList<Block> compatibleFusionBlocks = getCompatibleFusionBlocks();
		if(compatibleFusionBlocks.size() > 0){
			int maxFuseChoices = compatibleFusionBlocks.size();
			if(compatibleFusionBlocks.size() > 9){
				//TODO: NEED TO DRAW ARROWS TO PAGINATE
				maxFuseChoices = 9;
			}
			for (int i = 0; i < maxFuseChoices; i++){
				Block currBlock = compatibleFusionBlocks.get(i);
				if(PlayerSave.activeBlocks.contains(currBlock)){
					batcher.drawUISprite(guiCam, fuseX, fuseY, 50, 50, Assets.textureRegions.get("Bullet"));
				}
				blockRenderer.renderSimpleUIBlock(guiCam, currBlock, batcher, fuseX, fuseY, 24, 24);
				fuseX += 30;
				if(fuseX > 90){
					fuseX = 26;
					fuseY -= 30;
				}
			}
		}
		else{
			Assets.font.drawUITextCentered(guiCam, batcher, "Nothing to", 55, 263 + yAdd, 7, 7);
			Assets.font.drawUITextCentered(guiCam, batcher, "fuse, Upgrade", 55, 253 + yAdd, 7, 7);
			Assets.font.drawUITextCentered(guiCam, batcher, "other blocks.", 55, 243 + yAdd, 7, 7);
		}



	}

	public void getPotentialBlocks(){
		potentialBlocks.clear();
		ArrayList<Integer> emptyBlocks;
		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			Block currBlock = PlayerSave.activeBlocks.get(i);
			
			emptyBlocks = checkAdjacentBlocks(currBlock);
			for(int j = 0; j < emptyBlocks.size(); j++){
				BlankBlock newBlock = new BlankBlock(new Vector2(0,0));
				int xAdd = 0;
				int yAdd = 0;
				if(emptyBlocks.get(j) == 0){
					yAdd = 25;
				}
				else if(emptyBlocks.get(j) == 1){
					xAdd = 25;
				}
				else if(emptyBlocks.get(j) == 2){
					yAdd = -25;
				}
				else if(emptyBlocks.get(j) == 3){
					xAdd = -25;
				}
				newBlock.position.x = currBlock.position.x + xAdd;
				newBlock.position.y = currBlock.position.y + yAdd;
				addPotentialBlock(newBlock);
			}
			
		}
	}

	public ArrayList<Integer> checkAdjacentBlocks(Block block){
		ArrayList<Integer> emptyBlocks = new ArrayList<Integer>(){{
			add(0);
			add(1);
			add(2);
			add(3);
		}};
		
		for(int i = 0; i < PlayerSave.activeBlocks.size(); i++){
			if(emptyBlocks.size() == 0){
				continue;
			}
			Block currBlock = PlayerSave.activeBlocks.get(i);
			Integer toRemove;
			if(block.position.y + 25 == currBlock.position.y && block.position.x == currBlock.position.x){
				toRemove = 0;
			}
			
			else if(block.position.x + 25 == currBlock.position.x && block.position.y == currBlock.position.y){
				toRemove = 1;
			}
			
			else if(block.position.y - 25 == currBlock.position.y && block.position.x == currBlock.position.x){
				toRemove = 2;
			}
			
			else if(block.position.x - 25 == currBlock.position.x && block.position.y == currBlock.position.y){
				toRemove = 3;
			}
			else{
				continue;
			}
			emptyBlocks.remove(toRemove);


		}
		return emptyBlocks;
	}
	
	public void addPotentialBlock(Block newBlock){
		if(newBlock.position.x < 20 || newBlock.position.x + 25 > 320 || newBlock.position.y < 0 || newBlock.position.y +25 > 480){
			return;
		}
		if(newBlock.position.x < 64 && newBlock.position.y < 75 || newBlock.position.x > 250 && newBlock.position.y < 75){
			return;
		}
		for(int i = 0; i < potentialBlocks.size(); i++){
			Block currBlock = potentialBlocks.get(i);
			if(newBlock.position.equals(currBlock.position)){
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

	public ArrayList<Block> getCompatibleFusionBlocks(){
		ArrayList<Block> blocksOfType = getBlocksFromType(selectedActiveBlock.getClass());
		ArrayList<Block> compatibleBlocks = new ArrayList<Block>();
		for (int i = 0; i < blocksOfType.size(); i++){
			Block currBlock = blocksOfType.get(i);
			if(selectedActiveBlock.equals(currBlock)){
				continue;
			}
			if(selectedActiveBlock.blockLevel == currBlock.blockLevel && currBlock.getExperienceLevel(currBlock.getUpgradableAttributes().length) >= currBlock.getMaxAttributeNum()){
				compatibleBlocks.add(currBlock);
			}
		}
		return compatibleBlocks;
	}

	public void resetBlockBank(){
		if ( blockBankOption == BLOCK_BANK_TURRET) {
			ownedBlocksByType = getBlocksFromType(TurretBlock.class);
		}
		else if (blockBankOption == BLOCK_BANK_ARMOR) {
			ownedBlocksByType = getBlocksFromType(ArmorBlock.class);
		}
		else if ( blockBankOption == BLOCK_BANK_ENERGY) {
			ownedBlocksByType = getBlocksFromType(EnergyBlock.class);
		}
		else if ( blockBankOption == BLOCK_BANK_MULTIPLIER) {
			ownedBlocksByType = getBlocksFromType(MultiplierBlock.class);
		}
	}

	public boolean testShowFuse(){
		return selectedActiveBlock.getMaxAttributeNum() <= selectedActiveBlock.getExperienceLevel(selectedActiveBlock.getUpgradableAttributes().length);
	}

	public boolean checkForBlankBlocks(){
		boolean hasBlank = false;

		for (Block b : PlayerSave.activeBlocks){
			if (b.getClass().equals(BlankBlock.class)){
				hasBlank = true;
				break;
			}
		}

		return hasBlank;
	}

	public void setTurretDirections(){
		ArrayList<Block> tBlocks = getBlocksFromType(TurretBlock.class);
		for(Block currBlock : tBlocks){
			TurretBlock tBlock = (TurretBlock)currBlock;
			tBlock.setBeginningLastTouch();
		}
	}

	@Override
	public void pause() {
		Settings.save(game.getFileIO());
		PlayerSave.save(game.getFileIO());
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		
	}
}

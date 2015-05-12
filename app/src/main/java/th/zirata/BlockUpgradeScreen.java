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

public class BlockUpgradeScreen extends GLScreen{

	Camera2D guiCam;
	SpriteBatcher batcher;
	Rectangle backBounds;
	Rectangle forwardBounds;
	Rectangle armorBounds;
	Rectangle turretBounds;
	Rectangle machineGunBounds;
	Vector2 touchPoint;
	int blockNum;
	Block lastBlock;
	
	
	public BlockUpgradeScreen(Game game, int blockNum) {
		super(game);
		this.blockNum = blockNum;
        guiCam = new Camera2D(glGraphics, 320, 480);
        backBounds = new Rectangle(0, 0, 64, 64);
        forwardBounds = new Rectangle(320-64, 0, 64, 64);
        armorBounds = new Rectangle(0, 310-25, 350, 50);
        turretBounds = new Rectangle(0, 250-25, 350, 50);
        machineGunBounds = new Rectangle(0, 190-25, 350, 50);
		touchPoint = new Vector2();
		batcher = new SpriteBatcher(glGraphics, 500);
		lastBlock = PlayerSave.playerBlocks.get(blockNum);
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
					PlayerSave.playerBlocks.remove(blockNum);
					PlayerSave.playerBlocks.add(blockNum, lastBlock);
					game.setScreen(new BuildScreen(game));
					return;
				}
				
				if(OverlapTester.pointInRectangle(forwardBounds, touchPoint)){
					game.setScreen(new BuildScreen(game));
					return;
				}
				if(OverlapTester.pointInRectangle(armorBounds, touchPoint)){
					changeBlock("armor");
					game.setScreen(new BuildScreen(game));
					return;
				}
				if(OverlapTester.pointInRectangle(turretBounds, touchPoint)){
					changeBlock("turret");
					game.setScreen(new BuildScreen(game));
					return;
				}
				if(OverlapTester.pointInRectangle(machineGunBounds, touchPoint)){
					changeBlock("machineGun");
					game.setScreen(new BuildScreen(game));
					return;
				}
			}
		}		
		
	}
	
	public void changeBlock(String blockType){
		Block currBlock = PlayerSave.playerBlocks.get(blockNum);
		PlayerSave.playerBlocks.remove(blockNum);

		if(blockType.equals("armor")){
			ArmorBlock newBlock = new ArmorBlock(currBlock.position.x, currBlock.position.y, 20);
			PlayerSave.playerBlocks.add(blockNum, newBlock);
		}
		if(blockType.equals("turret")){
			TurretBlock newBlock = new TurretBlock(currBlock.position.x, currBlock.position.y, 10);
			PlayerSave.playerBlocks.add(blockNum, newBlock);
		}
		if(blockType.equals("machineGun")){
			MachineGunBlock newBlock = new MachineGunBlock(currBlock.position.x, currBlock.position.y, 10);
			PlayerSave.playerBlocks.add(blockNum, newBlock);
		}
		PlayerSave.save(game.getFileIO());
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
		Block currBlock = PlayerSave.playerBlocks.get(blockNum);
		int x = 160;
		int y = 450;
		if(currBlock.getClass().equals(TurretBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.turretBaseRegion);
			batcher.drawSprite(x, y, 24, 24, Assets. turretTopRegion);
		}

		else if(currBlock.getClass().equals(ArmorBlock.class)){
			batcher.drawSprite(x, y, 24 , 24, Assets.armorBlockRegion);
		}
		else if(currBlock.getClass().equals(MachineGunBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.machineGunBlockRegion);
		}

		else{
			batcher.drawSprite(x, y, 24, 24, Assets.baseBlockRegion);
		}
		batcher.drawSprite(160, 250, 350, 50, Assets.turretBaseRegion);
		batcher.drawSprite(160, 250, 350, 50, Assets. turretTopRegion);
		batcher.drawSprite(160, 310, 350, 50, Assets.armorBlockRegion);
		batcher.drawSprite(160, 190, 350, 50, Assets.machineGunBlockRegion);
		batcher.endBatch();
		
		batcher.beginBatch(Assets.mainMenuTextures);
		batcher.drawSprite(290, 30, 60, 60, Assets.arrowRegion);
		batcher.drawSprite(30, 30, -60, 60, Assets.arrowRegion);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_BLEND);

		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
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

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
	Rectangle armorBounds;
	Rectangle turretBounds;
	Rectangle multiplierBounds;
	Rectangle energyBounds;
	Vector2 touchPoint;
	int blockNum;
	Block lastBlock;
	
	
	public BlockUpgradeScreen(Game game, int blockNum) {
		super(game);
		this.blockNum = blockNum;
        guiCam = new Camera2D(glGraphics, 320, 480);
        armorBounds = new Rectangle(100-25, 250-25, 50, 50);
        turretBounds = new Rectangle(50-25, 250-25, 50, 50);
        multiplierBounds = new Rectangle(150-25, 250-25, 50, 50);
		energyBounds = new Rectangle(200-25, 250-25, 50, 50);
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

				if(OverlapTester.pointInRectangle(turretBounds, touchPoint)){
					Block currBlock = PlayerSave.playerBlocks.get(blockNum);
					game.setScreen(new BlockDirectionScreen(game, currBlock.position, blockNum));
					return;
				}
				else{
					String blockType = "";
					if(OverlapTester.pointInRectangle(armorBounds, touchPoint)){
						blockType = "armor";
					}
					else if(OverlapTester.pointInRectangle(multiplierBounds, touchPoint)){
						blockType = "multiplier";
					}
					else if(OverlapTester.pointInRectangle(energyBounds, touchPoint)){
						blockType = "energy";
					}
					else{
						//No valid selections were made
						return;
					}
					changeBlock(blockType);
					game.setScreen(new BuildScreen(game));
					return;
				}


			}
		}		
		
	}
	
	public void changeBlock(String blockType){
		Block currBlock = PlayerSave.playerBlocks.get(blockNum);
		PlayerSave.playerBlocks.remove(blockNum);

		if(blockType.equals("turret")){
			game.setScreen(new BlockDirectionScreen(game, currBlock.position, blockNum));
		}
		else{
			Block newBlock = null;
			if(blockType.equals("armor")){
				newBlock = new ArmorBlock(currBlock.position);
			}
			else if(blockType.equals("multiplier")){
				newBlock = new MultiplierBlock(currBlock.position);
			}
			else if(blockType.equals("energy")){
				newBlock = new EnergyBlock(currBlock.position);

			}
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
			batcher.drawSprite(x, y, 24, 24, Assets.multiplierBlockRegion);
		}
		else if(currBlock.getClass().equals(EnergyBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.energyBlockRegion);
		}
		else{
			batcher.drawSprite(x, y, 24, 24, Assets.baseBlockRegion);
		}
		batcher.drawSprite(50, 250, 50, 50, Assets.turretBaseRegion);
		batcher.drawSprite(50, 250, 50, 50, Assets. turretTopRegion);
		batcher.drawSprite(100, 250, 50, 50, Assets.armorBlockRegion);
		batcher.drawSprite(150, 250, 50, 50, Assets.multiplierBlockRegion);
		batcher.drawSprite(200, 250, 50, 50, Assets.energyBlockRegion);
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

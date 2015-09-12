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
	Vector2 touchPoint;
	Rectangle backBounds;
	Block block;
	
	
	public BlockUpgradeScreen(Game game, Block block) {
		super(game);
		this.block = block;
        guiCam = new Camera2D(glGraphics, 320, 480);
		touchPoint = new Vector2();
		backBounds = new Rectangle(0, 0, 64, 64);
		batcher = new SpriteBatcher(glGraphics, 500);
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
				if (OverlapTester.pointInRectangle(backBounds, touchPoint)) {
					game.setScreen(new BuildScreen(game));
					return;
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


		batcher.beginBatch(Assets.mainMenuTextures);
		batcher.drawSprite(30, 30, -60, 60, Assets.arrowRegion);
		batcher.endBatch();

		batcher.beginBatch(Assets.blockTextures);
		int x = 160;
		int y = 450;
		if(block.getClass().equals(TurretBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.turretBaseRegion);
			batcher.drawSprite(x, y, 24, 24, Assets. turretTopRegion);
		}
//test
		else if(block.getClass().equals(ArmorBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.armorBlockRegion);

			batcher.endBatch();
			drawArmorBlockAttributes();
		}
		else if(block.getClass().equals(MultiplierBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.multiplierBlockRegion);
		}
		else if(block.getClass().equals(EnergyBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.energyBlockRegion);
		}
		else{
			batcher.drawSprite(x, y, 24, 24, Assets.baseBlockRegion);
		}

		batcher.endBatch();

		gl.glDisable(GL10.GL_BLEND);

	}

	private void drawArmorBlockAttributes(){
		batcher.beginBatch(Assets.mainMenuTextures);
		batcher.drawSprite(80, 100, 160, 80, Assets.rectangleRegion);
		batcher.drawSprite(240, 100, 160, 80, Assets.rectangleRegion);
		//batcher.drawSprite(40, 175, 80, 50, Assets.darkGrayRectangleRegion);
		batcher.endBatch();
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

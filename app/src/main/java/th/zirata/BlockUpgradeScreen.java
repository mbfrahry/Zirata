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
				if (block.getClass() == ArmorBlock.class){
					updateArmorAttributes();
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
			batcher.drawSprite(x, y, 24, 24, Assets.turretTopRegion);
			//drawTurretBlockAttributes();
		}
		//Test
		else if(block.getClass().equals(ArmorBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.armorBlockRegion);
			//drawArmorBlockAttributes();
		}
		else if(block.getClass().equals(MultiplierBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.multiplierBlockRegion);
			//drawMultiplierBlockAttributes();
		}
		else if(block.getClass().equals(EnergyBlock.class)){
			batcher.drawSprite(x, y, 24, 24, Assets.energyBlockRegion);
			//drawEnergyBlockAttributes();
		}
		else{
			batcher.drawSprite(x, y, 24, 24, Assets.baseBlockRegion);
		}
		batcher.endBatch();
		drawAttributeButtons();


		gl.glDisable(GL10.GL_BLEND);

	}

	private void drawAttributeButtons(){
		batcher.beginBatch(Assets.mainMenuTextures);
		String[] upgradeableAttributes = block.getUpgradableAttributes();
		float[] attributeValues = block.getAttributeVals();
		float[] upgradeValues = block.getUpgradeValues();
		for (int i = 0; i < upgradeableAttributes.length; i++){
			float nextVal = attributeValues[i]+upgradeValues[i];
			batcher.drawSprite(160, 100 + i * 80, 320, 80, Assets.rectangleRegion);
			Assets.font.drawText(batcher, upgradeableAttributes[i], 15, 125 + i * 80);
			Assets.font.drawText(batcher, "Current: " + attributeValues[i] , 15, 100 + i*80);
			Assets.font.drawText(batcher, "Next: " + nextVal , 15, 75 + i*80);
		}
		batcher.endBatch();
	}

	private void updateArmorAttributes(){
		Rectangle pBlockBounds = new Rectangle(0, 50, 160, 80);
		if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
			game.setScreen(new BuildScreen(game));
		}
	}

	private void updateEnergyAttributes(){
		Rectangle pBlockBounds = new Rectangle(0, 50, 160, 80);
		if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
			game.setScreen(new BuildScreen(game));
		}
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

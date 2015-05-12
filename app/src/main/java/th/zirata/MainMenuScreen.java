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

public class MainMenuScreen extends GLScreen{

	Camera2D guiCam;
	SpriteBatcher batcher;
	Rectangle soundBounds;
	Rectangle playBounds;
	Rectangle resetBounds;
	Vector2 touchPoint;
	
	public MainMenuScreen(Game game){
		super(game);
		guiCam = new Camera2D(glGraphics, 320, 480);
		batcher = new SpriteBatcher(glGraphics, 100);
		playBounds = new Rectangle(160 - 50, 200 - 50, 128, 74);
		resetBounds = new Rectangle(160 - 50, 200 - 100, 128, 74);
		touchPoint = new Vector2();
	}
	
	public void update(float deltaTime){
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		
		int len = touchEvents.size();
		for(int i = 0; i < len; i++){
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP){
				touchPoint.set(event.x, event.y);
				guiCam.touchToWorld(touchPoint);
				
				if(OverlapTester.pointInRectangle(playBounds, touchPoint)){
					game.setScreen(new BuildScreen(game));
					return;
				}
				
				if(OverlapTester.pointInRectangle(resetBounds,touchPoint)){
					Settings.reset();
					PlayerSave.reset();
					Settings.save(game.getFileIO());
					return;
				}
				
			}
		}
	}
	
	public void present(float deltaTime){
		GL10 gl = glGraphics.getGL();
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		guiCam.setViewportAndMatrices();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		batcher.beginBatch(Assets.backgroundTextures);
		batcher.drawSprite(160, 240, 320, 480, Assets.backgroundRegion);
		batcher.drawSprite(160, 240, 320, 480, Assets.nearStarRegion);
		batcher.drawSprite(160, 240, 320, 480, Assets.farStarRegion);
		batcher.endBatch();
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		batcher.beginBatch(Assets.mainMenuTextures);
		batcher.drawSprite(160, 200, 128, 74, Assets.playRegion);
		batcher.endBatch();
		
		gl.glDisable(GL10.GL_BLEND);
	
	}
	
	public void pause(){
		Settings.save(game.getFileIO());
	}
	
	public void resume(){
		
	}
	
	public void dispose(){
		
	}
}

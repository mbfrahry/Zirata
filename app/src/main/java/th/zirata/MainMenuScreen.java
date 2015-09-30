package th.zirata;

import android.app.Activity;

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
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;

import th.zirata.Help.MainHelpScreen;

public class MainMenuScreen extends GLScreen{

	Camera2D guiCam;
	SpriteBatcher batcher;
	Rectangle soundBounds;
	Rectangle playBounds;
	Rectangle resetBounds;
	Rectangle helpBounds;
	Vector2 touchPoint;

	public MainMenuScreen(Game game){
		super(game);
		guiCam = new Camera2D(glGraphics, 320, 480);
		batcher = new SpriteBatcher(glGraphics, 100);
		playBounds = new Rectangle(160 - 50, 200-25, 128, 74);
		helpBounds = new Rectangle(160 - 50, 125-25, 110, 55);
		resetBounds = new Rectangle(160 - 50, 400 - 100, 128, 74);
		soundBounds = new Rectangle(0, 0, 50, 50);
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

					game.setScreen(new MapScreen(game));
					return;
				}

				if(OverlapTester.pointInRectangle(helpBounds, touchPoint)){
					//TODO take this comment out
					//game.setScreen(new MainHelpScreen(game));
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

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		batcher.beginBatch(Assets.imageTextures);
		batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("background"));
		batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("NearStarBG"));
		batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("StarBG"));

		batcher.drawSprite(160, 200, 128, 74, Assets.textureRegions.get("Play"));

		batcher.drawSprite(80, 130, 24, 24, Assets.textureRegions.get("GreenBase1"));
		batcher.drawSprite(80, 130, 24, 24, Assets.textureRegions.get("GreenTurret1"));
		batcher.drawSprite(120, 130, 24, 24, Assets.textureRegions.get("GreenBase2"));
		batcher.drawSprite(120, 130, 24, 24, Assets.textureRegions.get("GreenTurret2"));
		batcher.drawSprite(160, 130, 24, 24, Assets.textureRegions.get("GreenBase3"));
		batcher.drawSprite(160, 130, 24, 24, Assets.textureRegions.get("GreenTurret3"));

		batcher.drawSprite(80, 100, 24, 24, Assets.textureRegions.get("PurpleBase1"));
		batcher.drawSprite(80, 100, 24, 24, Assets.textureRegions.get("PurpleTurret1"));
		batcher.drawSprite(120, 100, 24, 24, Assets.textureRegions.get("PurpleBase2"));
		batcher.drawSprite(120, 100, 24, 24, Assets.textureRegions.get("PurpleTurret2"));
		batcher.drawSprite(160, 100, 24, 24, Assets.textureRegions.get("PurpleBase3"));
		batcher.drawSprite(160, 100, 24, 24, Assets.textureRegions.get("PurpleTurret3"));

		batcher.drawSprite(200, 130, 24, 24, Assets.textureRegions.get("GreenBase1"));
		batcher.drawSprite(200, 130, 36, 36, Assets.textureRegions.get("GreenTurret1"));
		batcher.drawSprite(240, 130, 24, 24, Assets.textureRegions.get("GreenBase2"));
		batcher.drawSprite(240, 130, 36, 36, Assets.textureRegions.get("GreenTurret2"));
		batcher.drawSprite(280, 130, 24, 24, Assets.textureRegions.get("GreenBase3"));
		batcher.drawSprite(280, 130, 36, 36, Assets.textureRegions.get("GreenTurret3"));

		batcher.drawSprite(200, 100, 24, 24, Assets.textureRegions.get("PurpleBase1"));
		batcher.drawSprite(200, 100, 36, 36, Assets.textureRegions.get("PurpleTurret1"));
		batcher.drawSprite(240, 100, 24, 24, Assets.textureRegions.get("PurpleBase2"));
		batcher.drawSprite(240, 100, 36, 36, Assets.textureRegions.get("PurpleTurret2"));
		batcher.drawSprite(280, 100, 24, 24, Assets.textureRegions.get("PurpleBase3"));
		batcher.drawSprite(280, 100, 24, 36, Assets.textureRegions.get("PurpleTurret3"));
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

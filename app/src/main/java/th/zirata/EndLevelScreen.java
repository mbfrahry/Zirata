package th.zirata.Help;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import th.zirata.Assets;
import th.zirata.BuildScreen;
import th.zirata.MainMenuScreen;
import th.zirata.PlayerSave;
import th.zirata.Settings;

/**
 * Created by Matthew on 9/7/2015.
 */
public class EndLevelScreen extends GLScreen {
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle selectBounds;
    Rectangle backBounds;
    Vector2 touchPoint;
    String result;

    public EndLevelScreen(Game game, boolean win){
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        batcher = new SpriteBatcher(glGraphics, 100);
        selectBounds = new Rectangle(160, 300, 200, 60);

        backBounds = new Rectangle(160, 300, 60, 60);
        touchPoint = new Vector2();
        result = win ? "Onward!" : "Try Again!";
    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);

            if (event.type == Input.TouchEvent.TOUCH_UP) {
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

        gl.glEnable(GL10.GL_TEXTURE_2D);
        batcher.beginBatch(Assets.backgroundTextures);
        batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("Background"));
        batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("NearStarBG"));
        batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("StarBG"));
        batcher.endBatch();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        batcher.beginBatch(Assets.mainMenuTextures);

        batcher.drawSprite(160, 300, 200, 60, Assets.textureRegions.get("Rectangle"));
        Assets.font.drawText(batcher, result, 80, 300);

        batcher.endBatch();

        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
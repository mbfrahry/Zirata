package th.zirata;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;


public class MapScreen  extends GLScreen {

    Camera2D guiCam;
    SpriteBatcher batcher;
    public static ArrayList<Rectangle> levelBounds = new ArrayList<Rectangle>(){{
        add(new Rectangle(100, 50, 60, 60));
        add(new Rectangle(50, 100, 60, 60));
        add(new Rectangle(130, 175, 60, 60));
        add(new Rectangle(100, 250, 60, 60));
        add(new Rectangle(160, 250, 60, 60));
    }};
    Vector2 touchPoint;

    public MapScreen(Game game){
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        batcher = new SpriteBatcher(glGraphics, 100);
        touchPoint = new Vector2();
    }

    public void update(float deltaTime){
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();

        int len = touchEvents.size();
        for(int i = 0; i < len; i++){
            Input.TouchEvent event = touchEvents.get(i);
            if(event.type == Input.TouchEvent.TOUCH_UP){
                touchPoint.set(event.x, event.y);
                guiCam.touchToWorld(touchPoint);

                for(int j = 0 ; j < Settings.maxLevel; j++){
                    Rectangle currLevel = levelBounds.get(j);
                    if(OverlapTester.pointInRectangle(currLevel, touchPoint)){
                        Settings.currLevel = j+1;
                        game.setScreen(new BuildScreen(game));
                        return;
                    }
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
        batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("Background"));
        batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("NearStarBG"));
        batcher.drawSprite(160, 240, 320, 480, Assets.textureRegions.get("StarBG"));
        batcher.endBatch();

        batcher.beginBatch(Assets.mainMenuTextures);
        Assets.font.drawTextCentered(batcher, "Select Your", 160, 460, 20, 23);
        Assets.font.drawTextCentered(batcher, "Coordinates", 160, 438, 25, 28);
        batcher.endBatch();

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        for(int i = 0; i < Settings.maxLevel; i++) {
            Rectangle currLevel = levelBounds.get(i);
            batcher.beginBatch(Assets.blockTextures);
            batcher.drawSprite(currLevel.lowerLeft.x + 35, currLevel.lowerLeft.y + 25, 40, 40, 45, Assets.textureRegions.get("BaseBlock"));
            batcher.endBatch();

            batcher.beginBatch(Assets.mainMenuTextures);
            Assets.font.drawText(batcher, i + 1 + "", currLevel.lowerLeft.x + 10, currLevel.lowerLeft.y + 45);
            batcher.endBatch();
        }
        if(Settings.maxLevel < levelBounds.size()){
            batcher.beginBatch(Assets.blockTextures);
            batcher.drawSprite(levelBounds.get(Settings.maxLevel ).lowerLeft.x + 35, levelBounds.get(Settings.maxLevel).lowerLeft.y + 25, 40, 40, 45, Assets.textureRegions.get("PotentialBlock"));
            batcher.endBatch();
            batcher.beginBatch(Assets.mainMenuTextures);
            Assets.font.drawText(batcher, Settings.maxLevel + 1 + "", levelBounds.get(Settings.maxLevel).lowerLeft.x + 10, levelBounds.get(Settings.maxLevel).lowerLeft.y + 45);
            batcher.endBatch();
        }


        gl.glDisable(GL10.GL_BLEND);

    }

    public void pause(){

    }

    public void resume(){

    }

    public void dispose(){

    }
}

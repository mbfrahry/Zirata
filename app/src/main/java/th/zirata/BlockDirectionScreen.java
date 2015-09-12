package th.zirata;


import android.os.Build;

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

public class BlockDirectionScreen extends GLScreen{
    Camera2D guiCam;
    SpriteBatcher batcher;
    Rectangle leftBounds;
    Rectangle rightBounds;
    Rectangle upBounds;
    Rectangle downBounds;
    Vector2 touchPoint;
    Vector2 position;
    int blockNum;

    public BlockDirectionScreen(Game game, Vector2 position, int blockNum) {
        super(game);
        guiCam = new Camera2D(glGraphics, 320, 480);
        rightBounds = new Rectangle(210-25, 250-25, 60, 60);
        leftBounds = new Rectangle(90-25, 250-25, 50, 50);
        upBounds = new Rectangle(150-25, 310-25, 50, 50);
        downBounds = new Rectangle(150-25, 190-25, 50, 50);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 500);
        this.position = position;
        this.blockNum = blockNum;
    }

    @Override
    public void update(float deltaTime) {
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            touchPoint.set(event.x, event.y);
            guiCam.touchToWorld(touchPoint);

            if(event.type == Input.TouchEvent.TOUCH_UP) {
                //TODO Fix this

                if(OverlapTester.pointInRectangle(rightBounds, touchPoint)){
                    PlayerSave.activeBlocks.remove(blockNum);
                    PlayerSave.createBlock(1, position.x, position.y, 0);
                    PlayerSave.save(game.getFileIO());
                    game.setScreen(new BuildScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(leftBounds, touchPoint)){
                    PlayerSave.activeBlocks.remove(blockNum);
                    PlayerSave.createBlock(1, position.x, position.y, 180);
                    PlayerSave.save(game.getFileIO());
                    game.setScreen(new BuildScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(upBounds, touchPoint)){
                    PlayerSave.activeBlocks.remove(blockNum);
                    PlayerSave.createBlock(1, position.x, position.y, 90);
                    PlayerSave.save(game.getFileIO());
                    game.setScreen(new BuildScreen(game));
                    return;
                }
                if(OverlapTester.pointInRectangle(downBounds, touchPoint)){
                    PlayerSave.activeBlocks.remove(blockNum);
                    PlayerSave.createBlock(1, position.x, position.y, 270);
                    PlayerSave.save(game.getFileIO());
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


        batcher.beginBatch(Assets.blockTextures);
        batcher.drawSprite(150, 250, 50, 50, Assets.turretBaseRegion);
        batcher.drawSprite(150, 250, 50, 50, Assets.turretTopRegion);
        batcher.endBatch();

        batcher.beginBatch(Assets.mainMenuTextures);
        //right
        batcher.drawSprite(210, 250, 60, 60, Assets.arrowRegion);
        //left
        batcher.drawSprite(90, 250, -60, 60, 0, Assets.arrowRegion);
        //up
        batcher.drawSprite(150, 310, 60, 60, 90, Assets.arrowRegion);
        //down
        batcher.drawSprite(150, 190, -60, 60, 90, Assets.arrowRegion);
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

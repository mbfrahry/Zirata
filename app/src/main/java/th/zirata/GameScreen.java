package th.zirata;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Input.TouchEvent;
import com.badlogic.androidgames.framework.gl.Camera2D;
import com.badlogic.androidgames.framework.gl.FPSCounter;
import com.badlogic.androidgames.framework.gl.SpriteBatcher;
import com.badlogic.androidgames.framework.impl.GLScreen;
import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class GameScreen extends GLScreen {
    static final int GAME_READY = 0;    
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;
  
    int state;
    Camera2D guiCam;
    Vector2 touchPoint;
    SpriteBatcher batcher;    
    Rectangle pauseBounds;
    Rectangle resumeBounds;
    Rectangle quitBounds;
    Rectangle pBlockBounds;
    World world;
    WorldRenderer renderer;
    FPSCounter fpsCounter;
    int[] blockNum = {-1, -1, -1, -1, -1};
    
    public GameScreen(Game game) {
        super(game);
        state = GAME_READY;
        guiCam = new Camera2D(glGraphics, 320, 480);
        touchPoint = new Vector2();
        batcher = new SpriteBatcher(glGraphics, 1000);
        
        PlayerSave.load(game.getFileIO());
        world = new World();
        renderer = new WorldRenderer(glGraphics, batcher, world);
        pauseBounds = new Rectangle(320- 64, 480- 64, 64, 64);
        resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
        quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);
        fpsCounter = new FPSCounter();
        
    }

	@Override
	public void update(float deltaTime) {
		
	    switch(state) {
	    case GAME_READY:
	        updateReady();
	        break;
	    case GAME_RUNNING:
	        updateRunning(deltaTime);
	        break;
	    case GAME_PAUSED:
	        updatePaused();
	        break;
	    case GAME_LEVEL_END:
	        updateLevelEnd();
	        break;
	    case GAME_OVER:
	        updateGameOver();
	        break;
	    }
	}
	
	private void updateReady() {
		world.player.getEnergy();
		state = GAME_RUNNING;
	}

	private void updateRunning(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for(int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			touchPoint.set(event.x, event.y);
			guiCam.touchToWorld(touchPoint);
			
			if(event.type == TouchEvent.TOUCH_DOWN){
				for(int j = 0; j < world.player.playerBlocks.size(); j++){
					Block currBlock = world.player.playerBlocks.get(j);
					pBlockBounds = new Rectangle(currBlock.position.x -12, currBlock.position.y-12, 25, 25);
					if(OverlapTester.pointInRectangle(pBlockBounds, touchPoint)){
						blockNum[event.pointer] = j;
						return;
					}
				}
				if(event.y > 450){
					if(event.x < 50){
						world.moveRight = true;
						world.moveLeft = false;
					}
					if(event.x > 50 && event.x < 100){
						world.moveLeft = true;
						world.moveRight = false;
					}
				}
			}
			
			if(event.type == TouchEvent.TOUCH_DRAGGED){
				if(blockNum[event.pointer] != -1){
					try{
						Block currBlock = world.player.playerBlocks.get(blockNum[event.pointer]);
						currBlock.lastTouch.x = touchPoint.x;
						currBlock.lastTouch.y = touchPoint.y;
						
						if(currBlock.getClass().equals(MachineGunBlock.class)){
							currBlock.action();
						}
					}catch(Exception e){
						
					}
				}
			}

			if(event.type == TouchEvent.TOUCH_UP){

				//if(OverlapTester.pointInRectangle(pauseBounds, touchPoint)) {
				//	state = GAME_PAUSED;
				//	return;
				//}
				if(blockNum[event.pointer] != -1){	
					try{
						Block currBlock = world.player.playerBlocks.get(blockNum[event.pointer]);
						currBlock.lastTouch.x = touchPoint.x;
						currBlock.lastTouch.y = touchPoint.y;
					
						currBlock.action();
						blockNum[event.pointer] = -1;
						return;
					} catch(Exception e){

					}
				}
				if(event.y > 450){
					if(event.x < 100){
						world.moveLeft = false;
						world.moveRight = false;
					}
				}

			}
		}


		world.update(deltaTime);
		if(world.state == world.WORLD_STATE_LEVEL_END){
			state = GAME_LEVEL_END;
		}
		if(world.state == World.WORLD_STATE_GAME_OVER){
			state = GAME_OVER;
			Settings.save(game.getFileIO());
		}
	}

	private void updatePaused() {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        
	        touchPoint.set(event.x, event.y);
	        guiCam.touchToWorld(touchPoint);
	        
	        if(OverlapTester.pointInRectangle(resumeBounds, touchPoint)) {	         
	            state = GAME_RUNNING;
	            return;
	        }
	        
	        if(OverlapTester.pointInRectangle(quitBounds, touchPoint)) {
	            game.setScreen(new MainMenuScreen(game));
	            return;
	        
	        }
	    }
	}
	
	private void updateLevelEnd() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	      
	    }
	    if(Settings.numEnemies >= 30){
        	Settings.numEnemies = 10;
        	Settings.enemyHealth += 10;
        }
        else{
        	Settings.numEnemies += 10;
        }
        Settings.save(game.getFileIO());
        game.setScreen(new BuildScreen(game));
	}
	
	private void updateGameOver() {
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    int len = touchEvents.size();
	    /*
	    for(int i = 0; i < len; i++) {                   
	        TouchEvent event = touchEvents.get(i);
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;
	        game.setScreen(new MainMenuScreen(game));
	    }*/
	    PlayerSave.load(game.getFileIO());
	    game.setScreen(new MainMenuScreen(game));
	}

	@Override
	public void present(float deltaTime) {
	    GL10 gl = glGraphics.getGL();
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    gl.glEnable(GL10.GL_TEXTURE_2D);
	    
	    renderer.render();
	   
	    guiCam.setViewportAndMatrices();
	    	    
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
	    switch(state) {
	    case GAME_READY:
	        presentReady();
	        break;
	    case GAME_RUNNING:
	        presentRunning();
	        break;
	    case GAME_PAUSED:
	        presentPaused();
	        break;
	    case GAME_LEVEL_END:
	    	presentLevelEnd();
	        break;
	    case GAME_OVER:
	        presentGameOver();
	        break;
	    }
	    
	    gl.glDisable(GL10.GL_BLEND);
	    //fpsCounter.logFrame();
	    
	}
	
	private void presentReady() {

	}
	
	private void presentRunning() {

	}
	
	private void presentPaused() {

	}

	private void presentLevelEnd(){
		
	}
	private void presentGameOver() {
	 
	}

    @Override
    public void pause() {
        if(state == GAME_RUNNING)
            state = GAME_PAUSED;
    }

    @Override
    public void resume() {        
    }

    @Override
    public void dispose() {       
    }
}
package th.zirata;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

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
	Rectangle powerBounds;
    World world;
    WorldRenderer renderer;
    FPSCounter fpsCounter;
    HashMap<Integer,Vector2> steerTouches;
	HashMap<Integer,Vector2> powerTouches;

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
        resumeBounds = new Rectangle(0, 220, 320, 50);
        quitBounds = new Rectangle(0, 175, 320, 50);
		powerBounds = new Rectangle(260, 0, 50, 50);
        fpsCounter = new FPSCounter();
		steerTouches = new HashMap<Integer, Vector2>();
		powerTouches = new HashMap<Integer, Vector2>();

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
		state = GAME_RUNNING;
	}

	private void updateRunning(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			touchPoint.set(event.x, event.y);
			guiCam.touchToWorld(touchPoint);

			Block touchedBlock = null;

			for (int j = 0; j < world.player.playerBlocks.size(); j++) {
				Block currBlock = world.player.playerBlocks.get(j);
				pBlockBounds = new Rectangle(currBlock.position.x - 12, currBlock.position.y - 12, 25, 25);
				if (OverlapTester.pointInRectangle(pBlockBounds, touchPoint)) {
					touchedBlock = currBlock;
					break;
				}
			}

			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (OverlapTester.pointInRectangle(powerBounds, touchPoint)  && powerTouches.size() == 0){
					world.player.power = false;
					powerTouches.put(event.pointer, new Vector2(touchPoint));
					return;
				}
				else if (OverlapTester.pointInRectangle(pauseBounds, touchPoint)){
					return;
				}
				if (touchedBlock == null && steerTouches.size() == 0) {
					steerTouches.put(event.pointer, new Vector2(touchPoint));
				}
				if(steerTouches.size() > 0 && steerTouches.keySet().contains(event.pointer)){
					updateRotation(event);
				}


			}


			if (event.type == TouchEvent.TOUCH_UP) {
				if(touchedBlock != null && !steerTouches.keySet().contains(event.pointer)  && !powerTouches.keySet().contains(event.pointer)){
					if (!touchedBlock.active && touchedBlock.energyCost <= world.player.energy) {
						touchedBlock.active = true;
						if(touchedBlock.energyCost > 0) {
							world.player.poweredBlocks.add(touchedBlock);
						}
					} else {
						touchedBlock.active = false;
						world.player.poweredBlocks.remove(touchedBlock);
					}
					return;
				}
				if (OverlapTester.pointInRectangle(pauseBounds, touchPoint) && !steerTouches.keySet().contains(event.pointer)  && !powerTouches.keySet().contains(event.pointer)) {
					state = GAME_PAUSED;
					steerTouches.clear();
					world.moveLeft = false;
					world.moveRight = false;
					return;
				}
				if (OverlapTester.pointInRectangle(powerBounds, touchPoint) && powerTouches.keySet().contains(event.pointer)){
					world.player.power = true;
					powerTouches.remove(event.pointer);
					return;
				}
				if (steerTouches.keySet().contains(event.pointer)) {
					steerTouches.remove(event.pointer);
					world.moveLeft = false;
					world.moveRight = false;
				}

			}
			if(event.type == TouchEvent.TOUCH_DRAGGED){
				if(powerTouches.size() > 0  && powerTouches.keySet().contains(event.pointer)){
					if(!OverlapTester.pointInRectangle(powerBounds, touchPoint)){
						world.player.power = true;
					}
					else{
						world.player.power = false;
					}
				}
				else if(steerTouches.size() > 0  && steerTouches.keySet().contains(event.pointer)){
					updateRotation(event);
				}
			}
		}

		world.update(deltaTime);
		if (world.state == World.WORLD_STATE_LEVEL_END) {
			state = GAME_LEVEL_END;
		}
		if (world.state == World.WORLD_STATE_GAME_OVER) {
			state = GAME_OVER;
			Settings.save(game.getFileIO());
		}
	}

	private void updateRotation(TouchEvent event){
		if (touchPoint.x < steerTouches.get(event.pointer).x) {
			world.moveRight = true;
			world.moveLeft = false;
		}
		if (touchPoint.x > steerTouches.get(event.pointer).x ) {
			world.moveLeft = true;
			world.moveRight = false;}
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
		world.clearBullets();

		Settings.spaceBucks += Settings.currLevel*1.5;
		world.spaceBucksEarned += Settings.currLevel*1.5;
		if(Settings.currLevel == Settings.maxLevel && Settings.maxLevel < Settings.totalLevels){
			Settings.maxLevel +=1;
			Settings.currLevel = Settings.maxLevel;
		}
		PlayerSave.load(game.getFileIO());
        Settings.save(game.getFileIO());
		game.setScreen(new EndLevelScreen(game, true, world.spaceBucksEarned, world.enemiesKilled));
	}
	
	private void updateGameOver() {
		world.clearBullets();
	    PlayerSave.load(game.getFileIO());
	    game.setScreen(new EndLevelScreen(game, false, world.spaceBucksEarned, world.enemiesKilled));
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
		batcher.beginBatch(Assets.imageTextures);
		batcher.drawSprite(160, 250, 320, 50, Assets.textureRegions.get("Rectangle"));
		batcher.drawSprite(160, 200, 320, 50, Assets.textureRegions.get("Rectangle"));
		Assets.font.drawTextCentered(batcher, "Continue", 160, 250, 32, 32);
		Assets.font.drawTextCentered(batcher, "Quit", 160, 200, 32, 32);
		batcher.endBatch();
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
package th.zirata.Game;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

import th.zirata.Blocks.Block;
import th.zirata.EnemyShips.Enemy;
import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.EnemyShips.Hydra;
import th.zirata.Blocks.MultiplierBlock;
import th.zirata.Blocks.TurretBlock;
import th.zirata.Blocks.Bullet;
import th.zirata.Menus.PopupManager;
import th.zirata.Settings.Settings;
import th.zirata.Settings.EnemySettings;

public class World {

	public static final float WORLD_WIDTH = 320;
	public Vector2 worldWidthVector;
	public static final float WORLD_HEIGHT = 480;
	public Vector2 worldHeightVector;
	public Vector2 WORLD_MID_POINT;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_LAST_ENEMY = 1;
	public static final int WORLD_STATE_LEVEL_END = 2;
	public static final int WORLD_STATE_GAME_OVER = 3;

	public static float POS_SIN_ANGLE = (float)Math.sin(0.0035);
	public static float NEG_SIN_ANGLE = (float) Math.sin(-0.0035);
	public static float POS_COS_ANGLE = (float)Math.cos(0.0035);
	public float world_sin;
	public float world_cos;
	public Vector2 world_x_axis;
	public Vector2 world_y_axis;
	
	public Player player;
	//public final ArrayList<Enemy> enemies;
	//public final ArrayList<Bullet> enemyBullets;
	public EnemyManager enemyManager;
	public static ArrayList<Bullet> playerBullets;
	public ArrayList<Background> backgrounds;
	public ArrayList<Background> nearBackgrounds;
	public ArrayList<Background> farBackgrounds;
	public Vector2[] grid;
	Rectangle currView;

	public float lastEnemyTime;
	public float timeToNextEnemy;
	//public float enemyNum;
	
	public int state;
	Random rand;

	public float enemyAngle;
	public float worldAngle;
	public boolean moveRight;
	public boolean moveLeft;

	public int spaceBucksEarned;
	public int enemiesKilled;

	public Level level;

	PopupManager popupManager;
	float eventCountdown;

	public World(Level currLevel, PopupManager popupManager){
		this.player = new Player();
//		enemies = new ArrayList<Enemy>();
//		enemyBullets = new ArrayList<Bullet>();
		playerBullets = new ArrayList<Bullet>();
		lastEnemyTime = 0;
		timeToNextEnemy = 4;
		this.state = WORLD_STATE_RUNNING;
		rand = new Random();

		WORLD_MID_POINT = new Vector2( WORLD_WIDTH/2, WORLD_HEIGHT/2);

		enemyAngle = 0;
		worldAngle = 0;
		moveRight = false;
		moveLeft = false;

		spaceBucksEarned = 0;
		enemiesKilled = 0;
		player.getEnergy();
		player.turnOnTurrets();
		backgrounds = new ArrayList<Background>();
		nearBackgrounds = new ArrayList<Background>();
		farBackgrounds = new ArrayList<Background>();

		world_cos = 1;
		world_sin = 0;
		world_x_axis = new Vector2(1, 0);
		world_y_axis = new Vector2(0, 1);


		grid = new Vector2[]{new Vector2(-1, 1), new Vector2(0, 1), new Vector2(1, 1),
				             new Vector2(-1, 0),                   new Vector2(1, 0),
				             new Vector2(-1, -1), new Vector2(0, -1), new Vector2(1, -1)};
		currView = new Rectangle(0, 0, 340, 500);
		worldWidthVector = new Vector2(WORLD_WIDTH, 0);
		worldHeightVector = new Vector2(0, WORLD_HEIGHT);

		this.popupManager = popupManager;
		level = currLevel;

		eventCountdown = 0;
		enemyManager = new EnemyManager(this);
	}
	
	public void update(float deltaTime){
		updateWorld(deltaTime);
		updateBackgrounds(deltaTime);
		updatePlayer(deltaTime);
		updatePlayerBullets(deltaTime);
		enemyManager.update(deltaTime);
		checkPlayerBullets();
		checkPlayerCollision();
		checkLevelEnd();
		checkGameOver();

	}

	private void updateWorld(float deltaTime){
		double angleDiff = findSpeed();

		//TODO transition to using the x and y axis and eliminate sin/cos calls

		POS_SIN_ANGLE = (float)Math.sin(Math.toRadians(angleDiff));
		NEG_SIN_ANGLE = (float)Math.sin(-POS_SIN_ANGLE);
		POS_COS_ANGLE = (float)Math.cos(Math.toRadians(angleDiff));
		if(moveRight){
			worldAngle -= angleDiff;
			enemyAngle = NEG_SIN_ANGLE;
			world_cos = (float)Math.cos(Math.toRadians(worldAngle));
			world_sin = (float)Math.sin(Math.toRadians(worldAngle));
			world_x_axis.rotate(POS_COS_ANGLE, NEG_SIN_ANGLE);
			world_y_axis.rotate(POS_COS_ANGLE, NEG_SIN_ANGLE);
		}
		if(moveLeft){
			worldAngle += angleDiff;
			enemyAngle = POS_SIN_ANGLE;
			world_cos = (float)Math.cos(Math.toRadians(worldAngle));
			world_sin = (float)Math.sin(Math.toRadians(worldAngle));
			world_x_axis.rotate(POS_COS_ANGLE, POS_SIN_ANGLE);
			world_y_axis.rotate(POS_COS_ANGLE, POS_SIN_ANGLE);
		}
	}

	public void updateBackgrounds(float deltaTime){
		updateBackgroundList(deltaTime, backgrounds, "background", -6);
		updateBackgroundList(deltaTime, farBackgrounds, "FarStar", -6);
		updateBackgroundList(deltaTime, nearBackgrounds, "NearStar", -10);
	}

	private void updateBackgroundList(float deltaTime, ArrayList<Background> backgrounds, String sprite, float velocity){
		Background playerOnBackground = null;

		for(int i = 0; i < backgrounds.size(); i++){
			Background currBackground = backgrounds.get(i);
			currBackground.isRelevant = false;
			if(moveLeft || moveRight) {
				currBackground.rotateConstantVelocity(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
			backgrounds.get(i).update(deltaTime);
			if(playerOnBackground == null && OverlapTester.pointInRotatedRectangle(currBackground.bounds, WORLD_MID_POINT)){
				playerOnBackground = currBackground;
				playerOnBackground.isRelevant = true;
			}
		}

		if(playerOnBackground == null){
			//TODO: Temporary workaround for background bug, still need to investigate this
			if(backgrounds.size() > 0){
				//Log.d("SKIPPING", "***************");
				return;
			}
			playerOnBackground = new Background(0, 0, 320, 480, new Vector2(0, velocity), sprite);
			playerOnBackground.isRelevant = true;
			backgrounds.add(playerOnBackground);
		}
		//TODO: Already have axes rotated, should be able to go along it 320 and 480...
		playerOnBackground.bounds.rotateVector(worldWidthVector);
		playerOnBackground.bounds.rotateVector(worldHeightVector);
		ArrayList<Vector2> undrawn = null;
		for(int i = 0; i < grid.length; i++){
			float widthAdd = grid[i].x*worldWidthVector.x + grid[i].y*worldHeightVector.x;
			float heightAdd = grid[i].x*worldWidthVector.y + grid[i].y*worldHeightVector.y;
			float currSpotX = playerOnBackground.position.x + widthAdd;
			float currSpotY = playerOnBackground.position.y + heightAdd;
			Background covers = null;
			for(int j = 0; j < backgrounds.size(); j++){
				if(backgrounds.get(j).isRelevant){
					continue;
				}
				if (Math.abs(currSpotX-backgrounds.get(j).position.x) < 5 && Math.abs(currSpotY - backgrounds.get(j).position.y) < 5){
					covers = backgrounds.get(j);
					covers.isRelevant = true;
					break;
				}
			}
			if (covers == null) {
				if(undrawn == null){
					undrawn = new ArrayList<Vector2>();
				}
				undrawn.add(grid[i]);
			}
		}
		if(undrawn != null){
			for (int i = 0; i < undrawn.size(); i++){
				float widthAdd = undrawn.get(i).x*worldWidthVector.x + undrawn.get(i).y*worldHeightVector.x;
				float heightAdd = undrawn.get(i).x*worldWidthVector.y + undrawn.get(i).y*worldHeightVector.y;
				float currSpotX = playerOnBackground.position.x + widthAdd;
				float currSpotY = playerOnBackground.position.y + heightAdd;
			    float currLowerLeftX = playerOnBackground.bounds.lowerLeft.x + widthAdd;
			    float currLowerLeftY = playerOnBackground.bounds.lowerLeft.y + heightAdd;
				Background toMove = null;
				for(Background b : backgrounds){
					if (!b.isRelevant){
						toMove = b;
						break;
					}
				}
				if(toMove == null){
				    toMove = new Background(currSpotX, currSpotY, 320, 480, new Vector2(0, velocity), sprite);
		    		toMove.bounds.rotationAngle.set(playerOnBackground.bounds.rotationAngle.x, playerOnBackground.bounds.rotationAngle.y);
				    toMove.position.set(currSpotX, currSpotY);
				    toMove.bounds.lowerLeft.set(currLowerLeftX, currLowerLeftY);
					toMove.bounds.setVertices();
					toMove.isRelevant = true;
				    backgrounds.add(toMove);
				}
				else{
					toMove.bounds.rotationAngle.set(playerOnBackground.bounds.rotationAngle.x, playerOnBackground.bounds.rotationAngle.y);
					toMove.position.set(currSpotX, currSpotY);
					toMove.bounds.lowerLeft.set(currLowerLeftX, currLowerLeftY);
					toMove.bounds.setVertices();
					toMove.isRelevant = true;
				}
			}
		}
		worldWidthVector.set(WORLD_WIDTH, 0);
		worldHeightVector.set(0, WORLD_HEIGHT);
	}

	private void updatePlayer(float deltaTime){
		Block currBlock;
		for(int i = 0; i < player.playerBlocks.size(); i++){
			currBlock = player.playerBlocks.get(i);
			if(currBlock.getClass().equals(TurretBlock.class) && currBlock.active && enemyManager.enemies.size() > 0 && enemyManager.enemies.get(0).enemyBlocks.size() > 0){
				TurretBlock tBlock = (TurretBlock) currBlock;
				tBlock.action(this);
			}
			if(currBlock.getClass().equals(MultiplierBlock.class) && currBlock.active){
				currBlock.action(this);
			}
		}
		while (player.energy < 0){
			for(int i = 0; i< player.playerBlocks.size(); i++){
				currBlock = player.playerBlocks.get(i);
				if(currBlock.energyCost > 0 && currBlock.active){
					currBlock.active = false;
					player.poweredBlocks.remove(currBlock);
					player.getEnergy();
					if(player.energy >= 0 ){
						return;
					}
				}
			}
		}
		player.update(deltaTime);
	}

	private double findSpeed(){
		double rotateSpeed = .1 + .02*player.energy - .01*player.playerBlocks.size();
		if(rotateSpeed > .6) {
			rotateSpeed = .6;
		}
		return rotateSpeed;
	}



	private void updatePlayerBullets(float deltaTime){

		for(int i = 0; i < playerBullets.size(); i++) {
            Bullet b = playerBullets.get(i);
            b.update(deltaTime);
			if (moveLeft || moveRight) {
				b.rotate(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
			if(playerBullets.get(i).outOfBounds()){
				playerBullets.remove(b);
			}
		}
	}

	public void createCurrency(){
		if(rand.nextFloat() < .25){
			Settings.spaceBucks += 1;
			spaceBucksEarned += 1;
		}
	}

	private void checkPlayerBullets(){
		for(int i = 0; i < playerBullets.size(); i++) {
			Bullet b = playerBullets.get(i);
			if (checkEnemyCollision(b)) {
				playerBullets.remove(i);
			}

		}
	}
	
	private boolean checkEnemyCollision(Bullet bullet){
		
		for(int i = 0; i < enemyManager.enemies.size(); i++){
			Enemy enemy = enemyManager.enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++){
				
				Block eBlock = enemy.enemyBlocks.get(j);
				if(OverlapTester.overlapPolygons(bullet.bounds, eBlock.bounds)){
					eBlock.health -= bullet.damage;
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkPlayerCollision(Bullet bullet){
		for(int i = 0; i < player.playerBlocks.size(); i++){
			Block pBlock = player.playerBlocks.get(i);
				if(OverlapTester.overlapRectangles(bullet.bounds, pBlock.bounds)){
					pBlock.health -= bullet.damage;
					return true;
				}
		}
		return false;
	}
	
	public boolean checkEnemyCollision(Block pBlock){
		for(int i = 0; i < enemyManager.enemies.size(); i++){
			Enemy enemy = enemyManager.enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++){
				
				Block eBlock = enemy.enemyBlocks.get(j);
				if(OverlapTester.overlapPolygons(pBlock.bounds, eBlock.bounds)){
					eBlock.health -= 10;
					pBlock.health -= 10;
					return true;
				}
			}
		}
		return false;
	}
	
	private void checkPlayerCollision(){
		for(int i = 0; i < player.playerBlocks.size(); i++){
			Block currBlock = player.playerBlocks.get(i);
			checkEnemyCollision(currBlock);
		}
	}
	
	private void checkLevelEnd(){
		if(state == WORLD_STATE_LAST_ENEMY && enemyManager.enemies.size() == 0){
			state = WORLD_STATE_LEVEL_END;
		}
	}
	
	private void checkGameOver() {
        if (player.playerBlocks.size() <= 0) {
            state = WORLD_STATE_GAME_OVER;
        }
    }

	public void clearBullets(){
		playerBullets.clear();
		for(int i = 0; i < player.playerBlocks.size(); i++){
			Block currBlock = player.playerBlocks.get(i);
			if(currBlock.getClass() == TurretBlock.class){
				TurretBlock tBlock = (TurretBlock) currBlock;
				tBlock.bullets.clear();
			}
		}
	}
}

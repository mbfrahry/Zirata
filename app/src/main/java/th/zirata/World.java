package th.zirata;

import android.preference.MultiSelectListPreference;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.androidgames.framework.math.OverlapTester;
import com.badlogic.androidgames.framework.math.Rectangle;
import com.badlogic.androidgames.framework.math.Vector2;

public class World {

	public static final float WORLD_WIDTH = 320;
	public static final float WORLD_HEIGHT = 480;
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
	
	public Player player;
	public final ArrayList<Enemy> enemies;
	public final ArrayList<Bullet> enemyBullets;
	public static ArrayList<Bullet> playerBullets;
	public ArrayList<Background> backgrounds;
	public ArrayList<Background> nearBackgrounds;
	public ArrayList<Background> farBackgrounds;
	public Background playerOnBackground;
	public Vector2[] grid;

	public float lastEnemyTime;
	public float timeToNextEnemy;
	public float enemyNum;
	
	public int state;
	Random rand;

	public float enemyAngle;
	public float worldAngle;
	public boolean moveRight;
	public boolean moveLeft;

	public int spaceBucksEarned;
	public int enemiesKilled;

	public World(){
		this.player = new Player();
		enemies = new ArrayList<Enemy>();
		enemyBullets = new ArrayList<Bullet>();
		playerBullets = new ArrayList<Bullet>();
		lastEnemyTime = 0;
		timeToNextEnemy = 4;
		enemyNum = 0;
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

		grid = new Vector2[]{new Vector2(-1, 1), new Vector2(0, 1), new Vector2(1, 1),
				             new Vector2(-1, 0),                   new Vector2(1, 0),
				             new Vector2(-1, -1), new Vector2(0, -1), new Vector2(1, -1)};
	}
	
	public void update(float deltaTime){
		updateWorld(deltaTime);
		updateBackgrounds(deltaTime);
		updatePlayer(deltaTime);
		updatePlayerBullets(deltaTime);
		updateEnemies(deltaTime);
		updateEnemyBullets(deltaTime);
		checkPlayerBullets();
		checkEnemyBullets();
		checkPlayerCollision();
		checkLevelEnd();
		checkGameOver();

	}

	private void updateWorld(float deltaTime){
		double angleDiff = findSpeed();

		POS_SIN_ANGLE = (float)Math.sin(Math.toRadians(angleDiff));
		NEG_SIN_ANGLE = (float)Math.sin(-POS_SIN_ANGLE);
		POS_COS_ANGLE = (float)Math.cos(Math.toRadians(angleDiff));
		if(moveRight){
			worldAngle -= angleDiff;
			enemyAngle = NEG_SIN_ANGLE;
			world_cos = (float)Math.cos(Math.toRadians(worldAngle));
			world_sin = (float)Math.sin(Math.toRadians(worldAngle));
			updateBackgroundAngles(world_cos, world_sin);
		}
		if(moveLeft){
			worldAngle += angleDiff;
			enemyAngle = POS_SIN_ANGLE;
			world_cos = (float)Math.cos(Math.toRadians(worldAngle));
			world_sin = (float)Math.sin(Math.toRadians(worldAngle));
			updateBackgroundAngles(world_cos, world_sin);
		}

	}

	private void updateBackgroundAngles(float cosChange, float sinChange){
		for(int i = 0; i < backgrounds.size(); i++){
			backgrounds.get(i).bounds.rotationAngle.x = cosChange;
			backgrounds.get(i).bounds.rotationAngle.y = sinChange;
		}
		for (int i = 0; i < farBackgrounds.size(); i++){
			farBackgrounds.get(i).bounds.rotationAngle.x = cosChange;
			farBackgrounds.get(i).bounds.rotationAngle.y = sinChange;
		}
		for (int i = 0; i < nearBackgrounds.size(); i++){
			nearBackgrounds.get(i).bounds.rotationAngle.x = cosChange;
			nearBackgrounds.get(i).bounds.rotationAngle.y = sinChange;
		}
	}

	private void updateBackgrounds(float deltaTime){
		updateBackgroundList(deltaTime, backgrounds, "background", -6);
		updateBackgroundList(deltaTime, farBackgrounds, "FarStar", -6);
		updateBackgroundList(deltaTime, nearBackgrounds, "NearStar", -20);
	}

	private void updateBackgroundList(float deltaTime, ArrayList<Background> backgrounds, String sprite, float velocity){
		ArrayList<Background> onScreen = new ArrayList<Background>();
		ArrayList<Background> notOnScreen = new ArrayList<Background>();
		Background playerOnBackground = null;
		Rectangle currView = new Rectangle(0, 0, 340, 500);

		for(int i = 0; i < backgrounds.size(); i++){
			Background currBackground = backgrounds.get(i);
			if(moveLeft || moveRight) {
				currBackground.rotateConstantVelocity(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
			backgrounds.get(i).update(deltaTime);
			Rectangle backgroundRect = currBackground.bounds;
			if(OverlapTester.pointInRotatedRectangle(currBackground.bounds, new Vector2(160, 240))){
				playerOnBackground = currBackground;
			}
			if(OverlapTester.overlapPolygons(backgroundRect, currView)){
				onScreen.add(currBackground);
			}
			else{
				notOnScreen.add(currBackground);
			}
		}
		Vector2 widthVector = new Vector2(320, 0);
		Vector2 heightVector = new Vector2(0, 480);

		if(playerOnBackground == null){
			playerOnBackground = new Background(0, 0, 320, 480, new Vector2(0, velocity), sprite);
			backgrounds.add(playerOnBackground);
			onScreen.add(playerOnBackground);
		}

		playerOnBackground.bounds.rotateVector(widthVector);
		playerOnBackground.bounds.rotateVector(heightVector);

		for(int i = 0; i < grid.length; i++){
			float widthAdd = grid[i].x*widthVector.x + grid[i].y*heightVector.x;
			float heightAdd = grid[i].x*widthVector.y + grid[i].y*heightVector.y;
			Vector2 currSpot = new Vector2(playerOnBackground.position.x + widthAdd, playerOnBackground.position.y + heightAdd);
			Vector2 currLowerLeft = new Vector2(playerOnBackground.bounds.lowerLeft.x + widthAdd, playerOnBackground.bounds.lowerLeft.y + heightAdd);
			Background covers = null;
			//currGrid.add(currSpot);
			for(int j = 0; j < onScreen.size(); j++){
				if(OverlapTester.pointInRotatedRectangle(onScreen.get(j).bounds, currSpot)){
					covers = onScreen.get(j);
					break;
				}
			}
			if (covers == null) {
				//Pull one from notOnScreen if possible, otherwise create one
				Background toMove;
				if (notOnScreen.size() > 0) {
					toMove = notOnScreen.get(0);
					notOnScreen.remove(toMove);
				}
				else{
					toMove = new Background(currSpot.x, currSpot.y, 320, 480, new Vector2(0, velocity), sprite);
					toMove.bounds.rotationAngle.set(playerOnBackground.bounds.rotationAngle.x, playerOnBackground.bounds.rotationAngle.y);
					backgrounds.add(toMove);
				}
				toMove.position.set(currSpot.x, currSpot.y);
				toMove.bounds.lowerLeft.set(currLowerLeft.x, currLowerLeft.y);
			}
			else{
				onScreen.remove(covers);
			}
		}
		for(int i = 0; i < notOnScreen.size(); i++){
			backgrounds.remove(notOnScreen.get(i));
		}
	}

	private void updatePlayer(float deltaTime){
		Block currBlock;
		for(int i = 0; i < player.playerBlocks.size(); i++){
			currBlock = player.playerBlocks.get(i);
			if(currBlock.getClass().equals(TurretBlock.class) && currBlock.active && enemies.size() > 0 && enemies.get(0).enemyBlocks.size() > 0){
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
			if (moveLeft || moveRight) {
				playerBullets.get(i).rotate(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
		}
	}
	
	private void updateEnemies(float deltaTime){
		lastEnemyTime += deltaTime;
		if(lastEnemyTime > timeToNextEnemy && state != WORLD_STATE_LAST_ENEMY){
			generateEnemy();
			
			if(enemyNum % 4 == 0 && timeToNextEnemy > 2){
				timeToNextEnemy -= 0.5;
			}
			
			lastEnemyTime = 0;
		}
		
		for(int i = 0; i < enemies.size(); i++){
			Enemy enemy = enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++){
				Block currBlock = enemy.enemyBlocks.get(j);

				if(moveLeft || moveRight) {
					currBlock.rotate(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
				}

				if(currBlock.getClass().equals(EnemyTurretBlock.class)){
					EnemyTurretBlock tBlock = (EnemyTurretBlock) currBlock;
					generateEnemyBullet(tBlock);
				}
			}
			enemy.update(deltaTime);
			
			if(enemy.checkDead()){
				enemies.remove(i);
				enemiesKilled += 1;
				createCurrency();
			}
		}
	}
	
	public void updateEnemyBullets(float deltaTime){
		for(int i = 0; i < enemyBullets.size(); i++){
			Bullet b = enemyBullets.get(i);
			if (moveLeft || moveRight) {
				b.rotate(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
			b.update(deltaTime);
			if(b.outOfBounds()){
				enemyBullets.remove(i);
			}
		}
	}
	
	public void generateEnemyBullet(EnemyTurretBlock tBlock){
		if(tBlock.state == tBlock.TURRET_READY && tBlock.bullets.size() < 1){
			if(player.playerBlocks.size() > 0){
				Block randBlock = player.playerBlocks.get(Math.abs(rand.nextInt()) % player.playerBlocks.size());
				enemyBullets.add(new Bullet(tBlock.position.x, tBlock.position.y,randBlock.position.x, randBlock.position.y, tBlock.bulletDamage, tBlock.fireRange));
				tBlock.resetBlock();
			}
		}
	}
	
	private void createCurrency(){
		if(rand.nextFloat() < .25){
			Settings.spaceBucks += 1;
			spaceBucksEarned += 1;
		}
	}
	
	private void generateEnemy(){

		String levelName = "level"+Settings.currLevel;
		double[] enemyLevelSettings = EnemySettings.enemiesInLevel.get(levelName);
		if((int)enemyLevelSettings[0] > enemyNum) {
			Enemy e = EnemySettings.getEnemy(Settings.currLevel);

			for (Block b : e.enemyBlocks) {
				b.rotate(world_sin, world_cos, WORLD_MID_POINT);
			}

			enemies.add(e);
		}
		else{
			state = WORLD_STATE_LAST_ENEMY;
		}
	}
	
	private void checkEnemyBullets(){
		Bullet b;
		for(int i = 0; i < enemyBullets.size(); i++){

			b = enemyBullets.get(i);
			if(checkPlayerCollision(b)){
				enemyBullets.remove(i);
			}
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
		
		for(int i = 0; i < enemies.size(); i++){
			Enemy enemy = enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++){
				
				Block eBlock = enemy.enemyBlocks.get(j);
				if(OverlapTester.overlapRectangles(bullet.bounds, eBlock.bounds)){
					eBlock.health -= bullet.damage;
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkPlayerCollision(Bullet bullet){
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
		for(int i = 0; i < enemies.size(); i++){
			Enemy enemy = enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++){
				
				Block eBlock = enemy.enemyBlocks.get(j);
				if(OverlapTester.overlapRectangles(pBlock.bounds, eBlock.bounds)){
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
		if(state == WORLD_STATE_LAST_ENEMY && enemies.size() == 0){
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

package th.zirata;

import android.preference.MultiSelectListPreference;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.androidgames.framework.math.OverlapTester;
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
	public static final ArrayList<Bullet> PLAYER_BULLETS = new ArrayList<Bullet>();
	
	public Player player;
	public final ArrayList<Enemy> enemies;
	public final ArrayList<Enemy> testEnemies;
	public final ArrayList<Bullet> enemyBullets;
	public final ArrayList<Bullet> playerBullets;
	public ArrayList<Background> backgrounds;

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
		testEnemies = EnemySettings.getEnemies(Settings.currLevel);
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
		backgrounds.add(new Background(0, 0, 300, 480, new Vector2(0, -3), "Background"));
		backgrounds.add(new Background(0, 0, 320, 480, new Vector2(0, -10), "NearStar"));
		backgrounds.add(new Background(0, 0, 320, 480, new Vector2(0,-3), "FarStar"));

		backgrounds.add(new Background(0, 480, 320, 480, new Vector2(0,-3), "Background"));
		backgrounds.add(new Background(0, 480, 320, 480, new Vector2(0,-10), "NearStar"));
		backgrounds.add(new Background(0, 480, 320, 480, new Vector2(0,-3), "FarStar"));

		backgrounds.add(new Background(320, 0, 320, 480, new Vector2(0,-3), "Background"));
		backgrounds.add(new Background(320, 0, 320, 480, new Vector2(0,-10), "NearStar"));
		backgrounds.add(new Background(320, 0, 320, 480, new Vector2(0,-3), "FarStar"));

		backgrounds.add(new Background(320, 480, 320, 480, new Vector2(0,-3), "Background"));
		backgrounds.add(new Background(320, 480, 320, 480, new Vector2(0,-10), "NearStar"));
		backgrounds.add(new Background(320, 480, 320, 480, new Vector2(0,-3), "FarStar"));
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
		}
		if(moveLeft){
			worldAngle += angleDiff;
			enemyAngle = POS_SIN_ANGLE;
		}

	}

	private void updateBackgrounds(float deltaTime){
		for(int i = 0; i < backgrounds.size(); i++){
			Background currBackground = backgrounds.get(i);
			if(moveLeft || moveRight) {
				currBackground.rotateConstantVelocity(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
			backgrounds.get(i).update(deltaTime);
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
		for(int i = 0; i < PLAYER_BULLETS.size(); i++) {
			if (moveLeft || moveRight) {
				PLAYER_BULLETS.get(i).rotate(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
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
				enemyBullets.add(new Bullet(tBlock.position.x, tBlock.position.y,randBlock.position.x, randBlock.position.y, tBlock.bulletDamage));
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

		if(testEnemies.size() > 0){
			enemies.add(testEnemies.remove(0));
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
		for(int i = 0; i < PLAYER_BULLETS.size(); i++) {
			Bullet b = PLAYER_BULLETS.get(i);
			if (checkEnemyCollision(b)) {
				PLAYER_BULLETS.remove(i);
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
		PLAYER_BULLETS.clear();
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

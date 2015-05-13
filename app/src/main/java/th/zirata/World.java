package th.zirata;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import com.badlogic.androidgames.framework.math.OverlapTester;

public class World {

	public static final float WORLD_WIDTH = 320;
	public static final float WORLD_HEIGHT = 480;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_LAST_ENEMY = 1;
	public static final int WORLD_STATE_LEVEL_END = 2;
	public static final int WORLD_STATE_GAME_OVER = 3;

	public static final float POS_SIN_ANGLE = (float)Math.sin(0.0035);
	public static final float NEG_SIN_ANGLE = (float) Math.sin(-0.0035);
	public static final float POS_COS_ANGLE = (float)Math.cos(0.0035);

	
	public Player player;
	public final ArrayList<Enemy> enemies;
	public final ArrayList<Bullet> enemyBullets;
	public float lastEnemyTime;
	public float timeToNextEnemy;
	public float enemyNum;
	
	public int state;
	Random rand;

	public float enemyAngle;
	public float worldAngle;
	public boolean moveRight;
	public boolean moveLeft;

	public World(){
		this.player = new Player();
		enemies = new ArrayList<Enemy>();
		enemyBullets = new ArrayList<Bullet>();
		lastEnemyTime = 0;
		timeToNextEnemy = 4;
		enemyNum = 0;
		this.state = WORLD_STATE_RUNNING;
		rand = new Random();

		//enemyAngle = (float)0.0035;
		enemyAngle = 0;
		worldAngle = 0;
		moveRight = false;
		moveLeft = false;
	}
	
	public void update(float deltaTime){
		updateWorld(deltaTime);
		updatePlayer(deltaTime);
		updateEnemies(deltaTime);
		updateEnemyBullets(deltaTime);
		checkPlayerBullets();
		checkEnemyBullets();
		checkPlayerCollision();
		checkLevelEnd();
		checkGameOver();
	}

	private void updateWorld(float deltaTime){
		if(moveRight){
			worldAngle -= .2;
			//enemyAngle = Math.abs(enemyAngle);
			enemyAngle = POS_SIN_ANGLE;
		}
		if(moveLeft){
			worldAngle += .2;
			//enemyAngle = -Math.abs(enemyAngle);
			enemyAngle = NEG_SIN_ANGLE;
		}
	}

	private void updatePlayer(float deltaTime){
		player.update(deltaTime);
	}
	
	private void updateEnemies(float deltaTime){
		lastEnemyTime += deltaTime;
		if(lastEnemyTime > timeToNextEnemy && state != WORLD_STATE_LAST_ENEMY){
			generateEnemy();
			
			if(enemyNum % 4 == 0 && timeToNextEnemy > 2){
				timeToNextEnemy -= 0.5;
			}
			
			if(enemyNum >= Settings.numEnemies){
				state = WORLD_STATE_LAST_ENEMY;
			}
			
			lastEnemyTime = 0;
		}
		
		for(int i = 0; i < enemies.size(); i++){
			Enemy enemy = enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++){
				Block currBlock = enemy.enemyBlocks.get(j);

				if(moveLeft || moveRight) {


					double x = currBlock.position.x;
					double y = currBlock.position.y;
					x -= 160;
					y -= 210;
					currBlock.position.x = (float) ( x * POS_COS_ANGLE + y * -enemyAngle);
					currBlock.position.y = (float)(x * enemyAngle + y * POS_COS_ANGLE);
					currBlock.position.x += 160;
					currBlock.position.y += 210;
				}

				if(currBlock.getClass().equals(EnemyTurretBlock.class)){
					EnemyTurretBlock tBlock = (EnemyTurretBlock) currBlock;
					generateEnemyBullet(tBlock);
				}
			}
			enemy.update(deltaTime);
			
			if(enemy.checkDead()){
				enemies.remove(i);
				createCurrency();
			}
		}
	}
	
	public void updateEnemyBullets(float deltaTime){
		//Log.d("EnemyBulletSize", enemyBullets.size() + " ");
		for(int i = 0; i < enemyBullets.size(); i++){
			enemyBullets.get(i).update(deltaTime);
			if(enemyBullets.get(i).outOfBounds()){
				enemyBullets.remove(i);
			}
		}
	}
	
	public void generateEnemyBullet(EnemyTurretBlock tBlock){
		if(tBlock.state == tBlock.TURRET_READY && tBlock.bullets.size() < 1){
			if(player.playerBlocks.size() > 0){
				Block randBlock = player.playerBlocks.get(Math.abs(rand.nextInt()) % player.playerBlocks.size());
				//BlockAction blockAction = new BlockAction();
				//blockAction.x = randBlock.position.x;
				//blockAction.y = randBlock.position.y;
				//tBlock.action(blockAction);
				enemyBullets.add(new Bullet(tBlock.position.x, tBlock.position.y,randBlock.position.x, randBlock.position.y));
				tBlock.resetBlock();
			}
		}
	}
	
	private void createCurrency(){
		if(rand.nextFloat() < .25){
			Settings.spaceBucks += 1;
		}
	}
	
	private void generateEnemy(){
		if(rand.nextFloat() > 0.25){
			enemies.add(new Enemy(1));
		}
		else{
			enemies.add(new Enemy(2));
		}
		enemyNum += 1;
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
		for(int i = 0; i < player.playerBlocks.size(); i++){
			Block currBlock = player.playerBlocks.get(i);
			if(currBlock.getClass().equals(TurretBlock.class)){
				TurretBlock tBlock = (TurretBlock)currBlock;
				Bullet b;
				for(int j = 0; j < tBlock.bullets.size(); j++){
					b = tBlock.bullets.get(j);
					if(checkEnemyCollision(b)){
						tBlock.bullets.remove(j);
					}
				}
			}
			if(currBlock.getClass().equals(MachineGunBlock.class)){
				MachineGunBlock mgBlock = (MachineGunBlock)currBlock;
				Bullet b;
				for(int j = 0; j < mgBlock.bullets.size(); j++){
					b = mgBlock.bullets.get(j);
					if(checkEnemyCollision(b)){
						mgBlock.bullets.remove(j);
					}
				}
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
}

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
	public Vector2 world_x_axis;
	public Vector2 world_y_axis;
	
	public Player player;
	public final ArrayList<Enemy> enemies;
	public final ArrayList<Bullet> enemyBullets;
	public static ArrayList<Bullet> playerBullets;
	public ArrayList<Background> backgrounds;
	public ArrayList<Background> nearBackgrounds;
	public ArrayList<Background> farBackgrounds;
	public Vector2[] grid;
	Rectangle currView;

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
		world_x_axis = new Vector2(1, 0);
		world_y_axis = new Vector2(0, 1);


		grid = new Vector2[]{new Vector2(-1, 1), new Vector2(0, 1), new Vector2(1, 1),
				             new Vector2(-1, 0),                   new Vector2(1, 0),
				             new Vector2(-1, -1), new Vector2(0, -1), new Vector2(1, -1)};
		currView = new Rectangle(0, 0, 340, 500);
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
			world_x_axis.rotate(POS_COS_ANGLE, NEG_SIN_ANGLE);
			world_y_axis.rotate(POS_COS_ANGLE, NEG_SIN_ANGLE);
			updateBackgroundAngles(world_cos, world_sin);
		}
		if(moveLeft){
			worldAngle += angleDiff;
			enemyAngle = POS_SIN_ANGLE;
			world_cos = (float)Math.cos(Math.toRadians(worldAngle));
			world_sin = (float)Math.sin(Math.toRadians(worldAngle));
			world_x_axis.rotate(POS_COS_ANGLE, POS_SIN_ANGLE);
			world_y_axis.rotate(POS_COS_ANGLE, POS_SIN_ANGLE);
			updateBackgroundAngles(world_cos, world_sin);
		}
		Log.d("x axis", world_x_axis.x + ", " + world_x_axis.y);
		Log.d("y axis", world_y_axis.x + ", " + world_y_axis.y);
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
		//ArrayList<Background> onScreen = new ArrayList<Background>();
		//ArrayList<Background> notOnScreen = new ArrayList<Background>();
		Background playerOnBackground = null;

		//Rectangle currView = new Rectangle(0, 0, 340, 500);

		for(int i = 0; i < backgrounds.size(); i++){
			Background currBackground = backgrounds.get(i);
			if(moveLeft || moveRight) {
				currBackground.rotateConstantVelocity(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
			}
			backgrounds.get(i).update(deltaTime);
			//Rectangle backgroundRect = currBackground.bounds;
			if(playerOnBackground == null && OverlapTester.pointInRotatedRectangle(currBackground.bounds, WORLD_MID_POINT)){
				playerOnBackground = currBackground;
			}
//			if(OverlapTester.overlapPolygons(backgroundRect, currView)){
//				onScreen.add(currBackground);
//			}
//			else{
//				notOnScreen.add(currBackground);
//			}
		}
		Vector2 widthVector = new Vector2(320, 0);
		Vector2 heightVector = new Vector2(0, 480);

		ArrayList<Background> newBackgrounds = new ArrayList<Background>();
		//ArrayList<Background> bCopy= new ArrayList<Background>();
		//bCopy.addAll(backgrounds);
		if(playerOnBackground == null){
			playerOnBackground = new Background(0, 0, 320, 480, new Vector2(0, velocity), sprite);

			//onScreen.add(playerOnBackground);
		}
		else{
			//bCopy.remove(playerOnBackground);

		}
		newBackgrounds.add(playerOnBackground);

		playerOnBackground.bounds.rotateVector(widthVector);
		playerOnBackground.bounds.rotateVector(heightVector);

		for(int i = 0; i < grid.length; i++){
			float widthAdd = grid[i].x*widthVector.x + grid[i].y*heightVector.x;
			float heightAdd = grid[i].x*widthVector.y + grid[i].y*heightVector.y;
			Vector2 currSpot = new Vector2(playerOnBackground.position.x + widthAdd, playerOnBackground.position.y + heightAdd);
			Vector2 currLowerLeft = new Vector2(playerOnBackground.bounds.lowerLeft.x + widthAdd, playerOnBackground.bounds.lowerLeft.y + heightAdd);
			Background covers = null;
			//currGrid.add(currSpot);
			for(int j = 0; j < backgrounds.size(); j++){
				if (Math.abs(currSpot.x-backgrounds.get(j).position.x) < 5 && Math.abs(currSpot.y - backgrounds.get(j).position.y) < 5){
					covers = backgrounds.get(j);
					break;
				}
			}
			if (covers == null) {
				Background toMove = new Background(currSpot.x, currSpot.y, 320, 480, new Vector2(0, velocity), sprite);
				toMove.bounds.rotationAngle.set(playerOnBackground.bounds.rotationAngle.x, playerOnBackground.bounds.rotationAngle.y);
				toMove.position.set(currSpot.x, currSpot.y);
				toMove.bounds.lowerLeft.set(currLowerLeft.x, currLowerLeft.y);
				newBackgrounds.add(toMove);
			}
			else{
				//bCopy.remove(covers);
				newBackgrounds.add(covers);
			}
		}
//		for(int i = 0; i < bCopy.size(); i++){
//			backgrounds.remove(bCopy.get(i));
//		}
//		for(int i = 0; i < newBackgrounds.size(); i++){
//			backgrounds.add(newBackgrounds.get(i));
//		}
		if(sprite.equals("background")){
			this.backgrounds = newBackgrounds;
		}
		else if(sprite.equals("FarStar")){
			this.farBackgrounds = newBackgrounds;
		}
		else{
			this.nearBackgrounds = newBackgrounds;
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
					if(enemy.getClass().equals(Hydra.class)){
						currBlock.rotateConstantVelocity(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
					}
					else {
						currBlock.rotate(enemyAngle, POS_COS_ANGLE, WORLD_MID_POINT);
					}
				}

				if(currBlock.getClass().equals(EnemyTurretBlock.class)){
					EnemyTurretBlock tBlock = (EnemyTurretBlock) currBlock;
					generateEnemyBullet(tBlock);
				}
			}
			enemy.update(deltaTime, this);
			
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
			//Enemy e = EnemySettings.getBoss("Hydra");
			e.editBlockAttributes();
			for (Block b : e.enemyBlocks) {
				b.rotate(world_sin, world_cos, WORLD_MID_POINT);
			}
			enemyNum += 1;
			enemies.add(e);
		}
		else{
			state = WORLD_STATE_LAST_ENEMY;
			if(enemyLevelSettings[3] >= 0) {
				if(enemyLevelSettings[3] == 1) {
					Enemy e = EnemySettings.getBoss("Hydra");
					for (Block b : e.enemyBlocks) {
						b.rotate(world_sin, world_cos, WORLD_MID_POINT);
					}
					enemies.add(e);
				}
			}
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

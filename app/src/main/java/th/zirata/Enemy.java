package th.zirata;

import java.util.ArrayList;
import java.util.Random;

public class Enemy{

	public ArrayList<Block> enemyBlocks;
	Random rand;
	int enemyType;
	
	public Enemy(int enemyType){
		enemyBlocks = new ArrayList<Block>();
		rand = new Random();
		this.enemyType = enemyType;
		editBlockAttributes();
	}

	public void editBlockAttributes(){
		float num = rand.nextFloat();
		
		int health = Settings.enemyHealth;
		float x;
		float y;
		float multiplier = 1;
		if(num <= 0.25f){
			x = -15;
			y = rand.nextFloat() * 480;
		}
		else if(num > 0.25f && num <= 0.5f ){
			x = 330;
			y = rand.nextFloat() * 480;
			multiplier = -1;
		}
		else if(num > 0.5f && num <= 0.75f){
			x = rand.nextFloat() * 320;
			y = -10;
			if(x > 160){
				multiplier = -1;
			}
		}
		else{
			x = rand.nextFloat() * 320;
			y = 490;
			if(x > 160){
				multiplier = -1;
			}
		}
		double angle = Math.atan((240-y)/(160-x));
		double xVelocity = multiplier*Math.cos(angle)*10;
		double yVelocity = multiplier*Math.sin(angle)*10;
		generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);

		if(enemyType == 1){
			//TurretBlock special actions
		}
		else if(enemyType == 2){
			//ArmorBlock special actions
		}
	}
	

	
	public void generateBlock(int enemyType, int health, float x, float y, float xVelocity, float yVelocity){
		Block newEnemyBlock = null;
		if(enemyType == 1){
			newEnemyBlock = new ArmorBlock(x, y, health);
		}
		else if(enemyType == 2){
			newEnemyBlock = new EnemyTurretBlock(x, y, health);
		}
		newEnemyBlock.velocity.add(xVelocity, yVelocity);
		enemyBlocks.add(newEnemyBlock);
	}
	
	public boolean checkDead(){
		if(enemyBlocks.size() == 0){
			return true;
		}
		
		return false;
	}
	
	public void update(float deltaTime){
		for(int i = 0; i < enemyBlocks.size(); i++){
			Block currBlock = enemyBlocks.get(i);
			currBlock.position.add(currBlock.velocity.x * deltaTime, currBlock.velocity.y * deltaTime);
			currBlock.bounds.lowerLeft.set(currBlock.position).sub(currBlock.BLOCK_WIDTH/2, currBlock.BLOCK_HEIGHT/2);
			currBlock.update(deltaTime);
			if(currBlock.checkDeath()){
				enemyBlocks.remove(i);
				Assets.playSound(Assets.explosionSound);
			}
			else if(currBlock.position.x > 600 || currBlock.position.y > 650 || currBlock.position.x < -180 || currBlock.position.y < -180){
				enemyBlocks.remove(i);
			}
		}
	}
	
	
}

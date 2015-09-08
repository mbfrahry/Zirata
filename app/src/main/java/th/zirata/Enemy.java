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
		if(enemyType == 1){
			generateArmorBlock();
		}
		if(enemyType == 2){
			generateTurretBlock();
		}
	}
	
	public void generateTurretBlock(){
		float num = rand.nextFloat();
		
		int health = Settings.enemyHealth;
		
		if(num <= 0.25f){
			float x = -15;
			float y = rand.nextFloat() * 480;
			
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity = Math.cos(angle)*10;
			double yVelocity = Math.sin(angle)*10;
			
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
		if(num > 0.25f && num <= 0.5f ){
			float x = 330;
			float y = rand.nextFloat() * 480;
			
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity = -Math.cos(angle)*10;
			double yVelocity = -Math.sin(angle)*10;
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
		
		if(num > 0.5f && num <= 0.75f){
			float x = rand.nextFloat() * 320;
			float y = -10;
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity;
			double yVelocity;
			if(x <= 160){
				yVelocity = Math.sin(angle)*10;
				xVelocity = Math.cos(angle)*10;
			}
			else{
				yVelocity = -Math.sin(angle)*10;
				xVelocity = -Math.cos(angle)*10;
			}
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
		else{
			float x = rand.nextFloat() * 320;
			float y = 490;
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity;
			double yVelocity;
			if(x <= 160){
				yVelocity = Math.sin(angle)*10;
				xVelocity = Math.cos(angle)*10;
			}
			else{
				yVelocity = -Math.sin(angle)*10;
				xVelocity = -Math.cos(angle)*10;
			}
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
	}
	
	public void generateArmorBlock(){
		float num = rand.nextFloat();
		
		int health = Settings.enemyHealth;
		
		if(num <= 0.25f){
			float x = -15;
			float y = rand.nextFloat() * 480;
			
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity = Math.cos(angle)*10;
			double yVelocity = Math.sin(angle)*10;
			
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
		if(num > 0.25f && num <= 0.5f ){
			float x = 330;
			float y = rand.nextFloat() * 480;
			
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity = -Math.cos(angle)*10;
			double yVelocity = -Math.sin(angle)*10;
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
		
		if(num > 0.5f && num <= 0.75f){
			float x = rand.nextFloat() * 320;
			float y = -10;
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity;
			double yVelocity;
			if(x <= 160){
				yVelocity = Math.sin(angle)*10;
				xVelocity = Math.cos(angle)*10;
			}
			else{
				yVelocity = -Math.sin(angle)*10;
				xVelocity = -Math.cos(angle)*10;
			}
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
						
		}
		else{
			float x = rand.nextFloat() * 320;
			float y = 490;
			double angle = Math.atan((240-y)/(160-x));
			double xVelocity;
			double yVelocity;
			if(x <= 160){
				yVelocity = Math.sin(angle)*10;
				xVelocity = Math.cos(angle)*10;
			}
			else{
				yVelocity = -Math.sin(angle)*10;
				xVelocity = -Math.cos(angle)*10;
			}
			generateBlock(enemyType, health, x, y, (float)xVelocity, (float)yVelocity);
		}
	}
	
	public void generateBlock(int enemyType, int health, float x, float y, float xVelocity, float yVelocity){
		if(enemyType == 1){
			ArmorBlock armorBlock;
			armorBlock = new ArmorBlock(x, y, health);
			armorBlock.velocity.add(xVelocity, yVelocity);
			enemyBlocks.add(armorBlock);
		}
		if(enemyType == 2){
			EnemyTurretBlock turretBlock;
			turretBlock = new EnemyTurretBlock(x, y, health);
			turretBlock.velocity.add(xVelocity, yVelocity);
			enemyBlocks.add(turretBlock);
		}
		
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
			if(currBlock.position.x > 450 || currBlock.position.y > 500 || currBlock.position.x < -30 || currBlock.position.y < -30){
				enemyBlocks.remove(i);
			}
		}
	}
	
	
}

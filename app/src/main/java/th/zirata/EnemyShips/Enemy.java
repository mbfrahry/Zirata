package th.zirata.EnemyShips;

import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

import th.zirata.Blocks.EnemyTurretBlock;
import th.zirata.Game.Player;
import th.zirata.Settings.Assets;
import th.zirata.Blocks.ArmorBlock;
import th.zirata.Blocks.Block;
import th.zirata.Game.World;

public class Enemy{

	public ArrayList<Block> enemyBlocks;
	Random rand;
	int enemyLevel;
    ArrayList<Integer> blockTypes;
    public boolean constantVelocity;
	public Vector2 position;

    public Enemy(int  enemyLevel){
		this.enemyBlocks = new ArrayList<Block>();
		this.rand = new Random();
		this.enemyLevel = enemyLevel;
		this.blockTypes = new ArrayList<Integer>();
		this.constantVelocity = false;
    }

	public float[] generateBlockAttributes(){
		float num = rand.nextFloat();

		float x;
		float y;
		float multiplier = 1;
		if(num <= 0.25f){
			x = -50;
			y = rand.nextFloat() * 480;
		}
		else if(num > 0.25f && num <= 0.5f ){
			x = 510;
			y = rand.nextFloat() * 480;
			multiplier = -1;
		}
		else if(num > 0.5f && num <= 0.75f){
			x = rand.nextFloat() * 320;
			y = -50;
			if(x > 160){
				multiplier = -1;
			}
		}
		else{
			x = rand.nextFloat() * 320;
			y = 510;
			if(x > 160){
				multiplier = -1;
			}
		}
		return generateSpecificAttributes(x, y, multiplier);
	}

	public float[] generateBlockAttributes(float x, float y){
		float multiplier = 1;
		if (x > 160){
			multiplier = -1;
		}
		return generateSpecificAttributes(x, y, multiplier);
	}

	public float[] generateSpecificAttributes(float x, float y, float multiplier){

		double angle = Math.atan((240-y)/(160-x));
		double xVelocity = multiplier*Math.cos(angle)*10;
		double yVelocity = multiplier*Math.sin(angle)*10;

		float[] attributes = {x, y, (float) xVelocity, (float)yVelocity};
		return attributes;

	}


	public void generateBlock(int enemyType, int health, float x, float y, float xVelocity, float yVelocity){
		Block newEnemyBlock = null;

		if (enemyType == 1) {
			newEnemyBlock = new ArmorBlock(x, y, health * enemyLevel, enemyLevel);
		} else if (enemyType == 2) {
			newEnemyBlock = new EnemyTurretBlock(x, y, health * enemyLevel / 2, 3 * enemyLevel);
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
	
	public void update(float deltaTime, World world){
		for(int i = 0; i < enemyBlocks.size(); i++){
			Block currBlock = enemyBlocks.get(i);
			currBlock.position.add(currBlock.velocity.x* deltaTime, currBlock.velocity.y* deltaTime);

			for (Vector2 v : currBlock.bounds.vertices){
				v.add(currBlock.velocity.x* deltaTime, currBlock.velocity.y* deltaTime);
			}
			//currBlock.bounds.rotationAngle.set(world.world_cos, world.world_sin);
            currBlock.update(deltaTime);
			if(currBlock.checkDeath()){
				enemyBlocks.remove(i);
				Assets.playSound(Assets.explosionSound);
			}
			else if(currBlock.position.x > 600 || currBlock.position.y > 650 || currBlock.position.x < -180 || currBlock.position.y < -180 && !constantVelocity){
				enemyBlocks.remove(i);
			}
		}
	}
	
	
}

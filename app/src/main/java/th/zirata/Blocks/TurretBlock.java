package th.zirata.Blocks;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;
import java.util.ArrayList;

import th.zirata.Settings.Assets;
import th.zirata.EnemyShips.Enemy;
import th.zirata.Game.World;

public class TurretBlock extends Block {



	public ArrayList<Bullet> bullets;
	int maxBullets;
	int numBullets;
	float fireRate;
	float currTime;

	public float fireAngle;
	float fireArcAngle;
	public int fireRange;

	float coneX1;
	float coneY1;
	float coneX2;
	float coneY2;

	public static final int TURRET_READY = 0;
	public static final int TURRET_RELOADING = 1;

    public static final int TURRET_TURNING_ON = 0;
    public static final int TURRET_TURNING_OFF = 1;
    public static final int TURRET_ON = 2;
    public static final int TURRET_OFF = 3;
    public int powerState;
    public float powerTime;
    public float currPowerTime;
    public int powerImage;
	
	int bulletDamage;
	public int state;

	public static String[] UpgradeAttributes = new String[]{"Health", "Damage", "Fire Rate", "Range"};
	public float[] defaultValueArray = {10, 10, 3, 150};
	public float[] upgradeValueArray = {5, 5, -0.1f, 5};
	public float[] maxValueLevelArray = {9999, 9999, 20, 9999};

	public TurretBlock(Vector2 position, float fireAngle){
        this(position.x, position.y, 10, 3, 1, fireAngle);
	}

	public TurretBlock(double[] info){
		this((float) info[0], (float) info[1], (int) info[2], (int) info[3], (int) info[4], 0);

		if(info.length >= 6){
			this.fireAngle = (int)info[5];
		}
		if(info.length >= 7){
			this.bulletDamage = (int)info[6];
		}
		if(info.length >= 8) {
			this.fireRate = (float)info[7];
		}
		if(info.length >= 9){
			this.fireRange = (int)info[8];
		}

		calcCone(position.x, position.y);

	}

	public TurretBlock(float x, float y, int health, int energyCost, int blockLevel, float fireAngle){
		super(x, y, health, energyCost, blockLevel);
		this.constructorArgLength = 9;
		bullets = new ArrayList<Bullet>();
		maxBullets = 1;
		numBullets = 0;
		bulletDamage = 10;
		
		fireRate = 3;
		currTime = 0;

		this.fireAngle = fireAngle;
		fireArcAngle = 30;
		fireRange = 150;

		calcCone(x, y);

		state = TURRET_READY;

        powerTime = 0.025f;
        currPowerTime = 0;
        powerImage = 311;
        powerState = TURRET_OFF;

        updateImageNums();
	}

	private void calcCone(float x, float y){
        //Finding the center of the correct edge of the block

		int x1add = 12;
		int x2add = 12;
		int y1add = 12;
		int y2add = 12;

		if(fireAngle == 0){
			x1add *= -1;
			y1add *= -1;
			x2add *= -1;
		}
		else if(fireAngle == 90){
			y1add *= -1;
			x2add *= -1;
			y2add *= -1;
		}
		else if(fireAngle == 180){
			y2add *= -1;
		}
		else{
			x1add *= -1;
		}
		coneX1 = (float)(x + x1add + (Math.cos(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
		coneY1 = (float)(y + y1add + (Math.sin((fireAngle + fireArcAngle) * Math.PI / 180)) * fireRange/2);
		coneX2 = (float)(x + x2add + (Math.cos((fireAngle - fireArcAngle) * Math.PI / 180)) * fireRange/2);
		coneY2 = (float)(y + y2add + (Math.sin((fireAngle - fireArcAngle) * Math.PI / 180)) * fireRange/2);
	}

	@Override
	public void action(World world) {
		int[] closestEnemy = enemyInRange(world.enemyManager.enemies);
		if(closestEnemy[0] >= 0){
			Block enemyBlock = world.enemyManager.enemies.get(closestEnemy[0]).enemyBlocks.get(closestEnemy[1]);
			if(state == TURRET_READY ){
				lastTouch.set(enemyBlock.position.x + 12, enemyBlock.position.y + 12);
				World.playerBullets.add(new Bullet(position.x, position.y, enemyBlock.position.x + 12 , enemyBlock.position.y + 12, bulletDamage, fireRange ));
				Assets.playSound(Assets.shootSound);
				numBullets++;
				if(numBullets >= maxBullets){
					state = TURRET_RELOADING;
				}
			}
		}
	}

	private int[] enemyInRange(ArrayList<Enemy> enemies){
		Enemy enemy;
		for(int i = 0; i < enemies.size(); i++){
			enemy = enemies.get(i);
			for(int j = 0; j < enemy.enemyBlocks.size(); j++) {
				if (position.dist(enemy.enemyBlocks.get(j).position) < fireRange - 35) {

					double angleBetween = position.angleBetween(enemy.enemyBlocks.get(j).position);
					float posAngle = fireAngle + fireArcAngle;
					float negAngle = fireAngle - fireArcAngle;
					if (negAngle < 0) {
						negAngle += fireArcAngle;
						posAngle += fireArcAngle;
						angleBetween = (angleBetween + fireArcAngle) % 360;
					}
					if (angleBetween <= posAngle) {
						if (angleBetween >= negAngle) {
							int[] enemyPosition = new int[2];
							enemyPosition[0] = i;
							enemyPosition[1] = j;
							return new int[] {i,j};
						}
					}
				}
			}
		}
		return new int[] {-1,-1};
	}

	public void update(float deltaTime){
		if(state == TURRET_RELOADING){
			currTime+= deltaTime;
			if(currTime >= fireRate){
				state = TURRET_READY;
				currTime = 0;
				numBullets = 0;
			}
		}
        if(powerState == TURRET_OFF && active){
            powerState = TURRET_TURNING_ON;
        }
        else if(powerState == TURRET_ON && !active){
            powerState = TURRET_TURNING_OFF;
        }
        else if(powerState == TURRET_TURNING_ON){
            currPowerTime+=deltaTime;
            if(currPowerTime >= powerTime){
                currPowerTime = 0;
                powerImage -= 1;
                if(powerImage == 300){
                    powerState = TURRET_ON;
                }
            }
        }
        else if(powerState == TURRET_TURNING_OFF){
            currPowerTime+=deltaTime;
            if(currPowerTime >= powerTime) {
                currPowerTime = 0;
                powerImage += 1;
                if (powerImage == 311) {
                    powerState = TURRET_OFF;
                }
            }
        }
	}

	public void writeExtraInfo(JsonWriter writer) throws IOException {
		writer.value(fireAngle);
		writer.value(bulletDamage);
		writer.value(fireRate);
		writer.value(fireRange);
	}

	public String[] getUpgradableAttributes() {
		return UpgradeAttributes;
	}
	public float[] getAttributeVals() {
		return new float[]{this.health, this.bulletDamage, this.fireRate, this.fireRange};
	}

	public float[] getUpgradeValues() {
		return upgradeValueArray;
	}

	public void updateAttribute(int attributeIndex, float upgradeNum) {
		if(attributeIndex == 0){
			this.maxHealth += upgradeNum;
			this.health += upgradeNum;
		}
		if(attributeIndex == 1 ){
			this.bulletDamage += upgradeNum;
		}
		if(attributeIndex == 2 ){
			this.fireRate += upgradeNum;
		}
		if(attributeIndex == 3 ){
			this.fireRange += upgradeNum;
			calcCone(position.x, position.y);
		}
	}

	public int getAttributeLevel(int attributeIndex) {
		float defaultVal = defaultValueArray[attributeIndex];
		float currVal = 0;
		if(attributeIndex == 0){
			currVal = this.health;
		}
		if(attributeIndex == 1){
			currVal = this.bulletDamage;
		}
		if(attributeIndex == 2){
			currVal = this.fireRate;
		}
		if(attributeIndex == 3){
			currVal = this.fireRange;
		}
		float delta = currVal - defaultVal;
		return (int) (Math.abs((delta/upgradeValueArray[attributeIndex]))+.1f);
	}

	public void setBeginningLastTouch(){
		int xDiff = 0;
		int yDiff = 0;
		if(fireAngle == 0){
			xDiff += 12;
		}
		else if(fireAngle == 90){
			yDiff += 12;
		}
		else if(fireAngle == 180){
			xDiff -= 12;
		}
		else{
			yDiff -= 12;
		}
		lastTouch.set(position.x + xDiff, position.y + yDiff);
	}


	public boolean checkMaxAttributeLevel(int attributeIndex){
		int currLevel = getAttributeLevel(attributeIndex);
		if(currLevel < maxValueLevelArray[attributeIndex]){
			return false;
		}
		return true;
	}

	public void multiply(float multiplier){
		this.bulletDamage *= multiplier;
	}

//	@Override
//	public void fuseWith(Block b) {
//		TurretBlock tBlock = (TurretBlock) b;
//		health += tBlock.getAttributeLevel(0)*upgradeValueArray[0];
//		maxHealth = health;
//		bulletDamage += tBlock.getAttributeLevel(1)*upgradeValueArray[1];
//		fireRate += tBlock.getAttributeLevel(2)*upgradeValueArray[2];
//		fireRange += tBlock.getAttributeLevel(3)*upgradeValueArray[3];
//
//		blockLevel ++;
//	}

	@Override
	public void fuseWith(Block b) {
		for(int i = 0; i < 4; i++){
			fuseLevels(i, b.getAttributeLevel(i));
		}
		blockLevel ++;
		energyCost *= 2;
	}

	public void fuseLevels(int attIndex, int levelsAdded){
		while (!checkMaxAttributeLevel(attIndex) && levelsAdded > 0){
			if(attIndex == 0){
				health += upgradeValueArray[attIndex];
			}
			else if(attIndex == 1){
				bulletDamage += upgradeValueArray[attIndex];
			}
			else if(attIndex == 2){
				fireRate += upgradeValueArray[attIndex];
			}
			else if(attIndex == 3){
				fireRange += upgradeValueArray[attIndex];
			}
			levelsAdded --;
		}
		health += levelsAdded*upgradeValueArray[0];
		maxHealth = health;
	}
}

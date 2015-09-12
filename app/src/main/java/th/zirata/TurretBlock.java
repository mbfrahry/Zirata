package th.zirata;

import android.util.JsonWriter;
import android.util.Log;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;
import java.util.ArrayList;

public class TurretBlock extends Block{

	public static String[] UpgradeAttributes = new String[]{"Health", "Cooldown", "Active Time", "Multiplier"};

	ArrayList<Bullet> bullets;
	int maxBullets;
	int numBullets;
	float reloadTime;
	float currTime;

	float fireAngle;
	float fireArcAngle;
	int fireRange;

	float coneX1;
	float coneY1;
	float coneX2;
	float coneY2;

	public static final int TURRET_READY = 0;
	public static final int TURRET_RELOADING = 1;
	
	int bulletDamage;
	int state;

	public double[] defaultValueArray = {0};

	public TurretBlock(Vector2 position, float fireAngle){
        this(position.x, position.y, 10, 3, fireAngle);
	}

	public TurretBlock(double[] info){
		this((float) info[0], (float) info[1], (int) info[2], (int) info[3], 0);

		if(info.length >= 5){
			this.fireAngle = (int)info[4];
		}

		calcCone(position.x, position.y);

	}

	public TurretBlock(float x, float y, int health, int energyCost, float fireAngle){
		super(x, y, health, energyCost);
		this.constructorArgLength = 5;
		bullets = new ArrayList<Bullet>();
		maxBullets = 1;
		numBullets = 0;
		bulletDamage = 10;
		
		reloadTime = 3;
		currTime = 0;

		this.fireAngle = fireAngle;
		fireArcAngle = 30;
		fireRange = 150;

		calcCone(x, y);

		state = TURRET_READY;


	}

	private void calcCone(float x, float y){
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
		coneY1 = (float)(y + y1add + (Math.sin(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
		coneX2 = (float)(x + x2add + (Math.cos(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
		coneY2 = (float)(y + y2add + (Math.sin(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
	}

	@Override
	public void action(World world) {
		int closestEnemy = enemyInRange(world.enemies);
		if(closestEnemy >= 0){
			Block enemyBlock = world.enemies.get(closestEnemy).enemyBlocks.get(0);
			if(state == TURRET_READY ){
				lastTouch.set(enemyBlock.position.x + 12, enemyBlock.position.y + 12);
				World.PLAYER_BULLETS.add(new Bullet(position.x, position.y, enemyBlock.position.x + 12 , enemyBlock.position.y + 12, bulletDamage ));
				Assets.playSound(Assets.shootSound);
				numBullets++;
				if(numBullets >= maxBullets){
					state = TURRET_RELOADING;
				}
			}
		}

	}

	private int enemyInRange(ArrayList<Enemy> enemies){
		Enemy enemy;
		for(int i = 0; i < enemies.size(); i++){
			enemy = enemies.get(i);
			if(position.dist(enemy.enemyBlocks.get(0).position) < fireRange - 35){

				double angleBetween = position.angleBetween(enemy.enemyBlocks.get(0).position);
				float posAngle = fireAngle + fireArcAngle;
				float negAngle = fireAngle - fireArcAngle;
				if (negAngle < 0){
					negAngle += fireArcAngle;
					posAngle += fireArcAngle;
					angleBetween = (angleBetween + fireArcAngle)%360;
				}
				if(angleBetween <= posAngle){
					if(angleBetween >= negAngle){
						return i;
					}
				}
			}
		}
		return -1;
	}

	public void update(float deltaTime){
		if(state == TURRET_RELOADING){
			currTime+= deltaTime;
			if(currTime >= reloadTime){
				state = TURRET_READY;
				currTime = 0;
				numBullets = 0;
			}
		}
		for(int i = 0; i < World.PLAYER_BULLETS.size(); i++){
			World.PLAYER_BULLETS.get(i).update(deltaTime);
			if(World.PLAYER_BULLETS.get(i).outOfBounds()){
				World.PLAYER_BULLETS.remove(i);
			}
		}

	}

	public void writeExtraInfo(JsonWriter writer) throws IOException {
		writer.value(fireAngle);
	}

	@Override
	public String[] getUpgradableAttributes() {
		return UpgradeAttributes;
	}

	public void multiply(float multiplier){
		this.bulletDamage *= multiplier;
	}
}

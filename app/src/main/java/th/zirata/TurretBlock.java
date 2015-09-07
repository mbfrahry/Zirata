package th.zirata;

import android.util.Log;

import java.util.ArrayList;

public class TurretBlock extends Block{

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
	
	public TurretBlock(float x, float y, int health, int energyCost, float fireAngle){
		super(x, y, health, energyCost);
		bullets = new ArrayList<Bullet>();
		maxBullets = 1;
		numBullets = 0;
		bulletDamage = 10;
		
		reloadTime = 3;
		currTime = 0;

		this.fireAngle = fireAngle;
		fireArcAngle = 30;
		fireRange = 150;

		if(fireAngle == 0){
			coneX1 = (float)(x - 12 + (Math.cos(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY1 = (float)(y - 12 + (Math.sin(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneX2 = (float)(x - 12 + (Math.cos(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY2 = (float)(y + 12 + (Math.sin(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
		}
		else if(fireAngle == 90){
			coneX1 = (float)(x + 12 + (Math.cos(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY1 = (float)(y - 12 + (Math.sin(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneX2 = (float)(x - 12 + (Math.cos(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY2 = (float)(y - 12 + (Math.sin(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
		}
		else if(fireAngle == 180){
			coneX1 = (float)(x + 12 + (Math.cos(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY1 = (float)(y + 12 + (Math.sin(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneX2 = (float)(x + 12 + (Math.cos(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY2 = (float)(y - 12 + (Math.sin(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
		}
		else{
			coneX1 = (float)(x - 12 + (Math.cos(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY1 = (float)(y + 12 + (Math.sin(( fireAngle + fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneX2 = (float)(x + 12 + (Math.cos(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
			coneY2 = (float)(y + 12 + (Math.sin(( fireAngle - fireArcAngle)*Math.PI/180)) * fireRange/2);
		}

		state = TURRET_READY;


	}


	
	public void action(Block enemyBlock){
		if(state == TURRET_READY ){
			lastTouch.set(enemyBlock.position.x + 12 , enemyBlock.position.y + 12 );
			World.PLAYER_BULLETS.add(new Bullet(position.x, position.y, enemyBlock.position.x + 12 , enemyBlock.position.y + 12, bulletDamage ));
			numBullets++;
			if(numBullets >= maxBullets){
				state = TURRET_RELOADING;
			}
		}
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
}

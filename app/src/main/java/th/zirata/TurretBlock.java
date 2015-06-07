package th.zirata;

import android.util.Log;

import java.util.ArrayList;

public class TurretBlock extends Block{

	ArrayList<Bullet> bullets;
	int maxBullets;
	int numBullets;
	float reloadTime;
	float currTime;
	
	public static final int TURRET_READY = 0;
	public static final int TURRET_RELOADING = 1;
	
	
	int state; 
	
	public TurretBlock(float x, float y, int health, int energyCost){
		super(x, y, health, energyCost);
		bullets = new ArrayList<Bullet>();
		maxBullets = 3;
		numBullets = 0;
		
		reloadTime = 3;
		currTime = 0;
		
		state = TURRET_READY;
	}
	
	public void action(){
		if(state == TURRET_READY){
			World.PLAYER_BULLETS.add(new Bullet(position.x, position.y, lastTouch.x, lastTouch.y));
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

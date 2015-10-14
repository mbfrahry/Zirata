package th.zirata.Blocks;

import java.util.Random;

import android.util.Log;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Bullet extends DynamicGameObject{

	public static final float BULLET_WIDTH = 5;
	public static final float BULLET_HEIGHT = 5;
	Random rand;
	public int damage;
	float range;
	
	public Bullet(float x, float y, float targX, float targY, int damage, float range) {
		super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
		rand = new Random();
		setVelocity(targX, targY);
		this.damage = damage;
		this.range = range;
	}

	public Bullet(float x, float y, float width, float height, float targX, float targY, int damage, float range) {
		super(x, y, width, height);
		rand = new Random();
		setVelocity(targX, targY);
		this.damage = damage;
		this.range = range;
	}
	
	public void setVelocity(float targX, float targY){
		double xDiff = position.x - targX + 12;
		double yDiff = position.y - targY + 12;
		double angle = Math.atan(xDiff / yDiff);
		double xVelocity = 0;
		double yVelocity = 0;
		double multiplier = 1;
		if((xDiff >= 0 && yDiff > 0) || (xDiff <= 0 && yDiff > 0)){
			multiplier = -1;
		}
		yVelocity = multiplier*Math.cos(angle);
		xVelocity = multiplier*Math.sin(angle);
		velocity.add((float)xVelocity * 50, (float)yVelocity * 50);
	}
	
	public boolean outOfBounds(){
		boolean toReturn = false;
		if(position.dist(origin) > range - range/5){
			toReturn = true;
		}
		return toReturn;
	}
	
	public void update(float deltaTime){
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		bounds.lowerLeft.add(velocity.x * deltaTime, velocity.y * deltaTime);
		bounds.setVertices();
	}

}

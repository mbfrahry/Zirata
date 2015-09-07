package th.zirata;

import java.util.Random;

import android.util.Log;

import com.badlogic.androidgames.framework.DynamicGameObject;

public class Bullet extends DynamicGameObject{

	public static final float BULLET_WIDTH = 5;
	public static final float BULLET_HEIGHT = 5;
	Random rand;
	int damage;
	
	public Bullet(float x, float y, float targX, float targY, int damage) {
		super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
		rand = new Random();
		setVelocity(targX, targY);
		this.damage = damage;
	}
	
	public void setVelocity(float targX, float targY){
		double xDiff = position.x - targX + 12;
		double yDiff = position.y - targY + 12;
		double angle = Math.atan(xDiff / yDiff);
		double xVelocity = 0;
		double yVelocity = 0;
		if(xDiff >= 0 && yDiff > 0){
			yVelocity = -Math.cos(angle);
			xVelocity = -Math.sin(angle);
		}
		if(xDiff >= 0 && yDiff < 0){
			yVelocity = Math.cos(angle);
			xVelocity = Math.sin(angle);
		}
		if(xDiff <= 0 && yDiff > 0){
			yVelocity = -Math.cos(angle);
			xVelocity = -Math.sin(angle);
		}
		if(xDiff < 0 && yDiff <= 0){
			yVelocity = Math.cos(angle);
			xVelocity = Math.sin(angle);
		}
		velocity.add((float)xVelocity * 50, (float)yVelocity * 50);
	}
	
	public boolean outOfBounds(){
		if(position.x > 320 || position.x < 0 || position.y < 0 || position.y > 480){
			return true;
		}
		return false;
	}
	
	public void update(float deltaTime){
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
		bounds.lowerLeft.set(position).sub(BULLET_WIDTH/2, BULLET_HEIGHT/2);
	}

}

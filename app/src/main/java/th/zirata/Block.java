package th.zirata;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

public class Block extends DynamicGameObject {

	public static final float BLOCK_WIDTH = 24;
	public static final float BLOCK_HEIGHT = 24;
	int health;
	int maxHealth;
	Vector2 lastTouch;
	
	public Block(float x, float y, int health){
		super(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);
		this.health = health;
		this.maxHealth = health;
		lastTouch = new Vector2(x, y + BLOCK_HEIGHT/2);
	}
	
	public void action() {
	}
	
	public void update(float deltaTime){
		
	}
	
	public boolean checkDeath(){
		if(health <= 0){
			return true;
		}
		return false;
	}
}

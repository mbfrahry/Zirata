package th.zirata;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Matthew on 9/27/2015.
 */
public class Background extends DynamicGameObject {

    String type;
    public Background(float x, float y, float width, float height, Vector2 velocity, String type) {
        super(x, y, width, height);
        this.velocity.set(velocity);
        this.type = type;
    }

    public void update(float deltaTime){
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }
}

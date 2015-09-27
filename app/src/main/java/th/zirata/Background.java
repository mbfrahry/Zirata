package th.zirata;

import com.badlogic.androidgames.framework.DynamicGameObject;

/**
 * Created by Matthew on 9/27/2015.
 */
public class Background extends DynamicGameObject {

    public Background(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity.set(0, -4);
    }

    public void update(float deltaTime){
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }
}

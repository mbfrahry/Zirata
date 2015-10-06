package th.zirata.Game;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Matthew on 9/27/2015.
 */
public class Background extends DynamicGameObject {

    String type;
    boolean isRelevant;

    public Background(float x, float y, float width, float height, Vector2 velocity, String type) {
        super(x, y, width, height);
        this.velocity.set(velocity);
        this.type = type;
        this.isRelevant = true;
    }

    public Background(float x, float y, float width, float height, Vector2 velocity, String type, Vector2 angle) {
        super(x, y, width, height, angle);
        this.velocity.set(velocity);
        this.type = type;
        this.isRelevant = true;
    }

    public void update(float deltaTime){
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        //bounds.lowerLeft.add(velocity.x * deltaTime, velocity.y * deltaTime);
        for (Vector2 v : bounds.vertices){
            v.add(velocity.x * deltaTime, velocity.y * deltaTime);
        }
    }
}

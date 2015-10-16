package th.zirata.Game;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Matthew on 9/27/2015.
 */
public class Background extends DynamicGameObject {

    String type;
    boolean isRelevant;
    float speedMultiplier;

    public Background(float x, float y, float width, float height, Vector2 velocity, float speedMultiplier, String type) {
        super(x, y, width, height);
        this.velocity.set(velocity);
        this.type = type;
        this.isRelevant = true;
        this.speedMultiplier = speedMultiplier;
    }

    public Background(float x, float y, float width, float height, Vector2 velocity, float speedMultiplier, String type, Vector2 angle) {
        super(x, y, width, height, angle);
        this.velocity.set(velocity);
        this.type = type;
        this.isRelevant = true;
        this.speedMultiplier = speedMultiplier;
    }

    public void update(float deltaTime){
        position.add(speedMultiplier*World.playerSpeed.x*deltaTime, speedMultiplier*World.playerSpeed.y * deltaTime);
        //bounds.lowerLeft.add(velocity.x * deltaTime, velocity.y * deltaTime);
        for (Vector2 v : bounds.vertices){
            v.add(speedMultiplier*World.playerSpeed.x*deltaTime, speedMultiplier*World.playerSpeed.y * deltaTime);
        }
    }
}

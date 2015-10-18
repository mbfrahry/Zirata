package th.zirata.Game;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

/**
 * Created by Max Bauer on 10/17/2015.
 */
public class Gate extends DynamicGameObject{



    public Gate(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity.set(Player.playerSpeed);
    }

    public void update(float deltaTime){
        position.add(velocity.x* deltaTime, velocity.y* deltaTime);

        for (Vector2 v : bounds.vertices){
            v.add(velocity.x* deltaTime, velocity.y* deltaTime);
        }
    }

}

package th.zirata.Game;

import com.badlogic.androidgames.framework.DynamicGameObject;
import com.badlogic.androidgames.framework.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

import th.zirata.Blocks.BlankBlock;
import th.zirata.Blocks.Block;

/**
 * Created by Matthew on 9/27/2015.
 */
public class Planet extends DynamicGameObject {

    String type;
    float speedMultiplier;
    float scale;
    Random rand;
    public static String[] planetTypes = new String[]{
        "BlueJaggedPlanet",
        "DarkShadeyPlanet",
        "DarkWatersPlanet",
        "Earthish",
        "GreenStripePlanet",
        "Nebula1",
        "PurpleSpeckPlanet",
        "ShadeyPlanet",
        "YellowStripedPlanet"
    };

    public Planet(float x, float y, float width, float height, Vector2 velocity, float speedMultiplier, float scale) {
        super(x, y, width, height);
        rand = new Random();
        this.velocity.set(velocity);
        this.speedMultiplier = speedMultiplier;
        this.scale = scale;
        this.type = planetTypes[rand.nextInt(planetTypes.length)];
    }



    public void update(float deltaTime){
        position.add(speedMultiplier*Player.playerSpeed.x*deltaTime, speedMultiplier*Player.playerSpeed.y * deltaTime);
        //bounds.lowerLeft.add(velocity.x * deltaTime, velocity.y * deltaTime);
        for (Vector2 v : bounds.vertices){
            v.add(speedMultiplier*Player.playerSpeed.x*deltaTime, speedMultiplier*Player.playerSpeed.y * deltaTime);
        }
    }
}

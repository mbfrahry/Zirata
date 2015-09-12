package th.zirata;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;

/**
 * Created by Matthew on 6/6/2015.
 */
public class EnergyBlock extends Block{

    public static String[] UpgradeAttributes = new String[]{"Health", "Energy"};

    public int energy;
    public double[] defaultValueArray = {10};

    public EnergyBlock(Vector2 position){
        this(position.x, position.y, 10, 0, 10);
    }

    public EnergyBlock(double[] info){
        this((float)info[0], (float)info[1], (int)info[2], (int)info[3], 10);

        if(info.length >= 5){
            this.energy = (int)info[4];
        }

    }



    public EnergyBlock(float x, float y, int health, int energyCost, int energy){
        super(x, y, health, energyCost);

        //Change this value when constructor arguments changes;
        this.constructorArgLength = 5;

        this.energy = energy;
    }

    @Override
    public void action(World world) {

    }

    @Override
    public void update(float deltaTime) {

    }

    public void writeExtraInfo(JsonWriter writer) throws IOException {
        writer.value(energy);
    }

    @Override
    public String[] getUpgradableAttributes() {
        return UpgradeAttributes;
    }

    public void multiply(float multiplier){
        this.energy *= multiplier;
    }

}

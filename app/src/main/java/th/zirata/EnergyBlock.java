package th.zirata;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;

/**
 * Created by Matthew on 6/6/2015.
 */
public class EnergyBlock extends Block{

    public static String[] UpgradeAttributes = new String[]{"Health", "Energy"};
    public float[] defaultValueArray = {10, 10};
    public float[] upgradeValueArray = {5, 1};
    public float[] maxValueLevelArray = {9999, 9999};

    public int energy;


    public EnergyBlock(Vector2 position){
        this(position.x, position.y, 10, 0, 1, 10);
    }

    public EnergyBlock(double[] info){
        this((float)info[0], (float)info[1], (int)info[2], (int)info[3], (int)info[4], 10);

        if(info.length >= 5){
            this.energy = (int)info[5];
        }

    }



    public EnergyBlock(float x, float y, int health, int energyCost, int blockLevel, int energy){
        super(x, y, health, energyCost, blockLevel);

        //Change this value when constructor arguments changes;
        this.constructorArgLength = 6;

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

    @Override
    public float[] getAttributeVals() {
        return new float[]{this.health, this.energy};
    }

    @Override
    public float[] getUpgradeValues() {
        return upgradeValueArray;
    }

    @Override
    public void updateAttribute(int attributeIndex, float upgradeNum) {
        if(attributeIndex == 0){
            this.maxHealth += upgradeNum;
            this.health += upgradeNum;
        }
        else if (attributeIndex == 1){
            this.energy += upgradeNum;
        }
    }

    public int getAttributeLevel(int attributeIndex) {
        float defaultVal = defaultValueArray[attributeIndex];
        float currVal = 0;
        if(attributeIndex == 0){
            currVal = this.health;
        }
        if(attributeIndex == 1){
            currVal = this.energy;
        }
        float delta = currVal - defaultVal;
        return (int) Math.abs((delta/upgradeValueArray[attributeIndex]));
    }

    public boolean checkMaxAttributeLevel(int attributeIndex){
        int currLevel = getAttributeLevel(attributeIndex);
        if(currLevel < maxValueLevelArray[attributeIndex]){
            return false;
        }
        return true;
    }



    public void multiply(float multiplier){
        this.energy *= multiplier;
    }

    @Override
    public void fuseWith(Block b) {
        for(int i = 0; i < 2; i++){
            fuseLevels(i, b.getAttributeLevel(i));
        }
        blockLevel ++;
    }

    public void fuseLevels(int attIndex, int levelsAdded){
        while (!checkMaxAttributeLevel(attIndex) && levelsAdded > 0){
            if(attIndex == 0){
                health += upgradeValueArray[attIndex];
            }
            else if(attIndex == 1){
                energy += upgradeValueArray[attIndex];
            }
            levelsAdded --;
        }
        health += levelsAdded*upgradeValueArray[0];
        maxHealth = health;
    }

}

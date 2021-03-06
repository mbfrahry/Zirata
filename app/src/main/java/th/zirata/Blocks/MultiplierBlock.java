package th.zirata.Blocks;

import android.util.JsonWriter;

import com.badlogic.androidgames.framework.math.Vector2;

import java.io.IOException;
import java.util.ArrayList;

import th.zirata.Game.World;

/**
 * Created by Matthew on 9/8/2015.
 */
public class MultiplierBlock extends Block {

    public static String[] UpgradeAttributes = new String[]{"Health", "Cooldown", "Up Time", "Multiply"};
    public float[] defaultValueArray = {10, 10, 5, 1.5f};
    public float[] upgradeValueArray = {5, -1, 1, .1f};
    public float[] maxValueLevelArray = {9999, 5, 15, 9999};

    float multiplier;
    float cooldown;
    float cooldownTime;
    float multiplierTime;
    float multiplierActiveTime;
    int state;
    ArrayList<Block> multiplyingBlocks;

    public static final int MULTIPLIER_READY = 0;
    public static final int MULTIPLIER_MULTIPLYING = 1;
    public static final int MULTIPLIER_COOLING = 2;

    public MultiplierBlock(Vector2 position){
        this(position.x, position.y, 10, 0, 1, 1.5f, 5, 10);
    }

    public MultiplierBlock(double[] info){
        this((float)info[0], (float)info[1], (int)info[2],(int)info[3], (int)info[4], (float)info[5], (float)info[6], (float)info[7]);
    }

    public MultiplierBlock(float x, float y, int health, int energyCost, int blockLevel, float multiplier,  float multiplierTime, float cooldown){
        super(x, y, health, energyCost, blockLevel);
        this.constructorArgLength = 8;

        this.multiplier = multiplier;
        this.cooldown = cooldown;
        this.multiplierTime = multiplierTime;
        state = MULTIPLIER_READY;
        cooldownTime = 0;
        multiplierActiveTime = 0;
        multiplyingBlocks = new ArrayList<Block>();
        updateImageNums();
    }

    @Override
    public void action(World world) {
        if( state == MULTIPLIER_READY ){
            state = MULTIPLIER_MULTIPLYING;

            multiplyingBlocks = grabAdjacentBlocks(world.player.playerBlocks);
            for(int i = 0; i < multiplyingBlocks.size(); i++){
                multiplyingBlocks.get(i).multiply(multiplier);
            }
        }
        active = false;
    }

    public void update(float deltaTime){
        if(state == MULTIPLIER_MULTIPLYING){
           multiplierActiveTime += deltaTime;
            if(multiplierActiveTime > multiplierTime){
                state = MULTIPLIER_COOLING;
                multiplierActiveTime = 0;
                for(int i = 0; i < multiplyingBlocks.size(); i++){
                    multiplyingBlocks.get(i).multiply(1/multiplier);
                }
            }

        }
        if(state == MULTIPLIER_COOLING){
            cooldownTime += deltaTime;
            if(cooldownTime > cooldown){
                state = MULTIPLIER_READY;
                cooldownTime = 0;

            }
        }

    }

    public void writeExtraInfo(JsonWriter writer) throws IOException {
        writer.value(multiplier);
        writer.value(multiplierTime);
        writer.value(cooldown);
    }

    @Override
    public String[] getUpgradableAttributes() {
        return UpgradeAttributes;
    }

    @Override
    public float[] getAttributeVals() {
        return new float[]{this.health, this.cooldown, this.multiplierTime, this.multiplier};
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
        if(attributeIndex == 1){
            this.cooldown += upgradeNum;
        }
        if(attributeIndex == 2){
            this.multiplierTime += upgradeNum;
        }
        if(attributeIndex == 3){
            this.multiplier += upgradeNum;
        }
    }

    public int getAttributeLevel(int attributeIndex) {
        float defaultVal = defaultValueArray[attributeIndex];
        float currVal = 0;
        if(attributeIndex == 0){
            currVal = this.health;
        }
        if(attributeIndex == 1){
            currVal = this.cooldown;
        }
        if(attributeIndex == 2){
            currVal = this.multiplierTime;
        }
        if(attributeIndex == 3){
            currVal = this.multiplier;
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

    }

    @Override
    public void fuseWith(Block b) {
        for(int i = 0; i < 4; i++){
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
                cooldown += upgradeValueArray[attIndex];
            }
            else if(attIndex == 2){
                multiplierTime += upgradeValueArray[attIndex];
            }
            else if(attIndex == 3){
                multiplier += upgradeValueArray[attIndex];
            }
            levelsAdded --;
        }
        health += levelsAdded*upgradeValueArray[0];
        maxHealth = health;
    }
}

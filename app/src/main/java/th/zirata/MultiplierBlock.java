package th.zirata;

import android.util.JsonWriter;

import java.io.IOException;

/**
 * Created by Matthew on 9/8/2015.
 */
public class MultiplierBlock extends Block{


    public double[] defaultValueArray = {1.5, 5, 15};

    float multiplier;
    float cooldown;
    float cooldownTime;
    float multiplierTime;
    float multiplierActiveTime;
    int state;

    public static final int MULTIPLIER_READY = 0;
    public static final int MULTIPLIER_MULTIPLYING = 1;
    public static final int MULTIPLIER_COOLING = 2;

    public MultiplierBlock(double[] info){
        this((float)info[0], (float)info[1], (int)info[2],(int)info[3], 1.5f, 5, 15);
        if(info.length >= 5){
            this.multiplier = (float)info[4];
        }
        if(info.length >= 6){
            this.multiplierTime = (float)info[5];
        }
        if(info.length >= 7){
            this.cooldown= (float)info[6];
        }

    }

    public MultiplierBlock(float x, float y, int health, int energyCost, float multiplier,  float multiplierTime, float cooldown){
        super(x, y, health, energyCost);
        this.constructorArgLength = 7;

        this.multiplier = multiplier;
        this.cooldown = cooldown;
        this.multiplierTime = multiplierTime;
        state = MULTIPLIER_READY;
        cooldownTime = 0;
        multiplierActiveTime = 0;
    }

    public void action(){
        if( state == MULTIPLIER_READY ){
            state = MULTIPLIER_MULTIPLYING;
        }
        active = false;
    }

    public void update(float deltaTime){
        if(state == MULTIPLIER_MULTIPLYING){
           multiplierActiveTime += deltaTime;
            if(multiplierActiveTime > multiplierTime){
                state = MULTIPLIER_COOLING;
                multiplierActiveTime = 0;
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

}

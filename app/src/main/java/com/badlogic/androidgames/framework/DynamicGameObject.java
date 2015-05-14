package com.badlogic.androidgames.framework;

import android.util.Log;

import com.badlogic.androidgames.framework.math.Vector2;

public class DynamicGameObject extends GameObject {
    public final Vector2 velocity;
    public final Vector2 accel;
    
    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
        accel = new Vector2();
    }

    public void rotate(float sinMath, float cosMath, Vector2 midPoint){
        float x = position.x;
        float y = position.y;
        x -= midPoint.x;
        y -= midPoint.y;
        position.x =  ( x * cosMath + y * -sinMath);
        position.y = (x * sinMath + y * cosMath);
        position.x += midPoint.x;
        position.y += midPoint.y;


        //x = velocity.x;
       // y = velocity.y;
        //Log.d("Velocity ", velocity.x + " " +  velocity.y);
        //velocity.x = ( x * cosMath + y * sinMath);
        //velocity.y = (x * -sinMath + y * cosMath);
    }
}
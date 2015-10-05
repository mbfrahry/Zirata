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

    public DynamicGameObject(float x, float y, float width, float height, Vector2 angle) {
        super(x, y, width, height, angle);
        velocity = new Vector2();
        accel = new Vector2();
    }

    public void rotate(float sinMath, float cosMath, Vector2 midPoint){
        float x = position.x;
        float y = position.y;
        //float leftX = bounds.lowerLeft.x;
        //float leftY = bounds.lowerLeft.y;
        //leftX -= midPoint.x;
        //leftY -= midPoint.y;
        x -= midPoint.x;
        y -= midPoint.y;
        position.x =  ( x * cosMath + y * -sinMath);
        position.y = (x * sinMath + y * cosMath);
        position.x += midPoint.x;
        position.y += midPoint.y;
        //bounds.lowerLeft.x =  ( leftX * cosMath + leftY * -sinMath);
        //bounds.lowerLeft.y = (leftX * sinMath + leftY * cosMath);
        //bounds.lowerLeft.x += midPoint.x;
        //bounds.lowerLeft.y += midPoint.y;


        x = velocity.x;
        y = velocity.y;
        velocity.x = ( x * cosMath - y * sinMath);
        velocity.y = (x * sinMath + y * cosMath);
        float vX;
        float vY;
        for (Vector2 v : bounds.vertices){
            //float leftX = bounds.lowerLeft.x;
            //float leftY = bounds.lowerLeft.y;
            //leftX -= midPoint.x;
            //leftY -= midPoint.y;
            //bounds.lowerLeft.x =  ( leftX * cosMath + leftY * -sinMath);
            //bounds.lowerLeft.y = (leftX * sinMath + leftY * cosMath);
            //bounds.lowerLeft.x += midPoint.x;
            //bounds.lowerLeft.y += midPoint.y;





            vX = v.x;
            vY = v.y;
            vX -= midPoint.x;
            vY -= midPoint.y;
            v.x = ( vX * cosMath - vY * sinMath);
            v.y = (vX * sinMath + vY * cosMath);
            v.x += midPoint.x;
            v.y += midPoint.y;
        }
    }

    public void rotateConstantVelocity(float sinMath, float cosMath, Vector2 midPoint){
        float x = position.x;
        float y = position.y;
        //float leftX = bounds.lowerLeft.x;
        //float leftY = bounds.lowerLeft.y;
        //leftX -= midPoint.x;
        //leftY -= midPoint.y;
        x -= midPoint.x;
        y -= midPoint.y;
        position.x =  ( x * cosMath + y * -sinMath);
        position.y = (x * sinMath + y * cosMath);
        position.x += midPoint.x;
        position.y += midPoint.y;
        //bounds.lowerLeft.x =  ( leftX * cosMath + leftY * -sinMath);
        //bounds.lowerLeft.y = (leftX * sinMath + leftY * cosMath);
        //bounds.lowerLeft.x += midPoint.x;
        //bounds.lowerLeft.y += midPoint.y;
        float vX;
        float vY;
        for (Vector2 v : bounds.vertices){
            vX = v.x;
            vY = v.y;
            vX -= midPoint.x;
            vY -= midPoint.y;
            v.x = ( vX * cosMath - vY * sinMath);
            v.y = (vX * sinMath + vY * cosMath);
            v.x += midPoint.x;
            v.y += midPoint.y;
        }
    }
}
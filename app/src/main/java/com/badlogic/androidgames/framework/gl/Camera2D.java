package com.badlogic.androidgames.framework.gl;

import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Vector2;

public class Camera2D {

	public Vector2 position;
	public Vector2 finalPosition;
	public Vector2 velocity;
	public int frameCounter;
	public float zoom;
	public float finalZoom;
	public float zDiff;
	public final float frustumWidth;
	public final float frustumHeight;
	final GLGraphics glGraphics;
	
	public Camera2D(GLGraphics glGraphics, float frustumWidth, float frustumHeight){
		this.glGraphics = glGraphics;
		this.frustumWidth = frustumWidth;
		this.frustumHeight = frustumHeight;
		this.position = new Vector2(frustumWidth/2, frustumHeight/2);
		this.position = new Vector2(frustumWidth/2, frustumHeight/2);
		this.finalPosition = new Vector2(position.x, position.y);
		this.zoom = 1.0f;
		this.finalZoom = zoom;
		velocity = new Vector2(0,0);
		frameCounter = 0;
		zDiff = 0;
	}
	
	public void setViewportAndMatrices(){
		GL10 gl = glGraphics.getGL();
		gl.glViewport(0,0,glGraphics.getWidth(), glGraphics.getHeight());
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(
			position.x - frustumWidth * zoom /2,
			position.x + frustumWidth * zoom /2,
			position.y - frustumHeight * zoom/2,
			position.y + frustumHeight * zoom/2, 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void touchToWorld(Vector2 touch){
		touch.x = (touch.x / (float) glGraphics.getWidth()) * frustumWidth * zoom;
		touch.y = (1 - touch.y / (float) glGraphics.getHeight()) * frustumHeight *zoom;
		touch.add(position).sub(frustumWidth * zoom / 2, frustumHeight * zoom / 2);
	}

	public void panToPosition(float targX, float targY, float newZoom){
		double xDiff = position.x - targX;
		double yDiff = position.y - targY;

		finalPosition.set(targX, targY);
		finalZoom = newZoom;
		frameCounter = 7;
		zDiff = (zoom - newZoom)/frameCounter;
		velocity.set((float)-xDiff/frameCounter, (float)-yDiff/frameCounter);
//		double multiplier = 1;
//		if((xDiff >= 0 && yDiff > 0) || (xDiff <= 0 && yDiff > 0)){
//			multiplier = -1;
//		}
//		yVelocity = multiplier*Math.cos(angle);
//		xVelocity = multiplier*Math.sin(angle);
//		velocity.add((float) xVelocity * 50, (float) yVelocity * 50);


	}

	public void update(float deltaTime){
		if(frameCounter > 0){
			position.add(velocity.x, velocity.y);
			zoom -= zDiff;
			Log.d("Zoom", zoom + "");
			frameCounter--;
			if(frameCounter == 0){
				position.set(finalPosition.x, finalPosition.y);
				zoom = finalZoom;
			}
		}
	}
	
	public boolean moveCamera(){
		boolean shouldMove = true;

		if (Math.abs((double)(position.x - finalPosition.x)) <= 12 && Math.abs((double)(position.y - finalPosition.y)) <= 12){
			shouldMove = false;
		}

		return shouldMove;
	}

}

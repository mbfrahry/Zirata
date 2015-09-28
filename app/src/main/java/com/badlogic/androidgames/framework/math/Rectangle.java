package com.badlogic.androidgames.framework.math;

import com.badlogic.androidgames.framework.gl.Camera2D;

public class Rectangle {

	public final Vector2 lowerLeft;
	public float width, height;
	public float rotationAngle;

	public Rectangle(float x, float y, float width, float height){
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
		rotationAngle = 0;
	}

	public Rectangle(float x, float y, float width, float height, float angle){
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
		this.rotationAngle = angle;
	}

	public Rectangle(Camera2D guiCam, float x, float y, float width, float height){
		x = guiCam.position.x + ((x -160)*guiCam.zoom );
		y = guiCam.position.y + ((y -240)*guiCam.zoom );
		width *= guiCam.zoom;
		height *= guiCam.zoom;
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
		rotationAngle = 0;
	}
}

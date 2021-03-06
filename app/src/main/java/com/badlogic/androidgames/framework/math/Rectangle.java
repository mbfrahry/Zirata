package com.badlogic.androidgames.framework.math;

import com.badlogic.androidgames.framework.gl.Camera2D;

public class Rectangle {

	public final Vector2 lowerLeft;
	public float width, height;
	public Vector2 rotationAngle;
	public Vector2[] vertices;
	public Vector2[] axes;
	public Vector2 widthVector;
	public Vector2 heightVector;

	public Rectangle(float x, float y, float width, float height){
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
		rotationAngle = new Vector2(1,0);
		vertices = new Vector2[4];
		vertices[0] = new Vector2();
		vertices[1] = new Vector2();
		vertices[2] = new Vector2();
		vertices[3] = new Vector2();
		axes = new Vector2[4];
		axes[0] = new Vector2();
		axes[1] = new Vector2();
		axes[2] = new Vector2();
		axes[3] = new Vector2();
		widthVector = new Vector2(width, 0);
		heightVector = new Vector2(0, height);
		setVertices();
		setAxes();
	}

	public Rectangle(float x, float y, float width, float height, Vector2 angle){
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
		this.rotationAngle = angle;
		vertices = new Vector2[4];
		vertices[0] = new Vector2();
		vertices[1] = new Vector2();
		vertices[2] = new Vector2();
		vertices[3] = new Vector2();
		axes = new Vector2[4];
		axes[0] = new Vector2();
		axes[1] = new Vector2();
		axes[2] = new Vector2();
		axes[3] = new Vector2();
		widthVector = new Vector2(width, 0);
		heightVector = new Vector2(0, height);
		setVertices();
		setAxes();
	}

	public Rectangle(Camera2D guiCam, float x, float y, float width, float height){
		x = guiCam.position.x + ((x -160)*guiCam.zoom );
		y = guiCam.position.y + ((y -240)*guiCam.zoom );
		width *= guiCam.zoom;
		height *= guiCam.zoom;
		this.lowerLeft = new Vector2(x,y);
		this.width = width;
		this.height = height;
		rotationAngle = new Vector2(1,0);
		vertices = new Vector2[4];
		vertices[0] = new Vector2();
		vertices[1] = new Vector2();
		vertices[2] = new Vector2();
		vertices[3] = new Vector2();
		axes = new Vector2[4];
		axes[0] = new Vector2();
		axes[1] = new Vector2();
		axes[2] = new Vector2();
		axes[3] = new Vector2();
		widthVector = new Vector2(width, 0);
		heightVector = new Vector2(0, height);
		setVertices();
		setAxes();
	}

	public Vector2[] setVertices(){
		rotateVector(widthVector);
		rotateVector(heightVector);

		vertices[0] = lowerLeft;
		vertices[1].set(lowerLeft.x + widthVector.x, lowerLeft.y + widthVector.y);
		vertices[2].set(lowerLeft.x + widthVector.x + heightVector.x, lowerLeft.y + widthVector.y + heightVector.y);
		vertices[3].set(lowerLeft.x + heightVector.x, lowerLeft.y + heightVector.y);

		widthVector.set(width, 0);
		heightVector.set(0, height);
		return vertices;
	}

	public void setVertex(int index, float newX, float newY){
		vertices[index].set(newX, newY);
	}

	public void rotateVector(Vector2 point){
		float leftX = point.x;
		float leftY = point.y;
		point.x =  ( leftX * rotationAngle.x) + leftY * -rotationAngle.y;
		point.y = (leftX * rotationAngle.y) + leftY * rotationAngle.x;
	}

	public void setAxes(){
		for (int i = 0; i < 4; i++) {
			// get the current vertex
			Vector2 p1 = vertices[i];
			// get the next vertex
			Vector2 p2 = vertices[i + 1 == vertices.length ? 0 : i + 1];
			// subtract the two to get the edge vector
			//Vector2 edge = new Vector2(p1.x-p2.x, p1.y-p2.y);
			// get either perpendicular vector
			//float temp = edge.x;
			//edge.x = -edge.y;
			//edge.y = temp;
			//Vector2 normal = edge.nor();

			axes[i].set(-(p1.y-p2.y), p1.x-p2.x);
		}
	}

	public Vector2 project(Vector2 axis){
		float min = axis.dot(vertices[0]);
		float max = min;
		for (int i = 1; i < vertices.length; i++) {
			// NOTE: the axis must be normalized to get accurate projections
			float p = axis.dot(vertices[i]);
			if (p < min) {
				min = p;
			} else if (p > max) {
				max = p;
			}
		}
		Vector2 proj = new Vector2(min, max);
		return proj;
	}

	public boolean pointInRotatedRectangle(Vector2 point){
		float origX = point.x;
		float origY = point.y;

		point.x -= lowerLeft.x;
		point.y -= lowerLeft.y;
		rotateVector(widthVector);
		rotateVector(heightVector);

		Vector2 unitWidth = new Vector2(widthVector.x, widthVector.y);
		Vector2 unitHeight = new Vector2(heightVector.x, heightVector.y);

		unitWidth.nor();
		unitHeight.nor();

		float pDotw = unitWidth.x*point.x + unitWidth.y*point.y;
		float pDoth = unitHeight.x*point.x + unitHeight.y*point.y;
		boolean toReturn = true;
		if(pDotw > widthVector.dot(unitWidth) || pDotw < 0){
			toReturn = false;
		}
		else if( pDoth > heightVector.dot(unitHeight) || pDoth < 0){
			toReturn = false;
		}

		point.set(origX, origY);
		widthVector.set(width, 0);
		heightVector.set(0, height);
		return toReturn;
	}
}

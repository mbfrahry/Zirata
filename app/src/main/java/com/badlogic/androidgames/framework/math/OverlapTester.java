package com.badlogic.androidgames.framework.math;

import com.badlogic.androidgames.framework.gl.Camera2D;

public class OverlapTester {

	public static boolean overlapCircles(Circle c1, Circle c2){
		float distance = c1.center.distSquared(c2.center);
		float radiusSum = c1.radius + c2.radius;
		return distance <= radiusSum * radiusSum;
	}
	
	public static boolean overlapRectangles(Rectangle r1, Rectangle r2){
		if(r1.lowerLeft.x < r2.lowerLeft.x + r2.width &&
		   r1.lowerLeft.x + r1.width > r2.lowerLeft.x &&
		   r1.lowerLeft.y < r2.lowerLeft.y + r2.height &&
		   r1.lowerLeft.y + r1.height > r2.lowerLeft.y)
			return true;
		else
			return false;
	}
	
	public static boolean overlapCircleRectangle(Circle c, Rectangle r){
		float closestX = c.center.x;
		float closestY = c.center.y;
		
		if(c.center.x < r.lowerLeft.x){
			closestX = r.lowerLeft.x;
		}
		
		else if(c.center.x > r.lowerLeft.x + r.width){
			closestX = r.lowerLeft.x + r.width;
		}
		
		if(c.center.y < r.lowerLeft.y){
			closestY = r.lowerLeft.y;
		}
		else if(c.center.y > r.lowerLeft.y + r.height){
			closestY = r.lowerLeft.y + r.height;
		}
		
		return c.center.distSquared(closestX, closestY) < c.radius * c.radius;
	}
	
	public static boolean pointInCircle(Circle c, Vector2 p){
		return c.center.distSquared(p) < c.radius * c.radius;
	}
	
	public static boolean pointInCircle(Circle c, float x, float y){
		return c.center.distSquared(x, y) < c.radius * c.radius;
	}
	
	public static boolean pointInRectangle(Rectangle r, Vector2 p){
		return r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x &&
				r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
	}

	public static boolean pointInRotatedRectangle(Rectangle r, Vector2 p){
		//rotate height and width vectors
		//dot them with the point, if > 1, no good, if sum > 2, no good
		return r.pointInRotatedRectangle(p);
	}

	public static boolean pointInUIRectangle(Camera2D guiCam, Rectangle r, Vector2 p){
		return r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x &&
				r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
	}
	
	public static boolean pointInRectangle(Rectangle r, float x, float y){
		return r.lowerLeft.x <= x && r.lowerLeft.x + r.width >= x &&
				r.lowerLeft.y <= y && r.lowerLeft.y + r.height >= y;
	}

	public static boolean overlapPolygons(Rectangle r1, Rectangle r2){
		r1.setAxes();
		r2.setAxes();

// loop over the axes1
		for (int i = 0; i < r1.axes.length; i++) {
			Vector2 axis = r1.axes[i];
			// project both shapes onto the axis
			Vector2 p1 = r1.project(axis);
			Vector2 p2 = r2.project(axis);
			// do the projections overlap?
			if (!overlap(p1, p2)) {
				// then we can guarantee that the shapes do not overlap
				return false;
			}
		}
// loop over the axes2
		for (int i = 0; i < r2.axes.length; i++) {
			Vector2 axis = r2.axes[i];
			// project both shapes onto the axis
			Vector2 p1 = r1.project(axis);
			Vector2 p2 = r2.project(axis);
			// do the projections overlap?
			if (!overlap(p1, p2)) {
				// then we can guarantee that the shapes do not overlap
				return false;
			}
		}
// if we get here then we know that every axis had overlap on it
// so we can guarantee an intersection
		return true;
	}

	private static boolean overlap(Vector2 first, Vector2 second){
		return !(first.x > second.y || second.x > first.y);
	}
}

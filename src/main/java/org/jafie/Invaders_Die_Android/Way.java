package org.jafie.Invaders_Die_Android;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Way extends Rectangle {
	
	//This class represents a Way. For each ways the unit follow "dir".
	
	private int dir; //1=up   2=down   3=left   4=right
	
	public Way(float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager pVertexBufferObjectManager,int walk) {
		super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
		
		setDir(walk);
		
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	
	
}

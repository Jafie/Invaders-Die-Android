package org.jafie.Invaders_Die_Android;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class PopupMenu extends Rectangle {

	
	private int stateplacement;
	private ArrayList<Integer> Circleshowed = new ArrayList<Integer>();
	private int limitW;
	private int limitH;
	private boolean player;
//Create the cursor
	public PopupMenu(float pX, float pY, float pWidth, float pHeight,
			VertexBufferObjectManager vertexBufferObjectManager,TMXLayer tmxLayer,boolean Player) {
		super(pX, pY, pWidth, pHeight, vertexBufferObjectManager);

		limitW=tmxLayer.getWidth();
		limitH=tmxLayer.getHeight();
		this.setColor(1.0f,0,0);
		setStateplacement(0);
		this.player = Player;
	}

	int testZone(ArrayList<Tower> ArrayTowerAlly,ArrayList<Rectangle> ArrayWall,Scene scene)
	{	float Xcurstop;
		float Ycursleft;
		float Xcursbot;
		float Ycursright;
		boolean state=false;
		Xcurstop=this.getX();
		Ycursleft=this.getY();
		Xcursbot=this.getX()+this.getWidth();
		Ycursright=this.getY()+this.getHeight();
		int k=0;
		int i=0;
		int l=0;
		//Test if the player put the cursor to the "Game zone" (left for the group owner and right for the other)
		if(player&&this.getX()>limitW)
		{
			state=true;
		}
		else
		{
			if(!player&&this.getX()<limitW)
			{
				state=true;
			}
			
		}
		
		for(l=0;l<Circleshowed.size();l++)
		{
			ArrayTowerAlly.get(Circleshowed.get(l)).detachcircle(scene);	//Detach all the circle of range	
		}
		Circleshowed.clear();
		Circleshowed = new ArrayList<Integer>();
		while((k<ArrayTowerAlly.size()))
		{

			if(this.collidesWith(ArrayTowerAlly.get(k)))
			{
				state=true;
				ArrayTowerAlly.get(k).showcircle(scene); //Get all the circle range of the tower under the cursor
				Circleshowed.add(k);
			}
			k++;
		}
		
		if(Xcurstop<(0)||Xcursbot>(limitW)||Ycursleft<0||Ycursright>limitH) //Switch the cursor to red if we quit the area of game
		{
			this.setColor(1.0f,0,0);
			setStateplacement(0);
		}
		else
		{
			
			while((i<ArrayWall.size())&&!state) //Switch to red if a wall is under the cursor
			{
				if(this.collidesWith(ArrayWall.get(i)))
				{
					state=true;
					
				}
				i++;

			}
			while((k<ArrayTowerAlly.size())&&!state)	//Switch to red if a tower is under the cursor
			{

				if(this.collidesWith(ArrayTowerAlly.get(k)))
				{
					state=true;
					ArrayTowerAlly.get(k).showcircle(scene);

					Circleshowed.add(k);
				}
				k++;
			}

			if(state)
			{
				this.setColor(1.0f,0,0);
				setStateplacement(0);
			}
			else
			{
				this.setColor(0,1.0f,0);
				setStateplacement(1);
			}
		}

		
		return 0;
	}
	
	public void setRect(float width, float heigth)
	{
		this.setWidth(width);
		this.setHeight(heigth);
	}

	public int getStateplacement() {
		return stateplacement;
	}

	public void setStateplacement(int stateplacement) {
		this.stateplacement = stateplacement;
	}
	
	
	public int testTower (float pTouchAreaLocalX,float zoomfactor )
	{
			//Test if a tower can be placed
 		   if(pTouchAreaLocalX<(180/zoomfactor))
 		   {
				   if(((this.getWidth()==64)||(this.getHeight()==64))&&(this.getStateplacement()==1))
				   {
					   	return 1;
 		   }
				   else
				   {
					   this.setRect(64, 64);
					   return 0;
				   }
 		   }   
 		   else
 			   if(pTouchAreaLocalX<(400/zoomfactor))
 			   {
 				   if(((this.getWidth()==128)||(this.getHeight()==128))&&(this.getStateplacement()==1))
 				   {
 					  return 2;	
 				   }
 				   else
 				   {
 					  this.setRect(128, 128);
 					 return 0;
 				   }
 				   }
			
 	   
 	  return 0;
	}
	
	
}

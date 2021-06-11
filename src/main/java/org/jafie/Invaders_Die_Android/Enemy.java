package org.jafie.Invaders_Die_Android;

import java.util.ArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Enemy extends AnimatedSprite {

	private int[] animation = new int[]{0,1,2};	//Manage the animation
	private long[] speed = new long[]{100,100,100};	//Speed animation
	private int lastdir;
	private int HP;	//Life points
	private ArrayList<Projectile> fireball = new ArrayList<Projectile>();	//The shots
private boolean player;
	private boolean isEnemy;
	//Create a new Enemy and attach it on the map
	public Enemy(float pX, float pY, float pWidth, float pHeight,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,Scene scene,ArrayList<Enemy> ArrayEnemy,int Life,boolean player,boolean isEnemy) {
		super(pX, pY, pWidth, pHeight, pTiledTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
		ArrayEnemy.add(this);
		  scene.attachChild(ArrayEnemy.get(ArrayEnemy.size()-1));
		 // ArrayEnnemy.get(ArrayEnnemy.size()-1).animate(new long[]{100,100,100},new int[]{12,13,14});
		this.HP=Life;
		this.animate(speed,animation);
		this.player=player;
		this.isEnemy=isEnemy;
	}

	
	public boolean Move(ArrayList<Way> ArrayWay,ArrayList<Rectangle> ArrayCastle,Scene scene)
	{	//This method move the enemy...The enemy patern change due to the type (Ally or Enemy) and the player (Groupowner or not)
		if(HP>0)
		{
		int[] animationtemp = new int[] {0,0,0};
		int k=0;
		int l=0;
		int dirtest;
		while(k<ArrayWay.size())
		{
		if(this.collidesWith(ArrayWay.get(k)))
		{
			
			dirtest=ArrayWay.get(k).getDir();
			
			if(this.player&&!this.isEnemy)   //For the group owner, change the ally patern
			{
				if(dirtest==1)
				{
					dirtest=2;
				}
				else if (dirtest==2)
				{
					dirtest=1;
				}
			} else if(!this.player&&this.isEnemy)	//For the other player, change the enemy patern
			{
				if(dirtest==1)
				{
					dirtest=2;
				}
				else if (dirtest==2)
				{
					dirtest=1;
				}
					
			}
				
				
				
			switch(dirtest)	//Move the enemy with the direction determines by the ways sprites (configured from right to left)
			{
			case 1:	this.setY(this.getY()-1);
			if(lastdir!=1)
			{ for(int i=0; i < animationtemp.length; i++){
				animationtemp[i]=animation[i]+36; //Change the sprite model (move to up)
			}
			this.animate(speed,animationtemp);}
				lastdir=dirtest;
			break;
				
			case 2:this.setY(this.getY()+1);
			if(lastdir!=2)
			{
			this.animate(speed,animation);}	//Change the sprite model (move to down)
			lastdir=dirtest;
			break;
				
			
			
				default:if(this.player&&isEnemy)
						{this.setX(this.getX()-1);
				if(lastdir!=-1)
				{	for(int i=0; i < animationtemp.length; i++){
					animationtemp[i]=animation[i]+12;	//Change the sprite model (move to left)
				}
				this.animate(speed,animationtemp);}
				}
					else if(this.player&&!isEnemy)
					{
						this.setX(this.getX()+1);
						if(lastdir!=-1)
						{	for(int i=0; i < animationtemp.length; i++){
							animationtemp[i]=animation[i]+24;	//Change the sprite (move to right)
						}			
						this.animate(speed,animationtemp);}
					}
					else if(!this.player&&!isEnemy)
					{this.setX(this.getX()-1);
			if(lastdir!=-1)
			{	for(int i=0; i < animationtemp.length; i++){
				animationtemp[i]=animation[i]+12;	//Change the sprite model (move to left)
			}
			this.animate(speed,animationtemp);}
			}
				else if(!this.player&&isEnemy)
				{
					this.setX(this.getX()+1);
					if(lastdir!=-1)
					{	for(int i=0; i < animationtemp.length; i++){
						animationtemp[i]=animation[i]+24;	//Change the sprite (move to right)
					}			
					this.animate(speed,animationtemp);}
				}
					lastdir=-1;
					break;
					
			
			}	
			
			k=ArrayWay.size();	
		}
		k++;
		}
		
		//Test enemy on castle
		while(l<ArrayCastle.size())
		{
		if(this.collidesWith(ArrayCastle.get(l)))
				{
			scene.detachChild(this);
			return true;
				}
		l++;
		}}
	
	return false;
}
	
	public boolean attackProjectile(Scene scene)  //Process to the shot pattern
	{
		if(!fireball.isEmpty())
		{
			
		for(int projatt=0;projatt<fireball.size();projatt++)
		{
			if(fireball.get(projatt).onAttack(this,scene)) //if the shot touches the enemy, it gets damage and the shot disappear.
			{
				this.HP = this.HP - fireball.get(projatt).getDamage();
				scene.detachChild(fireball.get(projatt));
				fireball.remove(projatt);
				if(this.HP<=0)
				{
					scene.detachChild(this);
				}
			}
		}

		}
		else
		{	if(this.HP<=0)
			{fireball.clear();
			return true;}
		}
		return false;
	}
	
	public void launchDamage(int damage,Scene scene,Tower tower)   //A shot is launch to the enemy from the tower "tower"
	{
		fireball.add(new Projectile(tower.getX()+tower.getWidth()/2, tower.getY()+tower.getHeight()/2, tower.getProjWidth(), tower.getProjHeight(), tower.getTextureProjectile(), getVertexBufferObjectManager(), tower,scene,this));

		
	}
	
	public int getHP()
	{
		return this.HP;
	}
	
}

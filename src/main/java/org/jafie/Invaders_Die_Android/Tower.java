package org.jafie.Invaders_Die_Android;

import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;


public class Tower extends Sprite{

	private float posX; //Position X
	private float posY; //Position Y
	private int ID;		//ID
	private Sprite range;		//The circle of range
	private int attackspeed;	//The speed of the attack
	private int stateattack;	//The cooldown state
	private int damage;			//The damage of the shots launched
	private ITextureRegion TextureProjectile;	//The textures of the shot
	private int projWidth;		
	private int projHeight;
	private int speedproj;
	private boolean projfinder;	//Determine if the shot is finder or not.
	
	
	public Tower(float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,int ident,ITextureRegion mCircle,Scene scene,ITextureRegion pTiledTextureRegion) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		// This class create a new tower and his type thanks to the ID...It can determine when an enemy is near the tower and attack it
		ID=ident;
		switch(ID)
		{
		case 1: this.setWidth(62);//Create tower type 1
				this.setHeight(62);
				this.attackspeed=30; //Set the cooldown of the attack
				 range = new Sprite(pX, pY, mCircle, getVertexBufferObjectManager());
				 setDamage(1);
					range.setWidth(300); //Set the range
					range.setHeight(300);
					TextureProjectile=pTiledTextureRegion;
					range.setPosition(pX+this.getWidth()/2-range.getWidth()/2,pY+this.getHeight()/2-range.getHeight()/2);
					range.setAlpha((float)0.3);
					projWidth = 16;
					projHeight = 16;
					speedproj = 7;
					setProjfinder(false);
				break;
			
		case 2 : this.setWidth(126);//Create tower type 2
				this.setHeight(126);
				range = new Sprite(pX, pY, mCircle, getVertexBufferObjectManager());
				setDamage(10);
				this.attackspeed=200;	//Set the cooldown of the attack
				range.setWidth(600);	//Set the range
				range.setHeight(600);
				TextureProjectile=pTiledTextureRegion;
				range.setPosition(pX+this.getWidth()/2-range.getWidth()/2,pY+this.getHeight()/2-range.getHeight()/2);
				range.setAlpha((float)0.3);
				projWidth = 40;
				projHeight = 40;
				speedproj = 4;
				setProjfinder(true);
				break;
			
		default: break;
		}
		stateattack=attackspeed;
		setPosX(pX);
		setPosY(pY);
		scene.attachChild(this);
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public void showcircle(Scene scene)
	{
		scene.attachChild(range); //Attach the circle of range on the map
	}

	public void detachcircle(Scene scene)
	{
		scene.detachChild(range);	//Detach the circle of range on the map
	}
	
	public void AttackEnnemy(ArrayList<Enemy> ArrayEnemy,Scene scene)
	{//This class test if an enemy is on the range of the tower and attack this if the timer is ready
		int i=0;
		if(stateattack>=attackspeed)
		{
			
		while(i<ArrayEnemy.size())
		{
			if(this.range.collidesWith(ArrayEnemy.get(i))&&(ArrayEnemy.get(i).getHP()>0))
			{
				stateattack=0;
				ArrayEnemy.get(i).launchDamage(1,scene,this);
				i=ArrayEnemy.size();			
			}
			i++;
		}
		
		}
	}
	
	
	public void preparenewattack()
	{
		if(stateattack<=attackspeed)
		{stateattack++;}
		
	}
	
	public ITextureRegion getTextureProjectile()
	{
		return this.TextureProjectile;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getProjWidth() {
		return projWidth;
	}

	public void setProjWidth(int projWidth) {
		this.projWidth = projWidth;
	}

	public int getProjHeight() {
		return projHeight;
	}

	public void setProjHeight(int projHeight) {
		this.projHeight = projHeight;
	}

	public int getSpeedproj() {
		return speedproj;
	}

	public void setSpeedproj(int speedproj) {
		this.speedproj = speedproj;
	}
	
	public void resetcooldown()
	{
		stateattack = attackspeed;
	}

	public boolean isProjfinder() {
		return projfinder;
	}

	public void setProjfinder(boolean projfinder) {
		this.projfinder = projfinder;
	}
}

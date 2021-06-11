package org.jafie.Invaders_Die_Android;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Projectile extends Sprite{

	
private int damage;	//Damage of the shot
private int speed;	//Speed of the shot
private float shootrotation;	//Rotation of the shot
private float Yway;	//Help to the rotation
private int Ywaysecond;	//Help to the rotation
private boolean projfinder;	//The shot is finder


	public Projectile(float pX, float pY, float pWidth, float pHeight,
			ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager,Tower towerlaunch,Scene scene,Enemy EnemyAttacked) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pVertexBufferObjectManager);
		// Create a new shot from the tower to the enemy
		speed = towerlaunch.getSpeedproj();
		setDamage(towerlaunch.getDamage());
		projfinder = towerlaunch.isProjfinder();
		
		//Set the rotation of the shot in order to it aims the enemy
		if(EnemyAttacked.getY()<this.getY())
		{
			Yway=0;
			Ywaysecond=-1;
		}
		else
		{	
			Yway=1;
			Ywaysecond=1;
		}
		
		if(!projfinder)
		{
			float shoottraitementX;
			float shoottraitementY;
			float shoottraitementsecond;
			int Xway;

			if(EnemyAttacked.getX()<this.getX())
			{
				Xway=-1;
			}
			else
			{
				Xway=1;
			}
			

			shoottraitementX = this.getX()-EnemyAttacked.getX();
			shoottraitementY = this.getY()-EnemyAttacked.getY();
			shoottraitementsecond = shoottraitementX*Xway*(-1)+shoottraitementY*(-1)*Ywaysecond;
			
			shootrotation = this.Yway*180+(90*shoottraitementX*Ywaysecond)/shoottraitementsecond ;
			this.setRotation(shootrotation);
		}
		scene.attachChild(this);
		
	}


	
	public boolean onAttack(Enemy EnemyAttacked,Scene scene)
	{
		
		float shoottraitementX;
		float shoottraitementY;
		float shoottraitementsecond;
		int Xway;
		
		//If the shot is "finder", the rotation follow the enemy (it aims)
		if(projfinder)
		{
		if(EnemyAttacked.getX()<this.getX())
		{
			Xway=-1;
		}
		else
		{
			Xway=1;
		}
		

		shoottraitementX = this.getX()-EnemyAttacked.getX();
		shoottraitementY = this.getY()-EnemyAttacked.getY();
		shoottraitementsecond = shoottraitementX*Xway*(-1)+shoottraitementY*(-1)*Ywaysecond;
		
		shootrotation = this.Yway*180+(90*shoottraitementX*Ywaysecond)/shoottraitementsecond ;
		this.setRotation(shootrotation);
		}
		
		//Follow the enemy due to the speed of the shot
		if((EnemyAttacked.getX()+EnemyAttacked.getWidth()/2)<this.getX())
		{
			this.setX(this.getX()-speed);
		}
		else
		{
			this.setX(this.getX()+speed);
		}
		if((EnemyAttacked.getY()+EnemyAttacked.getHeight()/2)<this.getY())
		{
			this.setY(this.getY()-speed);
		}
		else
		{
			this.setY(this.getY()+speed);
		}

		if(this.collidesWith(EnemyAttacked))
		{scene.detachChild(this);
			return true;
		}
		return false;
	}


	public int getDamage() {
		return damage;
	}


	public void setDamage(int damage) {
		this.damage = damage;
	}
	

}

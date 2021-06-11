package org.jafie.Invaders_Die_Android;

/**
 * Class that contains enough information to create a tower
 * with data from P2P Connection.
 *
 */
public class TowerWait {

	private float xPos; //x Position
	private float yPos; //y Position
	private int id; //Kind of Tower
	
	public TowerWait() {
		setxPos(0);
		setyPos(0);
		setId(0);
	}
	
	public TowerWait(float xPos, float yPos, int id) {
		this.setxPos(xPos);
		this.setyPos(yPos);
		this.setId(id);
	}

	public float getxPos() {
		return xPos;
	}

	public void setxPos(float xPos) {
		this.xPos = xPos;
	}

	public float getyPos() {
		return yPos;
	}

	public void setyPos(float yPos) {
		this.yPos = yPos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

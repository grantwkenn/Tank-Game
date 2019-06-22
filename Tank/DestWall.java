package Tank;

import java.awt.Image;

public class DestWall extends Wall {
	
	int health;
	
	public DestWall(Image img, int x, int y, int health) {
		super(img, x, y);
		this.health = health;
	}
	
	public void update()
	{
		if (collided)
		{
			health--;
			collided = false;
		}
		if(health == 0)
		{
			show = false;
		}

	}
}

package Tank;

import java.awt.Image;

public abstract class MoveableGameObject extends GameObject {

	int Xspeed;
	int Yspeed;
	
	public MoveableGameObject(Image img, int x, int y) {
		super(img, x, y);
	}

}

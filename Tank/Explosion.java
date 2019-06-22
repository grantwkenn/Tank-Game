package Tank;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Explosion extends GameObject{
	
	private Image sequence[];
	int timer = 60;


	public Explosion(Image img, int x, int y, Image[] seq) {
		super(img, x, y);
		this.sequence = seq;
	    
	}
	
	public void update()
	{
		timer--;
		if(timer < 0)
			show = false;
		else
		{
			img = sequence[timer/10];
		}

	}

}

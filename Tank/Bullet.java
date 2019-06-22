package Tank;

import static javax.imageio.ImageIO.read;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;


public class Bullet extends MoveableGameObject {
	
    private int angle;
    private Tank shotBy;
	
	public Bullet(Image img, int x, int y, int xs, int ys, int angle, Tank shotBy){
		super(img,x,y);
		Xspeed = xs;
		Yspeed = ys;
        this.show = true;
        this.angle = angle;
        this.x = (x + Xspeed);
        this.y = (y + Yspeed);
        this.shotBy = shotBy;
    }
	
    public Tank gotShotBy()
    {
    	return shotBy;
    }
	
    public boolean getShow(){
        return this.show;
    } 
    public void setShow(boolean s){
        this.show = s;
    }
    public void update(){
    	
    	if(collided)
            this.show = false;
    	else
        {
        	if(y < Game.MAP_HEIGHT-40 && y > 0 && x > 0 && x < Game.MAP_WIDTH-40 && show){ //BETWEEN BOUNDARY? 
                x = x + Xspeed;
                y = y + Yspeed;
                collisionBox.setLocation(x, y);
            }
            else{
                this.show = false;
            }
        }

    }
    public void draw(Graphics g, ImageObserver obs) {
        if(show)
        {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.getWidth() / 2.0, this.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
        }

     }
}

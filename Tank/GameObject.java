package Tank;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Random;

public class GameObject {
    protected int x, y, oldx, oldy, width, height;
    protected Image img;
    protected boolean show = true;
    protected Rectangle collisionBox;
    protected boolean collided = false;
    
    public GameObject(Image img, int x, int y){
        this.img = img;
        this.oldx = x;
        this.oldy = y;
        this.x = x;
        this.y = y;
        this.width = img.getWidth(null);
        this.height = img.getHeight(null);
        collisionBox = new Rectangle(x, y, width, height);
    }
    
    public boolean isCollided()
    {
    	return collided;
    }
    
    public int getX(){
        return this.x;
    }
    
    public int getY(){
        return this.y;
    }
    
    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return this.height;
    }
    
    public void setX(int a){
        this.x = a;
        collisionBox.setLocation(x, y);
    }
    
     public void setY(int b){
        this.y = b;
        collisionBox.setLocation(x, y);
    }
     public void draw(Graphics g, ImageObserver obs){
         g.drawImage(img, x, y, obs);
    
    }
     
     public boolean getShow()
     {
    	 return show;
     }
     
     public void update()
     {
    	 
     }
     
     public Rectangle getBox()
     {
    	 return collisionBox;
     }
     
     public void toggleShow()
     {
    	 if(show)
    		 show = false;
    	 else show = true;
     }
     
     public void hide()
     {
    	 show = false;
     }

}

package Tank;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Scanner;

import static javax.imageio.ImageIO.read;

public class Game extends JPanel
{
	public static final int SCREEN_WIDTH = 1920;
	public static final int SCREEN_HEIGHT = 1080;
	public static final int MAP_WIDTH = 2048;
	public static final int MAP_HEIGHT = 1536;
	private final Dimension minimap = new Dimension((MAP_WIDTH/6), (MAP_HEIGHT/6));
	private static boolean gameOver = false;
	
	private static BufferedImage world, p1img, screen1, screen2, bulletImage, gameOverScreen;
	
	private static Image backGround, wall, dWall, house, boostImg;
	private static Graphics2D buffer;
	private static Graphics2D g2;
	
	private static JFrame jf;
	private Tank p1;
	private Tank p2;
	
	private ArrayList<GameObject> gameObjs;
	private ArrayList<Bullet> bullets;
	
	private ArrayList<Health> hearts;
	private ArrayList<Boost> boosts;
	private ArrayList<Explosion> explosions;
	
	private final Image health[] = new Image[10];
	private final Image lives[] = new Image[5];
	private final Image explosion[] = new Image[6];	
	
	private SoundPlayer music;
	private final String bigExpl = "resources/sounds/Explosion_small.wav";
	private final String smallExpl = "resources/sounds/Explosion_large.wav";

    public static void main(String[] args) {
		jf = new JFrame("Grant's Tank Game");
        Game game = new Game();
        game.init();   

        jf.addWindowListener(new WindowAdapter() {
        });
        jf.getContentPane().add("Center", game);
        jf.pack();
        jf.setSize(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT + 30);
        jf.setVisible(true);
        jf.setResizable(false);
		jf.setBackground(Color.black);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {        	
        	while (true) { 
            	game.update();
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {

        }

    }
    
    public void restart()
    {
    	gameOver = false;
    	while(!gameObjs.isEmpty())
    		gameObjs.remove(0);
    	while(!bullets.isEmpty())
    		bullets.remove(0);
    	while(!hearts.isEmpty())
    		hearts.remove(0);
    	while(!boosts.isEmpty())
    		boosts.remove(0);
    	while(!explosions.isEmpty())
    		explosions.remove(0);

    	loadMap();
    	p1.newGame();
    	p2.newGame();

    }
    
	public void init()
	{		
		
		music = new SoundPlayer(1,"resources/sounds/01 Jeremy Soule - Dragonborn.wav");
        world = new BufferedImage(Game.MAP_WIDTH, Game.MAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        //Read resources from storage
        try {   	       	
        	boostImg = ImageIO.read(Game.class.getResource("resources/boost.png"));
        	bulletImage = ImageIO.read(Game.class.getResource("resources/shell.png"));
            backGround = ImageIO.read(Game.class.getResource("resources/dirt.png"));
            wall = ImageIO.read(Game.class.getResource("resources/wall.png"));
            dWall = ImageIO.read(Game.class.getResource("resources/dwall.png"));
            p1img = ImageIO.read(Game.class.getResource("resources/tank1.png"));
            gameOverScreen = ImageIO.read(Game.class.getResource("resources/gameover.png"));
            house = ImageIO.read(Game.class.getResource("resources/house.png"));
            
            for(int i =0; i<10; i++)
            {
            	health[i] = ImageIO.read(Game.class.getResource("resources/health/" + i + ".png"));
            }
            
            for(int i =0; i<6; i++)
            {
            	explosion[i] = ImageIO.read(Game.class.getResource("resources/explosion/" + i + ".png"));
            }
            
            for(int i =0; i<5; i++)
            {
            	lives[i] = ImageIO.read(Game.class.getResource("resources/lives/" + i + ".png"));
            }

            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
		explosions = new ArrayList<Explosion>();
		gameObjs = new ArrayList<GameObject>();
		bullets = new ArrayList<Bullet>();
		hearts = new ArrayList<>();
		boosts = new ArrayList<>();

		
		p1 = new Tank(p1img, 0, 0, 270, this);
		p2 = new Tank(p1img, 0, 0, 90, this);
		loadMap();
        
        TankControl tc2 = new TankControl(p2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        TankControl tc1 = new TankControl(p1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);

        jf.addKeyListener(tc1);
        jf.addKeyListener(tc2);
        
	}
	
	
	public void update()
	{			
	    detectCollisions();
			
		//update game objects
	    for(int i = 0; i< gameObjs.size(); i++)
	       {
	    	   gameObjs.get(i).update();
	    	   if(!gameObjs.get(i).getShow())
	    		   gameObjs.remove(i);
	       }
		
		//update bullets
		for(int i = 0; i< bullets.size(); i++)
	       {
			bullets.get(i).update();
	    	   if(!bullets.get(i).getShow())
	    		   bullets.remove(i);
	       }
		
		//update tanks
		p1.update();
	    p2.update();
		
		//update explosions
		for(int i = 0; i< explosions.size(); i++)
	       {
			explosions.get(i).update();
	    	   if(!explosions.get(i).getShow())
	    		   explosions.remove(i);
	       }
	    
		//update Powerups
	    for(int i = 0; i < hearts.size(); i++)
	    {
	    	   if(!hearts.get(i).getShow())
	    		   hearts.remove(i);
	    }
	    for(int i = 0; i < boosts.size(); i++)
	    {
	    	   if(!boosts.get(i).getShow())
	    		   boosts.remove(i);
	    }
	    
	    repaint();
	    
	    //if game is lost, wait for explosions to finish, end game
	    if((p1.lost() || p2.lost()) && explosions.isEmpty())
	    {
	    	gameOver = true;
	    	repaint();
	    	gameOver();
	    }

	}
	
	private void detectCollisions()
	{		
		int i;
		
	    //Tanks collide eachother?
	 	if(p1.getBox().intersects(p2.getBox())) {
	 		p2.collided = p1.collided = true;
	 	}
	 	else {
	 		p2.collided = p1.collided = false;
	 	}
	 	   
	 	//bullets hit tanks?
	 	for(i=0; i< bullets.size(); i++)
	 	{
		 	   if(bullets.get(i).gotShotBy() != p1 && bullets.get(i).getBox().intersects(p1.getBox()))
		 	   {
		 		  bullets.get(i).collided = true;
		 		  if(p1.takeDamage(1))
		 		  {
		 			  p2.addScore(10);
		 			  p2.addHit();
		 		  }

		 		  addExplosion(bullets.get(i).x, bullets.get(i).y, 0);
		 	   }
		 	   if(bullets.get(i).gotShotBy() != p2 && bullets.get(i).getBox().intersects(p2.getBox()))
		 	   {
		 		  bullets.get(i).collided = true;
		 		  if(p2.takeDamage(1))
		 		  {
		 			  p1.addScore(10);
		 			  p1.addHit();
		 		  }
		 		  addExplosion(bullets.get(i).x, bullets.get(i).y, 0);
		 	   }
	 	   }
   	 	  
	 	   
 	   //bullets collide gameObjects?
 	   for(i=0; i< bullets.size(); i++)
 	   {
 		   int goSize = gameObjs.size();
 		   for(int j=0; j<goSize; j++)
 	 	   {		   
 	 	 	   if(bullets.get(i).isCollided())
 	 	 		   continue;
 			   if(bullets.get(i).getBox().intersects(gameObjs.get(j).getBox()))
 	 		   {
 	 	 		  bullets.get(i).collided = true;
 	 	 		  gameObjs.get(j).collided = true;
 	 	 		  if (gameObjs.get(j) instanceof DestWall)
 	 	 		  {
 	 	 			  bullets.get(i).gotShotBy().addHit();
 	 	 		  }
 		 		  addExplosion(bullets.get(i).x, bullets.get(i).y, 0);
 	 		   }
 	 	   }
 	   }
 	   
 	   
 	   //Tanks collide Walls?
 	   for(i=0; i<gameObjs.size(); i++)
 	   {		   
 	 		if(gameObjs.get(i).getBox().intersects(p1.getBox()))
 	 		 {
 	 	 		 p1.collided = true;
 	 		 }
 	 		 if(gameObjs.get(i).getBox().intersects(p2.getBox()))
 	 		 {
 	 	 	     p2.collided = true;
 	 		 }
       }

 	   //Tanks receive powerups?
 	   for(i=0; i<hearts.size(); i++)
 	   {		   
 		   if(hearts.get(i).getBox().intersects(p1.getBox()))
 		   {
 			  hearts.get(i).hide();
 			  p1.takeDamage(-9);
 			  p2.addLife();
 		   }
 		   if(hearts.get(i).getBox().intersects(p2.getBox()))
 		   {
  			  hearts.get(i).hide();
 			  p2.takeDamage(-9);
 			  p2.addLife();
 		   }

 	   }
 	   for(i=0; i<boosts.size(); i++)
 	   {		   
 		   if(boosts.get(i).getBox().intersects(p1.getBox()))
 		   {
 			  boosts.get(i).hide();
 			  p1.boost();		  
 		   }
 		   if(boosts.get(i).getBox().intersects(p2.getBox()))
 		   {
 			  boosts.get(i).hide();
 			  p2.boost();
 		   }
 	   } 	     
	}
		
	
    public void paintComponent(Graphics g) 
    {
    	g.setColor(Color.black);
    	g2 = (Graphics2D) g;

        buffer = world.createGraphics();
        buffer.drawImage(backGround, 0, 0, null);
        
        int i;
        for(i = 0; i< hearts.size(); i++)
        {
     	   hearts.get(i).draw(buffer, null);
        }
        for(i = 0; i< boosts.size(); i++)
        {
     	   boosts.get(i).draw(buffer, null);
        }
        
       for(i = 0; i< gameObjs.size(); i++)
       {
    	   gameObjs.get(i).draw(buffer, null);
       }
       for(i = 0; i< bullets.size(); i++)
       {
    	   bullets.get(i).draw(buffer, null);
       }

       this.p1.draw(buffer);
       this.p2.draw(buffer);
       
       for(i = 0; i< explosions.size(); i++)
       {
    	   explosions.get(i).draw(buffer, null);
       }
  
       //update split screens to track tanks
       //check for screen boundary
       int s1x = p1.getX() - (SCREEN_WIDTH/4);
       int s1y = p1.getY() - (SCREEN_HEIGHT/2);
       if(s1x > MAP_WIDTH - SCREEN_WIDTH/2)
    	   s1x = MAP_WIDTH - SCREEN_WIDTH/2;
       if(s1y > MAP_HEIGHT - SCREEN_HEIGHT)
    	   s1y = MAP_HEIGHT - SCREEN_HEIGHT;
       if(s1x <= 0)
    	   s1x = 0;
       if(s1y <= 0)
    	   s1y = 0;
       screen1 = world.getSubimage(s1x, s1y, (SCREEN_WIDTH/2), SCREEN_HEIGHT);
           
       int s2x = p2.getX() - (SCREEN_WIDTH/4);
       int s2y = p2.getY() - (SCREEN_HEIGHT/2);
       if(s2x > MAP_WIDTH - (SCREEN_WIDTH/2))
    	   s2x = MAP_WIDTH - SCREEN_WIDTH/2;
       if(s2y > MAP_HEIGHT - SCREEN_HEIGHT)
    	   s2y = MAP_HEIGHT - SCREEN_HEIGHT;
       if(s2x <= 0)
    	   s2x = 0;
       if(s2y <= 0)
    	   s2y = 0;
       screen2 = world.getSubimage(s2x, s2y, (SCREEN_WIDTH/2), SCREEN_HEIGHT);
       if (gameOver)
       {
    	   //Blur the screen for gameOver
    	   int r = 3;
           int size = r * 2 + 1;
           float weight = 1.0f / (size * size);
           float[] d = new float[size * size];

           for (i = 0; i < d.length; i++) {
               d[i] = weight;
           }
           Kernel kernel = new Kernel(size, size, d);
             
          	BufferedImageOp op = new ConvolveOp(kernel);
         	screen1 = op.filter(screen1, null);
         	screen2 = op.filter(screen2, null);  	
       }
       g2.drawImage(screen1,0,0,null);
       g2.drawImage(screen2,(SCREEN_WIDTH/2 + 5),0,null);
       
       //Draw MiniMap
       g2.fillRect((SCREEN_WIDTH/2 - minimap.width/2 - 5), (SCREEN_HEIGHT - minimap.height - 5), minimap.width + 10, minimap.height + 10);
       g2.drawImage(world, (SCREEN_WIDTH/2 - minimap.width/2), (SCREEN_HEIGHT - minimap.height), (minimap.width), minimap.height, null); 

       //Draw health bars and lives       
       g2.drawImage(health[p1.getHealth()], 50,SCREEN_HEIGHT - 100, null);
       g2.drawImage(health[p2.getHealth()], (SCREEN_WIDTH - 325),SCREEN_HEIGHT - 100, null);
       g2.drawImage(lives[p1.getLives()], 50, SCREEN_HEIGHT - 150, null);
       g2.drawImage(lives[p2.getLives()], (SCREEN_WIDTH - 325),SCREEN_HEIGHT - 150, null);
    }
      
    public void addBullet(int x, int y, int xs, int ys, int angle, Tank shotBy)
    {
    	bullets.add(new Bullet(bulletImage, x-(bulletImage.getWidth(null)/2), 
    			y - (bulletImage.getHeight(null)/2), xs, ys, angle, shotBy));
    }
    
    public void gameOver() {
	
    	try {
			
	    	Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    	gameOver = true;
    	
    	g2 = (Graphics2D) this.getGraphics();

    	g2.fillRect(0, SCREEN_HEIGHT/2 - (gameOverScreen.getHeight()/2), SCREEN_WIDTH, gameOverScreen.getHeight() + 100);
        g2.drawImage(gameOverScreen, SCREEN_WIDTH/2 - gameOverScreen.getWidth()/2, SCREEN_HEIGHT/2 - (gameOverScreen.getHeight()/2), null);
        
        Font stringFont = new Font( "SansSerif", Font.PLAIN, 36 );
        g2.setFont( stringFont ); 
        g2.setColor(Color.white);
        
        String vict = "Victory!";
        String fail = "Failure!";
        String score = "Score: ";


        if(p1.lost())
        {
        	g2.drawString(fail,  SCREEN_WIDTH/7, SCREEN_HEIGHT/2);
        	g2.drawString(vict, (int) (3.75*(SCREEN_WIDTH/5)), SCREEN_HEIGHT/2);
        }
        else
        {
        	g2.drawString(vict,  SCREEN_WIDTH/7, SCREEN_HEIGHT/2);
        	g2.drawString(fail, (int) (3.75*(SCREEN_WIDTH/5)), SCREEN_HEIGHT/2);
        }
		g2.drawString("Score: " + p1.getScore(), SCREEN_WIDTH/7, SCREEN_HEIGHT/2 + 50);
		g2.drawString("Score: " + p2.getScore(), (int)(3.75*(SCREEN_WIDTH/5)), SCREEN_HEIGHT/2 + 50);
		g2.drawString("Weapon Accuracy: %" + p1.weaponAccuracy(), SCREEN_WIDTH/7, SCREEN_HEIGHT/2 + 100);
		g2.drawString("Weapon Accuracy: %" + p2.weaponAccuracy(), (int)(3.75*(SCREEN_WIDTH/5)), SCREEN_HEIGHT/2 + 100);
	
		try {
       for(int i=10; i>= 0; i--)
       {
    	   g2.setColor(Color.white);
    	   g2.drawString("Restarting in: " + i, SCREEN_WIDTH/2-100, SCREEN_HEIGHT/2 + 150);
			Thread.sleep(1000);
    	   g2.setColor(Color.black);
    	   g2.fillRect(SCREEN_WIDTH/2 + 110, SCREEN_HEIGHT/2 + 100, 300, 75);
       }
       Thread.sleep(1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
       restart();
        
    }
    
    private void loadMap()
    {
		//load game Objects from text file map
    	Scanner s = null;
		s = new Scanner(getClass().getResourceAsStream("resources/map.txt"));
		
       for(int i = 0; i< 64; i++)
       {
    	   for(int j =0; j<64; j++)
    	   {
   				if(s.hasNext())
   				{
   					String x = s.next();
   					if(x.equals("1"))
   						gameObjs.add(new Wall(wall, (j*32), (i*32)));
   					if(x.equals("2"))
   						gameObjs.add(new DestWall(dWall, (j*32), (i*32), 3));
   					if(x.equals("="))
   						gameObjs.add(new DestWall(house, (j*32), (i*32) ,10));
   					if(x.equals("$"))
   						hearts.add(new Health(lives[4], (j*32), (i*32)));
   					if(x.equals("!"))
   						boosts.add(new Boost(boostImg, (j*32), (i*32)));
   					if(x.equals("@"))
   						p1.setSpawn(j*32, i*32);
   					if(x.equals("#"))
   						p2.setSpawn(j*32, i*32);
   				}
    	   }
       }
       s.close();
    }
       
    
    public void addExplosion(int x, int y, int size)
    {    
			explosions.add(new Explosion(explosion[0], x, y, explosion));
			//play explosion sound clip
			SoundPlayer expl;
			if(size == 1)
				expl = new SoundPlayer(2,smallExpl);
			else
				expl = new SoundPlayer(2,bigExpl);
			expl.play();
    }
    
    
    private static void startMenu()
    {
    	JButton b, b1, b2, b3; 
    	b = new JButton("button1"); 
        b1 = new JButton("button2"); 
        b2 = new JButton("button3"); 
        b3 = new JButton("button4");
    	jf.add(b, BorderLayout.NORTH); 
        jf.add(b1, BorderLayout.SOUTH); 
        jf.add(b2, BorderLayout.EAST); 
        jf.add(b3, BorderLayout.WEST); 
    	jf.setBackground(Color.black);
    	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jf.remove(b);
		jf.remove(b1);
		jf.remove(b2);
		jf.remove(b3);
		jf.dispose();
    }
}
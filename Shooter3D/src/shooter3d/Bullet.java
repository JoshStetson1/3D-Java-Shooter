package shooter3d;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Bullet extends Object{
    public double velX, velY;
    
    public int damage;
    
    //used to make a line from the bullets past position to its position for collisions
    double[] pastPos;
    
    public Bullet(Screen s, BufferedImage sprite, double x, double y){
        super(s, x, y, sprite);
        
        s.l.bullets.add(this);
    }
    @Override
    public void update(){
        tick();
    }
    public void tick(){
        x += velX * s.deltaTime;
        y += velY * s.deltaTime;
        
        collision();
    }
    public void collision(){
        //bullet hits a wall
        double velocity = Math.hypot(velX, velY);
        if(Mathf.intersectArray(x, y, x+velX, y+velY, s.lm.blocks, s.lm.cellSize, velocity/2)){
            s.l.removeObj(this);
            return;
        }
        
        //check enemy collisions
        for(int i = 0; i < s.l.enemys.size(); i++){
            Enemy enemy = s.l.enemys.get(i);
            
            if(bounds().intersects(enemy.bounds())){//bullet hits enemy
                s.l.removeObj(this);
                
                //kickback
                double hypo = Mathf.dist(0, 0, velX, velY);
                double normalX = velX/hypo;
                double normalY = velY/hypo;
                
                enemy.velX = normalX * (damage/15);
                enemy.velY = normalY * (damage/15);
                
                enemy.takeDamage(damage);
                return;
            }
        }
    }
    public Line2D bounds(){
        return new Line2D.Double((int)(x-velX), (int)(y-velY), (int)(x+velX), (int)(y+velY));
    }
}
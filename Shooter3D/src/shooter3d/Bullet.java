package shooter3d;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Bullet extends Object{
    Screen s;
    
    public double velX, velY;
    
    public int damage;
    
    //who shot gun
    String holder;
    
    //used to make a line from the bullets past position to its position for collisions
    double[] pastPos;
    
    public Bullet(Screen s, BufferedImage sprite, String holder, double x, double y){
        super(s, x, y, sprite);
        
        this.s = s;
        
        this.holder = holder;
        
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
        
        if(holder.equals("player")){
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
        else if (holder.equals("enemy")){
            if(bounds().intersects(s.p.bounds())){//bullet hits player
                s.l.removeObj(this);

                //kickback
                double hypo = Mathf.dist(0, 0, velX, velY);
                double normalX = velX/hypo;
                double normalY = velY/hypo;

                s.p.velX = normalX * (damage/15);
                s.p.velY = normalY * (damage/15);

                s.p.takeDamage(damage);
                return;
            }
        }
    }
    public Line2D bounds(){
        return new Line2D.Double((int)(x-velX), (int)(y-velY), (int)(x+velX), (int)(y+velY));
    }
}
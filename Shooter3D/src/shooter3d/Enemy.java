package shooter3d;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

//base enemy class, plan to add more enemys
public class Enemy extends Object{
    int health = 100;
    
    public double velX, velY, maxMoveSpeed;
    public double acc = 0.5, dcc = 0.25;
    
    public int size = 50;
    
    public Enemy(Screen s, int x, int y, BufferedImage sprite){
        super(s, x, y, sprite);
        this.x = x;
        this.y = y;
        
        s.l.enemys.add(this);
    }
    
    public void Move(double xDir,double yDir){//1, -1, or 0 values
        //add given force
        velX += acc * xDir;
        velY += acc * yDir;
        
        //max out speed/ multiply normalized velocity by maxSpeed as to maintain current direction
        if(Mathf.dist(0, 0, velX, velY) > maxMoveSpeed){
            double velocity = Mathf.dist(0, 0, velX, velY);
            double normalX = velX/velocity;
            double normalY = velY/velocity;
            
            velX = normalX * maxMoveSpeed;
            velY = normalY * maxMoveSpeed;
        }
    }
    
    public void collisions(){
        int cellSize = s.lm.cellSize;
        double offset = maxMoveSpeed * s.deltaTime;
        
        //two points for each side
        double[][] upPoints = {{(x - size/2) + offset, y - (size/2)}, {(x + size/2) - offset, y - (size/2)}};
        double[][] downPoints = {{(x - size/2) + offset, y + (size/2)}, {(x + size/2) - offset, y + (size/2)}};
        double[][] leftPoints = {{x - (size/2), y - (size/2) + offset}, {x - (size/2), y + (size/2) - offset}};
        double[][] rightPoints = {{x + (size/2), y - (size/2) + offset}, {x + (size/2), y + (size/2) - offset}};
        
        //clamp values to make sure points lie within level bounds
        upPoints[0][0] = Mathf.Clamp(upPoints[0][0], 0, s.lm.blocks[0].length * cellSize); upPoints[0][1] = Mathf.Clamp(upPoints[0][1], 0, s.lm.blocks.length * cellSize);
        upPoints[1][0] = Mathf.Clamp(upPoints[1][0], 0, s.lm.blocks[0].length * cellSize); upPoints[1][1] = Mathf.Clamp(upPoints[1][1], 0, s.lm.blocks.length * cellSize);
        
        downPoints[0][0] = Mathf.Clamp(downPoints[0][0], 0, s.lm.blocks[0].length * cellSize); downPoints[0][1] = Mathf.Clamp(downPoints[0][1], 0, s.lm.blocks.length * cellSize);
        downPoints[1][0] = Mathf.Clamp(downPoints[1][0], 0, s.lm.blocks[0].length * cellSize); downPoints[1][1] = Mathf.Clamp(downPoints[1][1], 0, s.lm.blocks.length * cellSize);
        
        leftPoints[0][0] = Mathf.Clamp(leftPoints[0][0], 0, s.lm.blocks[0].length * cellSize); leftPoints[0][1] = Mathf.Clamp(leftPoints[0][1], 0, s.lm.blocks.length * cellSize);
        leftPoints[1][0] = Mathf.Clamp(leftPoints[1][0], 0, s.lm.blocks[0].length * cellSize); leftPoints[1][1] = Mathf.Clamp(leftPoints[1][1], 0, s.lm.blocks.length * cellSize);
        
        rightPoints[0][0] = Mathf.Clamp(rightPoints[0][0], 0, s.lm.blocks[0].length * cellSize); rightPoints[0][1] = Mathf.Clamp(rightPoints[0][1], 0, s.lm.blocks.length * cellSize);
        rightPoints[1][0] = Mathf.Clamp(rightPoints[1][0], 0, s.lm.blocks[0].length * cellSize); rightPoints[1][1] = Mathf.Clamp(rightPoints[1][1], 0, s.lm.blocks.length * cellSize);
        
        //check where points intersect on level array, checking for 1's (a block)
        if(blockType(upPoints[0]) == 1 || blockType(upPoints[1]) == 1){//up
            velY = 0;
            y = (int)(upPoints[0][1]/cellSize)*cellSize + cellSize + (size/2);
        }
        if(blockType(downPoints[0]) == 1 || blockType(downPoints[1]) == 1){//down
            velY = 0;
            y = (int)(downPoints[0][1]/cellSize)*cellSize - (size/2)-1;
        }
        if(blockType(leftPoints[0]) == 1 || blockType(leftPoints[1]) == 1){//left
            velX = 0;
            x = (int)(leftPoints[0][0]/cellSize)*cellSize + cellSize + (size/2);
        }
        if(blockType(rightPoints[0]) == 1 || blockType(rightPoints[1]) == 1){//right
            velX = 0;
            x = (int)(rightPoints[0][0]/cellSize)*cellSize - (size/2)-1;
        }
        
        //clamp end result player position so it lies within level bounds
        x = Mathf.Clamp(x, cellSize, s.lm.blocks[0].length*cellSize - cellSize);
        y = Mathf.Clamp(y, cellSize, s.lm.blocks.length*cellSize - cellSize);
    }
    public int blockType(double[] point){
        int cellSize = s.lm.cellSize;
        int xPoint = (int)(point[0]/cellSize);
        int yPoint = (int)(point[1]/cellSize);
        
        return s.lm.blocks[yPoint][xPoint];
    }
    public boolean canSee(){
        //enemy is in view
        if(Mathf.dist(x, y, s.p.x, s.p.y) > s.p.rw.renderDist) return false;
        
        //intersection between player and enemy
        return !Mathf.intersectArray(x+(size/2), y+(size/2), s.p.x+(s.p.size/2), s.p.y+(s.p.size/2), s.lm.blocks, s.lm.cellSize, s.lm.cellSize/2);
    }
    
    public void takeDamage(int damage){
        health -= damage;
        
        if(health <= 0) s.l.removeObj(this);
    }
    
    public Rectangle bounds(){
        return new Rectangle((int)x - size/2, (int)y - size/2, size, size);
    }
}

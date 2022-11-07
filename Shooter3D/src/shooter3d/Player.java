package shooter3d;
import java.awt.*;

public class Player {
    Screen s;
    
    //player positions
    double x, y, velX, velY;
    double ang, dx, dy;//players angle and directions of player
    
    int size = 50;
    
    //movement variables
    double maxMoveSpeed = 8;
    double acc = 0.25, dcc = 0.25;//acceleration/ decceleration
    double stopThreshold = 0.2;
    double rotationSpeed = 0.05;
    
    //wsad as booleans
    boolean[] moving = new boolean[4];
    
    //mouse variables
    double sensitivity = 0.15;
    boolean shooting;
    
    //health and death
    boolean isDead, hasWon;
    int health = 100;
    
    //weapon for player
    Gun gun;
    
    //world renderer for player
    RenderWorld rw;
    
    public Player(Screen s){
        this.s = s;
        
        rw = new RenderWorld(s, this);
        
        //default gun
        gun = new Gun(s, Gun.shotGun, "player", false, 5, 100, 10, 0.1, 5, 25, SpriteManager.bullet);
    }
    public void tick(){
        //if dead can't move
        if(isDead || hasWon) return;
        
        rotateHead();
        movement();
        collisions();
        
        x += velX * s.deltaTime;
        y += velY * s.deltaTime;
        
        if(shooting) gun.shoot(ang, x, y);
    }
    
    public void rotateHead(){
        //only lock mouse if window is active
        Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        if(activeWindow != s.frame) s.lockMouse = false;
        
        //check if getLocationOnScreen works because can be glitchy when first opening window
        try{int centerX = s.getLocationOnScreen().x + s.getWidth()/2;}
        catch(Exception e){ return;}
        
        if(s.lockMouse){
            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            
            int centerX = s.getLocationOnScreen().x + s.getWidth()/2;
            if(b.x != centerX){
                //add the offset of the mouse to the center of the screen to players angle
                double xOffset = b.x - centerX;
                ang = Mathf.limitFor(ang + (xOffset * (sensitivity / 100)));
                dx = Math.cos(ang);
                dy = Math.sin(ang);
                
                //reset mouse position to center of screen
                s.robo.mouseMove(centerX, s.getLocationOnScreen().y + s.getHeight()/2);
            }
        }
    }
    public void movement(){
        //speed of player, hypo of velocities
        double speed = Mathf.dist(0, 0, velX, velY);
        double[] normalVel = Mathf.normalize(velX, velY);//normalized velocity
        
        //movement
        if(moving[2]){//move forwards
            velX += dx * acc * s.deltaTime;
            velY += dy * acc * s.deltaTime;
        }
        if(moving[3]){//move backwards
            velX -= dx * acc * s.deltaTime;
            velY -= dy * acc * s.deltaTime;
        }
        if(moving[0]){//move left
            velX -= Math.cos(ang+(Math.PI/2)) * acc * s.deltaTime;
            velY += Math.sin(ang-(Math.PI/2)) * acc * s.deltaTime;
        }
        if(moving[1]){//move right
            velX += Math.cos(ang+(Math.PI/2)) * acc * s.deltaTime;
            velY -= Math.sin(ang-(Math.PI/2)) * acc * s.deltaTime;
        }
        
        //max out speed/ multiply normalized velocity by maxSpeed as to maintain current direction
        if(speed > maxMoveSpeed){
            velX = normalVel[0] * maxMoveSpeed;
            velY = normalVel[1] * maxMoveSpeed;
        }
        
        //drag
        if(!moving[0] && !moving[1] && !moving[2] && !moving[3])//no buttons are being pressed
        {
            //speed fits a stop threshold so no jitter when velocity approaches zero
            if(Mathf.dist(0, 0, velX, velY) > stopThreshold){
                //add deceleration based on current velocity
                velX -= normalVel[0] * dcc * s.deltaTime;
                velY -= normalVel[1] * dcc * s.deltaTime;
            }
            else{
                velX = 0;
                velY = 0;
            }
        }
    }
    //array based collisions
    public void collisions(){
        int cellSize = s.lm.cellSize;
        double offset = maxMoveSpeed * s.deltaTime;
        
        //two points for each side of player
        double[][] upPoints = {{(x - size/2) + offset, y - (size/2)}, {(x + size/2) - offset, y - (size/2)}};
        double[][] downPoints = {{(x - size/2) + offset, y + (size/2)}, {(x + size/2) - offset, y + (size/2)}};
        double[][] leftPoints = {{x - (size/2), y - (size/2) + offset}, {x - (size/2), y + (size/2) - offset}};
        double[][] rightPoints = {{x + (size/2), y - (size/2) + offset}, {x + (size/2), y + (size/2) - offset}};
        
        //clamp values to make sure points lie within level bounds
        upPoints[0][0] = Mathf.Clamp(upPoints[0][0], 1, (s.lm.blocks[0].length-1) * cellSize); upPoints[0][1] = Mathf.Clamp(upPoints[0][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        upPoints[1][0] = Mathf.Clamp(upPoints[1][0], 1, (s.lm.blocks[0].length-1) * cellSize); upPoints[1][1] = Mathf.Clamp(upPoints[1][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        
        downPoints[0][0] = Mathf.Clamp(downPoints[0][0], 1, (s.lm.blocks[0].length-1) * cellSize); downPoints[0][1] = Mathf.Clamp(downPoints[0][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        downPoints[1][0] = Mathf.Clamp(downPoints[1][0], 1, (s.lm.blocks[0].length-1) * cellSize); downPoints[1][1] = Mathf.Clamp(downPoints[1][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        
        leftPoints[0][0] = Mathf.Clamp(leftPoints[0][0], 1, (s.lm.blocks[0].length-1) * cellSize); leftPoints[0][1] = Mathf.Clamp(leftPoints[0][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        leftPoints[1][0] = Mathf.Clamp(leftPoints[1][0], 1, (s.lm.blocks[0].length-1) * cellSize); leftPoints[1][1] = Mathf.Clamp(leftPoints[1][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        
        rightPoints[0][0] = Mathf.Clamp(rightPoints[0][0], 1, (s.lm.blocks[0].length-1) * cellSize); rightPoints[0][1] = Mathf.Clamp(rightPoints[0][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        rightPoints[1][0] = Mathf.Clamp(rightPoints[1][0], 1, (s.lm.blocks[0].length-1) * cellSize); rightPoints[1][1] = Mathf.Clamp(rightPoints[1][1], 1, (s.lm.blocks[0].length-1) * cellSize);
        
        //check if points position in level array intersect a 1 (a block)
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
        
        //make sure x and y coordinates still lie within the level
        x = Mathf.Clamp(x, cellSize, s.lm.blocks[0].length*cellSize - cellSize);
        y = Mathf.Clamp(y, cellSize, s.lm.blocks.length*cellSize - cellSize);
    }
    public int blockType(double[] point){
        //get point in level array
        int cellSize = s.lm.cellSize;
        int xPoint = (int)(point[0]/cellSize);
        int yPoint = (int)(point[1]/cellSize);
        
        return s.lm.blocks[yPoint][xPoint];
    }
    
    public void takeDamage(int damage){
        health -= damage;
        if(health <= 0) isDead = true;
    }
    
    public Rectangle bounds(){
        return new Rectangle((int)x - size/2, (int)y - size/2, size, size);
    }
    
    //shooting and UI
    public void mouseClicked(){
        if(isDead || hasWon){
            rw.mouseClicked(s.mousePos()[0], s.mousePos()[1]);
        }
        else{
            //shooting
            if(gun.isAuto) shooting = true;
            else gun.shoot(ang, x, y);
            
            //mouse
            s.lockMouse = true;
            s.hideCursor(true);
        }
    }
}
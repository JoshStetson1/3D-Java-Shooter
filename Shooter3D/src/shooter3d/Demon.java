package shooter3d;

public class Demon extends Enemy{
    boolean attack = false;
    
    Gun gun;
    
    public Demon(Screen s, int x, int y){
        super(s, x, y, SpriteManager.demon);
        
        //set physics value to main enemy class
        super.maxMoveSpeed = 5;
        super.acc = 0.5;
        super.dcc = 0.5;
        
        super.range = 1000;
        
        gun = new Gun(s, Gun.gunShot, "enemy", false, 1, 45, 0, 1, 2, 10, SpriteManager.bullet2);
    }
    @Override
    public void update(){
        tick();
    }
    public void tick(){
        //once player is seen, attack and continue attacking
        if(canSee()) attack = true;
        
        if(attack){
            if(canSee()) Shoot();
            else Movement();
        }
        
        super.collisions();
    }
    
    public void Movement(){
        //normalized x and y direction to player
        double hypo = Mathf.dist(x, y, s.p.x, s.p.y);
        double xDir = (s.p.x - x)/hypo;
        double yDir = (s.p.y - y)/hypo;
        
        //move with this direction
        Move(xDir, yDir);
        
        //move
        x += velX * s.deltaTime;
        y += velY * s.deltaTime;
    }
    
    public void Shoot(){
        double angle = Math.atan2(s.p.y - y, s.p.x - x);
        
        gun.shoot(angle, x, y);
    }
}

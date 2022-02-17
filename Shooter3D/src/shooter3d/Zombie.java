package shooter3d;

public class Zombie extends Enemy{
    public boolean attack;
    public int damage = 20;
    int pushBack = 50;
    
    //enemy has to wait a certain amount of time before doing damage again
    double timeBetweenDamage = 0.5;
    long timeOfDamage;
    
    public Zombie(Screen s, int x, int y){
        super(s, x, y, SpriteManager.zombie);
        
        //set physics value to main enemy class
        super.maxMoveSpeed = 5;
        super.acc = 0.5;
        super.dcc = 0.5;
    }
    @Override
    public void update(){
        tick();
    }
    public void tick(){
        //once player is seen, attack and continue attacking
        if(canSee()) attack = true;
        if (!s.p.isDead && attack) Movement();
        
        collisions();
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
    
    @Override
    public void collisions(){
        //wall collisions
        super.collisions();
        
        //hit plauer
        if(bounds().intersects(s.p.bounds()))
        {
            //bounce back after hit
            double angleToPlayer = Math.atan2(s.p.y - y, s.p.x - x);
            velX = -Math.cos(angleToPlayer) * pushBack;
            velY = -Math.sin(angleToPlayer) * pushBack;
            
            //timeBetweenDamage seconds has past since last time damage to the player was dealt
            if(System.nanoTime() > timeOfDamage + (timeBetweenDamage * 1000000000)){
                //player take damage
                s.p.takeDamage(damage);

                //reset wait time
                timeOfDamage = System.nanoTime();
            }
        }
    }
}
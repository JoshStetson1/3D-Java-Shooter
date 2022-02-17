package shooter3d;
import java.io.File;
import java.util.*;

public class Gun {
    static File shotGun = new File("sounds\\shot.wav");
    static File gunShot = new File("sounds\\gunShot.wav");
    
    Screen s;
    
    File shotSound;
    
    public int amount, speed, damage;
    public double fireRate, recoil, spread;
    public boolean isAuto;
    
    Random rand = new Random();
    long nowTime, time;
    
    public Gun(Screen s, File shotSound, boolean isAuto, int amount, int speed, double spread, double fireRate, double recoil, int damage){
        this.s = s;
        
        this.shotSound = shotSound;
        
        this.isAuto = isAuto;
        this.amount = amount;
        this.speed = speed;
        this.spread = spread;
        this.fireRate = fireRate;
        this.recoil = recoil;
        this.damage = damage;
        
        nowTime = System.nanoTime();
    }
    public void shoot(double angle){
        if(!canShoot()) return;
        
        for(int i = 0; i < amount; i++){
            Bullet tempBullet = new Bullet(s, SpriteManager.bullet, s.p.x, s.p.y);
            
            //add to new bullet force with given spread/ accurecy
            double spreadX = (rand.nextDouble()-0.5) * spread;
            double spreadY = (rand.nextDouble()-0.5) * spread;
            tempBullet.velX = (speed * Math.cos(angle)) + spreadX;
            tempBullet.velY = (speed * Math.sin(angle)) + spreadY;
            tempBullet.damage = damage;

            s.l.objects.add(tempBullet);
        }
        //recoil
        s.p.velX -= Math.cos(angle) * recoil;
        s.p.velY -= Math.sin(angle) * recoil;
        
        s.PlaySound(shotSound);
        
        nowTime = System.nanoTime();
    }
    public boolean canShoot(){
        boolean canShoot = false;
        if(System.nanoTime() > nowTime + 1000000000*fireRate){
            canShoot = true;
        }
        
        return canShoot;
    }
}
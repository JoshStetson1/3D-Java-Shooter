package shooter3d;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Gun {
    static File shotGun = new File("sounds\\shotGun.wav");
    static File gunShot = new File("sounds\\_unShot.wav");
    static File sniper = new File("sounds\\sniper.wav");
    static File fireShot = new File("sounds\\enemyShot.wav");
    
    Screen s;
    
    File shotSound;
    
    public int amount, speed, damage;
    public double fireRate, recoil, spread;
    public boolean isAuto;
    
    Random rand = new Random();
    long nowTime, time;
    
    //whos holding the gun
    String holder;
    
    BufferedImage bulletImg;
    
    public Gun(Screen s, File shotSound, String holder, boolean isAuto, int amount, int speed, double spread, double fireRate, double recoil, int damage, BufferedImage bulletImg){
        this.s = s;
        
        this.shotSound = shotSound;
        
        this.holder = holder;
        
        this.isAuto = isAuto;
        this.amount = amount;
        this.speed = speed;
        this.spread = spread;
        this.fireRate = fireRate;
        this.recoil = recoil;
        this.damage = damage;
        
        this.bulletImg = bulletImg;
        
        nowTime = System.nanoTime();
    }
    public void shoot(double angle, double x, double y){
        if(!canShoot()) return;
        
        for(int i = 0; i < amount; i++){
            Bullet tempBullet = new Bullet(s, bulletImg, holder, x, y);
            
            //add to new bullet force with given spread/ accurecy
            double spreadX = (rand.nextDouble()-0.5) * spread;
            double spreadY = (rand.nextDouble()-0.5) * spread;
            tempBullet.velX = (speed * Math.cos(angle)) + spreadX;
            tempBullet.velY = (speed * Math.sin(angle)) + spreadY;
            tempBullet.damage = damage;

            s.l.objects.add(tempBullet);
        }
        //recoil
        if(holder.equals("player")){
            s.p.velX -= Math.cos(angle) * recoil;
            s.p.velY -= Math.sin(angle) * recoil;
        }
        
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
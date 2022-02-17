package shooter3d;
import java.awt.*;
import java.util.*;

//Lists that keep track of gameobjects
public class List {
    Screen s;
    
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public ArrayList<Enemy> enemys = new ArrayList<>();
    
    public ArrayList<Object> objects = new ArrayList<>();
    
    public List(Screen s){
        this.s = s;
    }
    public void update(){
        for(int i = 0; i < objects.size(); i++){
            Object obj = objects.get(i);
            obj.update();
        }
    }
    public void removeObj(Object obj){
        if(obj instanceof Enemy) enemys.remove((Enemy)obj);
        if(obj instanceof Bullet) bullets.remove((Bullet)obj);
        
        objects.remove(obj);
    }
    public void removeAll(){
        enemys.clear();
        bullets.clear();
        objects.clear();
    }
}
package shooter3d;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Object {
    Screen s;
    
    public double x, y;//object position
    public BufferedImage sprite;//object sprite
    
    public Object(Screen s, double x, double y, BufferedImage sprite){
        this.s = s;
        this.x = x;
        this.y = y;
        
        this.sprite = sprite;
    }
    public void update(){}
}

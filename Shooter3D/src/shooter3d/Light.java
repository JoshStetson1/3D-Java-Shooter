package shooter3d;
import java.awt.Color;

public class Light {
    Screen s;
    double x, y;
    
    Color color;
    double intensity;
    double reach;
    
    public Light(Screen s, double x, double y, Color color, double intensity, double reach){
        this.s = s;
        this.x = x;
        this.y = y;
        
        this.color = color;
        this.intensity = intensity;
        this.reach = reach;
    }
}

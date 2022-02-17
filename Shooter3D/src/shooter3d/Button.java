package shooter3d;
import java.awt.image.BufferedImage;
import java.awt.*;

public class Button {
    //shade when mouse is hovering over button
    static Color shade = new Color(200, 200, 200, 100);
    
    Screen s;
    
    BufferedImage sprite;//button image
    int x, y, width, height;//button position and bounds
    Color hovered;//color when hovered over
    
    public Button(Screen s, BufferedImage sprite, int x, int y, int width, int height, Color hovered){
        this.s = s;
        
        this.sprite = sprite;
        this.hovered = hovered;
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void paint(Graphics g){
        //mouse is hovering over button
        int[] mousePos = s.mousePos();
        if(mousePos[0] > x && mousePos[0] < x+width && mousePos[1] > y && mousePos[1] < y+height){
            g.setColor(hovered);
            g.fillRect(x, y, width, height);
        }
        
        //draw button image
        if(sprite != null) g.drawImage(sprite, x, y, width, height, s);
    }
    //mouse position lies within bounds of button
    public boolean checkHit(int mx, int my){
        return mx > x && mx < x+width && my > y && my < y+height;
    }
    
    public Rectangle bounds(){
        return new Rectangle(x, y, width, height);
    }
}


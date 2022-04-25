package shooter3d;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import static shooter3d.SpriteManager.*;

public class MainMenu {
    Screen s;
    
    int state = 0;
    
    int menu = 0;
    int select = 1;
    int settings = 2;
    
    //main menu
    BufferedImage menuBG = loadImage("paint\\menuBG.png");
    Button play, sett, quit;
    
    //play select
    BufferedImage selectBG = loadImage("paint\\selectBG.png");
    Button start, shot, auto, sniper, back;
    Rectangle selected = new Rectangle();
    
    //setting
    BufferedImage settingBG = loadImage("paint\\settingBG.png");
    
    public MainMenu(Screen s){
        this.s = s;
        
        //main menu button
        play = new Button(s, words[1], 10, 225, 126*2, 18*2, Button.shade);
        sett = new Button(s, words[2], 10, 285, 126*2, 18*2, Button.shade);
        quit = new Button(s, words[3], 10, 345, 126*2, 18*2, Button.shade);
        
        //weapon select buttons
        start = new Button(s, words[8], 600 - 262, 600 - 46, 126*2, 18*2, Button.shade);
        shot = new Button(s, words[5], 300 - 126, 225, 126*2, 18*2, Button.shade);
        auto = new Button(s, words[6], 300 - 126, 285, 126*2, 18*2, Button.shade);
        sniper = new Button(s, words[7], 300 - 126, 345, 126*2, 18*2, Button.shade);
        back = new Button(s, words[4], 10, 600 - 46, 126*2, 18*2, Button.shade);
        
        //set weopon selected
        selected = shot.bounds();
    }
    
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        
        if(state == menu){
            g.drawImage(menuBG, 0, 0, 600, 600, s);
        
            //shooter text
            g.drawImage(words[0], 111, 25, 126*3, 18*3, s);

            //buttons
            play.paint(g);
            sett.paint(g);
            quit.paint(g);
        }
        else if(state == select){
            g.drawImage(selectBG, 0, 0, 600, 600, s);
            
            //buttons
            start.paint(g);
            back.paint(g);
            
            shot.paint(g);
            auto.paint(g);
            sniper.paint(g);
            
            //draw square at selected weapon
            g.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g.drawRect(selected.x, selected.y, selected.width, selected.height);
        }
        else if(state == settings){
            g.drawImage(settingBG, 0, 0, 600, 600, s);
            
            //buttons
            back.paint(g);
        }
    }
    
    public void mouseClick(int x, int y){
        if(state == menu){
            if(play.checkHit(x, y)) state = select;
            if(sett.checkHit(x, y)) state = settings;
            if(quit.checkHit(x, y)) System.exit(0);
        }
        else if(state == select){
            if(back.checkHit(x, y)) mainMenu();
            
            //weapons
            if(shot.checkHit(x, y)){
                s.p.gun = new Gun(s, Gun.shotGun, "player", false, 5, 75, 10, 0.2, 5, 20, SpriteManager.bullet);//shotgun
                selected = shot.bounds();
            }
            if(auto.checkHit(x, y)){
                s.p.gun = new Gun(s, Gun.gunShot, "player", true, 1, 75, 2, 0.05, 1, 20, SpriteManager.bullet);//auto
                selected = auto.bounds();
            }
            if(sniper.checkHit(x, y)){
                s.p.gun = new Gun(s, Gun.gunShot, "player", false, 1, 75, 0, 0.1, 2, 35, SpriteManager.bullet);//pistol
                selected = sniper.bounds();
            }
            
            if(start.checkHit(x, y)) s.startGame();
        }
        else if(state == settings){
            if(back.checkHit(x, y)) mainMenu();
        }
    }
    
    public void mainMenu(){
        state = menu;
    }
}


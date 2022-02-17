package shooter3d;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public final class Screen extends JPanel implements ActionListener, KeyListener, MouseListener{
    JFrame frame;
    
    Timer t = new Timer(10, this);
    
    //game
    Player p = new Player(this);
    List l = new List(this);
    LevelManager lm = new LevelManager(this);
    MainMenu m = new MainMenu(this);
    
    //for setting mouse position
    Robot robo;
    
    //for fps
    int frames; int fps = 70;
    long nowTime, pastTime;
    
    //time between frames, used to keep movement constant for all computer speeds
    public double deltaTime;
    
    //the state the game is in, used to render either mainmenu or game
    public int state = 0;
    
    public int main = 0;
    public int game = 1;
    
    public Screen(JFrame frame) throws AWTException{
        this.frame = frame;
        
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        
        nowTime = System.nanoTime();
        
        t.start();
        
        robo = new Robot();
        
        //start audio system
        PlaySound(null);
    }
    
    //update
    public void actionPerformed(ActionEvent e) {
        if(state == game)
        {
            p.tick();
            l.update();
        }
        
        repaint();
    }
    public void FPS(Graphics g){
        frames++;
        if(System.nanoTime() > nowTime+1000000000){//one second has past
            fps = frames;
            frames = 0;
            nowTime = System.nanoTime();
        }
        
        //delta time
        double timePast = System.nanoTime() - pastTime;
        deltaTime = Mathf.Clamp(timePast/10000000, 0, 5);
        pastTime = System.nanoTime();
        
        //draw FPS to screen
        g.setColor(Color.white);
        g.setFont(new Font("arial", Font.BOLD, 20));
        g.drawString("FPS: " + Integer.toString(fps) + " " + l.enemys.size(), 0, 20);
    }
    
    //render
    public void paint(Graphics g){
        g.clearRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        //scaling for different screen size based on a 600 by 600 pixel screen
        g2.scale((double)getWidth()/600, (double)getHeight()/600);
        
        //paint
        if(state == main)
        {
            m.paint(g);
        }
        else if(state == game)
        {
            p.paint(g);
            //lm.paint(g);//2d testing
            lm.miniMap(g2, 475, 25, 100, 25);
        }
        
        FPS(g);
    }
    
    //---------Key Input---------
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_W) p.moving[2] = true;
        if(key == KeyEvent.VK_S) p.moving[3] = true;
        if(key == KeyEvent.VK_A) p.moving[0] = true;
        if(key == KeyEvent.VK_D) p.moving[1] = true;
        
        if(key == KeyEvent.VK_ESCAPE){
            p.lockMouse = false;
            hideCursor(false);
        }
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_W) p.moving[2] = false;
        if(key == KeyEvent.VK_S) p.moving[3] = false;
        if(key == KeyEvent.VK_A) p.moving[0] = false;
        if(key == KeyEvent.VK_D) p.moving[1] = false;
    }

    //----------Mouse Input----------
    public void mousePressed(MouseEvent e) {
        if(state == main)
        {
            m.mouseClick(mousePos()[0], mousePos()[1]);
        }
        else if(state == game)
        {
            p.mouseClicked(mousePos()[0], mousePos()[1]);
        }
    }
    public void mouseReleased(MouseEvent e) {
        if(state == game)
        {
            p.shooting = false;
        }
    }
    public int[] mousePos(){
        //check if getLocationOnScreen works because can be glitchy when first opening window
        try{int centerX = getLocationOnScreen().x + getWidth()/2;}
        catch(Exception e){ return new int[2];}
        
        PointerInfo a = MouseInfo.getPointerInfo();
        Point b = a.getLocation();
        
        //multiply the (mouse position - the JFrame location on screen) by (the inverse of the screen scale)
        int mx = (int)((b.x - getLocationOnScreen().x) * (600/(double)getWidth()));
        int my = (int)((b.y - getLocationOnScreen().y) * (600/(double)getHeight()));
        int[] point = {mx, my};
        
        return point;
    }
    public void hideCursor(boolean hide){
        if(hide){
            //set a transparent image as the mouse sprite
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
            frame.getContentPane().setCursor(blankCursor);
        }
        else frame.getContentPane().setCursor(Cursor.getDefaultCursor());
    }
    
    //----------Game Settings----------
    public void startGame(){
        l.removeAll();
        lm.makeLevel(100, 100);
        
        p.isDead = false;
        p.health = 100;
        
        hideCursor(true);
        state = game;
    }
    public void mainMenu(){
        state = main;
        m.mainMenu();
    }
    
    public void PlaySound(File sound){
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            clip.start();
        } catch(Exception e){}
    }
    
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
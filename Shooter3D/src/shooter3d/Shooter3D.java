package shooter3d;
import java.awt.*;
import javax.swing.JFrame;

public class Shooter3D {
    public static void main(String[] args) throws AWTException{
        JFrame f = new JFrame("Shooter3D");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(906, 935);
        f.setLayout(new GridLayout(1, 1, 0, 0));
        f.setLocationRelativeTo(null);
        //f.setResizable(false);
        Screen s = new Screen(f);
        f.add(s);
        f.setVisible(true);
    }
}

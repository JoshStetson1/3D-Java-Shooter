package shooter3d;
import java.awt.Color;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;

abstract class SpriteManager {
    //fonts
    static final BufferedImage wordsSheet = loadImage("paint\\words.png");
    static final BufferedImage[] words = {
        grabImage(wordsSheet, 1, 1, 126, 18),
        grabImage(wordsSheet, 1, 2, 126, 18),
        grabImage(wordsSheet, 1, 3, 126, 18),
        grabImage(wordsSheet, 1, 4, 126, 18),
        grabImage(wordsSheet, 1, 5, 126, 18),
        grabImage(wordsSheet, 1, 6, 126, 18),
        grabImage(wordsSheet, 1, 7, 126, 18),
        grabImage(wordsSheet, 1, 8, 126, 18),
        grabImage(wordsSheet, 1, 9, 126, 18),
        grabImage(wordsSheet, 1, 10, 126, 18),
        grabImage(wordsSheet, 1, 11, 126, 18),
        grabImage(wordsSheet, 1, 12, 126, 18)
    };
    
    //textures
    static final BufferedImage texSheet = loadImage("paint\\textures.jpg");
    static BufferedImage[] textures = {
        grabImage(texSheet, 1, 1, 128, 128),
        grabImage(texSheet, 2, 1, 128, 128),
        grabImage(texSheet, 3, 1, 128, 128),
        grabImage(texSheet, 4, 1, 128, 128),
        grabImage(texSheet, 1, 2, 128, 128),
        grabImage(texSheet, 2, 2, 128, 128)
    };
    
    //indexes for textures[]
    static int sandStone = 0;
    static int stone = 1;
    static int blueBrick = 2;
    static int wood = 3;
    static int grass = 4;
    static int dirt = 5;
    
    //objects
    static BufferedImage zombie = loadImage("paint\\lostSoul2.png");
    static BufferedImage demon = loadImage("paint\\lostSoul.png");
    static BufferedImage bullet = loadImage("paint\\bullet.png");
    static BufferedImage bullet2 = loadImage("paint\\bullet2.png");
    static BufferedImage tree = loadImage("paint\\tree.png");
    static BufferedImage barrel = loadImage("paint\\barrel.png");
    
    //loads an image
    public static BufferedImage loadImage(String path){
        BufferedImage tempImage = null;
        try {
            tempImage = ImageIO.read(new FileInputStream(path));
        } catch (IOException ex) {
            System.out.println("Could not load " + path);
        }
        return tempImage;
    }
    
    //grabs a sub image from a sprite sheet
    public static BufferedImage grabImage(BufferedImage image, int col, int row, int width, int height){
        BufferedImage img = image.getSubimage((col*width)-width, (row*height)-height, width, height);
        return img;
    }
    
    //grabs a section of a texture based on a hit point on a block
    public static BufferedImage getWallTex(BufferedImage tex, double sampleX, double fullWidth){
        //new image
        BufferedImage img;
        
        try{
            double imgX = (sampleX/fullWidth) * tex.getWidth();//getting the x point relative to the texture width
            double imgW = (1/fullWidth) * tex.getWidth();//getting width relative to image
            
            //sample width is at least one (can't sample less than a pixel)
            if(imgW < 1) imgW = 1;
            //sample x + the sample width is not more than the image being sampled
            if((imgX+imgW) > tex.getWidth()) imgW = tex.getWidth()-(int)imgX;
            
            img = tex.getSubimage((int)imgX, 0, (int)imgW, tex.getHeight());
        } catch(Exception e){
            //if failed just return a transparent image
            img = new BufferedImage(1, tex.getHeight(), BufferedImage.TRANSLUCENT);
        }
        
        return img;
    }
    
    //changes an image with a new Color and a number 0 - 1
    public static BufferedImage changeImage(BufferedImage img, Color change, double darken){
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        
        //new image to add darkened values to
        BufferedImage darkened = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        
        //go through all pixels in image
        for(int xx = 0; xx < imgWidth; xx++){
            for(int yy = 0; yy < imgHeight; yy++){
                int pixel = img.getRGB(xx, yy);
                int alpha = (pixel >> 31) & 0xff;
                if(alpha == 0) continue;
                
                //add darkened pixel to new image
                darkened.setRGB(xx, yy, changeColor(new Color(pixel), change, darken).getRGB());
            }
        }
        
        return darkened;
    }
    
    //makes a color look more like another color with a given intensity, used to create fog effect
    public static Color changeColor(Color color, Color change, double intensity){
        //calculates what mixing two transparent colors looks like and returns that color value
        //formula source: https://stackoverflow.com/questions/8743482/calculating-opacity-value-mathematically
        
        double[] c1 = {color.getRed(), color.getGreen(), color.getBlue()};//forground color
        double[] c2 = {change.getRed(), change.getGreen(), change.getBlue()};//background color
        
        double p1 = intensity;//opacity of forground color
        double p2 = (1 - intensity);//opacity of background color
        
        //calculate new color with given opacities
        double red = (p1*c1[0] + p2*c2[0] - p1*p2*c2[0]) / (p1+p2 - p1*p2);
        double green = (p1*c1[1] + p2*c2[1] - p1*p2*c2[1]) / (p1+p2 - p1*p2);
        double blue = (p1*c1[2] + p2*c2[2] - p1*p2*c2[2]) / (p1+p2 - p1*p2);
        
        //clamp values
        red = Mathf.Clamp(red, 0, 255);
        green = Mathf.Clamp(green, 0, 255);
        blue = Mathf.Clamp(blue, 0, 255);
        
        //return color with new rgb values
        return new Color((int)red, (int)green, (int)blue);
    }
}


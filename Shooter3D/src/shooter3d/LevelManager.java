package shooter3d;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import static shooter3d.SpriteManager.*;

public class LevelManager {
    Screen s;
    
    public int[][] blocks;//only has 1's for walls, used for collision
    
    //these 2d arrays contain the indexes of sprites in a textures array that can be found in the spritemanager
    public int[][] walls;//sprite info for walls
    public int[][] ground;//sprite info for ground
    public int[][] ceiling;//sprite info for ceiling
    
    int cellSize = 100;//size of each block in 2d array grid
    
    public LevelManager(Screen s){
        this.s = s;
    }
    
    public void makeLevel(int w, int h){
        //declare all map arrays
        blocks = new int[h][w];
        walls = new int[h][w];
        ground = new int[h][w];
        ceiling = new int[h][w];
        
        Random rand = new Random();
        
        //---------------box in---------------
        for(int i = 0; i < h; i++){//vertical
            blocks[i][0] = 1;
            blocks[i][w-1] = 1;
            
            walls[i][0] = blueBrick + 1;
            walls[i][w-1] = blueBrick + 1;
        }
        for(int i = 0; i < w; i++){//horizontal
            blocks[0][i] = 1;
            blocks[h-1][i] = 1;
            
            walls[0][i] = blueBrick + 1;
            walls[h-1][i] = blueBrick + 1;
        }
        //make ground grass
        for(int y = 0; y < ground.length; y++){
            for(int x = 0; x < ground[0].length; x++){
                ground[y][x] = grass;
            }
        }
        
        //---------------shelters---------------
        for(int j = 0; j < 5; j++){//j number of shelters
            while(true){
                //random position and size
                int posX = rand.nextInt(w-2)+1;
                int posY = rand.nextInt(h-2)+1;
                int width = rand.nextInt(5)+5;
                int height = rand.nextInt(5)+5;

                if(posX + width < w && posY + height < h){
                    int spaceX = rand.nextInt(width-1)+posX;
                    int spaceY = rand.nextInt(height-1)+posY;

                    //walls for shelter - add 1 for block collision and 4 for wood texture index
                    for(int i = posY; i <= posY + height; i++){//vertical
                        if(i == spaceY || i == spaceY + 1) continue;

                        blocks[i][posX] = 1;
                        blocks[i][posX+width] = 1;

                        walls[i][posX] = wood + 1;
                        walls[i][posX+width] = wood + 1;
                    }
                    for(int i = posX; i <= posX + width; i++){//horizontal
                        if(i == spaceX || i == spaceX + 1) continue;

                        blocks[posY][i] = 1;
                        blocks[posY + height][i] = 1;

                        walls[posY][i] = wood + 1;
                        walls[posY + height][i] = wood + 1;
                    }
                    
                    //floor and ceiling for shelter
                    for(int y = posY; y <= posY + height; y++){
                        for(int x = posX; x <= posX + width; x++){
                            ground[y][x] = dirt;
                            ceiling[y][x] = wood;
                        }
                    }
                }
                
                Light light = new Light(s, (posX + width/2) * cellSize, (posY + height/2) * cellSize, new Color(150, 100, 0), 0.7, 2000);
                s.l.lights.add(light);
                
                break;
            }
        }
        
        //---------------add random walls---------------
        int wallCount = rand.nextInt(20)+150;//int wallCount = rand.nextInt(20)+100;
        for(int i = 0; i < wallCount; i++){//number of walls
            //random position and size
            int posX = rand.nextInt(w-2)+1;
            int posY = rand.nextInt(h-2)+1;
            int blockCount = rand.nextInt(10)+5;
            
            for(int j = 0; j < blockCount; j++){//length of walls
                if(blocks[posY][posX] == 0 && ground[posY][posX] == grass){
                    blocks[posY][posX] = 1;
                    walls[posY][posX] = stone + 1;

                    int[] dir = {0, 0};
                    boolean dirFound = false;
                    for(int k = 0; k < 4; k++){//directions for wall to go
                        dir = randomDir(rand);
                        if((posX + dir[0] < 1 || posX + dir[0] > blocks[0].length-2) || (posY + dir[1] < 1 || posY + dir[1] > blocks.length-2));
                        else if(blocks[posY + dir[0]][posX + dir[1]] == 0 && ground[posY][posX] == grass){
                            dirFound = true;
                            break;
                        }
                    }
                    if(!dirFound){ dir[0] = 0; dir[1] = 0;}

                    posX = posX + dir[0];
                    posY = posY + dir[1];
                }
            }
        }
        
        //---------------add objects---------------
        int treeCount = rand.nextInt(10)+75;
        for(int i = 0; i < treeCount; i++){
            int[] pos = findSpawnPos(rand, w, h);
            s.l.objects.add(new Object(s, pos[0]*cellSize + cellSize/2, pos[1]*cellSize + cellSize/2, SpriteManager.tree));
            blocks[pos[1]][pos[0]] = 1;
        }
        int barrelCount = rand.nextInt(5)+15;
        for(int i = 0; i < barrelCount; i++){
            int[] pos = findSpawnPos(rand, w, h);
            s.l.objects.add(new Object(s, pos[0]*cellSize + cellSize/2, pos[1]*cellSize + cellSize/2, SpriteManager.barrel));
            blocks[pos[1]][pos[0]] = 1;
        }
        
        //---------------add enemies---------------
        int zombieCount = rand.nextInt(10)+5;
        for(int i = 0; i < zombieCount; i++){
            int posX = 0, posY = 0;
            while(true){//search for position without block
                posX = rand.nextInt(w-2)+1;
                posY = rand.nextInt(h-2)+1;
                if(Mathf.dist(s.p.x, s.p.y, posX*50, posY*50) < 250);
                else if(blocks[posY][posY] == 0) break;
            }
            //s.l.objects.add(new Zombie(s, posX*cellSize + cellSize/2, posY*cellSize + cellSize/2));
        }
        int demonCount = rand.nextInt(10)+5;
        for(int i = 0; i < demonCount; i++){
            int posX = 0, posY = 0;
            while(true){//search for position without block
                posX = rand.nextInt(w-2)+1;
                posY = rand.nextInt(h-2)+1;
                if(Mathf.dist(s.p.x, s.p.y, posX*50, posY*50) < 250);
                else if(blocks[posY][posY] == 0) break;
            }
            //s.l.objects.add(new Demon(s, posX*cellSize + cellSize/2, posY*cellSize + cellSize/2));
        }
        
        //---------------spawn player---------------
        int[] playerPos = findSpawnPos(rand, w, h);
        s.p.x = playerPos[0]*cellSize + cellSize/2;
        s.p.y = playerPos[1]*cellSize + cellSize/2;
        
        s.p.x = 200;
        s.p.y = 200;
    }
    
    public int[] findSpawnPos(Random rand, int w, int h){
        int posX = 0, posY = 0;
        while(true){//search for position without block
            posX = rand.nextInt(w-2)+1;
            posY = rand.nextInt(h-2)+1;
            if(blocks[posY][posY] == 0 && ground[posY][posX] == grass) break;
        }
        int[] pos = {posX, posY};
        
        return pos;
    }
    public int[] randomDir(Random rand){//returns direction vector
        int[] dir = {0, 0};
        
        int dirNum = rand.nextInt(4);
        if(dirNum == 0) dir[0] = -1;
        else if(dirNum == 1) dir[0] = 1;
        else if(dirNum == 2) dir[1] = -1;
        else if(dirNum == 3) dir[1] = 1;
        
        return dir;
    }
    
    //for 2d testing
    public void paint(Graphics g){
        int h = blocks.length;
        int w = blocks[0].length;
        int blockLength = 10;
        
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                if(blocks[y][x] == 1){
                    g.setColor(Color.GRAY);
                    g.fillRect(x*blockLength, y*blockLength, blockLength, blockLength);

                    g.setColor(Color.black);
                    g.drawRect(x*blockLength, y*blockLength, blockLength, blockLength);
                }
            }
        }
    }
    
    public void miniMap(Graphics2D g, int x, int y, int size, int dist){
        BufferedImage map = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);//image used to make map
        int center = size/2;//center of image
        
        //scale from image size to level size to see where pixel lies on level
        double levelScale = dist*cellSize / size;
        
        //size of each block based on how many we can fit in the size of the minimap judging by the veiw distance
        int blockSize = size/dist;
        
        //go through all pixels in image
        for(int xx = 0; xx < size; xx++){
            for(int yy = 0; yy < size; yy++){
                //distance from center to point is less than its radius
                if(Mathf.dist(size/2, size/2, xx, yy) < size/2)
                {
                    //x and y difference from the center of image scaled to level size
                    int xDiff = (int)((xx - center) * levelScale);
                    int yDiff = (int)((yy - center) * levelScale);
                    
                    //point on level array relative to a centered player veiw
                    int mapX = (int)((s.p.x + xDiff) / cellSize);
                    int mapY = (int)((s.p.y + yDiff) / cellSize);
                    
                    //clamp values so it lies within bounds of level array
                    mapX = (int)Mathf.Clamp(mapX, 0, blocks[0].length-1);
                    mapY = (int)Mathf.Clamp(mapY, 0, blocks.length-1);
                    
                    //each type of block has its own color
                    Color blockColor = new Color(0, 100, 0);
                    if(walls[mapY][mapX] == 2) blockColor = Color.gray;
                    else if(walls[mapY][mapX] == 3) blockColor = new Color(30, 30, 40);
                    else if(walls[mapY][mapX] == 4) blockColor = new Color(100, 50, 0);
                    else if(blocks[mapY][mapX] == 1) blockColor = new Color(0, 50, 0);
                    else if(ground[mapY][mapX] == 5) blockColor = new Color(150, 100, 0);
                    
                    //set color on mini map
                    map.setRGB(xx, yy, blockColor.getRGB());
                }
            }
        }
        
        //rotate relative to player
        g.rotate(-Mathf.limitFor(s.p.ang + Math.PI/2), x + size/2, y + size/2);
        
        //map
        g.drawImage(map, x, y, s);
        
        //draw visible enemys to map
        for(int i = 0; i < s.l.enemys.size(); i++){
            Enemy enemy = s.l.enemys.get(i);
            double ex = enemy.x;
            double ey = enemy.y;
            
            /*
            if(Mathf.dist(s.p.x, s.p.y, ex, ey)/cellSize > dist/2){
                ex = s.p.x + Mathf.normalize(enemy.x - s.p.x, enemy.y - s.p.y)[0] * ((dist/2) * cellSize);
                ey = s.p.y + Mathf.normalize(enemy.x - s.p.x, enemy.y - s.p.y)[1] * ((dist/2) * cellSize);
            }
            */
            if(Mathf.dist(s.p.x, s.p.y, ex, ey)/cellSize < dist/2){
                double imageScale = (double)size / ((double)dist*cellSize);

                //x and y difference from the center of image scaled to level size
                int xDiff = (int)((ex - s.p.x) * imageScale);
                int yDiff = (int)((ey - s.p.y) * imageScale);

                int posX = (int)Mathf.Clamp(center + xDiff, 0, size-1);
                int posY = (int)Mathf.Clamp(center + yDiff, 0, size-1);

                g.setColor(Color.red);
                g.fill(new Ellipse2D.Double(x + posX - blockSize/2 , y + posY - blockSize/2, blockSize, blockSize));
            }
        }
        
        //outline
        g.setColor(Color.lightGray);
        g.setStroke(new BasicStroke(5));
        g.draw(new Ellipse2D.Double(x, y, size, size));
        
        //cancel out rotation to continue drawing normally
        g.rotate(Mathf.limitFor(s.p.ang + Math.PI/2), x + size/2, y + size/2);
        
        //player in center of image
        Ellipse2D.Double player = new Ellipse2D.Double(x + center - blockSize/2, y + center - blockSize/2, blockSize, blockSize);
        g.setColor(Color.white);
        g.fill(player);
    }
    
}

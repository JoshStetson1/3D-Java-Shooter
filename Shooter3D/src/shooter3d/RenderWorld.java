package shooter3d;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import static shooter3d.SpriteManager.*;

public class RenderWorld {
    Screen s;
    Player p;
    
    int res = 2;//resolution
    int gRes = 2;//ground resolution
    int renderDist = 1500;//1500
    
    Color fog = new Color(0, 0, 0);//0, 0, 12
    
    public RenderWorld(Screen s, Player p){
        this.s = s;
        this.p = p;
    }
    
    public void drawWorld(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        
        //backGround
        g.setColor(fog);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        //wall info and cellSize
        int[][] wallTypes = s.lm.walls;
        int cellSize = s.lm.cellSize;
        
        double ra = Mathf.limitFor(p.ang-Mathf.dr*30);//starting angle
        double[] wallDists = new double[600/res];//wall distances, used when drawing objects to find what objects can be seen
        
        for(int r = 0; r < 600/res; r++){//making rays
            Raycast ray = new Raycast();
            ray.castRay(p.x, p.y, ra, wallTypes, cellSize);
            
            //fixing fish eye
            double ca = Mathf.limitFor(p.ang-ra);
            ray.rayDist *= Math.cos(ca);
            
            //finding height of ray and drawing it
            double lineH = (cellSize*getHeight())/ray.rayDist;
            double lineY = (getHeight()/2)-(lineH/2);
            
            //-----draw ground-----
            if(r % gRes == 0) drawGround(g2, r, ra, lineY, lineH);
            
            //-----draw walls-----
            //limits to make sure ray end point lies within level array
            boolean limitX = (ray.rayX > 0 || ray.rayX < wallTypes[0].length*cellSize);
            boolean limitY = (ray.rayY > 0 || ray.rayY < wallTypes.length*cellSize);
            
            //only render walls if they are close enough
            if(ray.rayDist < renderDist && limitX && limitY)
            {
                //find sample coordinate for texture using which side the ray hit
                int sampleX;
                if(ray.hitHorz) sampleX = (int)ray.rayX - ((int)(ray.rayX/cellSize) * cellSize);
                else sampleX = (int)ray.rayY - ((int)(ray.rayY/cellSize) * cellSize);
                
                //darkening image to give fog effect
                float darken = (float)(1 - Mathf.Clamp(ray.rayDist/renderDist, 0, 1 ));
                float[] light = {darken, darken, darken};
                float[] color = {fog.getRed() * (1 - darken), fog.getGreen() * (1 - darken), fog.getBlue() * (1 - darken)};
                RescaleOp op = new RescaleOp(light, color, null);
                
                //draw wall texture using info on the wallTypes array
                int texIndex = wallTypes[(int)(ray.rayY/cellSize)][(int)(ray.rayX/cellSize)]-1;
                BufferedImage wallImg = getWallTex(textures[texIndex], sampleX, cellSize);
                wallImg = op.filter(wallImg, null);
                
                g.drawImage(wallImg, r*res, (int)lineY, res, (int)lineH, s);
                //g.fillRect(r*res, (int)lineY, res, (int)lineH);
            }
            
            //add wall distance
            wallDists[r] = ray.rayDist/Math.cos(ca);
            ra = Mathf.limitFor(ra + (res * Mathf.dr/10));//add another degree
        }
        //-----draw objects-----
        drawObjects(g2, wallDists);
    }
    public void drawObjects(Graphics2D g2, double[] wallDists){
        HashMap<Double, Object> objToRender = new HashMap<>();
        ArrayList<Double> distList = new ArrayList<>();
        
        //add all objects visible to the player
        for(int i = 0; i < s.l.objects.size(); i++){
            Object obj = s.l.objects.get(i);
            double angToPlayer = Math.atan2((obj.y - p.y), (obj.x - p.x));
            double distToPlayer = Mathf.dist(p.x, p.y, obj.x, obj.y);
            
            //is in players feild of veiw and is not too far away
            if(Math.abs(Mathf.diff(p.ang, angToPlayer)) < Mathf.dr*50 && distToPlayer < renderDist && distToPlayer > s.lm.cellSize/2){
                objToRender.put(distToPlayer, obj);
                distList.add(distToPlayer);
            }
        }
        //sort objects to render from back to front so no overlaping images
        Collections.sort(distList);
        
        //draw objects from closest to furthest using sorted distances
        for(int i = distList.size()-1; i >= 0; i--){
            double dist = distList.get(i);
            Object obj = objToRender.get(dist);
            
            //object sprite width and height
            double sWidth = obj.sprite.getWidth();
            double sHeight = obj.sprite.getHeight();
            
            //angle and distance to player
            double angToPlayer = Math.atan2((obj.y - p.y), (obj.x - p.x));
            double distToPlayer = Mathf.dist(p.x, p.y, obj.x, obj.y);

            //the angle of the players angle to the objects angle
            double ang = Mathf.limitFor(-(p.ang-angToPlayer));
            
            //size of object relative to player using its distance
            double yDis = Math.cos(ang)*distToPlayer;
            double objSizeY = (s.lm.cellSize*getWidth())/yDis;
            double sizeScale = (sWidth/sHeight) * s.lm.cellSize;
            double objSizeX = (sizeScale*getWidth())/yDis;
            double objY = getHeight()/2 - objSizeY/2;
            
            //x difference to object relative to player
            double xDis = Math.abs(Mathf.diff(p.ang, angToPlayer)) * distToPlayer;
            if(Math.sin(ang)*distToPlayer < 0) xDis *= -1;
            
            //convert into screen space to render
            double screenSpace = (60*Mathf.dr) * distToPlayer;
            double scale = getWidth()/screenSpace;
            double objX = getWidth()/2 + (getWidth() - (screenSpace-xDis) * scale) - (objSizeX/2);

            //darkness of object, used for fog
            double darken = 1 - Mathf.Clamp(distToPlayer/renderDist, 0, 1);
            
            //go through all colums of scaled sprite, as to only render what parts of the sprite can be seen
            for(int xx = 0; xx < objSizeX; xx++){
                //location on screen
                int location = ((int)objX + xx)/res;
                
                //location is on screen
                if(location < wallDists.length && location > 0){
                    //if the sprite height is closer than the drawn wall to the screen
                    if(wallDists[location] > distToPlayer){
                        //draw darkened section of sprite
                        BufferedImage renderImg = changeImage(getWallTex(obj.sprite, xx, (int)objSizeX), fog, darken);
                        g2.drawImage(renderImg, (int)objX + xx, (int)objY, res, (int)objSizeY, s);
                        
                        //set the old wall distance to sprite distance, so no future drawn sprite is rendered on top
                        wallDists[location] = distToPlayer;
                    }
                }
            }
        }
    }
    public void drawGround(Graphics2D g, int r, double ra, double lineY, double lineH){
        for(int yy = (int)getHeight()/2; yy < getHeight(); yy+=gRes){
            double mid = yy - getHeight()/2;
            double raFix = Math.cos(s.p.ang-ra);
            
            //sometimes the ground coordinates will go outside the level array, its easier just to put it in a try statement
            try{
                double tx = s.p.x/2 + Math.cos(ra)*(145)*100/mid/raFix;
                double ty = s.p.y/2 + Math.sin(ra)*(145)*100/mid/raFix;
                double dark = 1 - Mathf.dist(s.p.x, s.p.y, tx*2, ty*2)/renderDist;
                
                //if outside of render distance don't draw
                if(dark <= 0) continue;
                
                //----------Ground----------
                double gx = tx;
                double gy = ty;

                int gIndex = s.lm.ground[(int)(ty/50)][(int)(tx/50)];
                BufferedImage gTexture = textures[gIndex];
                int gTexSize = gTexture.getHeight();

                //break into blocks
                gx = (int)(gx)%50;
                gy = (int)(gy)%50;

                //find location on the texure relative to block
                gx = (gx/50) * gTexSize;
                gy = (gy/50) * gTexSize;

                //get absolute value so no negatives
                gx = Math.abs(gx);
                gy = Math.abs(gy);

                //draw pixel
                g.setColor(changeColor(new Color(gTexture.getRGB((int)gx, (int)gy)), fog, dark));
                g.fillRect(r*res+1, yy, res*gRes, gRes);
                
                //----------Ceiling----------
                double cx = tx;
                double cy = ty;
                int cIndex = s.lm.ceiling[(int)(ty/50)][(int)(tx/50)];
                
                if(cIndex > 0){//if there is a ceiling to draw
                    BufferedImage cTexture = textures[cIndex];
                    int cTexSize = cTexture.getHeight();

                    //break into blocks
                    cx = (int)(cx)%50;
                    cy = (int)(cy)%50;

                    //find location on the texure relative to block
                    cx = (cx/50) * cTexSize;
                    cy = (cy/50) * cTexSize;

                    //get absolute value so no negatives
                    cx = Math.abs(cx);
                    cy = Math.abs(cy);

                    //draw pixel
                    g.setColor(changeColor(new Color(cTexture.getRGB((int)cx, (int)cy)), fog, dark));
                    g.fillRect(r*res+1, getHeight() - yy, res*gRes, gRes);
                }
            } catch(Exception e){ }
        }
    }
    
    //constant screen size so screen can be scaled
    public int getWidth(){
        return 600;
    }
    public int getHeight(){
        return 600;
    }
}
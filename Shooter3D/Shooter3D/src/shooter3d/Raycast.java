package shooter3d;

public class Raycast {
    double rayX, rayY, rayDist;//position of ray and the distance of the ray
    boolean hitHorz;//if ray hit a horizontal or vertical line
    
    //cast a ray looking for a block that is not a 0(empty space)
    public void castRay(double x, double y, double angle, int[][] array, int cellSize){
        double xOff=0, yOff=0;//x and y offset to check next line
        double rx, ry;//points to keep track of the ray position
        
        //size of level
        int levelHeight = array.length*cellSize;
        int levelWidth = array[0].length*cellSize;
        
        //-----------------Horizontal Lines Check---------------------
        double disH = (levelWidth*levelHeight)*cellSize;//distance of horizontal ray
        double hx = x, hy = y;//points for horizontal ray
        
        double aTan = -1/Math.tan(angle);//inverse of tan

        if(angle == 0 || angle == Math.PI){//looking left or right
            rx = x;
            ry = y;
        }
        else if(angle < Math.PI){//looking down
            ry = (int)(y/cellSize)*cellSize + cellSize;//getting next horizontal line
            rx = (y-ry)*aTan+x;

            yOff = cellSize;
            xOff = -yOff*aTan;
        }
        else{//looking up
            ry = (int)(y/cellSize)*cellSize -0.0000001;//getting next horizontal line
            rx = (y-ry)*aTan+x;

            yOff = -cellSize;
            xOff = -yOff*aTan;
        }

        //ray check points fit withing level array boundries
        while(rx > 0 && rx < levelWidth && ry > 0 && ry < levelHeight){
            //ray location on array
            int mapX = (int)(rx/cellSize);
            int mapY = (int)(ry/cellSize);
            
            if(array[mapY][mapX] != 0){//is a block
                hx = rx;
                hy = ry;
                disH = Mathf.dist(x, y, hx, hy);
                break;
            } else{
                rx += xOff;
                ry += yOff;
            }
        }

        //-----------------Vertical Lines Check----------------------
        double disV = (levelWidth*levelHeight)*cellSize;//distance of vertical ray
        double vx = x, vy = y;//points of vertical ray
        
        double nTan = -Math.tan(angle);//negative tan

        if(angle == 0 || angle == Math.PI){//looking up or down
            rx = x;
            ry = y;
        }
        else if(angle < Math.PI/2 || angle > 1.5*Math.PI){//looking right
            rx = (int)(x/cellSize)*cellSize + cellSize;
            ry = (x-rx)*nTan+y;

            xOff = cellSize;
            yOff = -xOff*nTan;
        }
        else{//looking left
            rx = (int)(x/cellSize)*cellSize -0.0000001;
            ry = (x-rx)*nTan+y;

            xOff = -cellSize;
            yOff = -xOff*nTan;
        }

        //ray check points fit withing level array boundries
        while(rx > 0 && rx < levelWidth && ry > 0 && ry < levelHeight){
            //ray location on array
            int mapX = (int)(rx/cellSize);
            int mapY = (int)(ry/cellSize);
            
            if(array[mapY][mapX] != 0){//is a block
                vx = rx;
                vy = ry;
                disV = Mathf.dist(x, y, vx, vy);
                break;
            } else{
                rx += xOff;
                ry += yOff;
            }
        }

        //------------Finding what line is bigger---------------
        if(disH < disV){//horizontal ray is smaller than vertical one
            rayX = hx; rayY = hy; rayDist = disH;
            hitHorz = true;
        }
        else if(disV < disH){//vertical ray is smaller than horizontal one
            rayX = vx; rayY = vy; rayDist = disV;
            hitHorz = false;
        }
    }
}


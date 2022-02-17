package shooter3d;

public class Raycast {
    double rayX, rayY, rayDist;//position of ray and the distance of the ray
    boolean hitHorz;//if ray hit a horizontal or vertical line
    
    //cast a ray looking for a block that is not a 0
    public void castRay(double x, double y, double angle, int[][] array, int cellSize){
        //r means the ray, rx = x coordinate of the ray
        double xOff=0, yOff=0, rx=0, ry=0;
        //ray angle, x point where ray will hit, y point where ray will hit, x offset to check next line, y offset to check next line, final distance of that ray
        int levelHeight = array.length*cellSize;
        int levelWidth = array[0].length*cellSize;
        
        //-----------------Horizontal Lines Check---------------------
        int dof = 0;//how far you can see
        double disH = (levelWidth*levelHeight)*cellSize, hx = x, hy = y;
        //distance of horizontal ray, and points for horizontal ray
        double aTan = -1/Math.tan(angle);//inverse of tan

        if(angle == 0 || angle == Math.PI){//looking left or right
            rx = x; ry = y; dof = levelHeight;
        } else if(angle < Math.PI){//looking down
            ry = (int)(y/cellSize)*cellSize + cellSize;//getting next horizontal line
            rx = (y-ry)*aTan+x;

            yOff = cellSize;
            xOff = -yOff*aTan;
        } else{//looking up
            ry = (int)(y/cellSize)*cellSize -0.0000001;//getting next horizontal line
            rx = (y-ry)*aTan+x;

            yOff = -cellSize;
            xOff = -yOff*aTan;
        }

        while(dof < levelHeight/cellSize){
            int mx = (int)Mathf.Clamp((int)(rx/cellSize), 0, levelWidth/cellSize - 1);
            int my = (int)Mathf.Clamp((int)(ry/cellSize), 0, levelWidth/cellSize - 1);

            if(array[my][mx] != 0){//if is a block
                hx = rx;
                hy = ry;
                disH = Mathf.dist(x, y, hx, hy);
                dof = levelHeight/cellSize;
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
            }
        }

        //-----------------Vertical Lines Check----------------------
        dof = 0;//reset depth of feild for vertical line
        double disV = (levelWidth*levelHeight)*cellSize, vx = x, vy = y;//distance of vertical ray, and points of vertical ray
        double nTan = -Math.tan(angle);//negative tan

        if(angle == 0 || angle == Math.PI){//looking up or down
            rx = x; ry = y; dof = levelWidth;
        } else if(angle < Math.PI/2 || angle > 1.5*Math.PI){//looking right
            rx = (int)(x/cellSize)*cellSize + cellSize;
            ry = (x-rx)*nTan+y;

            xOff = cellSize;
            yOff = -xOff*nTan;
        } else{//looking left
            rx = (int)(x/cellSize)*cellSize -0.0000001;
            ry = (x-rx)*nTan+y;

            xOff = -cellSize;
            yOff = -xOff*nTan;
        }

        while(dof < levelWidth/cellSize){
            int mx = (int)Mathf.Clamp((int)(rx/cellSize), 0, levelWidth/cellSize - 1);
            int my = (int)Mathf.Clamp((int)(ry/cellSize), 0, levelWidth/cellSize - 1);
            
            if(array[my][mx] != 0){//is a block
                vx = rx;
                vy = ry;
                disV = Mathf.dist(x, y, vx, vy);
                dof = levelWidth/cellSize;
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
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
    //cast a ray loocking for a specific block type
    public void castRayLookFor(double x, double y, double angle, int[][] array, int cellSize, int lookFor){
        //r means the ray, rx = x coordinate of the ray
        double xOff=0, yOff=0, rx=0, ry=0;
        //ray angle, x point where ray will hit, y point where ray will hit, x offset to check next line, y offset to check next line, final distance of that ray
        int levelHeight = array.length*cellSize;
        int levelWidth = array[0].length*cellSize;
        
        //-----------------Horizontal Lines Check---------------------
        int dof = 0;//how far you can see
        double disH = (levelWidth*levelHeight)*cellSize, hx = x, hy = y;
        //distance of horizontal ray, and points for horizontal ray
        double aTan = -1/Math.tan(angle);//inverse of tan

        if(angle == 0 || angle == Math.PI){//looking left or right
            rx = x; ry = y; dof = levelHeight;
        } else if(angle < Math.PI){//looking down
            ry = (int)(y/cellSize)*cellSize + cellSize;//getting next horizontal line
            rx = (y-ry)*aTan+x;

            yOff = cellSize;
            xOff = -yOff*aTan;
        } else{//looking up
            ry = (int)(y/cellSize)*cellSize -0.0000001;//getting next horizontal line
            rx = (y-ry)*aTan+x;

            yOff = -cellSize;
            xOff = -yOff*aTan;
        }

        while(dof < levelHeight/cellSize){
            int mx = (int)Mathf.Clamp((int)(rx/cellSize), 0, levelWidth/cellSize - 1);
            int my = (int)Mathf.Clamp((int)(ry/cellSize), 0, levelWidth/cellSize - 1);

            if(array[my][mx] == lookFor){
                hx = rx;
                hy = ry;
                disH = Mathf.dist(x, y, hx, hy);
                dof = levelHeight/cellSize;
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
            }
        }

        //-----------------Vertical Lines Check----------------------
        dof = 0;//reset depth of feild for vertical line
        double disV = (levelWidth*levelHeight)*cellSize, vx = x, vy = y;//distance of vertical ray, and points of vertical ray
        double nTan = -Math.tan(angle);//negative tan

        if(angle == 0 || angle == Math.PI){//looking up or down
            rx = x; ry = y; dof = levelWidth;
        } else if(angle < Math.PI/2 || angle > 1.5*Math.PI){//looking right
            rx = (int)(x/cellSize)*cellSize + cellSize;
            ry = (x-rx)*nTan+y;

            xOff = cellSize;
            yOff = -xOff*nTan;
        } else{//looking left
            rx = (int)(x/cellSize)*cellSize -0.0000001;
            ry = (x-rx)*nTan+y;

            xOff = -cellSize;
            yOff = -xOff*nTan;
        }

        while(dof < levelWidth/cellSize){
            int mx = (int)Mathf.Clamp((int)(rx/cellSize), 0, levelWidth/cellSize - 1);
            int my = (int)Mathf.Clamp((int)(ry/cellSize), 0, levelWidth/cellSize - 1);
            
            if(array[my][mx] == lookFor){
                vx = rx;
                vy = ry;
                disV = Mathf.dist(x, y, vx, vy);
                dof = levelWidth/cellSize;
            } else{
                rx += xOff;
                ry += yOff;
                dof++;
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


package shooter3d;

abstract class Mathf {
    
    public static double P2 = Math.PI/2;
    public static double P3 = 3*Math.PI/2;
    public static double dr = Math.PI / 180;
    
    //distance between two points, is also used to find hypo
    public static double dist(double ax, double ay, double bx, double by){
        double num = ((bx-ax)*(bx-ax) + (by-ay)*(by-ay));//pythag
        
        return Math.sqrt(num);
    }
    
    public static double[] normalize(double x, double y){
        double hypo = dist(0, 0, x, y);
        double normalX = x/hypo;
        double normalY = y/hypo;
        
        double[] normalized = {normalX, normalY};
        
        return normalized;
    }
    
    //if this were in degrees it would basiclly turn 410 degrees into 50, or 630 into 270
    public static double limitFor(double angle){
        if(angle < 0) angle += 2*Math.PI;
        if(angle > 2*Math.PI) angle -= 2*Math.PI;//limits for new degree
        return angle;
    }
    
    //see if a line intersects with walls/ blocks in an array
    public static boolean intersectArray(double x1, double y1, double x2, double y2, int[][] array, int cellSize, double checkLength){
        boolean hit = false;
        
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double distToPlayer = Mathf.dist(x1, y1, x2, y2);
        double xLength = Math.cos(angle);
        double yLength = Math.sin(angle);
        
        double checkDist = 0;
        int check = 0;
        while(checkDist < distToPlayer){
            double value = check * checkLength;
            double posX = x1 + (value * xLength);
            double posY = y1 + (value * yLength);
            
            int mapX = (int)(posX/cellSize);
            int mapY = (int)(posY/cellSize);
            
            //point is outside of array
            if(mapX < 0 || mapX > array[0].length-1 || mapY < 0 || mapY > array.length-1){
                hit = true;
                break;
            }
            //point hits a block
            if(array[mapY][mapX] == 1) hit = true;
            
            checkDist = dist(x1, y1, posX, posY) + checkLength;
            check++;
        }
        
        return hit;
    }
    
    public static boolean intersectArrayLookFor(double x1, double y1, double x2, double y2, int[][] array, int lookFor){
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double dist = dist(x1, y1, x2, y2);
        
        Raycast ray = new Raycast();
        ray.castRayLookFor(x1, y1, angle, array, 1, lookFor);
        
        return ray.rayDist < dist;
    }
    
    public static int getBlockType(double[] point, int[][] level, int cellSize){
        int xPoint = (int)(point[0]/cellSize);
        int yPoint = (int)(point[1]/cellSize);
        
        return level[yPoint][xPoint];
    }
    
    //shortest distance betwwen two angles
    public static double diff(double ang1, double ang2){
        double finalDiff;
        double diff1 = ang1 - ang2;
        double diff2 = Math.PI*2 - diff1;
        
        if(Math.abs(diff1) < Math.abs(diff2)) finalDiff = diff1;
        else finalDiff = diff2;
        
        return finalDiff;
    }
    //radians to degrees
    public static int radToDeg(double ang){
        return (int)(ang*180 / Math.PI);
    }
    //degrees to degrees
    public static double degToRad(double ang){
        return (ang * Math.PI / 1800);
    }
    
    //clamps a number between a min value and a max value
    public static double Clamp(double value, double min, double max){
        if(value < min) value = min;
        if(value > max) value = max;
        
        return value;
    }
}

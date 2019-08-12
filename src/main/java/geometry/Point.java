package geometry;

/**
 * Basic Point Class, contains important minus() function
 * for creating vectors as a difference between two points
 */
public class Point {

    public double x, y, z;

    public Point() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    public Vector minus(Point p) {
        double newX = this.x - p.x;
        double newY = this.y - p.y;
        double newZ = this.z - p.z;

        return new Vector(newX, newY, newZ);
    }
        
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
package geometry;

/**
 * Vector class with some basic operations
 */

public class Vector {

    public double x, y, z;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vector v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector plus(Vector v) {
        double newX = this.x + v.x;
        double newY = this.y + v.y;
        double newZ = this.z + v.z;

        return new Vector(newX, newY, newZ);
    }

    public Point plus(Point p) {
        double newX = this.x + p.x;
        double newY = this.y + p.y;
        double newZ = this.z + p.z;

        return new Point(newX, newY, newZ);
    }

    public Vector minus(Vector v) {
        double newX = this.x - v.x;
        double newY = this.y - v.y;
        double newZ = this.z - v.z;

        return new Vector(newX, newY, newZ);
    }

    public double dot(Vector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }
    
    public void normalize() {
        double magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);

        this.x /= magnitude;
        this.y /= magnitude;
        this.z /= magnitude;
    }

    public Vector cross(Vector v) {
        double resX = this.y * v.z - this.z * v.y;
        double resY = this.z * v.x - this.x * v.z;
        double resZ = this.x * v.y - this.y * v.x;

        return new Vector(resX, resY, resZ);
    }

    public Vector times(double d) {
        
        double resX = this.x * d;
        double resY = this.y * d;
        double resZ = this.z * d;       

        return new Vector(resX, resY, resZ);
    }
    
    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
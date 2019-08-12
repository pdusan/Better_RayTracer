package geometry;

/**
 * The Abstract Shape class.
 * Not of any use for Lab4a, as it only contains spheres
 */

public abstract class Shape {

    public abstract double hit(Ray ray);

    public abstract int getColor(double t, Ray ray);
}
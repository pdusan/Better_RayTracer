
package com.gfx.ray;

import geometry.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class RayTracer {

    public static void main (String[] args) {
        try {
            Scene scene = new Scene(args[0]);       //the name of the .xml input file is given as the only argument
            
            File image = scene.getOutputFile();
            
            int width = (int) scene.getCam().getPlaneWidth();
            int height = (int) scene.getCam().getPlaneHeight();

            BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);            
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    Ray ray = scene.getCam().makeRay(j, height - i);
                    buffer.setRGB(j, i, trace(ray, scene));
                }
            }

            ImageIO.write(buffer, "PNG", image);            
        } catch (Exception e) {
            System.err.println(e.getClass());
        }
    }

    /**
     * Looks for intersections on the surface and if it finds any,
     * assigns a color to the intersection point
     */
    public static int trace(Ray ray, Scene scene) {
        double hit = 0;
        Shape shape = null;
        for (Shape s : scene.getShapes())
            if (s.hit(ray) > 0) {
                hit = s.hit(ray);
                shape = s;
                break;
            }
        if (hit > 0)
            return shape.getColor(shape.hit(ray), ray);
        return scene.getBackgroundColor();
    }

}
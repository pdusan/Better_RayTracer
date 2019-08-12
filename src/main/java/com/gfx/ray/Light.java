package com.gfx.ray;

import geometry.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Here, all the light information is parsed in.
 * I made sub classes for all the different types of lights in 
 * an attempt to better organize the code and avoid too many fields in 
 * the Light class itself
 */
public class Light {

    private Document doc;
    private AmbientLight ambient;
    private ArrayList<PointLight> point = new ArrayList<PointLight>();
    private ArrayList<ParallelLight> parallel = new ArrayList<ParallelLight>();
    private ArrayList<SpotLight> spot = new ArrayList<SpotLight>();

    public Light(Document doc) {
        this.doc = doc;
        initAmbient(this.doc.getElementsByTagName("ambient_light").item(0));
        initPoint(this.doc.getElementsByTagName("point_light").item(0));
        initParallel(this.doc.getElementsByTagName("parallel_light").item(0));
        initSpot(this.doc.getElementsByTagName("spot_light").item(0));
    }

    public static class AmbientLight {
        private float r, g, b;

        public AmbientLight(float r, float g, float b) {
            this.b = b;
            this.g = g;
            this.r = r;
        }

        public HashMap<String, Float> getRgb() {
            HashMap<String, Float> rgb = new HashMap<String, Float>();
            rgb.put("r", this.r);
            rgb.put("g", this.g);
            rgb.put("b", this.b);

            return rgb;
        }
    }

    public static class PointLight {
        private float r, g, b;
        private Point position;

        public PointLight(float r, float g, float b, Point pos) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.position = pos;
        }

        public HashMap<String, Float> getRgb() {
            HashMap<String, Float> rgb = new HashMap<String, Float>();
            rgb.put("r", this.r);
            rgb.put("g", this.g);
            rgb.put("b", this.b);

            return rgb;
        }

        public Point getPostition() {
            return this.position;
        }
    }

    public static class ParallelLight {
        private float r, g, b;
        private Vector direction;

        public ParallelLight(float r, float g, float b, Vector dir) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.direction = dir;
        }

        public HashMap<String, Float> getRgb() {
            HashMap<String, Float> rgb = new HashMap<String, Float>();
            rgb.put("r", this.r);
            rgb.put("g", this.g);
            rgb.put("b", this.b);

            return rgb;
        }

        public Vector getDirection() {
            return this.direction;
        }
    }

    public static class SpotLight {
        private float r, g, b;
        private Point position;
        private Vector direction;
        private double alpha1, alpha2;

        public SpotLight(float r, float g, float b, Point pos, 
                Vector dir, double a1, double a2) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.position = pos;
            this.direction = dir;
            this.alpha1 = a1;
            this.alpha2 = a2;
        }

        public HashMap<String, Float> getRgb() {
            HashMap<String, Float> rgb = new HashMap<String, Float>();
            rgb.put("r", this.r);
            rgb.put("g", this.g);
            rgb.put("b", this.b);

            return rgb;
        }

        public Vector getDirection() {
            return this.direction;
        }

        public Point getPosition() {
            return this.position;
        }

        public HashMap<String, Double> getFalloff() {
            HashMap<String, Double> falloff = new HashMap<String, Double>();
            falloff.put("a1", alpha1);
            falloff.put("a2", alpha2);

            return falloff;
        }
    }

    private void initAmbient(Node ambientLight) {
        NodeList ambientOptions = ambientLight.getChildNodes();
        
        float r = Float.parseFloat(ambientOptions.item(1).getAttributes().getNamedItem("r").getTextContent());
        float g = Float.parseFloat(ambientOptions.item(1).getAttributes().getNamedItem("g").getTextContent());
        float b = Float.parseFloat(ambientOptions.item(1).getAttributes().getNamedItem("b").getTextContent());
        
        AmbientLight amb = new AmbientLight(r, g, b);
        this.ambient = amb;
    }

    private void initPoint(Node pointLight) {
        try {
            NodeList pointOptions = pointLight.getChildNodes();

            float r = Float.parseFloat(pointOptions.item(1).getAttributes().getNamedItem("r").getTextContent());
            float g = Float.parseFloat(pointOptions.item(1).getAttributes().getNamedItem("g").getTextContent());
            float b = Float.parseFloat(pointOptions.item(1).getAttributes().getNamedItem("b").getTextContent());

            double x = Double.parseDouble(pointOptions.item(3).getAttributes().getNamedItem("x").getTextContent());
            double y = Double.parseDouble(pointOptions.item(3).getAttributes().getNamedItem("y").getTextContent());
            double z = Double.parseDouble(pointOptions.item(3).getAttributes().getNamedItem("z").getTextContent());

            Point pos = new Point(x, y, z);

            this.point.add(new PointLight(r, g, b, pos));

            Node next = pointLight.getNextSibling();
            while (next.getNodeName() != "point_light")
                next = next.getNextSibling();
            if (next.getNodeName() == "point_light")
                initPoint(next);
            else return;
        } catch (Exception e) {
            return;
        }
    }
    
    private void initParallel(Node parallelLight) {
        try {
            NodeList parallelOptions = parallelLight.getChildNodes();

            float r = Float.parseFloat(parallelOptions.item(1).getAttributes().getNamedItem("r").getTextContent());
            float g = Float.parseFloat(parallelOptions.item(1).getAttributes().getNamedItem("g").getTextContent());
            float b= Float.parseFloat(parallelOptions.item(1).getAttributes().getNamedItem("b").getTextContent());

            double x = Double.parseDouble(parallelOptions.item(3).getAttributes().getNamedItem("x").getTextContent());
            double y = Double.parseDouble(parallelOptions.item(3).getAttributes().getNamedItem("y").getTextContent());
            double z = Double.parseDouble(parallelOptions.item(3).getAttributes().getNamedItem("z").getTextContent());

            Vector dir = new Vector(x, y, z);

            this.parallel.add(new ParallelLight(r, g, b, dir));

            Node next = parallelLight.getNextSibling();
            while (next.getNodeName() != "parallel_light")
                next = next.getNextSibling();
            if (next.getNodeName() == "parallel_light")
                initPoint(next);
            else return;
        } catch (Exception e) {
            return;
        }
    }

    private void initSpot(Node spotLight) {
        try {
            NodeList spotOptions = spotLight.getChildNodes();

            float r = Float.parseFloat(spotOptions.item(1).getAttributes().getNamedItem("r").getTextContent());
            float g = Float.parseFloat(spotOptions.item(1).getAttributes().getNamedItem("g").getTextContent());
            float b = Float.parseFloat(spotOptions.item(1).getAttributes().getNamedItem("b").getTextContent());

            double x = Double.parseDouble(spotOptions.item(3).getAttributes().getNamedItem("x").getTextContent());
            double y = Double.parseDouble(spotOptions.item(3).getAttributes().getNamedItem("y").getTextContent());
            double z = Double.parseDouble(spotOptions.item(3).getAttributes().getNamedItem("z").getTextContent());

            Point pos = new Point(x, y, z);
            
            double dirX = Double.parseDouble(spotOptions.item(5).getAttributes().getNamedItem("x").getTextContent());
            double dirY = Double.parseDouble(spotOptions.item(5).getAttributes().getNamedItem("y").getTextContent());
            double dirZ = Double.parseDouble(spotOptions.item(5).getAttributes().getNamedItem("z").getTextContent());

            Vector dir = new Vector(dirX, dirY, dirZ);

            double a1 = Double.parseDouble(spotOptions.item(7).getAttributes().getNamedItem("alpha1").getTextContent());
            double a2 = Double.parseDouble(spotOptions.item(7).getAttributes().getNamedItem("alpha2").getTextContent());

            this.spot.add(new SpotLight(r, g, b, pos, dir, a1, a2));

            Node next = spotLight.getNextSibling();
            while (next.getNodeName() != "parallel_light")
                next = next.getNextSibling();
            if (next.getNodeName() == "parallel_light")
                initPoint(next);
            else return;

        } catch (Exception e) {
            return;
        }
    }

    public AmbientLight getAmbient() {
        return this.ambient;
    }
    
    public ArrayList<PointLight> getPoint() {
        return this.point;
    }

    public ArrayList<ParallelLight> getParallel() {
        return this.parallel;
    }

    public ArrayList<SpotLight> getSpot() {
        return this.spot;
    }
}
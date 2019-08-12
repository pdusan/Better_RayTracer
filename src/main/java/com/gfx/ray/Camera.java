package com.gfx.ray;

import geometry.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * For parsing in all the camera information from the .xml file
 */
public class Camera {
    
    private Document doc;
    private Point location;
    private Point lookingAt;
    private Vector up;
    private double xFieldOfView;
    private double yFieldOfView;
    private double width;
    private double height;
    private double bounces;

    public Camera(Document doc) {
        this.doc = doc;
        initCam();
        
        this.yFieldOfView = this.height * this.xFieldOfView/this.width;
    }

    private void initCam() {
        NodeList cameraOptions = this.doc.getElementsByTagName("camera").item(0).getChildNodes();

        double posX = Double.parseDouble(cameraOptions.item(1).getAttributes().getNamedItem("x").getTextContent());
        double posY = Double.parseDouble(cameraOptions.item(1).getAttributes().getNamedItem("y").getTextContent());
        double posZ = Double.parseDouble(cameraOptions.item(1).getAttributes().getNamedItem("z").getTextContent());

        double lookX = Double.parseDouble(cameraOptions.item(3).getAttributes().getNamedItem("x").getTextContent());
        double lookY = Double.parseDouble(cameraOptions.item(3).getAttributes().getNamedItem("y").getTextContent());
        double lookZ = Double.parseDouble(cameraOptions.item(3).getAttributes().getNamedItem("z").getTextContent());

        double upX = Double.parseDouble(cameraOptions.item(5).getAttributes().getNamedItem("x").getTextContent());
        double upY = Double.parseDouble(cameraOptions.item(5).getAttributes().getNamedItem("y").getTextContent());
        double upZ = Double.parseDouble(cameraOptions.item(5).getAttributes().getNamedItem("z").getTextContent());

        Point pos = new Point(posX, posY, posZ);
        
        Point look = new Point(lookX, lookY, lookZ);
        Vector up = new Vector(upX, upY, upZ);
        double fov = Double.parseDouble(cameraOptions.item(7).getAttributes().getNamedItem("angle").getTextContent());
        double width = Double.parseDouble(cameraOptions.item(9).getAttributes().getNamedItem("horizontal").getTextContent());
        double height = Double.parseDouble(cameraOptions.item(9).getAttributes().getNamedItem("vertical").getTextContent());
        double bounceCount = Double.parseDouble(cameraOptions.item(11).getAttributes().getNamedItem("n").getTextContent());

        this.location = pos;
        this.lookingAt = look;
        this.up = up;
        this.xFieldOfView = fov;
        this.width = width;
        this.height = height;
        this.bounces = bounceCount;
    }

    public double getPlaneWidth() {
        return this.width;
    }
    
    public double getPlaneHeight() {
        return this.height;
    }

    public Point getLocation() {
        return this.location;
    }
    
    /**
     * Constructs rays from the camera position through a given pixel on the image
     */
    public Ray makeRay(double p, double q) {

        double x = Math.tan(Math.toRadians(this.xFieldOfView)) * ((2 * p - this.width)/this.width);
        double y = Math.tan(Math.toRadians(this.yFieldOfView)) * ((2 * q - this.height)/this.height);

        Point origin = this.location;
        Vector direction = new Vector(x, y, -1.0);
        direction.normalize();

        Ray res = new Ray(origin, direction);

        return res;
    }

    /**
     * Temps, for removing warnings while working on other aspects
     */
    public double getBounces() {
        return bounces;
    }
    public Vector getUp() {
        return up;
    }
    public Point getLook() {
        return lookingAt;
    }
}
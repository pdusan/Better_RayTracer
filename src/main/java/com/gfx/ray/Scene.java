package com.gfx.ray;

import geometry.*;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * The scene class holds all the objects parsed in from the .xml file.
 */
public class Scene {

    private Document doc;
    private int backgroundColor;
    private Camera cam;
    private Light light;
    private ArrayList<Shape> shapes = new ArrayList<Shape>();

    public Scene(String inputFileName) throws Exception {
        doc = makeDoc(inputFileName);
        backgroundColor = makeBackgroundColor();
        cam = new Camera(doc);
        light = new Light(doc);
        initShapes();
    }

    private int makeBackgroundColor() {
        NodeList list = this.doc.getElementsByTagName("background_color");
        Node col = list.item(0);
        Element background = (Element) col;

        float red = Float.parseFloat(background.getAttribute("r"));
        float green = Float.parseFloat(background.getAttribute("g"));
        float blue = Float.parseFloat(background.getAttribute("b"));

        Color background_color = new Color(red, green, blue);

        return background_color.getRGB();
    } 

    private static Document makeDoc(String inputFileName) throws Exception {

        File xml = new File("inputs/" + inputFileName);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(xml);

        doc.getDocumentElement().normalize();

        return doc;
    }

    private void initShapes() throws Exception {
        NodeList surfaces = this.doc.getElementsByTagName("surfaces").item(0).getChildNodes();

        int surfacesSize = surfaces.getLength();

        for (int i = 0; i < surfacesSize; ++i) {
            if (surfaces.item(i).getNodeName() == "sphere") {
                //Node sphere = surfaces.item(i);

                //this.shapes.add(new Sphere(sphere, this.light, this.cam));
            }
            else if (surfaces.item(i).getNodeName() == "mesh") {
                //Node mesh = surfaces.item(i);

                //this.shapes.add(new Mesh(mesh, this.light, this.cam));
            }
        }
        
    }

    public File getOutputFile() throws Exception {
        String filename = this.doc.getDocumentElement().getAttribute("output_file");
        File out = new File("results/" + filename);
        return out;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }   

    public Camera getCam() {
        return this.cam;
    }

    public Light getlight() {
        return this.light;
    }

    public ArrayList<Shape> getShapes() {
        return this.shapes;
    }
}
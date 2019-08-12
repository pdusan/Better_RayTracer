package geometry;

import java.io.InputStream;
import java.awt.Color;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.gfx.ray.Camera;
import com.gfx.ray.Light;

import org.w3c.dom.Node;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ReadableObj;

public class Mesh extends Shape {

    private ArrayList<Tri> tris = new ArrayList<Tri>();
    private float surfaceRed, surfaceGreen, surfaceBlue;
    private float ka, kd, ks;
    private double exp, reflect, transmit, refract;
    private Vector translateBy;
    private double scaleX, scaleY, scaleZ, rotateX, rotateY, rotateZ;
    private Light.AmbientLight ambient;
    private ArrayList<Light.ParallelLight> parallel;
    private ArrayList<Light.PointLight> point;
    private ArrayList<Light.SpotLight> spot;
    private Point eyeLocation;
    private Tri activeTri;

    public Mesh(Node mesh, Light light, Camera cam) throws Exception {

        String filename = mesh.getAttributes().getNamedItem("name").getTextContent();
        InputStream objInput = new FileInputStream("inputs/" + filename);
        Obj obj = ObjReader.read(objInput);

        int faceNumber = obj.getNumFaces();
        for (int i = 0; i < faceNumber; i++)
            this.tris.add(new Tri(obj.getFace(i), obj));

        this.eyeLocation = cam.getLocation();
        
        this.ambient = light.getAmbient();
        this.parallel = light.getParallel();
        this.point = light.getPoint();
        this.spot = light.getSpot();

        Node material = mesh.getChildNodes().item(1);

        initMaterial(material);

        Node transform = mesh.getChildNodes().item(3);

        initTransforms(transform);

    }

    private class Tri {
        private Point vert1, vert2, vert3;
        private Vector normal, texture;

        public Tri(ObjFace face, Obj obj) {
            FloatTuple a = obj.getVertex(face.getVertexIndex(0));
            FloatTuple b = obj.getVertex(face.getVertexIndex(1));
            FloatTuple c = obj.getVertex(face.getVertexIndex(2));
            
            this.vert1 = new Point(a.get(0), a.get(1), a.get(2));
            this.vert2 = new Point(b.get(0), b.get(1), b.get(2));
            this.vert3 = new Point(c.get(0), c.get(1), c.get(2));
            
            FloatTuple n = obj.getNormal(face.getNormalIndex(0));

            this.normal = new Vector(n.get(0), n.get(1), n.get(2));
        }

        public Point getV1() {
            return this.vert1;
        }

        public Point getV2() {
            return this.vert2;
        }

        public Point getV3() {
            return this.vert3;
        }

        public Vector getNormal() {
            return this.normal;
        }

        public Vector getTexture() {
            return this.texture;
        }
    }

    private void initMaterial(Node material) {
        Node color = material.getChildNodes().item(1);
        float colRed = Float.parseFloat(color.getAttributes().getNamedItem("r").getTextContent());
        float colGreen = Float.parseFloat(color.getAttributes().getNamedItem("g").getTextContent());
        float colBlue = Float.parseFloat(color.getAttributes().getNamedItem("b").getTextContent());

        this.surfaceBlue = colBlue;
        this.surfaceGreen = colGreen;
        this.surfaceRed = colRed;

        Node phong = material.getChildNodes().item(3);
        this.ka = Float.parseFloat(phong.getAttributes().getNamedItem("ka").getTextContent());
        this.kd = Float.parseFloat(phong.getAttributes().getNamedItem("kd").getTextContent());
        this.ks = Float.parseFloat(phong.getAttributes().getNamedItem("ks").getTextContent());
        this.exp = Double.parseDouble(phong.getAttributes().getNamedItem("exponent").getTextContent());

        Node reflectance = material.getChildNodes().item(5);
        this.reflect = Double.parseDouble(reflectance.getAttributes().getNamedItem("r").getTextContent());
        
        Node transmittance = material.getChildNodes().item(7);
        this.transmit = Double.parseDouble(transmittance.getAttributes().getNamedItem("t").getTextContent());
        
        Node refraction = material.getChildNodes().item(9);
        this.refract = Double.parseDouble(refraction.getAttributes().getNamedItem("iof").getTextContent());
    }

    private void initTransforms(Node transform) {
        try {
            Node translate = transform.getChildNodes().item(1);
            double posX = Double.parseDouble(translate.getAttributes().getNamedItem("x").getTextContent());
            double posY = Double.parseDouble(translate.getAttributes().getNamedItem("y").getTextContent());
            double posZ = Double.parseDouble(translate.getAttributes().getNamedItem("z").getTextContent());
            this.translateBy = new Vector(posX, posY, posZ);

            Node scale = transform.getChildNodes().item(3);
            double scaleX = Double.parseDouble(scale.getAttributes().getNamedItem("x").getTextContent());
            double scaleY = Double.parseDouble(scale.getAttributes().getNamedItem("y").getTextContent());
            double scaleZ = Double.parseDouble(scale.getAttributes().getNamedItem("z").getTextContent());

            double rotateX = Double.parseDouble(transform.getChildNodes().item(5).getAttributes().getNamedItem("theta").getTextContent());
            double rotateY = Double.parseDouble(transform.getChildNodes().item(7).getAttributes().getNamedItem("theta").getTextContent());
            double rotateZ = Double.parseDouble(transform.getChildNodes().item(9).getAttributes().getNamedItem("theta").getTextContent());
            
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.scaleZ = scaleZ;
            this.rotateX = rotateX;
            this.rotateY = rotateY;
            this.rotateZ = rotateZ;
        } catch(Exception e) {
            return;
        }
    }

	@Override
    public double hit(Ray ray) {
        Point o = ray.origin;
        Vector d = ray.direction;

        for (Tri t : this.tris) {
            Vector s = o.minus(t.getV1());
            Vector e1 = t.getV2().minus(t.getV1());
            Vector e2 = t.getV3().minus(t.getV1());

            double t0 = s.cross(e1).dot(e2) / d.cross(e2).dot(e1);
            
            if (t0 > 0) {
                this.activeTri = t;
                return t0;
            }
            else continue;
        }
        return -1;
    }



    //======================PHONG ILLUMINATION MODEL FUNCTIONS=========================
    private HashMap<String, Float> ambient() {
        float r = this.ka * this.surfaceRed * this.ambient.getRgb().get("r");
        float g = this.ka * this.surfaceGreen * this.ambient.getRgb().get("g");
        float b = this.ka * this.surfaceBlue  * this.ambient.getRgb().get("b");
        HashMap<String, Float> ambientMap = new HashMap<String, Float>();

        ambientMap.put("r", r);
        ambientMap.put("g", g);
        ambientMap.put("b", b);

        return ambientMap;
    }

    private HashMap<String, Float> diffuse(Point p) {
        HashMap<String, Float> diffuseMap = new HashMap<String, Float>();

        if (this.parallel.size() > 0) {
            Vector n = this.activeTri.getNormal(); 
            n.normalize();
            
            Vector v = this.eyeLocation.minus(p);
            v.normalize();

            Vector l = this.parallel.get(0).getDirection().times(-1);
            l.normalize();

            float r = (float) (this.kd * this.surfaceRed * Math.max(l.dot(n), 0));
            float g = (float) (this.kd * this.surfaceGreen * Math.max(l.dot(n), 0));
            float b = (float) (this.kd * this.surfaceBlue * Math.max(l.dot(n), 0));

            diffuseMap.put("r", r);
            diffuseMap.put("g", g);
            diffuseMap.put("b", b);
        }
        else {
            diffuseMap.put("r", (float) 0.0);
            diffuseMap.put("g", (float) 0.0);
            diffuseMap.put("b", (float) 0.0);
        }
        return diffuseMap;
    }

    private HashMap<String, Float> specular(Point p) {
        HashMap<String, Float> specularMap = new HashMap<String, Float>();

        if (this.parallel.size() > 0) {
            Vector n = this.activeTri.getNormal();        
            n.normalize();
            
            Vector v = this.eyeLocation.minus(p);
            v.normalize();

            Vector l = this.parallel.get(0).getDirection();
            l.normalize();

            Vector r = n.times(2 * l.dot(n)).minus(l).times(-1);
            r.normalize();

            float specR = (float) (this.ks * Math.pow(Math.max(r.dot(v), 0), this.exp));
            float specG = (float) (this.ks * Math.pow(Math.max(r.dot(v), 0), this.exp));
            float specB = (float) (this.ks * Math.pow(Math.max(r.dot(v), 0), this.exp));

            specularMap.put("r", specR);
            specularMap.put("g", specG);
            specularMap.put("b", specB);
        }
        else {
            specularMap.put("r", (float) 0.0);
            specularMap.put("g", (float) 0.0);
            specularMap.put("b", (float) 0.0);
        }
        return specularMap;
    }

    @Override
    public int getColor(double t, Ray ray) {
        double x = ray.origin.x + t * ray.direction.x;
        double y = ray.origin.y + t * ray.direction.y;
        double z = ray.origin.z + t * ray.direction.z;
        Point p = new Point(x, y, z);

        HashMap<String, Float> ambientMap = this.ambient();
        HashMap<String, Float> diffuseMap = this.diffuse(p);
        HashMap<String, Float> specularMap = this.specular(p);

        float finalRed = ambientMap.get("r") + diffuseMap.get("r") + specularMap.get("r");
        float finalGreen = ambientMap.get("g") + diffuseMap.get("g") + specularMap.get("g");
        float finalBlue = ambientMap.get("b") + diffuseMap.get("b") + specularMap.get("b");

        if (finalRed > 1) finalRed = 1;
        if (finalGreen > 1) finalGreen = 1;
        if (finalBlue > 1) finalBlue = 1;

        if (finalRed < 0) finalRed = 0;
        if (finalGreen < 0) finalGreen = 0;
        if (finalBlue < 0) finalBlue = 0;

        Color res = new Color(finalRed, finalGreen, finalBlue);
        return res.getRGB();
    }
}
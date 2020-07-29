package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TextureModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {
    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        //OpenGL expects vertices to be defined counter clockwise by default


        RawModel model = OBJLoader.loadObjModel("tree",loader);
//        ModelTexture texture = new ModelTexture(loader.loadTexture("yellow"));
//        TextureModel textureModel = new TextureModel(model,texture);
//        ModelTexture texture1 = textureModel.getTexture();
//        texture1.setShineDamper(10);
//        texture1.setReflectivity(1);
//        Entity entity = new Entity(textureModel, new Vector3f(0,0  ,-50),0,0,0,1);
        TextureModel staticModel = new TextureModel(model,new ModelTexture(loader.loadTexture("tree")));
        TextureModel grass = new TextureModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHadTransparency(true);
        grass.getTexture().setUseFakeLightning(true);
        TextureModel fern = new TextureModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHadTransparency(true);
        fern.getTexture().setUseFakeLightning(true);
        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i<500;i++){
            entities.add(new Entity(staticModel,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),0,0,0,3));
            entities.add(new Entity(grass,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),0,0,0,1));
            entities.add(new Entity(fern,new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()*-600),0,0,0,0.6f));
        }
        Light light = new Light(new Vector3f(3000,2000,2000),new Vector3f(1,1,1));
        Camera camera = new Camera();
        MasterRenderer renderer = new MasterRenderer();
        Terrain terrain = new Terrain(0,-1,loader,new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1,-1,loader,new ModelTexture(loader.loadTexture("grass")));
        while (!Display.isCloseRequested()) {
            //entity.increaseRotation(0,1,0);
            camera.move();
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain);
            for (Entity entity:entities){
                renderer.processEntity(entity);
            }
            renderer.render(light,camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}

package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TextureModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {
    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        //OpenGL expects vertices to be defined counter clockwise by default


        RawModel model = OBJLoader.loadObjModel("dragon",loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("yellow"));
        TextureModel textureModel = new TextureModel(model,texture);
        ModelTexture texture1 = textureModel.getTexture();
        texture1.setShineDamper(10);
        texture1.setReflectivity(1);
        Entity entity = new Entity(textureModel, new Vector3f(0,0  ,-50),0,0,0,1);
        Light light = new Light(new Vector3f(3000,2000,2000),new Vector3f(1,1,1));
        Camera camera = new Camera();
        MasterRenderer renderer = new MasterRenderer();
        Terrain terrain = new Terrain(0,-1,loader,new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1,-1,loader,new ModelTexture(loader.loadTexture("grass")));
        while (!Display.isCloseRequested()) {
            entity.increaseRotation(0,1,0);
            camera.move();
            renderer.processTerrain(terrain2);
            renderer.processTerrain(terrain);
            renderer.processEntity(entity);
            renderer.render(light,camera);
            DisplayManager.updateDisplay();
        }
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}

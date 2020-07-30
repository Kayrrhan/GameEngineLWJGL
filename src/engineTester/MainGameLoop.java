package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TextureModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {
    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        ModelData data = OBJFileLoader.loadOBJ("tree");
        RawModel model = loader.loadToVAO(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices());
        TextureModel staticModel = new TextureModel(model,new ModelTexture(loader.loadTexture("tree")));
        TextureModel grass = new TextureModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHadTransparency(true);
        grass.getTexture().setUseFakeLightning(true);
        TextureModel fern = new TextureModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHadTransparency(true);
        fern.getTexture().setUseFakeLightning(true);



        Light light = new Light(new Vector3f(20000,40000,20000),new Vector3f(1,1,1));

        MasterRenderer renderer = new MasterRenderer();

        // ==================== TEXTURES TERRAINS ==================== //

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        // =========================================================== //
        Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");

        RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny",loader);
        TextureModel stanfordBunny = new TextureModel(bunnyModel,new ModelTexture(loader.loadTexture("white")));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i<500;i++){
            float x= random.nextFloat()*800-400;
            float z = random.nextFloat()*-600;
            float y = terrain.getHeightOfTerrain(x,z);
            if (i % 5 == 0){
                entities.add(new Entity(staticModel,new Vector3f(x,y,z),0,0,0,3));
            }else if (i%7 == 0){
                entities.add(new Entity(grass,new Vector3f(x,y,z),0,0,0,1));

            }else{
                entities.add(new Entity(fern,new Vector3f(x,y,z),0,0,0,0.6f));
            }
        }
        Player player = new Player(stanfordBunny,new Vector3f(100,0,-50),0,0,0,1);
        Camera camera = new Camera(player);

        while (!Display.isCloseRequested()) {
            camera.move();
            player.move(terrain); // Cas avec plusieurs terrains : tester pour savoir dans quel terrain le joueur se trouve

            renderer.processEntity(player);
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

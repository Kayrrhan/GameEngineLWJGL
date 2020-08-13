package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TextureModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

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
        fern.getTexture().setNumberOfRows(2);


        Light light = new Light(new Vector3f(0,10000,-7000),new Vector3f(0.4f,0.4f,0.4f));
        List<Light> lights = new ArrayList<>();
        lights.add(light);
        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(2,0,0),new Vector3f(1,0.01f,0.002f)));
        lights.add(new Light(new Vector3f(370,17,-300),new Vector3f(0,2,2),new Vector3f(1,0.01f,0.002f)));
        lights.add(new Light(new Vector3f(293,7,-305),new Vector3f(2,2,10),new Vector3f(1,0.01f,0.002f)));
        MasterRenderer renderer = new MasterRenderer(loader);

        // ==================== TEXTURES TERRAINS ==================== //

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        // =========================================================== //
        Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");
        List<Terrain> terrains = new ArrayList<>();
        terrains.add(terrain);
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
            }else{
                entities.add(new Entity(fern,new Vector3f(x,y,z),0,0,0,0.6f,random.nextInt(4)));
            }
        }
        Player player = new Player(stanfordBunny,new Vector3f(100,0,-50),0,0,0,1);
        entities.add(player);
        Camera camera = new Camera(player);
        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"),new Vector2f(0.5f,0.5f),new Vector2f(0.25f,0.25f));
        GuiTexture gui2 = new GuiTexture(loader.loadTexture("thinmatrix"),new Vector2f(0.3f,0.58f),new Vector2f(0.4f,0.4f));
        guis.add(gui);
        guis.add(gui2);
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MousePicker picker = new MousePicker(camera,renderer.getProjectionMatrix(),terrain);

        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader,waterShader,renderer.getProjectionMatrix());
        List<WaterTile> waters = new ArrayList<>();
        waters.add(new WaterTile(75,-75,0));
        while (!Display.isCloseRequested()) {
            camera.move();
            player.move(terrain); // Cas avec plusieurs terrains : tester pour savoir dans quel terrain le joueur se trouve
            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if (terrainPoint != null && Keyboard.isKeyDown(Keyboard.KEY_T)){
                entities.add(new Entity(staticModel,new Vector3f(terrainPoint.x,terrainPoint.y,terrainPoint.z),0,0,0,3));
            }
            renderer.renderScene(entities,terrains,lights,camera);
            waterRenderer.render(waters,camera);
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}

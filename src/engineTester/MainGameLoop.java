package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TextureModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {
    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();


        TextMaster.init(loader);
        FontType font = new FontType(loader.loadTexture("candara",0),new File("res/candara.fnt"));
        GUIText text = new GUIText("Bite !",3,font,new Vector2f(0.5f,0.5f),0.5f,true);

        ModelData data = OBJFileLoader.loadOBJ("tree");
        RawModel model = loader.loadToVAO(data.getVertices(),data.getTextureCoords(),data.getNormals(),data.getIndices());
        TextureModel staticModel = new TextureModel(model,new ModelTexture(loader.loadTexture("tree",-0.4f)));
        TextureModel grass = new TextureModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("grassTexture",-0.4f)));
        grass.getTexture().setHadTransparency(true);
        grass.getTexture().setUseFakeLightning(true);
        TextureModel fern = new TextureModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern",-0.4f)));
        fern.getTexture().setHadTransparency(true);
        fern.getTexture().setUseFakeLightning(true);
        fern.getTexture().setNumberOfRows(2);


        Light light = new Light(new Vector3f(0,10000,-7000),new Vector3f(0.4f,0.4f,0.4f));
        List<Light> lights = new ArrayList<>();
        lights.add(light);
//        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(2,0,0),new Vector3f(1,0.01f,0.002f)));
//        lights.add(new Light(new Vector3f(370,17,-300),new Vector3f(0,2,2),new Vector3f(1,0.01f,0.002f)));
//        lights.add(new Light(new Vector3f(293,7,-305),new Vector3f(2,2,10),new Vector3f(1,0.01f,0.002f)));
        MasterRenderer renderer = new MasterRenderer(loader);

        // ==================== TEXTURES TERRAINS ==================== //

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy",-0.4f));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt",-0.4f));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers",-0.4f));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path",-0.4f));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap",-0.4f));

        // =========================================================== //
        Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap,"heightmap");
        List<Terrain> terrains = new ArrayList<>();
        terrains.add(terrain);
        RawModel bunnyModel = OBJLoader.loadObjModel("stanfordBunny",loader);
        TextureModel stanfordBunny = new TextureModel(bunnyModel,new ModelTexture(loader.loadTexture("white",-0.4f)));

        List<Entity> entities = new ArrayList<>();
        List<Entity> normalMapEntities = new ArrayList<>();

        TextureModel barrelModel = new TextureModel(NormalMappedObjLoader.loadOBJ("barrel",loader),new ModelTexture(loader.loadTexture("barrel",-0.4f)));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
        barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal",-0.4f));
        normalMapEntities.add(new Entity(barrelModel,new Vector3f(75,10,-75),0,0,0,1));

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
//        GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"),new Vector2f(0.5f,0.5f),new Vector2f(0.25f,0.25f));
//        GuiTexture gui2 = new GuiTexture(loader.loadTexture("thinmatrix"),new Vector2f(0.3f,0.58f),new Vector2f(0.4f,0.4f));
//        guis.add(gui);
//        guis.add(gui2);
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        MousePicker picker = new MousePicker(camera,renderer.getProjectionMatrix(),terrain);
      //  WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader,waterShader,renderer.getProjectionMatrix(),buffers);
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile(75,-75, terrain.getHeightOfTerrain(75,-75)+1);
        waters.add(water);
        System.out.println(terrain.getHeightOfTerrain(75, -75) + 1);
        while (!Display.isCloseRequested()) {
            camera.move();
            player.move(terrain); // Cas avec plusieurs terrains : tester pour savoir dans quel terrain le joueur se trouve
            picker.update();
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if (terrainPoint != null && Keyboard.isKeyDown(Keyboard.KEY_T)){
                entities.add(new Entity(staticModel,new Vector3f(terrainPoint.x,terrainPoint.y,terrainPoint.z),0,0,0,3));
            }
            //render reflection texture
            buffers.bindReflectionFrameBuffer();
            float distance = 2* (camera.getPosition().y-water.getHeight());
            camera.getPosition().y-= distance;
            camera.invertPitch();
            renderer.renderScene(entities,normalMapEntities,terrains,lights,camera,new Vector4f(0,1,0,-water.getHeight()+1));
            camera.getPosition().y+= distance;
            camera.invertPitch();
            //render refraction texture
            buffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities,normalMapEntities,terrains,lights,camera,new Vector4f(0,-1,0,water.getHeight()+1));

            //render to screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.renderScene(entities,normalMapEntities,terrains,lights,camera,new Vector4f(0,0,0,0));
            waterRenderer.render(waters,camera,light);
            guiRenderer.render(guis);

            TextMaster.render();

            DisplayManager.updateDisplay();


        }
        TextMaster.cleanUp();
        buffers.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}

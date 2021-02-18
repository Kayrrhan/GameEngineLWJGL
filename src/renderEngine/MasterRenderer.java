package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TextureModel;
import normalMappingRenderer.NormalMappingRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
    public static final float FOV = 70; //Field of View
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;
    private Matrix4f projectionMatrix;
    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;
    private Map<TextureModel, List<Entity>> entities = new HashMap<>();
    private Map<TextureModel, List<Entity>> normalMapEntities = new HashMap<>();
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private List<Terrain> terrains = new ArrayList<>();
    public static final float RED = 0.5f;
    public static final float GREEN = 0.5f;
    public static final float BLUE = 0.5f;
    private SkyboxRenderer skyboxRenderer;
    private NormalMappingRenderer normalMapRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;


    private static MasterRenderer uniqueMasterRenderer = null;

    public static MasterRenderer Instance(Loader loader, Camera camera){
        if (uniqueMasterRenderer == null){
            uniqueMasterRenderer = new MasterRenderer(loader,camera);
        }
        return uniqueMasterRenderer;
    }

    private MasterRenderer(Loader loader, Camera camera){
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader,projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader,projectionMatrix);
        normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
        shadowMapRenderer = new ShadowMapMasterRenderer(camera);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane){
        prepare();
        shader.start();
        shader.loadToShadowSpaceMatrix(shadowMapRenderer.getToShadowMapSpaceMatrix());
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColour(RED,GREEN,BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        normalMapRenderer.render(normalMapEntities,clipPlane,lights,camera);
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColour(RED,GREEN,BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains,shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyboxRenderer.render(camera,RED,GREEN,BLUE);
        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }

    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    public void prepare(){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(RED,GREEN,BLUE,1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,getShadowMapTexture());
    }

    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void renderScene(List<Entity> entities,List<Entity> normalMapEntities, List<Terrain> terrains,List<Light> lights,Camera camera, Vector4f clipPlane){
        for (Terrain terrain:terrains){
            processTerrain(terrain);
        }
        for(Entity entity:entities){
            processEntity(entity);
        }

        for(Entity entity:normalMapEntities){
            processNormalMapEntity(entity);
        }
        render(lights,camera,clipPlane);
    }

    public void processEntity(Entity entity){
        TextureModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel,newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity){
        TextureModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if (batch != null){
            batch.add(entity);
        }else{
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel,newBatch);
        }
    }

    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float)Display.getHeight();
        float y_scale = (float) ((1f/Math.tan(Math.toRadians(FOV/2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE+NEAR_PLANE)/frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2*NEAR_PLANE*FAR_PLANE)/frustum_length);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public int getShadowMapTexture(){
        return shadowMapRenderer.getShadowMap();
    }

    public void renderShadowMap(List<Entity> entityList, Light sun){
        for(Entity entity: entityList){
            processEntity(entity);
        }
        shadowMapRenderer.render(entities,sun);
        entities.clear();
    }

    public void cleanUp(){
        shader.cleanUp();
        normalMapRenderer.cleanUp();
        terrainShader.cleanUp();
        shadowMapRenderer.cleanUp();
    }

}

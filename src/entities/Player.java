package entities;

import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import models.TextureModel;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;

import java.security.Key;
import java.util.List;

public class Player extends Entity {

    private static final float WALK_SPEED = 20;
    private static final float RUN_SPEED = 50;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

    private boolean isInAir = false;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private static Player uniquePlayer = null;

    public static Player Instance(TextureModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale){
        if (uniquePlayer == null)
            uniquePlayer = new Player(model, position, rotX, rotY, rotZ, scale);
        return uniquePlayer;
    }

    private Player(TextureModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(List<Terrain> terrains){

        checkInputs();
        super.increaseRotation(0,currentTurnSpeed* DisplayManager.getFrameTimeSeconds(),0);
        float distance = currentSpeed*DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0,dz);
        upwardsSpeed+= GRAVITY*DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed*DisplayManager.getFrameTimeSeconds(),0);
        float terrainHeight = 0;

        Terrain terrain = selectTerrain(terrains);
        if (terrain != null) {
            terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
        }
//        System.out.println("("+terrain.getX()/Terrain.SIZE+" ; "+terrain.getZ()/Terrain.SIZE+") : ("+(int)(super.getPosition().x/Terrain.SIZE)+" ; "+(int)((super.getPosition().z/Terrain.SIZE)-1)+ ") //"+super.getPosition().x+" | "+super.getPosition().z);
        if (super.getPosition().y<terrainHeight){
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
    }


    public void move(Terrain terrain){

        checkInputs();
        super.increaseRotation(0,currentTurnSpeed* DisplayManager.getFrameTimeSeconds(),0);
        float distance = currentSpeed*DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0,dz);
        upwardsSpeed+= GRAVITY*DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed*DisplayManager.getFrameTimeSeconds(),0);
        float terrainHeight = 0;
        if (terrain != null) {
            terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x,super.getPosition().z);
        }
//        System.out.println("("+terrain.getX()/Terrain.SIZE+" ; "+terrain.getZ()/Terrain.SIZE+") : ("+(int)(super.getPosition().x/Terrain.SIZE)+" ; "+(int)((super.getPosition().z/Terrain.SIZE)-1)+ ") //"+super.getPosition().x+" | "+super.getPosition().z);
        if (super.getPosition().y<terrainHeight){
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
    }



    private Terrain selectTerrain(List<Terrain> terrains){
        Terrain terrain = null;
        for(Terrain terrainTmp:terrains) {
            if (terrainTmp.getX()/Terrain.SIZE == (int)(super.getPosition().x/Terrain.SIZE) && terrainTmp.getZ()/Terrain.SIZE == (int)((super.getPosition().z/Terrain.SIZE)-1)){
                terrain = terrainTmp;
            }
        }
        return terrain;
    }

    private void jump(){
        if (!isInAir){
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }

    }

    private void checkInputs(){
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)){
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                this.currentSpeed = RUN_SPEED;
            else
                this.currentSpeed = WALK_SPEED;
        }else if (Keyboard.isKeyDown(Keyboard.KEY_S)){
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                this.currentSpeed = -RUN_SPEED;
            else
                this.currentSpeed = -WALK_SPEED;
        }else{
            this.currentSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        }else if (Keyboard.isKeyDown(Keyboard.KEY_Q)){
            this.currentTurnSpeed = TURN_SPEED;
        }else{
            this.currentTurnSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            jump();
        }
    }

    public static float getGRAVITY() {
        return GRAVITY;
    }
}

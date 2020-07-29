package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(100,35,50);
    private float pitch = 10;
    private float yaw = 0;
    private float roll;

    public Camera() {
    }

    public void move(){
//        if (Keyboard.isKeyDown(Keyboard.KEY_Z)){
//            position.z-= 0.2f;
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_D)){
//            position.x+= 0.2f;
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_Q)){
//            position.x -= 0.2f;
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_S)){
//            position.z+= 0.2f;
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
//            position.y -= 0.2f;
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_UP)){
//            position.y+= 0.2f;
//        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}

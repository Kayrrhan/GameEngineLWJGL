package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(100,35,50);
    private float pitch = 10;
    private float yaw = 0;
    private float roll;
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;

    private Player player;

    private static Camera uniqueCamera = null;

    public static Camera Instance(Player player){
        if (uniqueCamera == null){
            uniqueCamera = new Camera(player);
        }
        return uniqueCamera;
    }

    private Camera(Player player) {
        this.player = player;
    }

    public void move(){
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        yaw = 180 - (player.getRotY() + angleAroundPlayer);

    }

    public void invertPitch(){
        this.pitch = -pitch;
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

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.getRotY()+angleAroundPlayer;
        float offsetX = (float) (horizontalDistance*Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance*Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x-offsetX;
        position.z = player.getPosition().z-offsetZ;
        position.y = player.getPosition().y+verticalDistance;
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer*Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer*Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() *0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch(){
        if (Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() *0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer(){
        if (Mouse.isButtonDown(0)){
            float angleChange = Mouse.getDX()*0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
}
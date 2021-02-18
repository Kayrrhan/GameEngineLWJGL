package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;
    private static final int FPS = 120;
    private static long lastFrameTime;
    private static float delta;


    public static void createDisplay(){
        ContextAttribs attribs = new ContextAttribs(3,3).withForwardCompatible(true).withProfileCore(true);
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH,HEIGHT));
            Display.create(new PixelFormat().withSamples(4),attribs);
            Display.setTitle("Premiers Test Display");
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        GL11.glViewport(0,0,WIDTH,HEIGHT);
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay(){
        Display.sync(FPS);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime)/1000f;
        lastFrameTime = currentFrameTime;

    }

    public static void closeDisplay(){
        Display.destroy();
    }

     private static long getCurrentTime(){
        return Sys.getTime()*1000/Sys.getTimerResolution();
     }

     public static float getFrameTimeSeconds(){
        return delta;
     }
}
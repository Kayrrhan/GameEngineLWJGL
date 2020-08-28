package postProcessing;

import gaussianBlur.HorizontalBlur;
import gaussianBlur.VerticalBlur;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import models.RawModel;
import renderEngine.Loader;

public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static VerticalBlur vBlur;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur2;
	private static HorizontalBlur hBlur2;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();
		hBlur = new HorizontalBlur(Display.getWidth()/8,Display.getHeight()/8);
		vBlur = new VerticalBlur(Display.getWidth()/8,Display.getHeight()/8);
		hBlur2 = new HorizontalBlur(Display.getWidth()/2,Display.getHeight()/2);
		vBlur2 = new VerticalBlur(Display.getWidth()/2,Display.getHeight()/2);
	}
	
	public static void doPostProcessing(int colourTexture){
		start();
		hBlur2.render(colourTexture);
		vBlur2.render(hBlur2.getOutputTexture());
		hBlur.render(vBlur2.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());
		contrastChanger.render(vBlur.getOutputTexture());
		end();
	}
	
	public static void cleanUp(){
		hBlur.cleanUp();
		vBlur.cleanUp();
		hBlur2.cleanUp();
		vBlur2.cleanUp();
		contrastChanger.cleanUp();
	}
	
	private static void start(){
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	private static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}


}

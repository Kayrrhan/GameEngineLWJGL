package main;

import lensFlare.FlareManager;
import lensFlare.FlareRenderer;
import lensFlare.FlareTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import extra.Camera;
import loaders.LoaderSettings;
import loaders.SceneLoader;
import loaders.SceneLoaderFactory;
import renderEngine.RenderEngine;
import scene.Scene;
import sunRenderer.Sun;
import sunRenderer.SunRenderer;
import textures.Texture;
import utils.MyFile;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Arrays;

public class MainApp {

	public static void main(String[] args) {

		RenderEngine engine = RenderEngine.init();
		SceneLoader loader = SceneLoaderFactory.createSceneLoader();
		Scene scene = loader.loadScene(new MyFile(LoaderSettings.RES_FOLDER, "Socuwan Scene"));

		engine.renderEnvironmentMap(scene.getEnvironmentMap(), scene, new Vector3f(0, 2, 0));

		MyFile flareFolder = new MyFile("res", "lensFlare");
		//loading textures for lens flare
		Texture texture1 = Texture.newTexture(new MyFile(flareFolder, "tex1.png")).normalMipMap().create();
		Texture texture2 = Texture.newTexture(new MyFile(flareFolder, "tex2.png")).normalMipMap().create();
		Texture texture3 = Texture.newTexture(new MyFile(flareFolder, "tex3.png")).normalMipMap().create();
		Texture texture4 = Texture.newTexture(new MyFile(flareFolder, "tex4.png")).normalMipMap().create();
		Texture texture5 = Texture.newTexture(new MyFile(flareFolder, "tex5.png")).normalMipMap().create();
		Texture texture6 = Texture.newTexture(new MyFile(flareFolder, "tex6.png")).normalMipMap().create();
		Texture texture7 = Texture.newTexture(new MyFile(flareFolder, "tex7.png")).normalMipMap().create();
		Texture texture8 = Texture.newTexture(new MyFile(flareFolder, "tex8.png")).normalMipMap().create();
		Texture texture9 = Texture.newTexture(new MyFile(flareFolder, "tex9.png")).normalMipMap().create();
		Texture sun = Texture.newTexture(new MyFile(flareFolder, "sun.png")).normalMipMap().create();

		FlareManager lensFlare = new FlareManager(0.4f,new FlareTexture(texture6,0.5f), new FlareTexture(texture4,0.23f),new FlareTexture(texture2,0.1f),new FlareTexture(texture7,0.05f), new FlareTexture(texture3,0.06f),new FlareTexture(texture5,0.07f),new FlareTexture(texture7,0.2f), new FlareTexture(texture3,0.07f),new FlareTexture(texture5,0.3f),new FlareTexture(texture4,0.4f), new FlareTexture(texture8,0.6f));

		//init sun and set sun direction
		Vector3f lightDir = new Vector3f(0.1f, -0.34f, 3f);
		Sun theSun = new Sun(sun, 55);
		SunRenderer sunRenderer = new SunRenderer();
		theSun.setDirection(lightDir.x, lightDir.y, lightDir.z);
		scene.setLightDirection(lightDir);

		FlareTexture flare = new FlareTexture(texture8,0.5f);
		flare.setScreenPos(new Vector2f(0.5f,0.5f));
		FlareRenderer flareRenderer= new FlareRenderer();

		while (!Display.isCloseRequested()) {
			((Camera) scene.getCamera()).move();
			engine.renderScene(scene);
			sunRenderer.render(theSun, scene.getCamera());
			lensFlare.render(scene.getCamera(),theSun.getWorldPosition(scene.getCamera().getPosition()));
			engine.update();
		}
		lensFlare.cleanUp();
		flareRenderer.cleanUp();
		sunRenderer.cleanUp();
		scene.delete();
		engine.close();

	}

}

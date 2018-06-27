package engineTester;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.AudioPlayer;
import toolbox.MazeGenerator;

public class MainGameLoop {
	
	private static final int DIMENSION = 3;
	private static final int CUBE_SCALE = 15;
	
	public static List<Entity> entities = new ArrayList<>();
	public static List<Entity> cubes = new ArrayList<>();
	public static List<Entity> cubesAll = new ArrayList<>();

	public static void main(String[] args) {
		
		
		MazeGenerator maze = new MazeGenerator(DIMENSION);
		maze.updateGrid();
		
		AudioPlayer.load();
		AudioPlayer.getMusic("music").loop();

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		//****Terrain texture stuff****///
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFLowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		///*******
		

		RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
		RawModel fernModel = OBJLoader.loadObjModel("fern", loader); 
		RawModel cubeModel = OBJLoader.loadObjModel("cube", loader);
		
		TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));
		fern.getTexture().setHasTransparency(true);
		TexturedModel cubeTextured = new TexturedModel(cubeModel, new ModelTexture(loader.loadTexture("texture")));
		cubeTextured.getTexture().setUseFakeLighting(true);
		
		Terrain terrain = new Terrain(0,-1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1,-1, loader, texturePack, blendMap);
		
		
		Random random = new Random();
		for(int i=0;i<500;i++) {
			entities.add(new Entity(grass, new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()* -600),0,0,0,1));
			entities.add(new Entity(fern, new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()* -600),0,0,0,0.6f));
		}
		
		float wallX = 100;
		float wallZ = -80;
		for(int i=0;i<maze.getGrid().length;i++) {
			for(int j=0;j<maze.getGrid()[i].length;j++) {
				if(maze.getGrid()[i][j] == 'X') {
					cubesAll.add(new Entity(cubeTextured, new Vector3f(wallX,0,wallZ),0,0,0,CUBE_SCALE));
				}
				if(maze.getGrid()[i][j] == 'X' && !((i==0 && j==1) || (i==maze.getGrid().length-1 && j==maze.getGrid()[i].length-2))) {
					cubes.add(new Entity(cubeTextured, new Vector3f(wallX,0,wallZ),0,0,0,CUBE_SCALE));
				}
				wallX+=CUBE_SCALE;
			}
			wallX=100;
			wallZ-=CUBE_SCALE;
		}
		
		Light light = new Light(new Vector3f(100,200,100), new Vector3f(1,1,1));
		
		MasterRenderer renderer = new MasterRenderer();
		
		RawModel person = OBJLoader.loadObjModel("person", loader);
		TexturedModel playerModel = new TexturedModel(person, new ModelTexture(loader.loadTexture("playerTexture1")));
		Player player = new Player(playerModel, new Vector3f(100,7.5f,-50),0,180,0,0.35f);
		
		Camera camera = new Camera(player);
		
		
		
		while(!Display.isCloseRequested()) {
			camera.move();
			player.move();
			renderer.processEntity(player);
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			for(Entity entity:entities) {
				renderer.processEntity(entity);
			}
			for(Entity cube:cubes) {
				renderer.processEntity(cube);
			}
			
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}

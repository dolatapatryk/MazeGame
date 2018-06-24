package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MazeGenerator;

public class MainGameLoop {

	public static void main(String[] args) {
		
		final int DIMENSION = 10;
		
		MazeGenerator maze = new MazeGenerator(DIMENSION);
		maze.updateGrid();

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
		
		ModelData data = OBJFileLoader.loadOBJ("tree");
		RawModel model = loader.loadToVao(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		RawModel grassModel = OBJLoader.loadObjModel("grassModel", loader);
		RawModel fernModel = OBJLoader.loadObjModel("fern", loader); 
		RawModel cubeModel = OBJLoader.loadObjModel("cube", loader);
		
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(loader.loadTexture("fern")));
		fern.getTexture().setHasTransparency(true);
		TexturedModel cubeTextured = new TexturedModel(cubeModel, new ModelTexture(loader.loadTexture("texture")));
		cubeTextured.getTexture().setUseFakeLighting(true);
		
		
		List<Entity> entities = new ArrayList<>();
		List<Entity> cubes = new ArrayList<>();
		Random random = new Random();
		for(int i=0;i<500;i++) {
			//entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()* -600),0,0,0,3));
			entities.add(new Entity(grass, new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()* -600),0,0,0,1));
			entities.add(new Entity(fern, new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()* -600),0,0,0,0.6f));
			//entities.add(new Entity(cubeTextured, new Vector3f(random.nextFloat()*800-400,0,random.nextFloat()* -600),0,0,0,15));
		}
//		float shift =-80;
//		for(int i=0;i<10;i++) {
//			cubes.add(new Entity(cubeTextured, new Vector3f(100,0,shift),0,0,0,15));
//			shift-=cubes.get(0).getScale();
//		}
//		shift= 85;
//		float lastPosition = cubes.get(cubes.size()-1).getPosition().getZ();
//		for(int i=0;i<10;i++) {
//			cubes.add(new Entity(cubeTextured, new Vector3f(shift,0,lastPosition),0,0,0,15));
//			shift-=cubes.get(0).getScale();
//		}
		float wallX = 100;
		float wallZ = -80;
		for(int i=0;i<maze.getGrid().length;i++) {
			for(int j=0;j<maze.getGrid()[i].length;j++) {
				if(maze.getGrid()[i][j] == 'X') {
					cubes.add(new Entity(cubeTextured, new Vector3f(wallX,0,wallZ),0,0,0,15));
				}
				wallX+=15;
			}
			wallX=100;
			wallZ-=15;
		}
		
		
		Light light = new Light(new Vector3f(20000,20000,2000), new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0,-1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1,-1, loader, texturePack, blendMap);
		
		
		MasterRenderer renderer = new MasterRenderer();
		
		RawModel person = OBJLoader.loadObjModel("person", loader);
		TexturedModel playerModel = new TexturedModel(person, new ModelTexture(loader.loadTexture("playerTexture1")));
		
		Player player = new Player(playerModel, new Vector3f(100,0,-50),0,180,0,0.35f);
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

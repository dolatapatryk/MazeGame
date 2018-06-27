package entities;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import models.TexturedModel;
import renderEngine.DisplayManager;

public class Player extends Entity{
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	
	private static final float TERRAIN_HEIGHT = 0;
	
	private static final float DELTA = 8.5f;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;
	private boolean collision = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public void move() {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed*DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		if(!checkCollisionWithCubes(MainGameLoop.cubes)) {
		super.increasePosition(0, upwardsSpeed*DisplayManager.getFrameTimeSeconds(), 0);
		}
		
		if(super.getPosition().y<TERRAIN_HEIGHT) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = TERRAIN_HEIGHT;
		}
	}
	
	private void jump() {
		if(!isInAir) {
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		}else {
			this.currentSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		}else {
			this.currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}

	
	private boolean checkCollisionWithCubes(List<Entity> cubes) {
		for(Entity cube:cubes) {
			float middleX  = cube.getPosition().x;
			float middleZ = cube.getPosition().z;
			float leftX = middleX - DELTA;
			float rightX = middleX + DELTA;
			float frontZ = middleZ + DELTA;
			float backZ = middleZ - DELTA;
			
			if(super.getPosition().y<=7.5f) {
				if((super.getPosition().x>=leftX && super.getPosition().x<=rightX) && (super.getPosition().z<=frontZ && super.getPosition().z>=backZ)) { 
					if(super.getPosition().x>=leftX && super.getPosition().x<=rightX && super.getPosition().z<=frontZ+0.5f && super.getPosition().z>=frontZ-0.5f) {
						super.setPosition(new Vector3f(super.getPosition().x,super.getPosition().y,super.getPosition().z+0.2f));
					}else if(super.getPosition().x>=rightX-0.5f && super.getPosition().x<=rightX+0.5f && super.getPosition().z<=frontZ && super.getPosition().z>=backZ) {
						super.setPosition(new Vector3f(super.getPosition().x+0.2f,super.getPosition().y,super.getPosition().z));
					}else if(super.getPosition().x>=leftX && super.getPosition().x<=rightX && super.getPosition().z<=backZ+0.5f && super.getPosition().z>=backZ-0.5f) {
						super.setPosition(new Vector3f(super.getPosition().x,super.getPosition().y,super.getPosition().z-0.2f));
					}else if(super.getPosition().x>=leftX-0.5f && super.getPosition().x<=leftX+0.5f && super.getPosition().z<=frontZ && super.getPosition().z>=backZ) {
						super.setPosition(new Vector3f(super.getPosition().x-0.2f,super.getPosition().y,super.getPosition().z));
					}
			return true;
		}
		}
	}
		return false;
	}
	
}

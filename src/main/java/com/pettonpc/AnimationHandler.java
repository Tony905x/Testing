package com.pettonpc;

import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.RuneLiteObject;

public class AnimationHandler {
	private final Client client;
	private RuneLiteObject transmogObject; // Remove the final keyword
	private final int SPAWN_ANIMATION = 197;
	private boolean isSpawning;
	int SPAWN_ANIMATION_DURATION = 10000;

	public AnimationHandler(Client client) {
		this.client = client;
		// Remove the line this.transmogObject = transmogObject;
	}

	public void setTransmogObject(RuneLiteObject transmogObject) {this.transmogObject = transmogObject;}

	public void triggerSpawnAnimation() {
		Animation spawnAnimation = client.loadAnimation(SPAWN_ANIMATION);
		transmogObject.setAnimation(spawnAnimation);
		isSpawning = true;
		System.out.println("spawnAnimation starting");

		new Thread(() -> {
			try {
				Thread.sleep(SPAWN_ANIMATION_DURATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isSpawning = false;
			transmogObject.setShouldLoop(false);
			System.out.println("spawnAnimation has ended: ");
		}).start();
	}
	public boolean isSpawning() {
		return isSpawning;
	}
}

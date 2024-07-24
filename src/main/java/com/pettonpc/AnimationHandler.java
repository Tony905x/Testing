package com.pettonpc;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.RuneLiteObject;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;


public class AnimationHandler {

	@Inject
	private Client client;
	@Inject
	private PtnConfig config;
	@Inject
	private ClientThread clientThread;

	@Setter
	public RuneLiteObject transmogObject; // Remove the final keyword
	private final int SPAWN_ANIMATION = 197;
	private boolean isSpawning;
	int SPAWN_ANIMATION_DURATION = 10000;
	private Thread animationThread;

	@Provides
	PtnConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PtnConfig.class);
	}

	public AnimationHandler(Client client, PtnConfig config) {
		this.client = client;
		this.config = config;
	}


	public boolean isSpawning() {
		return isSpawning;
	}


	public void triggerSpawnAnimation() {
		if (transmogObject == null) {
			System.out.println("transmogObject is null!");
			return;
		}

		// Cancel the previous animation thread if it's still running
		if (animationThread != null && animationThread.isAlive()) {
			animationThread.interrupt();
		}
//		Animation spawnAnimation = client.loadAnimation(SPAWN_ANIMATION);
		Animation spawnAnimation = client.loadAnimation(config.spawnAnimationID());
		isSpawning = true;
		transmogObject.setAnimation(spawnAnimation);
		transmogObject.setShouldLoop(true);



		System.out.println("spawnAnimation starting");

		animationThread = new Thread(() -> {
			try {
//				Thread.sleep(SPAWN_ANIMATION_DURATION);
				Thread.sleep(config.spawnAnimationDuration());
				System.out.println("animation: " + config.spawnAnimationID());
				System.out.println("duration: " + config.spawnAnimationDuration());
			} catch (InterruptedException e) {
				// The thread was interrupted, so stop the animation and return
				isSpawning = false;
				transmogObject.setShouldLoop(false);
				return;
			}
			transmogObject.setShouldLoop(false);
			isSpawning = false;
			System.out.println("spawnAnimation has ended: ");
		});
		animationThread.start();
	}
	// Add this method to cancel the current animation
	public void cancelCurrentAnimation() {
		if (animationThread != null && animationThread.isAlive()) {
			animationThread.interrupt();
		}
		isSpawning = false;
		if (transmogObject != null) {
			transmogObject.setShouldLoop(false);
		}
	}
}
package com.pettonpc;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
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
	private NpcFollowerConfig config;

	@Inject
	private ClientThread clientThread;

	@Setter
	private RuneLiteObject transmogObject;

	private boolean isSpawning;
	private Thread animationThread;
	private int currentFrame;
	private int previousFrame;

	@Getter
	private PlayerState playerState;

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(NpcFollowerConfig.class);
	}

	public AnimationHandler(Client client, NpcFollowerConfig config) {
		this.client = client;
		this.config = config;
	}

	//	public boolean isSpawning() {
//		return isSpawning;
//	}

	public void triggerSpawnAnimation() {
		if (transmogObject == null) {
			return;
		}
		cancelCurrentAnimation();

		Animation spawnAnimation = client.loadAnimation(config.spawnAnimationID());
		playerState = PlayerState.SPAWNING;
		transmogObject.setAnimation(spawnAnimation);
		transmogObject.setShouldLoop(true);

		animationThread = new Thread(() -> {
			try {
				Thread.sleep(config.spawnAnimationDuration());
//				currentFrame = transmogObject.getAnimationFrame();
//				System.out.println("currentFrame " + currentFrame);
				System.out.println("playerState " + playerState);
			} catch (InterruptedException e) {
				playerState = PlayerState.STANDING;
				transmogObject.setShouldLoop(false);
				return;
			}
			playerState = PlayerState.STANDING;
			transmogObject.setShouldLoop(false);
		});
		animationThread.start();
	}

	public void cancelCurrentAnimation() {
		if (animationThread != null && animationThread.isAlive()) {
			animationThread.interrupt();
		}
		playerState = PlayerState.STANDING;
		if (transmogObject != null) {
			transmogObject.setShouldLoop(false);
		}
	}

	public void checkAnimationFrame() {
//		if (playerState == PlayerState.SPAWNING) {
			int currentFrame = transmogObject.getAnimationFrame();
			System.out.println("currentFrame " + currentFrame);
			System.out.println("playerState " + playerState);
//		}
	}
}

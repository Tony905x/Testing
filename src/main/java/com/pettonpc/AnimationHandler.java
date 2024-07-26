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
	private NpcFollowerConfig config;

	@Inject
	private ClientThread clientThread;

	@Setter
	private RuneLiteObject transmogObject;

	private boolean isSpawning;
	private Thread animationThread;

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(NpcFollowerConfig.class);
	}

	public AnimationHandler(Client client, NpcFollowerConfig config) {
		this.client = client;
		this.config = config;
	}

	public boolean isSpawning() {
		return isSpawning;
	}

	public void triggerSpawnAnimation() {
		if (transmogObject == null) {
			return;
		}

		cancelCurrentAnimation();

		Animation spawnAnimation = client.loadAnimation(config.spawnAnimationID());
		isSpawning = true;
		transmogObject.setAnimation(spawnAnimation);
		transmogObject.setShouldLoop(true);

		animationThread = new Thread(() -> {
			try {
				Thread.sleep(config.spawnAnimationDuration());
			} catch (InterruptedException e) {
				isSpawning = false;
				transmogObject.setShouldLoop(false);
				return;
			}
			transmogObject.setShouldLoop(false);
			isSpawning = false;
		});
		animationThread.start();
	}

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

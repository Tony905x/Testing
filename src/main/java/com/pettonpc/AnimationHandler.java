package com.pettonpc;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Setter;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import java.util.*;

public class AnimationHandler {
	@Inject
	private Client client;

	@Inject
	private NpcFollowerConfig config;

	@Inject
	private ClientThread clientThread;

	@Setter
	private RuneLiteObject transmogObject;

	@Setter
	private List<RuneLiteObject> transmogObjects;

	private boolean isSpawning;
	private Thread animationThread;
//	private List<RuneLiteObject> transmogObjects;
	private NpcFollowerPlugin npcFollowerPlugin;
	private int previousWalkingFrame = -1;
	private int previousStandingFrame = -1;
	private int currentFrame;

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

	public void setNpcFollowerPlugin(NpcFollowerPlugin npcFollowerPlugin) {
		this.npcFollowerPlugin = npcFollowerPlugin;
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


	void handleWalkingAnimation(NPC follower)
	{
		if (isSpawning()) {
			cancelCurrentAnimation();
			return;
		}


		NpcData selectedNpc = config.selectedNpc();
		NPC currentFollower = client.getFollower();
		int walkingAnimationId = (config.enableCustom()) ? config.walkingAnimationId() : selectedNpc.getWalkAnim();
		Animation walkingAnimation = client.loadAnimation(walkingAnimationId);

		if (selectedNpc == null)
		{
			return;
		}


		if (walkingAnimation == null)
		{
			return;
		}

		if (currentFollower == null)
		{
			return;
		}

		transmogObjects.forEach(transmogObject ->
		{
			if (transmogObject != null)
			{
				currentFrame = transmogObject.getAnimationFrame();
				transmogObject.setActive(true);
				transmogObject.setShouldLoop(true);

				if (previousWalkingFrame == -1)
				{
					transmogObject.setAnimation(walkingAnimation);
				}

				if (previousWalkingFrame > currentFrame)
				{
					transmogObject.setAnimation(walkingAnimation);
				}
				previousWalkingFrame = currentFrame;
			}
		});
	}

	//Standing Animation with similar functionality as the walking method
	void handleStandingAnimation(NPC follower)
	{
		NpcData selectedNpc = config.selectedNpc();
		if (selectedNpc == null)
		{
			return;
		}

		int standingAnimationId;

		if (config.enableCustom())
		{
			standingAnimationId = config.standingAnimationId();
		}
		else
		{
			standingAnimationId = selectedNpc.getStandingAnim();
		}

		Animation standingAnimation = client.loadAnimation(standingAnimationId);
		NPC followerLoop = client.getFollower();
		for (RuneLiteObject transmogObject : transmogObjects)
		{
			if (transmogObject != null && followerLoop != null)
			{
				currentFrame = transmogObject.getAnimationFrame();

				transmogObject.setActive(true);
				transmogObject.setShouldLoop(true);
				if (previousStandingFrame == -1)
				{
					transmogObject.setAnimation(standingAnimation);
				}

				if (previousStandingFrame > currentFrame)
				{
					transmogObject.setAnimation(standingAnimation);
				}
				previousStandingFrame = currentFrame;
			}
		}
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

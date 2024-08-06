package com.pettonpc;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Animation;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import java.util.*;

@SuppressWarnings("LombokSetterMayBeUsed")
public class AnimationHandler
{
	private Client client;
	private NpcFollowerConfig config;
	private ClientThread clientThread;
	private RuneLiteObject transmogObject;
	private List<RuneLiteObject> transmogObjects;
	private boolean isSpawning;
	private Thread animationThread;
	private NpcFollowerPlugin npcFollowerPlugin;
	private int previousWalkingFrame = -1;
	private int previousStandingFrame = -1;
	private int currentFrame;

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NpcFollowerConfig.class);
	}

	public AnimationHandler(Client client, NpcFollowerConfig config)
	{
		this.client = client;
		this.config = config;
	}

	public boolean isSpawning()
	{
		return isSpawning;
	}

//	public void setNpcFollowerPlugin(NpcFollowerPlugin npcFollowerPlugin)
//	{
//		this.npcFollowerPlugin = npcFollowerPlugin;
//	}

//	public void setTransmogObject(RuneLiteObject transmogObject)
//	{
//		this.transmogObject = transmogObject;
//	}

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
		this.transmogObjects = transmogObjects;
	}

	public void triggerSpawnAnimation()
	{
		if (transmogObject == null)
		{
			System.out.println("triggerSpawnAnimation if (transmogObject == null) {");
			return;
		}

		cancelCurrentAnimation();

		Animation spawnAnimation = client.loadAnimation(config.spawnAnimationID());
		isSpawning = true;
		transmogObject.setAnimation(spawnAnimation);
		transmogObject.setShouldLoop(true);

		System.out.println("triggerSpawnAnimation " + transmogObject);

		animationThread = new Thread(() -> {
			try
			{
				Thread.sleep(config.spawnAnimationDuration());
				System.out.println("animationThread = new Thread(() -> {" + config.spawnAnimationDuration());
				System.out.println("setShouldloop in thread" + transmogObject.getAnimation());
			}
			catch (InterruptedException e)
			{
				System.out.println("} catch (InterruptedException e) { ");
				isSpawning = false;
				transmogObject.setShouldLoop(false);
				return;
			}
			transmogObject.setShouldLoop(false);
			isSpawning = false;
		});
		animationThread.start();
	}

	public synchronized void handleWalkingAnimation(NPC follower)
	{
		if (isSpawning())
		{
			cancelCurrentAnimation();
			return;
		}

		NpcData selectedNpc = config.selectedNpc();
		NPC currentFollower = client.getFollower();
		int walkingAnimationId = (config.enableCustom()) ? config.walkingAnimationId() : selectedNpc.getWalkAnim();
		Animation walkingAnimation = client.loadAnimation(walkingAnimationId);

		if (selectedNpc == null || walkingAnimation == null || currentFollower == null)
		{
			return;
		}

		transmogObjects.forEach(transmogObject -> {
			if (transmogObject != null)
			{
				System.out.println("TransmogObject walking " + transmogObject );
				currentFrame = transmogObject.getAnimationFrame();
				transmogObject.setActive(true);
				transmogObject.setShouldLoop(true);

				if (previousWalkingFrame == -1 || previousWalkingFrame > currentFrame)
				{
					transmogObject.setAnimation(walkingAnimation);
				}
				previousWalkingFrame = currentFrame;
			}
		});
	}

	public synchronized void handleStandingAnimation(NPC follower)
	{
		NpcData selectedNpc = config.selectedNpc();
		if (selectedNpc == null)
		{
			return;
		}

		int standingAnimationId = (config.enableCustom()) ? config.standingAnimationId() : selectedNpc.getStandingAnim();
		Animation standingAnimation = client.loadAnimation(standingAnimationId);
		NPC followerLoop = client.getFollower();

		for (RuneLiteObject transmogObject : transmogObjects)
		{
			if (transmogObject != null && followerLoop != null)
			{
				currentFrame = transmogObject.getAnimationFrame();
				System.out.println("TransmogObject standing " + transmogObject );
				transmogObject.setActive(true);
				transmogObject.setShouldLoop(true);
				if (previousStandingFrame == -1 || previousStandingFrame > currentFrame)
				{
					transmogObject.setAnimation(standingAnimation);
				}
				previousStandingFrame = currentFrame;
			}
		}
	}

	public void cancelCurrentAnimation()
	{
		if (animationThread != null && animationThread.isAlive())
		{
			animationThread.interrupt();
		}
		isSpawning = false;
		if (transmogObject != null)
		{
			transmogObject.setShouldLoop(false);
		}
	}
}

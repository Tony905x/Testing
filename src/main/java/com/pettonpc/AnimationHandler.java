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
//	private RuneLiteObject transmogObject;
	private List<RuneLiteObject> transmogObjects;
//	private boolean isSpawning;
	private Thread animationThread;
	private NpcFollowerPlugin npcFollowerPlugin;
	private int previousWalkingFrame = -1;
	private int previousStandingFrame = -1;
	private int currentFrame;
	private PlayerStateTracker playerStateTracker;
	private PlayerState playerState;
	private PlayerState currentState;

	public void setPlayerStateTracker(PlayerStateTracker playerStateTracker)
	{
		this.playerStateTracker = playerStateTracker;
		this.currentState = (playerStateTracker != null) ? playerStateTracker.getCurrentState() : null;
	}

	public AnimationHandler(Client client, NpcFollowerConfig config, PlayerStateTracker playerStateTracker)
	{
		this.client = client;
		this.config = config;
		this.playerStateTracker = playerStateTracker;
//		this.currentState = playerStateTracker.getCurrentState();
	}

//	PlayerState currentState = playerStateTracker.getCurrentState();

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NpcFollowerConfig.class);
	}



//	public boolean isSpawning()
//	{
//		return isSpawning;
//	}

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

	public void triggerSpawnAnimation(NPC follower)
	{

			System.out.println("start of block");
//			if (playerState != null)
//			{
//				PlayerState currentState = playerStateTracker.getCurrentState();
//			cancelCurrentAnimation();

			NpcData selectedNpc = config.selectedNpc();
			if (selectedNpc == null)
			{
				return;
			}

//				int spawnAnimationId = (config.enableCustom()) ? config.spawnAnimationID() : selectedNpc.getStandingAnim();
			int spawnAnimationId = config.spawnAnimationID();
			Animation spawnAnimation = client.loadAnimation(spawnAnimationId);
//				NPC follower = client.getFollower();

//			if (selectedNpc == null || standingAnimation == null || followerLoop == null)
//			{
//				return;
//			}
//		Thread animationThread = new Thread(() -> {
//			System.out.println("before for");
			for (RuneLiteObject transmogObject : transmogObjects)
			{
				int previousSpawnFrame = -1;
				int currentSpawnFrame;
				playerStateTracker.setCurrentState(PlayerState.IDLE);
//				System.out.println("in for");
				if (transmogObject != null && follower != null)
				{
					currentSpawnFrame = transmogObject.getAnimationFrame();
//				System.out.println("TransmogObject standing " + transmogObject );
					transmogObject.setActive(true);
					transmogObject.setAnimation(spawnAnimation);
					transmogObject.setShouldLoop(false);
					playerStateTracker.setCurrentState(PlayerState.IDLE);

//						transmogObject.setShouldLoop(true);
//					if (previousSpawnFrame == -1)
//					{
//						transmogObject.setAnimation(spawnAnimation);
//						System.out.println("transmogObject.setAnimation(spawnAnimation); " + spawnAnimation);
//						playerStateTracker.setCurrentState(PlayerState.IDLE);
//						cancelCurrentAnimation();
//						return;
//					}
//
//					if (previousSpawnFrame > currentSpawnFrame)

					System.out.println(previousSpawnFrame + " " + currentSpawnFrame);
					if (previousSpawnFrame > currentSpawnFrame)
					{
						System.out.println("in loop");
//						playerStateTracker.setCurrentState(PlayerState.IDLE);
//						transmogObject.setActive(true);
//						playerStateTracker.updateFollowerState(follower);
//						playerStateTracker.updateFollowerMovement(follower);
//						cancelCurrentAnimation();
						return;
					}
					previousSpawnFrame = currentSpawnFrame;
				}
			}
//			}
//		});
//		animationThread.start();
	}

//	public synchronized void handleWalkingAnimation(NPC follower)
	public void handleWalkingAnimation(NPC follower)
	{
		if (playerState != null)
		{
		PlayerState currentState = playerStateTracker.getCurrentState();

			if (currentState == PlayerState.SPAWNING)
			{
				System.out.println("moving Spawning check");
				cancelCurrentAnimation();
			}
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
//				System.out.println("TransmogObject walking " + transmogObject );
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

//	public synchronized void handleStandingAnimation(NPC follower)
	public void handleStandingAnimation(NPC follower)
	{
			PlayerState currentState = playerStateTracker.getCurrentState();

			if (currentState == PlayerState.SPAWNING || currentState == PlayerState.IDLE)
			{
				System.out.println("if (currentState == PlayerState.SPAWNING)");
				return;
			}



//		if (isSpawning)
//		{
////			cancelCurrentAnimation();
//			return;
//		}
//		if (playerState != null)
//		{
//		PlayerState currentState = playerStateTracker.getCurrentState();
//
//
//			if (currentState == PlayerState.SPAWNING)
//			{
//				System.out.println("standing Spawning check");
//				return;
//			}
//		}

		NpcData selectedNpc = config.selectedNpc();
		if (selectedNpc == null)
		{
			return;
		}

		int standingAnimationId = (config.enableCustom()) ? config.standingAnimationId() : selectedNpc.getStandingAnim();
		Animation standingAnimation = client.loadAnimation(standingAnimationId);
		NPC followerLoop = client.getFollower();

		if (selectedNpc == null || standingAnimation == null || followerLoop == null)
		{
			return;
		}

		for (RuneLiteObject transmogObject : transmogObjects)
		{
			if (transmogObject != null && followerLoop != null)
			{
				currentFrame = transmogObject.getAnimationFrame();
//				System.out.println("TransmogObject standing " + transmogObject );
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
			//need to check why this block is never entered when I come back.  could be why spawn not updating every time
			System.out.println("thread cancelled");
			animationThread.interrupt();
		}
//		isSpawning = false;
		for (RuneLiteObject transmogObject : transmogObjects)
		{
			if (transmogObject != null)
			{
				System.out.println("transmog loop false now ");
				transmogObject.setShouldLoop(false);
				transmogObject.setActive(false);

			}
		}
	}
}

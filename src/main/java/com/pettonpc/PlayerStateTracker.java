package com.pettonpc;

import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayManager;

@SuppressWarnings("LombokGetterMayBeUsed")
public class PlayerStateTracker
{
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TextOverlay textOverlay;

	private PlayerState currentState = PlayerState.IDLE;
	private PlayerState previousState;
	private Client client;
	private AnimationHandler animationHandler;
	private LocalPoint lastFollowerLocation;
	private boolean wasMoving = false;
	private boolean wasStanding = false;
	private List<RuneLiteObject> transmogObjects;

//	public PlayerStateTracker(Client client, AnimationHandler animationHandler, List<RuneLiteObject> transmogObjects)
	public PlayerStateTracker(Client client, AnimationHandler animationHandler)
	{
		this.client = client;
		this.animationHandler = animationHandler;
//		this.transmogObjects = transmogObjects;
	}

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects) {
		this.transmogObjects = transmogObjects;
	}

	public void setCurrentState(PlayerState currentState) {
		this.currentState = currentState; // Default state
	}

	public synchronized void updateFollowerMovement(NPC follower)
	{
		System.out.println("transmogObject updatefollowermovement start " + transmogObjects);

		LocalPoint currentLocation = follower.getLocalLocation();
		boolean isFollowerMoving = lastFollowerLocation != null && !currentLocation.equals(lastFollowerLocation);

		lastFollowerLocation = currentLocation;
		if (isFollowerMoving)
		{
			if (wasStanding)
			{
				for (RuneLiteObject transmogObject : transmogObjects)
				{
					if (transmogObject != null)
					{
						System.out.println("transmogObject.setFinished(true); wasStanding");
						System.out.println("transmogObject updatefollowermovement wasStanding " + transmogObjects);
						transmogObject.setFinished(true);
					}
				}
			}
			wasStanding = false;
			animationHandler.handleWalkingAnimation(follower);
		}
		else
		{
			if (wasMoving)
			{
				for (RuneLiteObject transmogObject : transmogObjects)
				{
					if (transmogObject != null)
					{
						System.out.println("transmogObject.setFinished(true); wasMoving");
						System.out.println("transmogObject updatefollowermovement wasMoving " + transmogObjects);
						transmogObject.setFinished(true);
					}
				}
			}
			wasMoving = false;
			wasStanding = true;

			if (animationHandler != null && !animationHandler.isSpawning())
			{
				animationHandler.handleStandingAnimation(follower);
			}
		}
	}

//	public void setSpawning()
//	{
//		currentState = PlayerState.SPAWNING;
//		animationHandler.triggerSpawnAnimation();
//	}

//	public synchronized void updateState(PlayerState newState)
//	{
//		previousState = currentState;
//		currentState = newState;
//	}
//
//	public PlayerState getCurrentState()
//	{
//		return currentState;
//	}
//
//	public PlayerState getPreviousState()
//	{
//		return previousState;
//	}
}

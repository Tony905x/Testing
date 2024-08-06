package com.pettonpc;

import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayManager;

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

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
		this.transmogObjects = transmogObjects;
	}

	public void setCurrentState(PlayerState currentState)
	{
		this.currentState = currentState; // Default state
	}

	public synchronized void updateFollowerMovement(NPC follower)
	{
		{
//			NPC follower = client.getFollower();
//		System.out.println("Entered PlayerStateTracker update");
			if (follower == null)
			{
				return;
			}

			LocalPoint currentLocation = follower.getLocalLocation();
			PlayerState newState;

			if (lastFollowerLocation != null && !currentLocation.equals(lastFollowerLocation))
			{
//			System.out.println("Moving!");
				newState = PlayerState.MOVING;
			}
			else
			{
//			System.out.println("Standing!");
				newState = PlayerState.STANDING;
			}

			// If the state has changed, cancel the current animation
			if (newState != currentState)
			{
				System.out.println("cancel animation");
				animationHandler.cancelCurrentAnimation();
			}

			currentState = newState;
			lastFollowerLocation = currentLocation;

			switch (newState)
			{
				case MOVING:
					System.out.println("newState walking");
					animationHandler.handleWalkingAnimation(follower);
					break;
				case STANDING:
					if (animationHandler != null && !animationHandler.isSpawning())
					{
						System.out.println("newState standing");
						animationHandler.handleStandingAnimation(follower);
					}
					break;
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

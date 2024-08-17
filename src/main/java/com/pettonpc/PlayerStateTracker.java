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

	private PlayerState currentState;
//	private PlayerState previousState;
	private Client client;
	private NpcFollowerPlugin npcFollowerPlugin;
	private AnimationHandler animationHandler;
	private LocalPoint lastFollowerLocation;
	private boolean wasMoving = false;
	private boolean wasStanding = false;
	private List<RuneLiteObject> transmogObjects;
	public List<RuneLiteObject> getTransmogObjects() {
		return transmogObjects;
	}
//	private PlayerState newState;
//	private NPC follower = client.getFollower();

	//	public PlayerStateTracker(Client client, AnimationHandler animationHandler, List<RuneLiteObject> transmogObjects)
	public PlayerStateTracker(Client client, AnimationHandler animationHandler, NpcFollowerPlugin npcFollowerPlugin)
	{
		this.client = client;
		this.animationHandler = animationHandler;
		this.npcFollowerPlugin = npcFollowerPlugin;
//		this.transmogObjects = transmogObjects;
	}

	public void setTransmogObjects(List<RuneLiteObject> transmogObjects)
	{
		this.transmogObjects = transmogObjects;
	}

	public void setCurrentState(PlayerState newState)
	{
		NPC follower = client.getFollower();
		this.currentState = newState;
		System.out.println(newState);
		updateFollowerMovement(follower);
		updateFollowerState(follower);
	}


	public PlayerState getCurrentState() {
		return this.currentState;
	}



//	public void setCurrentState()
//	{
//		this.currentState = currentState; // Default state
//	}

//	public synchronized void updateFollowerMovement(NPC follower)
	public void updateFollowerMovement(NPC follower)
	{
			if (transmogObjects == null)
			{
				System.out.println("null object");
				return;
			}

			if (follower == null)
			{
				System.out.println("Null follower");
				return;
			}

			if (currentState == PlayerState.SPAWNING) {
				return;
			}

			LocalPoint currentLocation = follower.getLocalLocation();
			PlayerState newState;





//			if (currentState == PlayerState.SPAWNING)
//			{
//				updateFollowerState(follower);
//				return;
//			}



//			System.out.println("start of block");
			if (lastFollowerLocation != null && !currentLocation.equals(lastFollowerLocation))
			{
//				System.out.println("MOVING detected");
				newState = PlayerState.MOVING;
			}
			else
			{
//				System.out.println("STANDING detected");
				newState = PlayerState.STANDING;
			}

			// If the state has changed, cancel the current animation
			if (newState != currentState)
			{
					System.out.println("cancel animation");
				animationHandler.cancelCurrentAnimation();
			}

			currentState = newState;


			updateFollowerState(follower);
			lastFollowerLocation = currentLocation;

	}


//	public synchronized void updateFollowerState(NPC follower)
	public void updateFollowerState(NPC follower)
	{

			switch (currentState)
			{
				case MOVING:
//					System.out.println("case MOVING");
					animationHandler.handleWalkingAnimation(follower);
					break;
				case STANDING:
//					System.out.println("case STANDING");
					animationHandler.handleStandingAnimation(follower);
					break;
				case SPAWNING:
//					System.out.println("case SPAWNING:");
					animationHandler.triggerSpawnAnimation(follower);
					break;
				case IDLE:
//					System.out.println("case IDLE:");
					updateFollowerMovement(follower);
					break;
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
////
//	public PlayerState getPreviousState()
//	{
//		return previousState;
//	}

}

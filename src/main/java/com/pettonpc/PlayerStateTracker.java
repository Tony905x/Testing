package com.pettonpc;
//committing test

import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayManager;


public class PlayerStateTracker
{


	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TextOverlay textOverlay;
	@Getter
	private PlayerState currentState;
	@Getter
	private PlayerState previousState;


	private Client client;
	private AnimationHandler animationHandler;


	private LocalPoint lastFollowerLocation;


	public PlayerStateTracker(Client client, AnimationHandler animationHandler) {
		this.client = client;
		this.animationHandler = animationHandler;
		this.currentState = PlayerState.IDLE; // Default state
	}

	public void update()
	{
		NPC follower = client.getFollower();

		if (follower == null)
		{
			return;
		}

		LocalPoint currentLocation = follower.getLocalLocation();
		PlayerState newState;

		if (lastFollowerLocation != null && !currentLocation.equals(lastFollowerLocation))
		{
			newState = PlayerState.MOVING;
		}
		else
		{
			newState = PlayerState.STANDING;
		}

		// If the state has changed, cancel the current animation
		if (newState != currentState)
		{
			animationHandler.cancelCurrentAnimation();
		}

		currentState = newState;
		lastFollowerLocation = currentLocation;
	}

	public void setSpawning()
	{
		currentState = PlayerState.SPAWNING;
		animationHandler.triggerSpawnAnimation();
	}

	public void updateState(PlayerState newState) {
		previousState = currentState;
		currentState = newState;
	}

	//	public int getCurrentAnimationFrame(RuneLiteObject transmogObject) {
//		if (transmogObject != null && isSpawning) {
//			return transmogObject.getAnimationFrame();
//		}
//		return -1; // or some other default value
//	}
}


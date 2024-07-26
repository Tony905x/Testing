package com.pettonpc;

import com.google.inject.Provides;
import java.util.concurrent.CountDownLatch;
import net.runelite.api.*;
import net.runelite.api.coords.Angle;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.callback.ClientThread;
import javax.inject.Inject;
import java.util.*;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Pet-to-NPC Transmog",
	description = "Customize your pets appearance to be any NPC/Object",
	tags = {"pet", "npc", "transmog", "companion", "follower"}
)

public class NpcFollowerPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private NpcFollowerConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private TextOverlay textOverlay;
	@Inject
	private ClientThread clientThread;
	@Inject
	private Hooks hooks;

	protected boolean transmogInitialized = false;
	private LocalPoint lastFollowerLocation;
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;
	protected List<RuneLiteObject> transmogObjects;
	private int previousWalkingFrame = -1;
	private int previousStandingFrame = -1;
	private int textClearTick = -1;
	private int currentFrame;
	private boolean wasMoving = false;
	private boolean wasStanding = false;

	private static final int ANGLE_CONSTANT = 2048;
	private static final int ANGLE_OFFSET = 1500;
	private static final int TILE_TO_LOCAL_UNIT = 128;

	private AnimationHandler animationHandler;

	@Provides
	NpcFollowerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NpcFollowerConfig.class);
	}

	@Override
	protected void startUp()
	{
		initializeVariables();
		System.out.println("config: " + config);
		animationHandler = new AnimationHandler(client, config);
		hooks.registerRenderableDrawListener(drawListener);
	}

	@Override
	protected void shutDown() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);

		clientThread.invokeLater(() -> {
			transmogObjects.forEach(transmogObject -> {
				transmogObject.setActive(false);
				transmogObject.setFinished(true);
				System.out.println("shutdown the transmog");
			});
			transmogObjects.clear();
			latch.countDown();  // Signal that the cleanup work is done
		});

		latch.await();  // Wait for the cleanup work to finish

		hooks.unregisterRenderableDrawListener(drawListener);
		initializeVariables();
		overlayManager.remove(textOverlay);
		System.out.println("reached the end of shutdown");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			initializeVariables();
		}
	}

	private void initializeVariables()
	{
		transmogInitialized = false;
		transmogObjects = null;
		previousWalkingFrame = -1;
		previousStandingFrame = -1;

	}

	//Updating the transmogs location and movement using ClientTick used as a check
	// instead of GameTick as it executes much more frequent and makes the animation smoother
	@Subscribe
	public void onClientTick(ClientTick event)
	{
		NPC follower = client.getFollower();
		NpcData selectedNpc = config.selectedNpc();
		Actor player = client.getLocalPlayer();

		if (follower != null)
		{
			if (transmogObjects == null)
			{
				transmogObjects = new ArrayList<>();
			}
			if (!transmogInitialized)
			{
				RuneLiteObject transmogObject = initializeTransmogObject(follower);
				System.out.println("TransmogObject: " + transmogObject);
				if (transmogObject != null)
				{
//					transmogObjects.add(transmogObject);
					transmogInitialized = true;
					//call animation handler for transmog spawn animation
					if (selectedNpc.name.equals("GnomeChild"))
					{
						animationHandler.setTransmogObject(transmogObject);
						animationHandler.triggerSpawnAnimation();
						overlayManager.add(textOverlay);
					}


				}
				else
				{
					return;
				}
			}
			updateTransmogObject(follower);
			updateFollowerMovement(follower);

		}
	}

	//RuneLiteObject used to create the Model as it allows assigning animation ID's and merging multiple ModelIDs
	private RuneLiteObject initializeTransmogObject(NPC follower)
	{
		transmogObjects.clear();

		RuneLiteObject transmogObject = client.createRuneLiteObject();
		NpcData selectedNpc = config.selectedNpc();

		if (transmogObject != null)
		{
			Model mergedModel = createNpcModel();
			if (mergedModel != null)
			{
				transmogObject.setModel(mergedModel);
				transmogObjects.add(transmogObject);
				transmogObject.setActive(true);

				if (config.enableCustom())
				{
					transmogObject.setRadius(config.modelRadius());
				}
				else
				{
					transmogObject.setRadius(selectedNpc.radius);
				}
			}
		}
		return transmogObject;
	}

	//builds the new model when config panel altered
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		System.out.println("config: " + config);

		if (event.getGroup().equals("petToNpcTransmog"))
		{
			System.out.println("onConfigChanged entered " + transmogInitialized);
			NpcData selectedNpc = config.selectedNpc();
			List<Integer> modelIds = selectedNpc.getModelIDs();
			if (modelIds.isEmpty())
			{
				return;
			}
//
//			if (!event.getGroup().equals(NpcFollowerConfig.GROUP))
//			{
//				return;
//			}

			if (event.getKey().equals("selectedNpc") || config.enableCustom() || !config.enableCustom())
			{
				clientThread.invokeLater(() -> {
					// Cancel the current animation
					animationHandler.cancelCurrentAnimation();

					// Clear the transmogObjects list
//					transmogObjects.clear();

					// Create and set the new model
					Model mergedModel = createNpcModel();
					if (mergedModel != null) {
						RuneLiteObject transmogObject = transmogObjects.get(0);
						transmogObject.setModel(mergedModel);
						transmogObject.setActive(true);
						if (config.enableCustom())
						{
							transmogObject.setRadius(config.modelRadius());
						}
						else
						{
							transmogObject.setRadius(selectedNpc.radius);
						}
						if (selectedNpc.name.equals("GnomeChild"))
						{
							animationHandler.setTransmogObject(transmogObject);
							animationHandler.triggerSpawnAnimation();
						}
					}
				});
			}
		}
	}


	//tracking the location of the in game follower, if it moves to a new location it will execute walking method
	//otherwise it will execute standing method. wasStanding and wasMoving are necessary to cut off animation
	//looping when you change states
	private void updateFollowerMovement(NPC follower)
	{
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
						transmogObject.setFinished(true);
					}
				}
			}
			wasStanding = false;
			handleWalkingAnimation(follower);
		}
		else
		{
			if (wasMoving)
			{
				for (RuneLiteObject transmogObject : transmogObjects)
				{
					if (transmogObject != null)
					{
						transmogObject.setFinished(true);
					}
				}
			}
			wasMoving = false;
			wasStanding = true;

			if (animationHandler != null && !animationHandler.isSpawning()) {
				handleStandingAnimation(follower);
			}
		}
	}

	// Animation looping is handled by getAnimationFrame tracks the frames within the walking animation.  Checking
	// if the previous frame is greater than the current one meaning the animation loop ended. Only resetting the
	// animation when at the end of the loop so that the animation doesn't get cut based off the timing of gameTick
	// or ClientTick
	private void handleWalkingAnimation(NPC follower)
	{
		if (animationHandler.isSpawning()) {
			animationHandler.cancelCurrentAnimation();
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
	private void handleStandingAnimation(NPC follower)
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

	//update the location on every client tick so the transmog doesn't flicker.  Offset added to allow for
	//gap when larger NPC's are used.  Orientation for larger offsets managed by angle calculations so
	//that the transmog properly looks at the player
	private void updateTransmogObject(NPC follower)
	{
		WorldView worldView = client.getTopLevelWorldView();
		LocalPoint followerLocation = follower.getLocalLocation();

		int offsetX = config.offsetX() * TILE_TO_LOCAL_UNIT; // Convert tiles to local units
		int offsetY = config.offsetY() * TILE_TO_LOCAL_UNIT; // Convert tiles to local units

		int newX = followerLocation.getX() + offsetX;
		int newY = followerLocation.getY() + offsetY;
		LocalPoint newLocation = new LocalPoint(newX, newY);

		// Calculate the angle between the follower and the player
		Player player = client.getLocalPlayer();
		int dx = player.getLocalLocation().getX() - newX;
		int dy = player.getLocalLocation().getY() - newY;
		int angle;

		// If the offset is set to 0, use the follower's current orientation
		if (config.offsetX() == 0 && config.offsetY() == 0)
		{
			angle = follower.getCurrentOrientation();
		}
		else
		{
			angle = (int) ((Math.atan2(-dy, dx) * ANGLE_CONSTANT) / (2 * Math.PI) + ANGLE_OFFSET) % ANGLE_CONSTANT;
		}

		Angle followerOrientation = new Angle(angle);


		if (transmogObjects != null)
		{
			for (RuneLiteObject transmogObject : transmogObjects)
			{
				if (transmogObject != null)
				{
					transmogObject.setLocation(newLocation, worldView.getPlane());
					transmogObject.setOrientation(followerOrientation.getAngle());
				}
			}
		}
	}

	//grab the modelID's from the enum within NpcData file for preset models,
	//or if custom model ids are used it grabs from config. They are then merged to create a complete model
	public Model createNpcModel()
	{
		NpcData selectedNpc = config.selectedNpc();
		List<Integer> modelIds = new ArrayList<>();

		if (config.enableCustom())
		{
			// Add custom NPC model IDs if they are valid
			int[] npcModelIDs = {config.npcModelID1(), config.npcModelID2(), config.npcModelID3(), config.npcModelID4(), config.npcModelID5(), config.npcModelID6(), config.npcModelID7(), config.npcModelID8(), config.npcModelID9(), config.npcModelID10()};

			for (int modelId : npcModelIDs)
			{
				if (modelId > 0)
				{
					modelIds.add(modelId);
				}
			}
		}
		else
		{
			// Add predefined NPC model IDs
			modelIds.addAll(selectedNpc.getModelIDs());
		}

		if (modelIds.isEmpty())
		{
			return null; // No NPC is selected or custom data is not set
		}

		ModelData[] modelDataArray = modelIds.stream().map(client::loadModelData).toArray(ModelData[]::new);

		if (Arrays.stream(modelDataArray).anyMatch(Objects::isNull))
		{
			return null; // Return if any model data failed to load
		}

		// Merge the ModelData objects into a single ModelData
		ModelData mergedModelData = client.mergeModels(modelDataArray);
		if (mergedModelData == null)
		{
			return null;
		}

		// Light the merged ModelData to create the final Model
		Model finalModel = mergedModelData.light();
		if (finalModel == null)
		{
			return null;
		}

		return finalModel;
	}

	//hides in game pet
	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;
			if (npc == client.getFollower())
			{
				return false;
			}
		}
		return true;
	}
}
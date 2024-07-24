package com.pettonpc;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(PtnConfig.GROUP)
public interface PtnConfig extends Config
{
	String GROUP = "petToNpcTransmog";

	@ConfigItem(
		keyName = "selectedNpc",
		name = "Select NPC",
		description = "Have your pet out and select one of these sample NPCs. Cats/kittens work as well.",
		position = 0
	)
	default NpcData selectedNpc()
	{
		return NpcData.GnomeChild;
	}

	@ConfigItem(
		keyName = "enableCustom",
		name = "Enable Custom Configuration",
		description = "Check this box to enable custom configuration. To find the modelIDs and AnimationIDs for an NPC, " +
			"you can perform a web search for 'OSRS NPC modelIDs' or 'RuneScape animation IDs'. Some users have found " +
			"resources like https://runemonk.com/tools/entityviewer/ or https://chisel.weirdgloop.org/moid/index.html site " +
			"to be helpful. Please note that these are external sites and may change over time.",
		position = 1
	)
	default boolean enableCustom()
	{
		return false;
	}


	@ConfigItem(
		keyName = "npcModelID1",
		name = "CUSTOM ModelID1",
		description = "Enter the modelID of the NPC you want to overlay. Most NPC's will have a single ModelIDs" +
			"you can leave the rest of the ModelID fields at 0 if this is the case.  Make sure you are using the " +
			"ModelID and not the NPCID.",
		position = 2
	)
	default int npcModelID1()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID2",
		name = "CUSTOM ModelID2",
		description = "Used when an NPC has multiple ModelIDs",
		position = 3
	)
	default int npcModelID2()
	{
		return -1; // Default model ID
	}


	@ConfigItem(
		keyName = "npcModelID3",
		name = "CUSTOM ModelID3",
		description = "Used when an NPC has multiple ModelIDs",
		position = 4
	)
	default int npcModelID3()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID4",
		name = "CUSTOM ModelID4",
		description = "Used when an NPC has multiple ModelIDs",
		position = 5
	)
	default int npcModelID4()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID5",
		name = "CUSTOM ModelID5",
		description = "Used when an NPC has multiple ModelIDs",
		position = 6
	)
	default int npcModelID5()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID6",
		name = "CUSTOM ModelID6",
		description = "Used when an NPC has multiple ModelIDs",
		position = 7
	)
	default int npcModelID6()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID7",
		name = "CUSTOM ModelID7",
		description = "Used when an NPC has multiple ModelIDs",
		position = 8
	)
	default int npcModelID7()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID8",
		name = "CUSTOM ModelID8",
		description = "Used when an NPC has multiple ModelIDs",
		position = 9
	)
	default int npcModelID8()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID9",
		name = "CUSTOM ModelID9",
		description = "Used when an NPC has multiple ModelIDs",
		position = 10
	)
	default int npcModelID9()
	{
		return -1; // Default model ID
	}

	@ConfigItem(
		keyName = "npcModelID10",
		name = "CUSTOM ModelID10",
		description = "Used when an NPC has multiple ModelIDs",
		position = 11
	)
	default int npcModelID10()
	{
		return -1; // Default model ID
	}


	@ConfigItem(
		keyName = "standingAnimationId",
		name = "Standing Animation ID",
		description = "Enter the standingAnimation ID of the NPC",
		position = 12
	)
	default int standingAnimationId()
	{
		return -1; // Default standing animation ID
	}

	@ConfigItem(
		keyName = "walkingAnimationId",
		name = "Walking Animation ID",
		description = "Enter the walkingAnimation ID of the NPC",
		position = 13
	)
	default int walkingAnimationId()
	{
		return -1; // Default standing animation ID
	}

	@ConfigItem(
		keyName = "modelRadius",
		name = "NPC Radius",
		description = "Set the radius for the model, larger models will clip if it has a small radius set." +
			"Rule of thumb: for each square the NPC takes up add 60.  eg) 1 tile NPC = 60, 3 tile NPC = 180",
		position = 14
	)
	default int modelRadius()
	{
		return 60; // Default radius
	}

	@ConfigItem(
		keyName = "offsetX",
		name = "Offset X",
		description = "Horizontal offset for the model.  Larger models will be too close if set to the default 0." +
			"  Try increasing to find the position you want",
		position = 15
	)
	default int offsetX()
	{
		return 0; // Default horizontal offset
	}

	@ConfigItem(
		keyName = "offsetY",
		name = "Offset Y",
		description = "Vertical offset for the model.  Larger models will be too close if set to the default 0." +
			"  Try increasing to find the position you want",
		position = 16
	)
	default int offsetY()
	{
		return 0; // Default vertical offset
	}

	@ConfigItem(
		keyName = "spawnAnimationID",
		name = "Spawn Animation ID",
		description = "Enter the spawn animation ID",
		position = 17
	)
	default int spawnAnimationID()
	{
		return -1; // Default vertical offset
	}

	@ConfigItem(
		keyName = "spawnAnimationDuration",
		name = "Spawn Animation Duration",
		description = "Enter how many milliseconds the Spawn Duration will be",
		position = 18
	)
	default int spawnAnimationDuration()
	{
		return -1; // Default vertical offset
	}

}


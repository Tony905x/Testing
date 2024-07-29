package com.pettonpc;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum NpcData
{
	WiseOldMan("Wise Old Man", Lists.newArrayList(187, 9103, 4925, 292, 323, 170, 176, 3711, 265, 181), 813, 1146, 60),
	KONAR("Konar", Lists.newArrayList(36162), 8219, 8218, 60),
	GnomeChild("Gnome Child", Lists.newArrayList(2909, 2899, 2918), 195, 189, 60),
	Zilyana("Zilyana", Lists.newArrayList(27989, 27937, 27985, 27968, 27990), 6966, 6965, 120),
	Guthan("Guthan", Lists.newArrayList(6654, 6673, 6642, 6666, 6679, 6710), 813, 1205, 60),
	Nightmare("Nightmare", Lists.newArrayList(39196), 8593, 8634, 300),
	NIEVE("Nieve", Lists.newArrayList(392, 27644, 27640, 19951, 3661, 28827, 9644, 27654, 9640, 11048), 813, 1205, 60),
	DrunkenDwarf("DrunkenDwarf", Lists.newArrayList(2974, 2986, 2983, 2979, 2981, 2985, 2992), 900, 104, 60);

	// Getters for each property
	final String name;
	final List<Integer> modelIDs;
	final int standingAnim;
	final int walkAnim;
	final int radius;


	NpcData(String name, List<Integer> modelIDs, int standingAnim, int walkAnim, int radius)
	{
		this.name = name;
		this.modelIDs = modelIDs;
		this.standingAnim = standingAnim;
		this.walkAnim = walkAnim;
		this.radius = radius;

	}
}




package com.technicjelle.BlueMapSpawnMarker;

import com.flowpowered.math.vector.Vector2i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class Vector2 {
	@Comment("X coordinate")
	private @Nullable Integer x;

	@Comment("Y coordinate")
	private @Nullable Integer y;

	public @NotNull Vector2i toVector2i() {
		if (x == null || y == null) return Vector2i.ZERO;
		return Vector2i.from(x, y);
	}

	@Override
	public String toString() {
		return "Vector2 { x: " + x + ", y: " + y + " }";
	}
}

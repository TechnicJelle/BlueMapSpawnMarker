package com.technicjelle.BlueMapSpawnMarker;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.technicjelle.BMUtils.BMCopy;
import com.technicjelle.BMUtils.BMNative;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.io.IOException;
import java.nio.file.Path;

@ConfigSerializable
public class Config {
	private static final String fileName = "marker.conf";

	@Comment("Name of the marker and marker set")
	private @NotNull String name;

	//Marker Set Options
	@Comment("Whether the marker set is toggleable on the website")
	private @Nullable Boolean toggleable;
	@Comment("Whether the marker set is hidden by default on the website")
	private @Nullable Boolean defaultHidden;

	//Marker Options
	@Comment("Icon of the marker")
	private @Nullable String icon;
	@Comment("The icon anchor")
	private @Nullable Vector2 anchor;
	@Comment("Minimum distance to show the marker")
	private @Nullable Double minDistance;
	@Comment("Maximum distance to show the marker")
	private @Nullable Double maxDistance;

	public static Config load(BlueMapAPI api) throws IOException {
		BMCopy.Native.jarResourceToAllocatedConfigDirectory(api, Config.class.getClassLoader(), fileName, fileName, false);
		Path configDirectory = BMNative.getAllocatedConfigDirectory(api, Config.class.getClassLoader());
		Path configFile = configDirectory.resolve(fileName);

		HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
				.defaultOptions(options -> options.implicitInitialization(false))
				.path(configFile).build();

		Config config = loader.load().get(Config.class);
		if (config == null) {
			throw new IOException("Failed to load config");
		}
		return config;
	}

	public MarkerSet createMarkerSet() {
		return MarkerSet.builder()
				.label(name)
				.toggleable(toggleable != null ? toggleable : true)
				.defaultHidden(defaultHidden != null ? defaultHidden : false)
				.build();
	}

	public POIMarker createMarker(Vector3i spawnPoint) {
		POIMarker marker = POIMarker.builder()
				.label(name)
				.detail("<b>" + name + "</b><br>" + createHtml(spawnPoint))
				.styleClasses("spawn-marker")
				.position(spawnPoint.toDouble().add(0.5, 0.0, 0.5)) // centre on block
				.build();

		if (icon != null) marker.setIcon(icon, anchor != null ? anchor.toVector2i() : Vector2i.ZERO);
		if (minDistance != null) marker.setMinDistance(minDistance);
		if (maxDistance != null) marker.setMaxDistance(maxDistance);

		return marker;
	}

	final static String htmlTemplate = """
			<div class="content">
				<div class="entry"><span class="label">x: </span><span class="value">{{x}}</span></div>
				<div class="entry"><span class="label">y: </span><span class="value">{{y}}</span></div>
				<div class="entry"><span class="label">z: </span><span class="value">{{z}}</span></div>
			</div>
			""".strip();

	private static String createHtml(Vector3i spawnPoint) {
		return htmlTemplate
				.replace("{{x}}", String.valueOf(spawnPoint.getX()))
				.replace("{{y}}", String.valueOf(spawnPoint.getY()))
				.replace("{{z}}", String.valueOf(spawnPoint.getZ()));
	}
}

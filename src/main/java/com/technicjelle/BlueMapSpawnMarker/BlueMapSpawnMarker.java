package com.technicjelle.BlueMapSpawnMarker;

import com.flowpowered.math.vector.Vector3i;
import com.technicjelle.BMUtils.BMCopy;
import com.technicjelle.BMUtils.BMNative;
import com.technicjelle.UpdateChecker;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.common.api.BlueMapWorldImpl;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class BlueMapSpawnMarker implements Runnable {
	String addonID;
	String addonVersion;
	Logger logger;
	UpdateChecker updateChecker;

	@Override
	public void run() {
		try {
			addonID = BMNative.getAddonID(this.getClass().getClassLoader());
			addonVersion = BMNative.getAddonMetadataKey(this.getClass().getClassLoader(), "version");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger = Logger.getLogger(addonID);
		logger.log(java.util.logging.Level.INFO, "Starting " + addonID + " " + addonVersion);
		updateChecker = new UpdateChecker("TechnicJelle", "BlueMapSpawnMarker", addonVersion);
		updateChecker.checkAsync();
		BlueMapAPI.onEnable(onEnableListener);
	}

	final Consumer<BlueMapAPI> onEnableListener = api -> {
		updateChecker.logUpdateMessage(logger);

		Config config;
		try {
			config = Config.load(api);
			BMCopy.jarResourceToWebApp(api, this.getClass().getClassLoader(), "style.css", "bmsm.css", true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		for (BlueMapWorld world : api.getWorlds()) {
			//Get world spawn point
			BlueMapWorldImpl worldImpl = (BlueMapWorldImpl) world;
			Vector3i spawnPoint = worldImpl.world().getSpawnPoint();

			//Create markerSet
			MarkerSet markerSet = config.createMarkerSet();

			//Create Marker
			POIMarker marker = config.createMarker(spawnPoint);

			//Add Marker to markerSet
			markerSet.put("spawn", marker);

			//Add markerSet to all maps
			for (BlueMapMap map : world.getMaps()) {
				logger.info("Adding spawn marker to map " + map.getId());
				map.getMarkerSets().put("spawn", markerSet);
			}
		}
	};
}

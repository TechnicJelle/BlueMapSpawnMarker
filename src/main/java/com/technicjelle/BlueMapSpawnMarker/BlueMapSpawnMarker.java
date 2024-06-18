package com.technicjelle.BlueMapSpawnMarker;

import com.flowpowered.math.vector.Vector3i;
import com.technicjelle.BMUtils.BMCopy;
import com.technicjelle.BMUtils.BMNative.BMNLogger;
import com.technicjelle.BMUtils.BMNative.BMNMetadata;
import com.technicjelle.UpdateChecker;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import de.bluecolored.bluemap.common.api.BlueMapWorldImpl;

import java.io.IOException;
import java.util.function.Consumer;

public class BlueMapSpawnMarker implements Runnable {
	private BMNLogger logger;
	private UpdateChecker updateChecker;

	@Override
	public void run() {
		String addonID;
		String addonVersion;
		try {
			addonID = BMNMetadata.getAddonID(this.getClass().getClassLoader());
			addonVersion = BMNMetadata.getKey(this.getClass().getClassLoader(), "version");
			logger = new BMNLogger(this.getClass().getClassLoader());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logger.logInfo("Starting " + addonID + " " + addonVersion);
		updateChecker = new UpdateChecker("TechnicJelle", addonID, addonVersion);
		updateChecker.checkAsync();
		BlueMapAPI.onEnable(onEnableListener);
	}

	final private Consumer<BlueMapAPI> onEnableListener = api -> {
		updateChecker.getUpdateMessage().ifPresent(logger::logWarning);

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
				logger.logInfo("Adding spawn marker to map " + map.getId());
				map.getMarkerSets().put("spawn", markerSet);
			}
		}
	};
}

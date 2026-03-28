package com.technicjelle.BlueMapSpawnMarker;

import com.flowpowered.math.vector.Vector3i;
import de.bluecolored.bluemap.core.resources.pack.datapack.DataPack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluenbt.NBTName;

@SuppressWarnings("FieldMayBeFinal")
public class LevelData {

    @NBTName("Data")
    private Data data = new Data();

    public Data getData() {
        return data;
    }

    public static class Data {

        private Spawn spawn = null;

        // legacy-spawn notation
        @NBTName("SpawnX")
        private int spawnX = 0;

        @NBTName("SpawnY")
        private int spawnY = 0;

        @NBTName("SpawnZ")
        private int spawnZ = 0;

        public Spawn getSpawn() {
            if (spawn == null) {
                spawn = new Spawn(new Vector3i(spawnX, spawnY, spawnZ));
            }
            return spawn;
        }

    }

    public static class Spawn {

        private Key dimension = DataPack.DIMENSION_OVERWORLD;
        private Vector3i pos = Vector3i.ZERO;
        private float yaw = 0;
        private float pitch = 0;

        public Spawn(Vector3i pos) {
            this.pos = pos;
        }

        public Key getDimension() {
            return dimension;
        }

        public Vector3i getPos() {
            return pos;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }

    }

}

package com.goldenglow.common.data.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class OOWorldData extends WorldSavedData {
    private static final String DATA_NAME = "obscureobsidian_worlddata";

    private Calendar lastReset = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

    private OOWorldData() {
        super(DATA_NAME);
    }
    private OOWorldData(String name) {
        super(name);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        lastReset.setTimeInMillis(nbt.getLong("lastReset"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("lastReset", lastReset.getTimeInMillis());
        return compound;
    }

    public static OOWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        OOWorldData instance = (OOWorldData)storage.getOrLoadData(OOWorldData.class, DATA_NAME);
        if(instance==null) {
            instance = new OOWorldData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    public int getLastEarthDay() {
        Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        current.setTime(Date.from(Instant.now()));
        if(current.get(Calendar.DAY_OF_YEAR) != lastReset.get(Calendar.DAY_OF_YEAR)) {
            lastReset = current;
        }
        return lastReset.get(Calendar.DAY_OF_YEAR);
    }

}

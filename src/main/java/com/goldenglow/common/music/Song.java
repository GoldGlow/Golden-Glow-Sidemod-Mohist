package com.goldenglow.common.music;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

/**
 * Created by JeanMarc on 5/9/2019.
 */
public class Song {
    public PositionedSoundRecord sound;
    public String path;

    public Song(String path){
        this.sound=new PositionedSoundRecord(new ResourceLocation(path), SoundCategory.getByName("ambient"), 100000, 1.0F, true, 0, ISound.AttenuationType.NONE, 0, 0, 0);
        this.path=path;
    }

    public PositionedSoundRecord getSound(){
        return this.sound;
    }

    public String getPath(){
        return this.path;
    }
}

package com.goldenglow.common.util;

import com.goldenglow.common.events.OOPokedexEvent;
import com.goldenglow.common.util.scripting.OtherFunctions;
import com.goldenglow.common.util.scripting.QuestConditionFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.entity.player.EntityPlayerMP;

public class TitleMethods {
    public static void unlockBugCatcher(OOPokedexEvent event){
        if(QuestConditionFunctions.registeredAmountOfTypeInDex(event, "Bug", 5)){
            OtherFunctions.unlockBugCatcher((EntityPlayerMP) event.player.getMCEntity());
        }
    }
}

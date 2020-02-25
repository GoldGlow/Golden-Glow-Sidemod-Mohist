package com.goldenglow.common.util;

import net.minecraft.util.text.TextFormatting;

/**
 * Created by JeanMarc on 7/30/2018.
 */
public class Reference {
    public static String bold=TextFormatting.BOLD+"";
    public static String italics=TextFormatting.ITALIC+"";
    public static String obfuscated=TextFormatting.OBFUSCATED+"";
    public static String underline=TextFormatting.UNDERLINE+"";
    public static String strike=TextFormatting.STRIKETHROUGH+"";
    public static String resetText=TextFormatting.RESET+"";
    public static String aqua=TextFormatting.AQUA+"";
    public static String darkAqua=TextFormatting.DARK_AQUA+"";
    public static String green=TextFormatting.GREEN+"";
    public static String darkGreen=TextFormatting.DARK_GREEN+"";
    public static String yellow=TextFormatting.YELLOW+"";
    public static String gold=TextFormatting.GOLD+"";
    public static String red=TextFormatting.RED+"";
    public static String darkRed=TextFormatting.DARK_RED+"";
    public static String purple=TextFormatting.LIGHT_PURPLE+"";
    public static String darkPurple=TextFormatting.DARK_PURPLE+"";
    public static String black=TextFormatting.BLACK+"";
    public static String white=TextFormatting.WHITE+"";
    public static String grey=TextFormatting.GRAY+"";
    public static String darkGrey=TextFormatting.DARK_GRAY+"";

    public static String messagePrefix = gold+bold+"[GG] "+TextFormatting.RESET;
    public static String gymMessagePrefix =aqua+bold+"[GG-Gym] "+TextFormatting.RESET;

    public static String configDir="config/GoldenGlow";
    public static String routeDir=configDir+"/routes";
    public static String spawnerDir=configDir+"/spawners";
    public static String inventoryDir=configDir+"/inventories";
    public static String shopsDir=configDir+"/shops";
    public static String sealsDir=configDir+"/seals";
    public static String gymsDir=configDir+"/gyms";
    public static String statsDir=configDir+"/stats";
    public static String bossDir=configDir+"/bosses";
}
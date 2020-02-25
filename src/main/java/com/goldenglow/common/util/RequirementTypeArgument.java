package com.goldenglow.common.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class RequirementTypeArgument implements ArgumentType<String> {

    public String parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readString();
        for(Requirement.RequirementType type : Requirement.RequirementType.values()) {
            if(type.name().equalsIgnoreCase(s)) {
                return s;
            }
        }
        return null;
    }
}

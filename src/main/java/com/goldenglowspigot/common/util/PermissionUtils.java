package com.goldenglowspigot.common.util;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.util.Tristate;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Collection;

public class PermissionUtils implements com.goldenglow.common.util.PermissionUtils {
    public void unsetPermissionsWithStart(EntityPlayerMP player, String permissionStart){
        User user= LuckPermsProvider.get().getUserManager().getUser(player.getName());
        Collection<Node> nodes=user.getNodes();
        for(Node node: nodes){
            if(node.getKey().startsWith(permissionStart)){
                user.getNodes().remove(node);
            }
        }
        LuckPermsProvider.get().getUserManager().saveUser(user);
    }

    public void setPrefix(EntityPlayerMP player, String prefix){
        User user= LuckPermsProvider.get().getUserManager().getUser(player.getName());
        PrefixNode.Builder node= PrefixNode.builder(prefix, 3);
        node.clearExpiry();
        user.getNodes().add(node.build());
        LuckPermsProvider.get().getUserManager().saveUser(user);
    }

    public void addPermissionNode(EntityPlayerMP player, String node){
        User user= LuckPermsProvider.get().getUserManager().getUser(player.getName());
        user.getNodes().add(Node.builder(node).build());
        LuckPermsProvider.get().getUserManager().saveUser(user);
    }

    public boolean checkPermission(EntityPlayerMP player, String node){
        User user= LuckPermsProvider.get().getUserManager().getUser(player.getName());
        Tristate value= user.data().contains(Node.builder(node).build(), NodeEqualityPredicate.EXACT);
        if(value==Tristate.TRUE){
            return true;
        }
        return false;
    }
}

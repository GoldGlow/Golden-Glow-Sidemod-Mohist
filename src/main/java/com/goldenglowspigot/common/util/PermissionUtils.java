package com.goldenglowspigot.common.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.util.Tristate;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Collection;

public class PermissionUtils implements com.goldenglow.common.util.PermissionUtils {
    LuckPerms api;
    ArrayList<String> titles;

    public PermissionUtils(){
        GGLogger.info("Loading PermissionUtils");
        this.api=null;
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.api = provider.getProvider();
            GGLogger.info("Loaded PermissionUtils");
        }
        this.titles=new ArrayList<String>();
        this.titles.add("titles.trainer");
        this.titles.add("titles.bug_catcher");
        this.titles.add("titles.sneaker");
        this.titles.add("titles.magical_girl");
        this.titles.add("titles.magical_boy");
    }

    public void unsetPermissionsWithStart(EntityPlayerMP player, String permissionStart){
        User user= this.api.getUserManager().getUser(player.getUniqueID());
        Collection<Node> nodes=user.getNodes();
        for(Node node: nodes){
            if(node.getKey().startsWith(permissionStart)){
                user.data().remove(node);
            }
        }
        this.api.getUserManager().saveUser(user);
    }

    public void setPrefix(EntityPlayerMP player, String prefix){
        User user= this.api.getUserManager().getUser(player.getUniqueID());
        PrefixNode.Builder node= PrefixNode.builder(prefix, 3);
        node.clearExpiry();
        user.data().add(node.build());
        this.api.getUserManager().saveUser(user);
    }

    public void addPermissionNode(EntityPlayerMP player, String node){
        User user= this.api.getUserManager().getUser(player.getUniqueID());
        user.data().add(Node.builder(node).build());
        this.api.getUserManager().saveUser(user);
    }

    public boolean checkPermission(EntityPlayerMP player, String node){
        User user= this.api.getUserManager().getUser(player.getUniqueID());
        Tristate value= user.data().contains(Node.builder(node).build(), NodeEqualityPredicate.EXACT);
        if(value==Tristate.TRUE){
            return true;
        }
        return false;
    }

    public String getPrefix(EntityPlayerMP player){
        User user= this.api.getUserManager().getUser(player.getUniqueID());
        Collection<Node> nodes=user.getNodes();
        for(Node node: nodes){
            if(node.getKey().startsWith("prefix.3.")){
                return node.getKey().replace("prefix.3.", "");
            }
        }
        return "";
    }

    public int getUnlockedPrefixTotal(EntityPlayerMP playerMP){
        int unlocked=1;
        for(String title: this.titles){
            if(this.checkPermission(playerMP, title)){
                unlocked++;
            }
        }
        return unlocked;
    }

    public int getPrefixTotal(){
        return this.titles.size();
    }
}

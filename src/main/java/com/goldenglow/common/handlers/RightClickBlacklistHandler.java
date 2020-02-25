package com.goldenglow.common.handlers;

import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;

import java.io.*;
import java.util.ArrayList;

public class RightClickBlacklistHandler {
    public ArrayList<String> blacklistedItems=new ArrayList<String>();
    File dir;

    public void init(){
        dir = new File(Reference.configDir, "rightClickBlacklist.cfg");
        if(!dir.exists()) {
            if (!dir.getParentFile().exists())
                dir.getParentFile().mkdirs();
            try {
                dir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                loadConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadConfig() throws FileNotFoundException {
        BufferedReader reader= new BufferedReader(new FileReader(this.dir));
        String strLine;
        try {
            while ((strLine=reader.readLine())!=null){
                this.blacklistedItems.add(strLine.replace(" ",""));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

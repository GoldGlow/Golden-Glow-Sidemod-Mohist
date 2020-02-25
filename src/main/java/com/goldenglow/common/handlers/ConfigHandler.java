package com.goldenglow.common.handlers;


import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;

import java.io.*;

public class ConfigHandler {
    public boolean spawner;
    public String oAuthToken;
    File dir;
    public ConfigHandler(){
        spawner=true;
    }
    public void init(){
        dir = new File(Reference.configDir, "goldenglow.cfg");
        if(!dir.exists()) {
            if (!dir.getParentFile().exists())
                dir.getParentFile().mkdirs();
        }
        else {
            try {
                loadConfig();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public void loadConfig() throws FileNotFoundException {
        BufferedReader reader= new BufferedReader(new FileReader(dir));
        String strLine;
        try {
            while ((strLine=reader.readLine())!=null){
                if(strLine.startsWith("useSpawners=")){
                    spawner=Boolean.parseBoolean(strLine.substring(12));
                    GGLogger.info(strLine.substring(12));
                }
                else if (strLine.startsWith("oAuthToken=")) {
                    oAuthToken=strLine.substring(11);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

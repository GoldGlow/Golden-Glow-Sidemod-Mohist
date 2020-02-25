package com.goldenglow.http;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.util.Reference;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class OOStatsServer implements Runnable {

    Javalin server;

    public void run() {
        GoldenGlow.logger.info("[RV97] Starting stats server...");

        server = Javalin.create();

        server.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });

        server.get("/badges/:name", ctx -> sendStat("badges", ctx.pathParam("name"), ctx));

        server.get("/dex/:name", ctx -> sendStat("dex", ctx.pathParam("name"), ctx));

        server.get("/time/:name", ctx -> sendStat("time", ctx.pathParam("name"), ctx));

        GoldenGlow.logger.info("[RV97] Stats server started...");
        server.start(8023);
    }

    void sendStat(String stat, String name, Context ctx) {
        UUID uuid = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(name).getId();
        File f = new File(Reference.statsDir, uuid+".json");
        if(!f.exists()) {
            if (stat == "time")
                ctx.result("00:00");
            else
                ctx.result("0");
        } else {
            try {
                InputStream iStream = new FileInputStream(f);
                JsonObject json = new JsonParser().parse(new InputStreamReader(iStream, StandardCharsets.UTF_8)).getAsJsonObject();
                ctx.result(json.get(stat).getAsString());
            }
            catch (Exception e) {
                ctx.result("...");
            }
        }
    }

}

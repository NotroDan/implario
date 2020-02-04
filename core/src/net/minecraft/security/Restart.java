package net.minecraft.security;

import net.minecraft.entity.player.Module;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.core.jmx.Server;

import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

public class Restart {
    static boolean restarting;
    private static String args[] = null;

    public static void setArgs(String args[]){
        if(Restart.args != null)throw new IllegalStateException("Args already setted");
        Restart.args = args;
    }

    public static void restart(){
        restarting = true;
        boolean isClient = true;
        if(MinecraftServer.mcServer != null){
            MinecraftServer.mcServer.stopServer();
            isClient = !MinecraftServer.mcServer.isDedicatedServer();
        }
        try {
            Runtime.getRuntime().exec("java -cp " + ManagementFactory.getRuntimeMXBean().getClassPath() + " "
                    + String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments()) + (isClient ? " Start " : " net.minecraft.server.ServerStart ") +
                    String.join(" ", args));
        }catch (Exception error){
            error.printStackTrace();
        }
        System.exit(0);
    }
}

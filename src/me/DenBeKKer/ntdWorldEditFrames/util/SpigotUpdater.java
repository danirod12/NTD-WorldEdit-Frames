package me.DenBeKKer.ntdWorldEditFrames.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotUpdater {
	
    private int project = 0;
    private URL checkURL;
    private String newVersion = "";
    private Plugin plugin;
    
    public SpigotUpdater(JavaPlugin plugin, int projectID) {
        this.plugin = plugin;
        this.newVersion = plugin.getDescription().getVersion();
        this.project = projectID;
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
        } catch (MalformedURLException e) {
    		Bukkit.getConsoleSender().sendMessage(e.getLocalizedMessage() + " [Cannot update url, SpigotUpdater:27]");
        }
    }
    
    public int getProjectID() {
        return project;
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public String getLatestVersion() {
        return newVersion;
    }
    
    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + project;
    }
    
    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equalsIgnoreCase(newVersion);
    }
    
}

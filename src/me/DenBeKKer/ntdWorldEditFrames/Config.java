package me.DenBeKKer.ntdWorldEditFrames;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
	
	private File folder;
	private String name;
	private Plugin instance;
	private String resource = null;
	
	public Config(Plugin plugin, File folder, String name) {
		
		this.folder = folder == null ? plugin.getDataFolder() : folder;
		this.name = name.contains(".") ? name : name + ".yml";
		this.instance = plugin;
		
	}
	
	public Config(Plugin plugin, String resource, File folder, String name) {
		
		this.folder = folder == null ? plugin.getDataFolder() : folder;
		this.name = name.contains(".") ? name : name + ".yml";
		this.instance = plugin;
		this.resource = resource;
		
	}
	
	private FileConfiguration config;
	
	@Deprecated
	public void copy() { copy(true); }
	
    public void copy(boolean b) {
        try {
        	folder.mkdirs();
            File file = new File(folder, name);
            if (!file.exists()) {
            	
//            	try {
//            		if(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name) == null) {
//                		resource = resource == null ? name : (resource.split(".")[0] + ".").replace(".", "/") + name;
//                	}
//            	} catch(Exception ex) {
//            		instance.getLogger().log(Level.WARNING, "Resource " + (resource == null ? name : resource) + " was not found. Remapping give exception:");
//            		ex.printStackTrace();
//            	}
            	
                Files.copy(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name), file.toPath(), new CopyOption[0]);
            }
            if(b) this.load();
        } catch(IOException ex) {}
    }
    
    public void write() {
    	try {
        	folder.mkdirs();
            new File(folder, name).createNewFile();
        } catch(IOException ex) {}
    }
    
    public void save() {
    	try {
        	folder.mkdirs();
        	File file = new File(folder, name);
        	if (!file.exists()) {
//            	Files.copy(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name), file.toPath(), new CopyOption[0]);
        		copy(false);
        	} else if(config != null) config.save(new File(folder, name));
        } catch(IOException ex) {}
    }
    
    public void reload() {  load(); }
    
    private void load() {
        try {
        	config = new YamlConfiguration();
        	config.load(new File(folder, name));
        } catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
    }
    
    public FileConfiguration get() { return config; }
    
	public String getName() { return name; }
	
	public void save(File folder) {
		try {
        	folder.mkdirs();
        	File file = new File(this.folder, name);
        	if (!file.exists()) {
            	Files.copy(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name), file.toPath(), new CopyOption[0]);
        	} else if(config != null) config.save(new File(folder, name));
        } catch(IOException ex) {}
	}
	
	public void delete() {
    	File file = new File(folder, name);
		if(file != null && file.exists())
			file.delete();
	}
	
	private boolean need_save;
	public boolean need_save() { return need_save; }
	
	@Deprecated
	public FileConfiguration getDefault(boolean delete) {
		return getDefault(delete, null);
	}
	public FileConfiguration getDefault(boolean delete, String path) {
		if(resource == null)
			resource = path;
		need_save = true;
		
		try {
        	folder.mkdirs();
            File file = new File(folder, name.split("\\.")[0] + "TEMP." + name.split("\\.")[1]);
            if (!file.exists()) {
            	
//            	try {
//            		if(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name) == null) {
//                		resource = resource == null ? name : (resource.split(".")[0] + ".").replace(".", "/") + name;
//                	}
//            	} catch(Exception ex) {
//            		instance.getLogger().log(Level.WARNING, "Resource " + (resource == null ? name : resource) + " was not found. Remapping give exception:");
//            		ex.printStackTrace();
//            	}
            	
                Files.copy(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name), file.toPath(), new CopyOption[0]);
            }
            FileConfiguration yml = new YamlConfiguration();
            yml.load(file);
            if(delete) deleteDefault();
            return yml;
        } catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public void deleteDefault() {
		File file = new File(folder, name.split("\\.")[0] + "TEMP." + name.split("\\.")[1]);
		if(file.exists()) file.delete();
	}
	
	public boolean hasResource() {
		
		folder.mkdirs();
        File file = new File(folder, name);
        if (!file.exists()) {
        	
        	return !(instance.getResource(resource == null ? name : (resource + ".").replace(".", "/") + name) == null);
        	
        }
		return true;
        
	}
	
	public void need_save(boolean b) { need_save = b; }
	
}

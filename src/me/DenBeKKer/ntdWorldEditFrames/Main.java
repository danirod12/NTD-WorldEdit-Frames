package me.DenBeKKer.ntdWorldEditFrames;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import me.DenBeKKer.ntdWorldEditFrames.util.Metrics;
import me.DenBeKKer.ntdWorldEditFrames.util.SpigotUpdater;

public class Main extends JavaPlugin implements CommandExecutor {
	
	private Config config;
	private int speed;
	private int cooldown;
	private SpigotUpdater updater;
	
	public void onEnable() {
		
		if(!Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled()) {
			Bukkit.getConsoleSender().sendMessage("\u00a7cWorldEdit not found! This plugin depends on WorldEdit!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		config = new Config(this, getDataFolder(), "config.yml");
		config.copy(true);
		
		speed = config.get().getInt("placing");
		if(speed < 10) speed = 10;
		
		cooldown = config.get().getInt("cooldown");
		if(cooldown < 1) cooldown = 1;
		
		Bukkit.getPluginCommand("/frames").setExecutor(this);
		
		new Metrics(this, 11819);
		
		updater = new SpigotUpdater(this, 93630);
		
		try {
			if(updater.checkForUpdates()) {
				
				log(ChatColor.GOLD + "╔");
				log(ChatColor.GOLD + "║   " + ChatColor.RED + ChatColor.BOLD + "[!] " + ChatColor.GREEN +
						"New plugin version for " + ChatColor.YELLOW + "WorldEdit Frames" + ChatColor.GREEN + " has been released!");
				log(ChatColor.GOLD + "║ " + ChatColor.GREEN + "Your current version is " + ChatColor.GRAY + 
						updater.getLatestVersion() + " (outdated)" + ChatColor.GREEN + ". New version is " + ChatColor.RED +  updater.getLatestVersion());
				log(ChatColor.GOLD + "║ " + ChatColor.GREEN + "Check " + ChatColor.AQUA + updater.getResourceURL() + ChatColor.GREEN + " for more details");
				log(ChatColor.GOLD + "╚");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void log(String string) {
		Bukkit.getConsoleSender().sendMessage(string);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("\u00a7cCommand only for players");
			return true;
		}
		
		Player player = (Player) sender;
		if(!player.hasPermission("worldedit.frames")) {
			player.sendMessage(config.get().getString("no-permissions").replace("&", "\u00a7"));
		    return true;
		}
		
		LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
		Region region;
		com.sk89q.worldedit.world.World selectionWorld = localSession.getSelectionWorld();
		try {
		    if (selectionWorld == null) throw new IncompleteRegionException();
		    region = localSession.getSelection(selectionWorld);
		    if(!(region instanceof CuboidRegion)) throw new IncompleteRegionException();
		} catch (IncompleteRegionException ex) {
			player.sendMessage(config.get().getString("make-region").replace("&", "\u00a7"));
		    return true;
		}
		
		BlockFace face0 = BlockFace.UP;
		if(args.length > 0) {
			if(supported(args[0], "DOWN", "NORTH", "EAST", "SOUTH", "WEST")) {
				face0 = BlockFace.valueOf(args[0].toUpperCase());
			}
		}
		
		final BlockFace face = face0;
		final World world = BukkitAdapter.adapt(selectionWorld);
		Iterator<BlockVector3> iterator = region.iterator();
		new BukkitRunnable() {
			
			int amount = 0;
			int delay = 0;
			int skipped = 0;
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				
				delay--;
				for(int i = 0; i < speed; i++) {
					if(iterator.hasNext()) {
						BlockVector3 vector = iterator.next();
						try {
							world.spawn(world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()).getLocation(), ItemFrame.class, itemFrame -> {
							    itemFrame.setFacingDirection(face, false);
							});
						} catch(Exception ex) {
							skipped++;
						}
						amount++;
					} else {
						if(player.isOnline()) {
							player.sendMessage(config.get().getString("pending").replace("%replaced%", String.valueOf(amount))
									.replace("%pending%", String.valueOf(region.getArea())).replace("&", "\u00a7"));
							player.sendMessage(config.get().getString("done").replace("%skipped%", String.valueOf(skipped)).replace("&", "\u00a7"));
						}
						cancel();
						return;
					}
				}
				
				if(delay <= 0) {
					delay = 5;
					if(player.isOnline())
						player.sendMessage(config.get().getString("pending").replace("%replaced%", String.valueOf(amount))
								.replace("%pending%", String.valueOf(region.getArea())).replace("&", "\u00a7"));
				}
				
			}
			
		}.runTaskTimer(this, 10, cooldown);
		return true;
		
	}
	
	private boolean supported(String string, String... collection) {
		for(String h : collection)
			if(h.equalsIgnoreCase(string)) return true;
		return false;
	}
	
}

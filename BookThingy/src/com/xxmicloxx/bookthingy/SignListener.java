package com.xxmicloxx.bookthingy;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

	private BookThingy plugin;
	
	public SignListener(BookThingy plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
		} else {
			if ((e.getClickedBlock().getState() instanceof Sign)) {
				Sign sign = (Sign) e.getClickedBlock().getState();
				if (sign.getLine(0).equalsIgnoreCase("[BookThingy]")) {
					if (sign.getLine(1).equalsIgnoreCase("load")) {
						plugin.load(e.getPlayer(), sign.getLine(2));
					}
				}
			}
		}
	}
}

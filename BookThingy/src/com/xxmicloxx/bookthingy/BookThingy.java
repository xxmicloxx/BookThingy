package com.xxmicloxx.bookthingy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BookThingy extends JavaPlugin {

	public String ConvToStrWithColor(String str) {
		str = str.replaceAll("&0", ChatColor.BLACK + "");
		str = str.replaceAll("&1", ChatColor.DARK_BLUE + "");
		str = str.replaceAll("&2", ChatColor.DARK_GREEN + "");
		str = str.replaceAll("&3", ChatColor.DARK_AQUA + "");
		str = str.replaceAll("&4", ChatColor.DARK_RED + "");
		str = str.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		str = str.replaceAll("&6", ChatColor.GOLD + "");
		str = str.replaceAll("&7", ChatColor.GRAY + "");
		str = str.replaceAll("&8", ChatColor.DARK_GRAY + "");
		str = str.replaceAll("&9", ChatColor.BLUE + "");
		str = str.replaceAll("&a", ChatColor.GREEN + "");
		str = str.replaceAll("&b", ChatColor.AQUA + "");
		str = str.replaceAll("&c", ChatColor.RED + "");
		str = str.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
		str = str.replaceAll("&e", ChatColor.YELLOW + "");
		str = str.replaceAll("&f", ChatColor.WHITE + "");
		str = str.replaceAll("&k", ChatColor.MAGIC + "");
		str = str.replaceAll("&l", ChatColor.BOLD + "");
		str = str.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
		str = str.replaceAll("&n", ChatColor.UNDERLINE + "");
		str = str.replaceAll("&o", ChatColor.ITALIC + "");
		str = str.replaceAll("&r", ChatColor.RESET + "");
		str = ReplaceColorCodes(str);
		return str;
	}

	private String ReplaceColorCodes(String str) {
		str = str.replaceAll("&0", "");
		str = str.replaceAll("&1", "");
		str = str.replaceAll("&2", "");
		str = str.replaceAll("&3", "");
		str = str.replaceAll("&4", "");
		str = str.replaceAll("&5", "");
		str = str.replaceAll("&6", "");
		str = str.replaceAll("&7", "");
		str = str.replaceAll("&8", "");
		str = str.replaceAll("&9", "");
		str = str.replaceAll("&a", "");
		str = str.replaceAll("&b", "");
		str = str.replaceAll("&c", "");
		str = str.replaceAll("&d", "");
		str = str.replaceAll("&e", "");
		str = str.replaceAll("&f", "");
		str = str.replaceAll("&k", "");
		str = str.replaceAll("&l", "");
		str = str.replaceAll("&m", "");
		str = str.replaceAll("&n", "");
		str = str.replaceAll("&o", "");
		str = str.replaceAll("&r", "");
		str = str.replaceAll("&", ChatColor.getLastColors(str));
		return str;
	}

	private void checkIfDirsExist() {
		File file = new File("plugins/BookThingy/");
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public void fixCodes(ItemStack itemStack, Player player) {
		if (itemStack.getType() != Material.BOOK_AND_QUILL) {
			player.sendMessage(ChatColor.RED
					+ "Du kannst ein signiertes Buch nicht bearbeiten!");
			player.sendMessage(ChatColor.LIGHT_PURPLE
					+ "Versuche den Befehl '/book unsign'!");
			return;
		}
		CraftItemStack cItemStack = (CraftItemStack) itemStack;
		NBTTagCompound tags = cItemStack.getHandle().getTag();
		NBTTagList pages = tags.getList("pages");
		int size = pages.size();
		List<String> pagesResult = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			pagesResult.add(ConvToStrWithColor(((NBTTagString) pages
					.get(i)).data));
		}
		NBTTagList pageSaves = new NBTTagList();
		for (int i = 0; i < pagesResult.size(); i++) {
			pageSaves.add(new NBTTagString("page" + i, pagesResult
					.get(i)));
		}
		NBTTagCompound saveTag = cItemStack.getHandle().getTag();
		saveTag.set("pages", pageSaves);
		cItemStack.getHandle().setTag(saveTag);
		player.sendMessage(ChatColor.GREEN + "Die Farbcodes wurden erfolgreich angewandt!");
	}
	
	public void unsign(ItemStack itemStack, Player player) {
		CraftItemStack item = (CraftItemStack) itemStack;
		NBTTagCompound tag = item.getHandle().getTag();
		if (player.getName() != tag.getString("author")
				&& !player.hasPermission("bookthingy.unsign.other")) {
			player.sendMessage(ChatColor.RED + "Keine Rechte!");
		}
		String title = tag.getString("title");
		tag.setString("title", "");
		tag.setString("author", "");
		CraftItemStack returnItem = item.clone();
		returnItem.setType(Material.BOOK_AND_QUILL);
		returnItem.getHandle().setTag(tag);
		player.setItemInHand(returnItem);
		player.sendMessage(ChatColor.GREEN + "Das Buch '" + title
				+ "' wurde wieder geöffnet!");
	}
	
	public void save(ItemStack itemStack, Player player, String name) {
		CraftItemStack item = (CraftItemStack) itemStack;
		NBTTagCompound tags = item.getHandle().getTag();
		try {
			File file = new File("plugins/BookThingy/" + name
					+ ".yml");
			if (file.exists()) {
				player.sendMessage(ChatColor.RED
						+ "Dieser Speicherstand existiert schon!");
				return;
			}
			file.createNewFile();
			FileConfiguration config = YamlConfiguration
					.loadConfiguration(file);
			config.set("title", tags.getString("title"));
			config.set("author", tags.getString("author"));
			HashMap<String, String> pages = new HashMap<String, String>();
			NBTTagList list = tags.getList("pages");
			for (int i = list.size() - 1; i > -1; i--) {
				pages.put("page" + i, ((NBTTagString) list.get(i)).data);
			}
			for (int i = 0; i < pages.size(); i++) {
				config.createSection("pages", pages);
			}
			config.save(file);
			player.sendMessage(ChatColor.GREEN
					+ "Das Buch wurde gespeichert!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(Player player, String name) {
		CraftItemStack item = new CraftItemStack(Material.WRITTEN_BOOK);
		item.setAmount(1);
		File file = new File("plugins/BookThingy/" + name + ".yml");
		if (!file.exists()) {
			player.sendMessage(ChatColor.RED
					+ "Dieser Speicherstand existiert nicht!");
			return;
		}
		NBTTagCompound tags = new NBTTagCompound();
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		tags.setString("title", config.getString("title"));
		tags.setString("author", config.getString("author"));
		ConfigurationSection section = config.getConfigurationSection("pages");
		Map<String, Object> hashMap = section.getValues(false);
		NBTTagList pages = new NBTTagList();
		for (int i = hashMap.size() - 1; i > -1; i--) {
			pages.add(new NBTTagString((String) hashMap.keySet().toArray()[i], (String) hashMap.values().toArray()[i]));
		}
		tags.set("pages", pages);
		item.getHandle().setTag(tags);
		player.getInventory().setItem(
				player.getInventory().firstEmpty(), item);
		player.sendMessage(ChatColor.GREEN + "Du hast das Buch erhalten!");
	}
	
	public void setAuthor(ItemStack itemStack, Player player, String name) {
		CraftItemStack item = (CraftItemStack) itemStack;
		NBTTagCompound tags = item.getHandle().getTag();
		if (tags.getString("author") != player.getName() && !player.hasPermission("bookthingy.setauthor.other")) {
			player.sendMessage(ChatColor.RED + "Du hast nicht genügend Rechte, um den Author dieses Buches zu ändern!");
			return;
		}
		tags.setString("author", name);
		item.getHandle().setTag(tags);
		player.sendMessage(ChatColor.GREEN + "Der Autor wurde erfolgreich geändert!");
	}
	
	public void delete(Player player, String name) {
		File file = new File("plugins/BookThingy/" + name + ".yml");
		if (!file.exists()) {
			player.sendMessage(ChatColor.RED + "Dieser Speicherstand existiert nicht!");
			return;
		}
		file.delete();
		player.sendMessage(ChatColor.GREEN + "Speicherstand erfolgreich gelöscht!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		checkIfDirsExist();
		if (label.equalsIgnoreCase("book")) {
			if (args.length < 1) {
				sender.sendMessage("Parameter vergessen!");
				return true;
			}
			if (args[0].equalsIgnoreCase("fixcodes")) {
				Player player = (Player) sender;
				if (player == null) {
					sender.sendMessage("Geht nur Ingame!");
					return true;
				}
				sender.sendMessage("Buch in der Hand wird gefixxt...");
				ItemStack itemStack = player.getItemInHand();
				fixCodes(itemStack, player);
			} else if (args[0].equalsIgnoreCase("unsign")) {
				Player player = (Player) sender;
				if (player == null) {
					sender.sendMessage("Geht nur Ingame!");
					return true;
				}
				ItemStack itemStack = player.getItemInHand();
				unsign(itemStack, player);
			} else if (args[0].equalsIgnoreCase("save")) {
				Player player = (Player) sender;
				if (player == null) {
					sender.sendMessage("Geht nur Ingame!");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED
							+ "Bitte gebe einen Namen ein!");
					return true;
				}
				ItemStack itemStack = player.getItemInHand();
				save(itemStack, player, args[1]);
			} else if (args[0].equalsIgnoreCase("load")) {
				Player player = (Player) sender;
				if (player == null) {
					sender.sendMessage("Geht nur Ingame!");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED
							+ "Bitte gebe einen Namen ein!");
					return true;
				}
				load(player, args[1]);
			} else if (args[0].equalsIgnoreCase("setauthor")) {
				Player player = (Player) sender;
				if (player == null) {
					sender.sendMessage("Geht nur Ingame!");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED
							+ "Bitte gib einen Author ein!");
					return true;
				}
				ItemStack itemStack = player.getItemInHand();
				setAuthor(itemStack, player, args[1]);
			} else if (args[0].equalsIgnoreCase("delete")) {
				Player player = (Player) sender;
				if (player == null) {
					sender.sendMessage("Geht nur Ingame!");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED
							+ "Bitte gib einen Namen ein!");
					return true;
				}
				delete(player, args[1]);
			}
		}
		return false;
	}

	@Override
	public void onDisable() {

	}

	@SuppressWarnings("unused")
	private SignListener signListener;
	
	@Override
	public void onEnable() {
		signListener = new SignListener(this);
	}

}

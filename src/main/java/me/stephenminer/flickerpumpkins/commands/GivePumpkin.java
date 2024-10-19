package me.stephenminer.flickerpumpkins.commands;

import me.stephenminer.flickerpumpkins.FlickeringPumpkins;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GivePumpkin implements CommandExecutor {
    private final FlickeringPumpkins plugin;

    public GivePumpkin(){
        this.plugin = JavaPlugin.getPlugin(FlickeringPumpkins.class);

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("flickerpumpkins.commands.give")){
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
                return false;
            }
            player.getInventory().addItem(plugin.pumpkinItem());
            return true;
        }else sender.sendMessage(ChatColor.RED + "You need to be a player to use this command!");
        return false;
    }
}

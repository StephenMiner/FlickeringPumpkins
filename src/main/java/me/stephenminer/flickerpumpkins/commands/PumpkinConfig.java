package me.stephenminer.flickerpumpkins.commands;

import me.stephenminer.flickerpumpkins.FlickeringPumpkins;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PumpkinConfig implements CommandExecutor {
    private final FlickeringPumpkins plugin;

    public PumpkinConfig(){
        this.plugin = JavaPlugin.getPlugin(FlickeringPumpkins.class);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (!sender.hasPermission("flickerpumpkins.commands.reload")){
            sender.sendMessage(ChatColor.RED + "No permission to use this command!");
            return false;
        }
        plugin.reloadConfig();
        return true;
    }
}

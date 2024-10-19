package me.stephenminer.flickerpumpkins;

import me.stephenminer.flickerpumpkins.commands.GivePumpkin;
import me.stephenminer.flickerpumpkins.commands.PumpkinConfig;
import me.stephenminer.flickerpumpkins.listeners.PumpkinLoading;
import me.stephenminer.flickerpumpkins.pumpkins.FlickerPumpkin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

public final class FlickeringPumpkins extends JavaPlugin {
    public NamespacedKey flickerKey;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        // Plugin startup logic
        registerEvents();
        addCommands();
        flickerKey = new NamespacedKey(this,"jackitem");
    }

    private void registerEvents(){
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PumpkinLoading(),this);
    }
    private void addCommands(){
        GivePumpkin givePumpkin = new GivePumpkin();
        getCommand("give-pumpkin").setExecutor(givePumpkin);
        getCommand("pumpkinreload").setExecutor(new PumpkinConfig());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public String genKey(Block block){
        return block.getX() +"," +block.getY()+ "," + block.getZ();
    }

    public int getFlickerPeriod(){
        return Math.max(5,this.getConfig().getInt("flicker-period"));
    }

    public BlockVector fromString(String str){
        String[] split = str.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        return new BlockVector(x,y,z);
    }

    public ItemStack pumpkinItem(){
        ItemStack item = new ItemStack(Material.JACK_O_LANTERN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Flickering Pumpkin");
        meta.getPersistentDataContainer().set(flickerKey, PersistentDataType.BOOLEAN,true);
        meta.addEnchant(Enchantment.ARROW_FIRE,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

}

package me.stephenminer.flickerpumpkins.pumpkins;

import me.stephenminer.flickerpumpkins.FlickeringPumpkins;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FlickerPumpkin {
    private final FlickeringPumpkins plugin;
    private final Location loc;
    private boolean kill;

    public FlickerPumpkin(Location loc){
        this.loc = loc;
        this.plugin = JavaPlugin.getPlugin(FlickeringPumpkins.class);
    }

    public void run(){
        kill = false;
        new BukkitRunnable(){
            int count = 0;
            int period = plugin.getFlickerPeriod();
            boolean lit = loc.getBlock().getType() == Material.JACK_O_LANTERN;
            @Override
            public void run(){
                if (kill){
                    this.cancel();
                    return;
                }
                if (count >= period){
                    lit = !lit;
                    updateState(lit);
                    count = 0;
                    period = plugin.getFlickerPeriod();
                }
                count++;
            }
        }.runTaskTimer(plugin,1, 1);
    }

    private void updateState(boolean light){
        Block block = loc.getBlock();
        if (block.getBlockData() instanceof Directional directional){
            BlockFace face = directional.getFacing();
            if (light) block.setType(Material.JACK_O_LANTERN);
            else block.setType(Material.CARVED_PUMPKIN);
            //changing the material changed the block data so pattern variable cannot be used
            Directional data = (Directional) block.getBlockData();
            data.setFacing(face);
            block.setBlockData(data);
        }
    }

    public void kill(){ kill = true; }
}

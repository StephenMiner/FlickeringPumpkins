package me.stephenminer.flickerpumpkins.listeners;

import me.stephenminer.flickerpumpkins.FlickeringPumpkins;
import me.stephenminer.flickerpumpkins.pumpkins.FlickerPumpkin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PumpkinLoading implements Listener {
    public final Map<BlockVector, FlickerPumpkin> pumpkinMap;
    private final NamespacedKey pumpkinKey;
    private final FlickeringPumpkins plugin;

    public PumpkinLoading(){
        pumpkinMap = new HashMap<>();
        this.plugin = JavaPlugin.getPlugin(FlickeringPumpkins.class);
        pumpkinKey = new NamespacedKey(plugin,"pumpkins");
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event){
        Chunk chunk = event.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        if (container.has(pumpkinKey, PersistentDataType.STRING))
            parseChunkData(chunk.getWorld(),container.get(pumpkinKey, PersistentDataType.STRING));
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event){
        Chunk chunk = event.getChunk();
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        if (container.has(pumpkinKey,PersistentDataType.STRING))
            unloadPumpkins(container.get(pumpkinKey,PersistentDataType.STRING));
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        ItemStack hand = event.getItemInHand();
        if (!hand.hasItemMeta() || !hand.getItemMeta().getPersistentDataContainer().has(plugin.flickerKey,PersistentDataType.BOOLEAN))return;
        Block block = event.getBlock();
        addPumpkin(block.getChunk(),block.getLocation());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        removePumpkin(block.getChunk(),block.getLocation());
    }

    @EventHandler
    public void entityExplode(EntityExplodeEvent event){
        List<Block> blocks = event.blockList();
        for (int i = blocks.size()-1; i >= 0; i--){
            Block block = blocks.get(i);
            removePumpkin(block.getChunk(),block.getLocation());
        }
    }

    @EventHandler
    public void blockExplode(BlockExplodeEvent event){
        List<Block> blocks = event.blockList();
        for (int i = blocks.size()-1; i>=0; i--){
            Block block = blocks.get(i);
            removePumpkin(block.getChunk(),block.getLocation());
        }
    }


    public void parseChunkData(World world, String data){
        String[] split = data.split("/");
        for (String entry : split){
            BlockVector pos = plugin.fromString(entry);
            if (pumpkinMap.containsKey(pos)) continue;
            FlickerPumpkin pumpkin = new FlickerPumpkin(world.getBlockAt(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ()).getLocation());
            pumpkin.run();
            pumpkinMap.put(pos,pumpkin);
        }
    }


    public void unloadPumpkins(String data){
        String[] split = data.split("/");
        for (String entry : split){
            BlockVector pos = plugin.fromString(entry);
            if (pumpkinMap.containsKey(pos))
                pumpkinMap.remove(pos).kill();
        }
    }

    public void addPumpkin(Chunk chunk, Location pos){
        PersistentDataContainer container = chunk.getPersistentDataContainer();
        BlockVector vec = new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        if (pumpkinMap.containsKey(vec)) return;
        if (!container.has(pumpkinKey, PersistentDataType.STRING)){
            container.set(pumpkinKey,PersistentDataType.STRING, plugin.genKey(pos.getBlock()));
        }else{
            String current = container.get(pumpkinKey,PersistentDataType.STRING);
            current = current + "/" + plugin.genKey(pos.getBlock());
            container.set(pumpkinKey,PersistentDataType.STRING,current);
        }
        FlickerPumpkin pumpkin = new FlickerPumpkin(pos);
        pumpkin.run();
        pumpkinMap.put(new BlockVector(pos.getBlockX(),pos.getBlockY(), pos.getBlockZ()), pumpkin);
    }

    public void removePumpkin(Chunk chunk, Location pos){
        BlockVector vec = new BlockVector(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
        if (pumpkinMap.containsKey(vec)){
            FlickerPumpkin pumpkin = pumpkinMap.remove(vec);
            pumpkin.kill();
        }else return;
        PersistentDataContainer container = chunk.getPersistentDataContainer();

        if (container.has(pumpkinKey, PersistentDataType.STRING)){
            String current = container.get(pumpkinKey,PersistentDataType.STRING);
            String[] split = current.split("/");
            StringBuilder builder = new StringBuilder();
            for (String entry : split){
                if (entry.equals(plugin.genKey(pos.getBlock()))) continue;
                builder.append(entry).append('/');
            }
            if (!builder.isEmpty()) builder.deleteCharAt(builder.length()-1);
            container.set(pumpkinKey,PersistentDataType.STRING,current);
        }

    }



}

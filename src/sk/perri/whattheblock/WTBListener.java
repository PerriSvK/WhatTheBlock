package sk.perri.whattheblock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import sk.perri.whattheblock.db.Database;

public class WTBListener implements Listener
{
    @EventHandler
    @SuppressWarnings({"deprecation", "unused"})
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        Database.insertSql(Database.INS_PREF+event.getBlockPlaced().getLocation().getBlockX()+", "+
                event.getBlockPlaced().getLocation().getBlockY()+", "+
                event.getBlockPlaced().getLocation().getBlockZ()+", "+
                "'PLACE', '"+event.getBlockPlaced().getTypeId()+", "+event.getBlockPlaced().getType().name()+"', '"+
                event.getPlayer().getName()+"');");
    }

    @EventHandler
    @SuppressWarnings({"deprecation", "unused"})
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        Database.insertSql(Database.INS_PREF+event.getBlock().getLocation().getBlockX()+", "+
                event.getBlock().getLocation().getBlockY()+", "+
                event.getBlock().getLocation().getBlockZ()+", "+
                "'BREAK', '"+event.getBlock().getTypeId()+", "+event.getBlock().getType().name()+"', '"+
                event.getPlayer().getName()+"');");
    }
}

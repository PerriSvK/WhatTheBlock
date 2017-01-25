package sk.perri.whattheblock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import sk.perri.whattheblock.db.Database;

public class WTBListener implements Listener
{
    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event)
    {
        String b = Database.insertSql(Database.INS_PREF+event.getBlockPlaced().getLocation().getBlockX()+", "+
                event.getBlockPlaced().getLocation().getBlockY()+", "+
                event.getBlockPlaced().getLocation().getBlockZ()+", "+
                "'PLACE', '"+event.getBlockPlaced().getTypeId()+" : "+event.getBlockPlaced().getType().name()+"', '"+
                event.getPlayer().getName()+"');");
        event.getPlayer().sendMessage("Place "+b);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event)
    {
        String b = Database.insertSql(Database.INS_PREF+event.getBlock().getLocation().getBlockX()+", "+
                event.getBlock().getLocation().getBlockY()+", "+
                event.getBlock().getLocation().getBlockZ()+", "+
                "'BREAK', '"+event.getBlock().getTypeId()+" : "+event.getBlock().getType().name()+"', '"+
                event.getPlayer().getName()+"');");
        event.getPlayer().sendMessage("Break "+b);
    }
}

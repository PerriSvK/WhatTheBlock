package sk.perri.whattheblock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class WhatTheBlockMain extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        getServer().getLogger().info("Enabling plugin [WhatTheBlock]");
    }

    @Override
    public void onDisable()
    {
        getServer().getLogger().info("Disabling plugin [WhatTheBlock]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender instanceof Player)
        {

            if(args.length > 0 && args[0].equalsIgnoreCase("hand"))
            {
                if(sender.hasPermission("whattheblock.hand"))
                {
                    ItemStack is = ((Player) sender).getInventory().getItemInMainHand();
                    sender.sendMessage(ChatColor.AQUA+"[WhatTheBlock] You have currently in hand block id: "+
                            is.getType().getId()+", "+is.getType().name());
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED+"[WhatTheBlock] You don't have permission for this command!");
                    return true;
                }
            }


            if(sender.hasPermission("whattheblock.view"))
            {
                Block b = ((Player) sender).getTargetBlock((Set<Material>) null, 5);
                sender.sendMessage(ChatColor.AQUA+"[WhatTheBlock] You are looking at block ID: "+
                        b.getType().getId()+", "+b.getType().name());
            }
            else
            {
                sender.sendMessage(ChatColor.RED+"[WhatTheBlock] You don't have permission for this command!");
            }
        }
        else
        {
            sender.sendMessage("[WhatTheBlock] Only players can use this command!");
        }

        return true;
    }
}

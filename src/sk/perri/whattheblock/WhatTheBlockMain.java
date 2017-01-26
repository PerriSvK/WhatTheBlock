package sk.perri.whattheblock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import sk.perri.whattheblock.db.Database;

import java.sql.ResultSet;
import java.util.Set;

public class WhatTheBlockMain extends JavaPlugin implements Listener
{
    private static boolean running = true;

    @Override
    public void onEnable()
    {
        if(getConfig().isSet("enabled") && !(getConfig().getBoolean("enabled")))
        {
            running = false;
            return;
        }
        else
        {
            if (!getDataFolder().exists())
                if(!getDataFolder().mkdirs())
                    getLogger().info("[WhatTheBlock] Unable to create plugin config directory!");

            String dbConnectionInfo = Database.init(getDataFolder().getPath()+"\\");
            getLogger().info("[WhatTheBlock] "+dbConnectionInfo);

            if(!getConfig().isSet("enabled"))
            {
                saveDefaultConfig();
                getServer().getLogger().info("[WhatTheBlock] "+Database.createTable());

                String dbConnectionInfo2 = Database.init(getDataFolder().getPath()+"\\");
                getLogger().info("[WhatTheBlock] "+dbConnectionInfo2);
            }
        }
        getServer().getPluginManager().registerEvents(new WTBListener(), this);
        getServer().getLogger().info("Enabling plugin [WhatTheBlock]");
    }

    @Override
    public void onDisable()
    {
        Database.closeConnection();
        getServer().getLogger().info("Disabling plugin [WhatTheBlock]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!running)
        {
            sender.sendMessage(ChatColor.RED+"[WhatTheBlock] Plugin not enabled");
            return true;
        }
        else if(args.length == 0)
        {
            printHelp(sender);
            return true;
        }

        if(args[0].equalsIgnoreCase("cleardatabase"))
        {
            commandClear(sender);
        }
        else if(args[0].equalsIgnoreCase("help"))
        {
            printHelp(sender);
        }
        else if(sender instanceof Player)
        {
            switch (args[0].toLowerCase())
            {
                case "hand": commandHand(sender); break;
                case "view": commandView(sender); break;
                case "history": commandHistory(sender, args); break;
                default: printHelp(sender);
            }
        }
        else
        {
            sender.sendMessage("[WhatTheBlock] Only players can use this command!");
        }

        return true;
    }

    private void printHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.GOLD+"[WhatTheBlock] Help:\n" +
                ChatColor.DARK_RED+"/blockinfo view "+ChatColor.GOLD+"- Shows info about block you are looking at\n" +
                ChatColor.DARK_RED+"/blockinfo hand "+ChatColor.GOLD+"- Shows info about blovk in your hand\n" +
                ChatColor.DARK_RED+"/blockinfo history "+ChatColor.GOLD+"- Shows history of block you are looking at\n" +
                ChatColor.DARK_RED+"/blockinfo cleardatabase "+ChatColor.GOLD+"- Clears all history logs\n"+
                ChatColor.DARK_RED+"/blockinfo help "+ChatColor.GOLD+"- Shows this help");
    }

    @SuppressWarnings( "deprecation" )
    private void commandView(CommandSender sender)
    {
        if(!sender.hasPermission("whattheblock.view"))
        {
            sender.sendMessage(ChatColor.RED+"[WhatTheBlock] You don't have permission for this command!");
            return;
        }

        Block b = ((Player) sender).getTargetBlock((Set<Material>) null, 5);
        sender.sendMessage(ChatColor.GOLD+"[WhatTheBlock] You are looking at block ID  "+ChatColor.DARK_RED+
                b.getType().getId()+ChatColor.GOLD+", "+ChatColor.DARK_RED+b.getType().name());
    }

    @SuppressWarnings( "deprecation" )
    private void commandHand(CommandSender sender)
    {
        if(!sender.hasPermission("whattheblock.hand"))
        {
            sender.sendMessage(ChatColor.RED+"[WhatTheBlock] You don't have permission for this command!");
            return;
        }

        ItemStack is = ((Player) sender).getInventory().getItemInMainHand();
        sender.sendMessage(ChatColor.GOLD+"[WhatTheBlock] You have currently in hand block ID "+
                ChatColor.DARK_RED+is.getType().getId()+ChatColor.GOLD+", "+ChatColor.DARK_RED+is.getType().name());
    }

    private void commandClear(CommandSender sender)
    {
        if(sender.hasPermission("cleardatabase") || sender instanceof ConsoleCommandSender)
        {
            Database.clearTable();
            sender.sendMessage(ChatColor.YELLOW+"[WhatTheBlock] Database cleared!");
        }
        else
        {
            sender.sendMessage(ChatColor.RED+"[WhatTheBlock] You don't have permission for this command!");
        }
    }

    private void commandHistory(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("history"))
        {
            sender.sendMessage(ChatColor.RED+"[WhatTheBlock] You don't have permission for this command!");
            return;
        }

        Block b = ((Player) sender).getPlayer().getTargetBlock((Set<Material>) null, 5);
        ResultSet rs = Database.selectSql(b.getX(), b.getY(), b.getZ());
        int page = 1;
        String msg= ChatColor.GOLD+"No history found!";
        String vys = "";
        if(args.length == 2)
        {
            page = Integer.parseInt(args[1]);
        }
        try
        {
            int size = 0;
            if (rs == null)
            {
                sender.sendMessage(ChatColor.GOLD+"[WhatTheBlock] "+msg);
                return;
            }

            while(rs.next())
            {
                size++;
                int myPage = (int) Math.ceil(size / 6.0);

                if(myPage < page)
                    continue;
                else if(myPage > page)
                {
                    vys += ChatColor.GOLD+"\nFor next page type: "+ChatColor.DARK_RED+"/blockinfo history "+myPage;
                    break;
                }

                vys += ChatColor.DARK_AQUA+"\nPlayer "+ChatColor.GREEN+rs.getString("PLAYER")+
                        ChatColor.DARK_AQUA+" action: "+ChatColor.GREEN+rs.getString("ACTION")+
                        ChatColor.DARK_AQUA+" bock: "+ChatColor.GREEN+rs.getString("BLOCK");
            }
        }
        catch (Exception e)
        {
            getLogger().info("[WhatTheBlock] NullPointerError select from database!");
        }

        if(!vys.equals(""))
        {
            msg = ChatColor.GOLD+"History of the block at "+ChatColor.DARK_RED+b.getX()+ChatColor.GOLD+
                    ", "+ChatColor.DARK_RED+b.getY()+ChatColor.GOLD+", "+ChatColor.DARK_RED+b.getZ();
            msg += vys;
        }

        sender.sendMessage(ChatColor.GOLD+"[WhatTheBlock] "+msg);
    }
}

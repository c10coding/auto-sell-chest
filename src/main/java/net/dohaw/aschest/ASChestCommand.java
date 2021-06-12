package net.dohaw.aschest;

import net.dohaw.corelib.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ASChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage(StringUtils.colorString("&cOnly players can use this command!"));
            return false;
        }

        if(!sender.hasPermission("asc.use")){
            sender.sendMessage(StringUtils.colorString("&cYou do not have permission to use this command!"));
            return false;
        }

        Player player = (Player) sender;
        if(args[0].equalsIgnoreCase("create")){
            player.getInventory().addItem(ASChestPlugin.asChest);
            player.sendMessage(StringUtils.colorString("&bYou have been given an auto sell chest!"));
        }

        return true;
    }

}

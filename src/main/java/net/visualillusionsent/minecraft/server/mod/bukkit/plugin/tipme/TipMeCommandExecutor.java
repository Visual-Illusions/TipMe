package net.visualillusionsent.minecraft.server.mod.bukkit.plugin.tipme;

import java.util.logging.Level;
import net.visualillusionsent.minecraft.server.mod.plugin.tipme.TipMeData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * TipMe Command Executor for Bukkit
 * 
 * @author Jason (darkdiplomat)
 */
public class TipMeCommandExecutor implements CommandExecutor{
    private final TipMeData tmd;
    private final TipMe_Bukkit tipme;

    TipMeCommandExecutor(TipMe_Bukkit tipme){
        this.tmd = tipme.tmd;
        this.tipme = tipme;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (label.equals("tipme")) {
            try {
                if (args.length > 0) {
                    if (args.length > 1) {
                        if (args[0].equalsIgnoreCase("add")) {
                            String tip = StringUtils.join(args, " ", 1, args.length);
                            if (tmd.createTip(tip)) {
                                sender.sendMessage("\u00A72Tip Added!");
                            }
                            else {
                                sender.sendMessage("\u00A7CFailed to add tip... Error has been logged.");
                            }
                            return true;
                        }
                    }
                    else if (args[0].equalsIgnoreCase("remove")) {
                        if (args.length > 2) {
                            try {
                                int index = Integer.parseInt(args[1]);
                                boolean removed = tmd.removeTip(index);
                                sender.sendMessage(removed ? "\u00A74Tip Removed!" : "\u00A7cFailed to remove tip... Error has been logged.");
                            }
                            catch (NumberFormatException nfe) {
                                sender.sendMessage("\u00A7CInvaild Tip #");
                            }
                            return true;
                        }
                    }
                    else if (args[0].equalsIgnoreCase("getall")) {
                        if (sender instanceof Player) {
                            tmd.sendAll((Player) sender);
                        }
                        else {
                            tmd.sendAllConsole();
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("reload")) {
                        if (!tmd.reload()) {
                            sender.sendMessage("\u00A7CFailed to reload tips... Error has been logged.");
                        }
                        else {
                            sender.sendMessage("\u00A72Tips reloaded!");
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("tipserver")) {
                        tmd.sendTip();
                        return true;
                    }
                }
                sender.sendMessage("\u00A7CUsage: /tipme <add|remove|getall|reload|tipserver> [index|tipmessage] (index needed for removal, message need for adding)");
            }
            catch (Exception ex) {
                sender.sendMessage("\u00A7CAn unhandled exception has occurred in TipMe! Error has been logged!");
                tipme.getLogger().log(Level.SEVERE, "An unhandled exception has occurred in TipMe! Report this to DarkDiplomat on GitHub!", ex);
            }
            return true;
        }
        return false;
    }

}

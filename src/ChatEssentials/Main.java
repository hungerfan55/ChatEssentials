package ChatEssentias;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;



public class Main extends PluginBase implements Listener {

    @Override
    public void onEnable() {
        registerevents();
    }

    @Override
    public void onDisable() {

    }

    public void registerevents(){

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("chat")) {
           if (args.length == 0){
               if (p.hasPermission("chat.help")){
                   sender.sendMessage("§cCommands:");
                   sender.sendMessage("§7chat");
                   sender.sendMessage("§7chat clear");
                   sender.sendMessage("§7chat toggle");
                   sender.sendMessage("§7chat slow");
                       return true;
                   }else {
                   sender.sendMessage("§cYou do not have permission : chat.help ");
               }
           }
           if (args.length == 1 && (args[0].equalsIgnoreCase("clear"))){
               if (sender.hasPermission("chat.clear")){
                   for (int x = 0; x < 100; x++){
                       for (Player playerall : this.getServer().getOnlinePlayers().values()){
                           playerall.sendMessage("");
                       }
                   }
                   for (Player player : this.getServer().getOnlinePlayers().values()){
                       player.sendMessage("§7Chat has been cleared by §c" + sender.getName());
                   }
               }else {
                   sender.sendMessage("§cYou do not have permission : chat.clear");
               }
           }
        }
        return false;
    }


}


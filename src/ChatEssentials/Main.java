package ChatEssentials;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Main extends PluginBase implements Listener {

    HashMap<Player, ArrayList<Player>> i = Manager.getInstance().getIgnore();
    public static boolean Muted = false;
    public ArrayList<String> CmdSpy = new ArrayList<String>();


    @Override
    public void onEnable(){

        getLogger().info(TextFormat.GREEN + "has been enabled");
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        this.saveConfig();

    }


    @Override
    public void onDisable() {

    }




    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("cmdspy")) {
            if (sender.hasPermission("chat.cmdSpy")) {
                if (!this.CmdSpy.contains(sender.getName())) {
                    sender.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.spy_onn"));
                    this.CmdSpy.add(sender.getName());
                } else {
                    sender.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.spy_off"));
                    this.CmdSpy.remove(sender.getName());
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("chat")) {
           if (args.length == 0){
               if (p.hasPermission("chat.help")){
                   sender.sendMessage( "§cCommands:");
                   sender.sendMessage( getConfig().getString("messages.prefix") + "§7chat");
                   sender.sendMessage(getConfig().getString("messages.prefix") + "§7/clearchat");
                   sender.sendMessage(getConfig().getString("messages.prefix") +  "§7/mutechat");
                   sender.sendMessage( getConfig().getString("messages.prefix") + "§7/ignore <player>");
                   sender.sendMessage(getConfig().getString("messages.prefix") +  "§7/cmdspy");
                       return true;
                   }else {
                   return true;
               }
           }
        }
        if (cmd.getName().equalsIgnoreCase("clearchat")) {
            if (!sender.hasPermission("chat.clearchat")) {
                return true;
            }
            if (sender.hasPermission("chat.clearchat")){
                for (int i = 0; i < 121; i++) {


                    for (Player player : this.getServer().getOnlinePlayers().values()) {
                        player.sendMessage("");

                    }
                }
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("mutechat")){
            if (!p.hasPermission("chat.mutechat")){
                return true;
            }

            if (!Muted && p.hasPermission("chat.mutechat")){
                Muted = true;
                for (Player player : this.getServer().getOnlinePlayers().values()){
                    player.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.mutechat") + sender.getName());
                    return true;
                }
            }

            if (Muted && p.hasPermission("chat.mutechat")){
                Muted = false;
                for (Player player : this.getServer().getOnlinePlayers().values()){
                    player.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.unmutechat") + sender.getName());
                    return true;
                }
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfig().getString("messages.prefix") + "§cOnly Players can ignore other users!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("ignore")) {
            if (sender.hasPermission("chat.ignore")) {
                if (args.length == 0) {
                    p.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.specifyplayer"));
                    return true;
                }
                Player t = getServer().getPlayer(args[0]);
                if (t == null) {
                    sender.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.playernotfound") + args[0]);
                    return true;
                }
                if (this.i.get(p) == null) {
                    ArrayList<Player> al = new ArrayList<>();
                    al.add(t);
                    this.i.put(p, al);
                    p.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.nowignoring") + t.getName());
                    return true;
                }
                if (((ArrayList) this.i.get(p)).contains(t)) {
                    ArrayList<Player> al = this.i.get(p);
                    al.remove(t);
                    this.i.put(p, al);
                    p.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.stoppedignoring") + t.getName());
                    return true;
                }
                if (!((ArrayList) this.i.get(p)).contains(t)) {
                    ArrayList<Player> al = this.i.get(p);
                    al.add(t);
                    this.i.put(p, al);
                    p.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.nowignoring") + t.getName() + "!");
                    return true;
                }
            }
        }   else {

            return true;
        }
     return true;
 }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerChatEvent(PlayerChatEvent event){
        Player player = event.getPlayer();
            if (Muted){
                if (!player.hasPermission("chat.mutechat")){
                    event.setCancelled(true);
                    player.sendMessage(getConfig().getString("messages.prefix") + getConfig().getString("messages.currentlymuted"));
                }
                else if (player.hasPermission("chat.mutechat")){
                    event.setCancelled(false);
                }
            }
        Set<CommandSender> r = event.getRecipients();
         for (Player pls : getServer().getOnlinePlayers().values()) {
             if (!this.i.containsKey(pls))
                 return;
             if (((ArrayList)this.i.get(pls)).contains(player)) {
                    r.remove(pls);
             }
         }

    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        for (Player p : getServer().getOnlinePlayers().values()) {
            if (this.CmdSpy.contains(p.getName())) {
                p.sendMessage("§c" + player.getName() + "§4 : §7"  + e.getMessage());
            }
        }
    }
}


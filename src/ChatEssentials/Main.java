package ChatEssentials;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Main extends PluginBase implements Listener {
    HashMap<Player, ArrayList<Player>> i = Manager.getInstance().getIgnore();
    public static boolean Muted = false;
    public static boolean SlowChat = false;

    @Override
    public void onEnable(){
        getLogger().info(TextFormat.GREEN + "has been enabled.");
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        reloadConfig();

    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("chat")) {
           if (args.length == 0){
               if (p.hasPermission("chat.help")){
                   sender.sendMessage("§cCommands:");
                   sender.sendMessage("§7chat");
                   sender.sendMessage("§7/clearchat");
                   sender.sendMessage("§7/mutechat");
                       return true;
                   }else {
                   sender.sendMessage("§cYou do not have permission : chat.help ");
               }
           }
        }
        if (cmd.getName().equalsIgnoreCase("clearchat")) {
                if (sender.hasPermission("chat.clearchat")) {
                    for (int i = 0; i < 121; i++) {


                    for (Player player : this.getServer().getOnlinePlayers().values()) {
                       player.sendMessage("");

                    }
                    }
                } else {
                    sender.sendMessage("§cYou do not have permission : chat.clearchat");
                    return true;
                }
        }
        if (cmd.getName().equalsIgnoreCase("mutechat")){
            if (!p.hasPermission("chat.mutechat")){
                sender.sendMessage("§cYou do not have permission : chat.mutechat");
                return true;
            }

            if (!Muted && p.hasPermission("chat.mutechat")){
                Muted = true;
                for (Player player : this.getServer().getOnlinePlayers().values()){
                    player.sendMessage("§7Chat has been muted by §c" + sender.getName());
                    return true;
                }
            }

            if (Muted && p.hasPermission("chat.mutechat")){
                Muted = false;
                for (Player player : this.getServer().getOnlinePlayers().values()){
                    player.sendMessage("§7Chat has been unmuted by §c" + sender.getName());
                    return true;
                }
            }
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("[Ignore] Only Players can ignore other users!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("ignore")) {
            if (sender.hasPermission("chat.ignore")) {
                if (args.length == 0) {
                    p.sendMessage("Please specify a player to ignore!");
                    return true;
                }
                Player t = getServer().getPlayer(args[0]);
                if (t == null) {
                    sender.sendMessage("[Ignore] Could not find player " + args[0] + "!");
                    return true;
                }
                if (this.i.get(p) == null) {
                    ArrayList<Player> al = new ArrayList<>();
                    al.add(t);
                    this.i.put(p, al);
                    p.sendMessage(" You are now ignoring " + t.getName() + "!");
                    return true;
                }
                if (((ArrayList) this.i.get(p)).contains(t)) {
                    ArrayList<Player> al = this.i.get(p);
                    al.remove(t);
                    this.i.put(p, al);
                    p.sendMessage("You are no longer ignoring " + t.getName() + "!");
                    return true;
                }
                if (!((ArrayList) this.i.get(p)).contains(t)) {
                    ArrayList<Player> al = this.i.get(p);
                    al.add(t);
                    this.i.put(p, al);
                    p.sendMessage("[Ignore] You are now ignoring " + t.getName() + "!");
                    return true;
                }
            }
        }

        return false;
    }
    //Events start here
    private final Map<String, Long> cooldownTime = new HashMap();
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerChatEvent(PlayerChatEvent event){
        Player player = event.getPlayer();

        if (Muted){
            if (!player.hasPermission("chat.mutechat")){
                event.setCancelled(true);
                player.sendMessage("§cThe chat is currently muted");
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
}

package ChatEssentias;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;

import java.util.HashMap;
import java.util.Map;


public class Main extends PluginBase implements Listener {

    public static boolean Muted = false;

    @Override
    public void onEnable(){
        getServer().getLogger().alert("§2PLUGIN ENABLED");
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
                   sender.sendMessage("§7/slowchat");
                       return true;
                   }else {
                   sender.sendMessage("§cYou do not have permission : chat.help ");
               }
           }
        }
        if (cmd.getName().equalsIgnoreCase("clearchat")) {
            if (args.length == 1) {
                if (sender.hasPermission("chat.clear")) {
                    for (int x = 0; x < 100; x++) {
                        for (Player playerall : this.getServer().getOnlinePlayers().values()) {
                            playerall.sendMessage("");
                        }
                    }
                    for (Player player : this.getServer().getOnlinePlayers().values()) {
                        player.sendMessage("§7Chat has been cleared by §c" + sender.getName());
                    }
                } else {
                    sender.sendMessage("§cYou do not have permission : chat.clear");
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("mutechat")){
            if (!p.hasPermission("chat.mutechat")){
                sender.sendMessage("§cYou do not have permission : chat.mutechat");
            }

            if (!Muted && p.hasPermission("chat.mutechat")){
                Muted = true;
                for (Player player : this.getServer().getOnlinePlayers().values()){
                    player.sendMessage("§7Chat has been muted by §c" + sender.getName());
                }
            }

            if (Muted && p.hasPermission("chat.mutechat")){
                Muted = false;
                for (Player player : this.getServer().getOnlinePlayers().values()){
                    player.sendMessage("§7Chat has been muted by §c" + sender.getName());
                }
            }
        }

        if (cmd.getName().equalsIgnoreCase("slowchat")){
            if (args.length == 1) {
                if (sender.hasPermission("chat.slowchat")) {
                    sender.sendMessage("§7Chat slow : §c" + String.valueOf(this.getConfig().getInt("Chatessential.Slow")) + "§7seconds");
                    return true;
                } else {
                    sender.sendMessage("§cYou do not have permission : chat.slowchat");
                }
            }

            if (args.length == 2){
                 if (sender.hasPermission("chat.slowchat"))
                    try {
                        int interval = Integer.parseInt(args[1]);
                        this.getConfig().set("Chatessential.Slow", Integer.valueOf(interval));
                        this.saveConfig();
                        for (Player playerall : this.getServer().getOnlinePlayers().values()){
                            playerall.sendMessage("§7Chat has been slowed by §c" + sender.getName());
                        }
                    }
                    catch (NumberFormatException exception){
                        p.sendMessage("§c" + args[1] + "§7is not a valid number");
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

        if (player.hasPermission("chat.slowchat")){
            return;
        }

        Long now = System.currentTimeMillis();
        String name = player.getName();
        Long lastchat = (long)cooldownTime.get(name);
        if (lastchat != null){
            long earlierNext = lastchat.longValue() + this.getConfig().getInt("Chatessential.Slow") * 1000;
            if (now < earlierNext) {
                int timeremaining = (int)((earlierNext - now) / 1000L) + 1;

                player.sendMessage("§7Please wait §c" + timeremaining + "§7more seconds");
                event.setCancelled(true);
                return;
            }
        }
        cooldownTime.put(name, Long.valueOf(now));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player p = event.getPlayer();

        cooldownTime.remove(p.getName());
    }
}
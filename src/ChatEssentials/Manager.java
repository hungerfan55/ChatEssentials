package ChatEssentials;


import cn.nukkit.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    static Manager instance = new Manager();

    public static Manager getInstance() {
        return instance;
    }

    HashMap<Player, ArrayList<Player>> ignore = new HashMap<>();

    public HashMap<Player, ArrayList<Player>> getIgnore() {
        return this.ignore;
    }
}

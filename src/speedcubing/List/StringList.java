package speedcubing.List;

import cubing.bukkit.ItemUtils;
import cubingserver.configcache;
import org.bukkit.Material;

public class StringList {
    public static final String[][] typename = {
            {"§bNormal", "§bNormal", "§bNormal", "§bNormal"},
            {"§bShort", "§bShort", "§bShort", "§bShort"},
            {"§bInclined", "§bInclined", "§bInclined", "§bInclined"},
            {"§bOneStack", "§bOneStack", "§bOneStack", "§bOneStack"},
            {"§bInclinedShort", "§bInclinedShort", "§bInclinedShort", "§bInclinedShort"},
            {"§bCustom", "§bCustom", "§bCustom", "§bCustom"}
    };
    public static final String[] MenuTitle = {"§bFastBuilder Menu", "§bFastBuilder Menu", "§bFastBuilder Menu", "§bFastBuilder Menu"};
    public static final String[] Settings = {"§cSettings", "§cSettings", "§cSettings", "§cSettings"};

    public static String[] min = {
            "§cThe minimum value is 1",
            "§cThe minimum value is 1",
            "§cThe minimum value is 1",
            "§cThe minimum value is 1"
    };

    public static String[] max = {
            "§cThe maximum value is 20",
            "§cThe maximum value is 20",
            "§cThe maximum value is 20",
            "§cThe maximum value is 20"
    };
    public static String[][] LoopLore = {
            {"§bLoop the bridging arena","", "§dCurrent: §aOn", "§dCurrent: §cOff"},
            {"§bLoop the bridging arena","", "§dCurrent: §aOn", "§dCurrent: §cOff"},
            {"§bLoop the bridging arena","", "§dCurrent: §aOn", "§dCurrent: §cOff"},
            {"§bLoop the bridging arena","", "§dCurrent: §aOn", "§dCurrent: §cOff"}
    };
    public static String[][] FallDelayLore = {
            {"§bRespawn Delay", "", "§dCurrent: §e%int%§d block(s)", "§aLeft--§8/§cRight++", "", "§cdefault: 5"},
            {"§bRespawn Delay", "", "§dCurrent: §e%int%§d block(s)", "§aLeft--§8/§cRight++", "", "§cdefault: 5"},
            {"§bRespawn Delay", "", "§dCurrent: §e%int%§d block(s)", "§aLeft--§8/§cRight++", "", "§cdefault: 5"},
            {"§bRespawn Delay", "", "§dCurrent: §e%int%§d block(s)", "§aLeft--§8/§cRight++", "", "§cdefault: 5"}
    };
    public static final String[] timedisplay = {
            "§aTime: §e%time%",
            "§aTime: §e%time%",
            "§aTime: §e%time%",
            "§aTime: §e%time%"
    };
    public static final String[][] SideBar = {
            {"§0", "§1§8» §e", "§aTime:", "§3", "§6§f", "§5§f", "§4§f", "§aSession Top 3:", "§8", "§2§8» §e", "§b§lPersonal Best:", "§f", "§6" + configcache.SERVERIP, "§3FastBuilder"},
            {"§0", "§1§8» §e", "§aTime:", "§3", "§6§f", "§5§f", "§4§f", "§aSession Top 3:", "§8", "§2§8» §e", "§b§lPersonal Best:", "§f", "§6" + configcache.SERVERIP, "§3FastBuilder"},
            {"§0", "§1§8» §e", "§aTime:", "§3", "§6§f", "§5§f", "§4§f", "§aSession Top 3:", "§8", "§2§8» §e", "§b§lPersonal Best:", "§f", "§6" + configcache.SERVERIP, "§3FastBuilder"},
            {"§0", "§1§8» §e", "§aTime:", "§3", "§6§f", "§5§f", "§4§f", "§aSession Top 3:", "§8", "§2§8» §e", "§b§lPersonal Best:", "§f", "§6" + configcache.SERVERIP, "§3FastBuilder"}
    };
    public static final String[][] CustomSideBar = {
            {"§0", "§1§8» §e", "§aTime:", "§3", "§2§8» §e", "§aHeight:", "§5", "§3§8» §e", "§aLength:", "§8", "§6" + configcache.SERVERIP, "§3FastBuilder"},
            {"§0", "§1§8» §e", "§aTime:", "§3", "§2§8» §e", "§aHeight:", "§5", "§3§8» §e", "§aLength:", "§8", "§6" + configcache.SERVERIP, "§3FastBuilder"},
            {"§0", "§1§8» §e", "§aTime:", "§3", "§2§8» §e", "§aHeight:", "§5", "§3§8» §e", "§aLength:", "§8", "§6" + configcache.SERVERIP, "§3FastBuilder"},
            {"§0", "§1§8» §e", "§aTime:", "§3", "§2§8» §e", "§aHeight:", "§5", "§3§8» §e", "§aLength:", "§8", "§6" + configcache.SERVERIP, "§3FastBuilder"}
    };

    public static final String[] hover = {
            "§eclick to view %name%§b's stats!",
            "§eclick to view %name%§b's stats!",
            "§eclick to view %name%§b's stats!",
            "§eclick to view %name%§b's stats!"
    };

    public static final String[][] ranks = {
            {"§7-----------------------------", "\n§bNormal§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclined§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bOneStack§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclinedShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§7-----------------------------", "\n§8- %p%"},
            {"§7-----------------------------", "\n§bNormal§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclined§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bOneStack§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclinedShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§7-----------------------------", "\n§8- %p%"},
            {"§7-----------------------------", "\n§bNormal§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclined§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bOneStack§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclinedShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§7-----------------------------", "\n§8- %p%"},
            {"§7-----------------------------", "\n§bNormal§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclined§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bOneStack§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§bInclinedShort§7: %d% §8[§b#%r%§8/§b#%a%§8]", "\n§7-----------------------------", "\n§8- %p%"}};


    public static final String[] Stats = {
            "§7-----------------------------\n§bPlayer§7: %player%\n§bNormal Personal best§7:§e %db% §8[§b#%pos%§8/§b#%all%§8]\n§bShort Personal best§7:§e %db1% §8[§b#%pos1%§8/§b#%all1%§8]\n§bInclined Personal best§7:§e %db2% §8[§b#%pos2%§8/§b#%all2%§8]\n§bOneStack Personal best§7:§e %db3% §8[§b#%pos3%§8/§b#%all3%§8]\n§bInclinedShort Personal best§7:§e %db4% §8[§b#%pos4%§8/§b#%all4%§8]\n§7-----------------------------",
            "§7-----------------------------\n§bPlayer§7: %player%\n§bNormal Personal best§7:§e %db% §8[§b#%pos%§8/§b#%all%§8]\n§bShort Personal best§7:§e %db1% §8[§b#%pos1%§8/§b#%all1%§8]\n§bInclined Personal best§7:§e %db2% §8[§b#%pos2%§8/§b#%all2%§8]\n§bOneStack Personal best§7:§e %db3% §8[§b#%pos3%§8/§b#%all3%§8]\n§bInclinedShort Personal best§7:§e %db4% §8[§b#%pos4%§8/§b#%all4%§8]\n§7-----------------------------",
            "§7-----------------------------\n§bPlayer§7: %player%\n§bNormal Personal best§7:§e %db% §8[§b#%pos%§8/§b#%all%§8]\n§bShort Personal best§7:§e %db1% §8[§b#%pos1%§8/§b#%all1%§8]\n§bInclined Personal best§7:§e %db2% §8[§b#%pos2%§8/§b#%all2%§8]\n§bOneStack Personal best§7:§e %db3% §8[§b#%pos3%§8/§b#%all3%§8]\n§bInclinedShort Personal best§7:§e %db4% §8[§b#%pos4%§8/§b#%all4%§8]\n§7-----------------------------",
            "§7-----------------------------\n§bPlayer§7: %player%\n§bNormal Personal best§7:§e %db% §8[§b#%pos%§8/§b#%all%§8]\n§bShort Personal best§7:§e %db1% §8[§b#%pos1%§8/§b#%all1%§8]\n§bInclined Personal best§7:§e %db2% §8[§b#%pos2%§8/§b#%all2%§8]\n§bOneStack Personal best§7:§e %db3% §8[§b#%pos3%§8/§b#%all3%§8]\n§bInclinedShort Personal best§7:§e %db4% §8[§b#%pos4%§8/§b#%all4%§8]\n§7-----------------------------"};

    public static String[] nomapsavaliable = {
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.",
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.",
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.",
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot."};

    public static String[] cancelled = {
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)",
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)",
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)",
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)"};
}

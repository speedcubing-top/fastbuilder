package top.speedcubing.fastbuilder.List;

import top.speedcubing.server.lang.LangMessage;

public class StringList {
    public static final LangMessage[] typename = {
            new LangMessage("§bNormal", "§bNormal", "§bNormal", "§bNormal"),
            new LangMessage("§bShort", "§bShort", "§bShort", "§bShort"),
            new LangMessage("§bLong", "§bLong", "§bLong", "§bLong"),
            new LangMessage("§bInclined", "§bInclined", "§bInclined", "§bInclined"),
            new LangMessage("§bInclined Short", "§bInclined Short", "§bInclined Short", "§bInclined Short"),
            new LangMessage("§bOneStack", "§bOneStack", "§bOneStack", "§bOneStack"),
            new LangMessage("§bExtraShort", "§bExtraShort", "§bExtraShort", "§bExtraShort"),
            new LangMessage("§bCustom", "§bCustom", "§bCustom", "§bCustom")
    };
    public static final LangMessage MenuTitle = new LangMessage("§bFastBuilder Menu", "§bFastBuilder Menu", "§bFastBuilder Menu", "§bFastBuilder Menu");
    public static final LangMessage Settings = new LangMessage("§cSettings", "§cSettings", "§cSettings", "§cSettings");
    public static String[][] LoopLore = {
            {"§bLoop the bridging arena", "", "§dCurrent: §aOn", "§dCurrent: §cOff"},
            {"§bLoop the bridging arena", "", "§dCurrent: §aOn", "§dCurrent: §cOff"},
            {"§bLoop the bridging arena", "", "§dCurrent: §aOn", "§dCurrent: §cOff"},
            {"§bLoop the bridging arena", "", "§dCurrent: §aOn", "§dCurrent: §cOff"}
    };

    public static final LangMessage replayNotFound = new LangMessage(
            "not found.",
            "not found.",
            "not found.",
            "not found."
    );
    public static final LangMessage timedisplay = new LangMessage(
            "§aTime: §e%1%",
            "§aTime: §e%1%",
            "§aTime: §e%1%",
            "§aTime: §e%1%"
    );
    public static final String[][] SideBar = {
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§b§lPersonal Best:", "§2§8» §e", "§8", "§aSession Top 3:", "§3", "§aTime:", "§1§8» §e", "§0"},
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§b§lPersonal Best:", "§2§8» §e", "§8", "§aSession Top 3:", "§3", "§aTime:", "§1§8» §e", "§0"},
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§b§lPersonal Best:", "§2§8» §e", "§8", "§aSession Top 3:", "§3", "§aTime:", "§1§8» §e", "§0"},
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§b§lPersonal Best:", "§2§8» §e", "§8", "§aSession Top 3:", "§3", "§aTime:", "§1§8» §e", "§0"}
    };
    public static final String[][] replaySidebar = {
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§aX: §8» §b", "§aY: §8» §b", "§aZ: §8» §b", "§aYaw: §8» §b", "§aPitch: §8» §b", "§a"},
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§aX: §8» §b", "§aY: §8» §b", "§aZ: §8» §b", "§aYaw: §8» §b", "§aPitch: §8» §b", "§a"},
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§aX: §8» §b", "§aY: §8» §b", "§aZ: §8» §b", "§aYaw: §8» §b", "§aPitch: §8» §b", "§a"},
            {"§3FastBuilder", "§6speedcubing.top", "§f", "§aX: §8» §b", "§aY: §8» §b", "§aZ: §8» §b", "§aYaw: §8» §b", "§aPitch: §8» §b", "§a"}
    };
    public static final String[][] CustomSideBar = {
            {"§3FastBuilder", "§6speedcubing.top", "§8", "§aLength:", "§3§8» §e", "§5", "§aHeight:", "§2§8» §e", "§3", "§aTime:", "§1§8» §e", "§0"},
            {"§3FastBuilder", "§6speedcubing.top", "§8", "§aLength:", "§3§8» §e", "§5", "§aHeight:", "§2§8» §e", "§3", "§aTime:", "§1§8» §e", "§0"},
            {"§3FastBuilder", "§6speedcubing.top", "§8", "§aLength:", "§3§8» §e", "§5", "§aHeight:", "§2§8» §e", "§3", "§aTime:", "§1§8» §e", "§0"},
            {"§3FastBuilder", "§6speedcubing.top", "§8", "§aLength:", "§3§8» §e", "§5", "§aHeight:", "§2§8» §e", "§3", "§aTime:", "§1§8» §e", "§0"}
    };

    public static final LangMessage hover = new LangMessage(
            "§eclick to view %1%§b's stats!",
            "§eclick to view %1%§b's stats!",
            "§eclick to view %1%§b's stats!",
            "§eclick to view %1%§b's stats!"
    );

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

    public static LangMessage nomapsavaliable = new LangMessage(
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.",
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.",
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.",
            "§7[§bFastBuilder§7] §cWe couldn't find any available slot.");


    public static LangMessage tooFast = new LangMessage(
            "§cThis score is out of range.", "§cThis score is out of range.", "§cThis score is out of range.", "§cThis score is out of range."
    );

    public static LangMessage firstScore = new LangMessage(
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "             §aYou got your first score of §e%1%§a!\n" +
                    "§8---------------------------------------------",
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "             §aYou got your first score of §e%1%§a!\n" +
                    "§8---------------------------------------------",
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "             §aYou got your first score of §e%1%§a!\n" +
                    "§8---------------------------------------------",
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "             §aYou got your first score of §e%1%§a!\n" +
                    "§8---------------------------------------------"
    );

    public static LangMessage pb = new LangMessage(
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "                        §e%1% §8-> §e%2% \n" +
                    "                        §8(§bbeat by §a%3%§8)\n" +
                    "§8---------------------------------------------",
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "                        §e%1% §8-> §e%2% \n" +
                    "                        §8(§bbeat by §a%3%§8)\n" +
                    "§8---------------------------------------------",
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "                        §e%1% §8-> §e%2% \n" +
                    "                        §8(§bbeat by §a%3%§8)\n" +
                    "§8---------------------------------------------",
            "§8---------------------------------------------\n" +
                    "                        §bCongratulations!\n" +
                    "                        §e%1% §8-> §e%2% \n" +
                    "                        §8(§bbeat by §a%3%§8)\n" +
                    "§8---------------------------------------------"
    );
    public static LangMessage cancelled = new LangMessage(
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)",
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)",
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)",
            "§7[§bFastBuilder§7] §cThe bridging attempt is cancelled! (timed out)");
}

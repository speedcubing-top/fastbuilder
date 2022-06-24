package speedcubing.Core;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import speedcubing.Core.timed.TimedMode;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.lib.bukkit.SideBarAPI;
import speedcubing.replay.test;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class Core {
    public static String msToTime(int ms) {
        return String.format("%.3f", ms * 0.01);
    }

    public static String tickToTime(int tick) {
        return String.format("%.3f", tick * 0.05);
    }

    public static int cal(int oldroom, boolean left, boolean[] b) {
        int a = left ? 0 : 1;
        int l = b.length - (a * -1 + 2);
        while (oldroom != l) {
            oldroom += (oldroom == a ? 1 : (oldroom % 2) * 4 - 2) * (a * -2 + 1);
            if (b[oldroom])
                return oldroom;
        }
        return -1;
    }

    public static List<String>[] getSideBar(int i, List<UUID> session, Map<UUID, Integer> score) {
        List<String> prefix = new ArrayList<>();
        List<String> suffix = new ArrayList<>();
        int size = session.size();
        for (int j = i; j < 3; j++) {
            if (size <= j) {
                prefix.add("§7-,---");
                suffix.add("");
            } else {
                UUID ses = session.get(j);
                int sc = score.get(ses);
                if (sc == 0) {
                    prefix.add("§7-,---");
                    suffix.add("");
                } else {
                    String[] x = SideBarAPI.prefixSuffixSplit(speedcubingServer.getFormat(Member.getMember(ses).user.rank)[1] + Bukkit.getPlayer(ses).getName() + "§8: §e" + tickToTime(sc));
                    prefix.add(x[0]);
                    suffix.add(x[1]);
                }
            }
        }
        return new List[]{prefix, suffix};
    }

    public static TimedMode getInstance(String world) {
        switch (world.toLowerCase()) {
            case "world":
                return Main.Long;
            case "short":
                return Main.Short;
            case "inclined":
                return Main.Inclined;
            case "onestack":
                return Main.OneStack;
            case "inclinedshort":
                return Main.InclinedShort;
        }
        return null;
    }
}

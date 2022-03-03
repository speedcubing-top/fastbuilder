package speedcubing.NPC;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class NPC extends Reflection {


    private int entityID;
    private org.bukkit.Location location;
    private GameProfile gameprofile;


    public void animation(int animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) animation);
        sendPacket(packet);
    }

    public void status(int status) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) status);
        sendPacket(packet);
    }

    public void sendMetadataPacket(String type) {
        DataWatcher dw = new DataWatcher(null);

        if (type.equals("sneaking"))
            dw.a(0, (byte) 2);
        else
            dw.a(0, (byte) 0);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityID, dw, true);
        sendPacket(packet);
    }

    public void items(ItemStack stack) {
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entityID, 0, CraftItemStack.asNMSCopy(stack));
        sendPacket(packet);
    }

    public NPC(String name, Location location, Player player) {
        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
        gameprofile = new GameProfile(UUID.randomUUID(), name);
        changeSkin(player);
        this.location = location.clone();
    }

    public void changeSkin(Player player) {
//        String[] skins = Skin.getSkin(player);
//        gameprofile.getProperties().put("textures", new Property("textures", skins[0], skins[1]));
    }

    public void spawn() {
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();

        setValue(packet, "a", entityID);
        setValue(packet, "b", gameprofile.getId());
        setValue(packet, "c", getFixLocation(location.getX()));
        setValue(packet, "d", getFixLocation(location.getY()));
        setValue(packet, "e", getFixLocation(location.getZ()));
        setValue(packet, "f", getFixRotation(location.getYaw()));
        setValue(packet, "g", getFixRotation(location.getPitch()));
        setValue(packet, "h", 0);
        DataWatcher w = new DataWatcher(null);
        w.a(6, (float) 20);
        w.a(10, (byte) 127);
        setValue(packet, "i", w);
        addToTablist();
        sendPacket(packet);
        headRotation(location.getYaw(), location.getPitch());
    }

    public void teleport(Location location) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setValue(packet, "a", entityID);
        setValue(packet, "b", getFixLocation(location.getX()));
        setValue(packet, "c", getFixLocation(location.getY()));
        setValue(packet, "d", getFixLocation(location.getZ()));
        setValue(packet, "e", getFixRotation(location.getYaw()));
        setValue(packet, "f", getFixRotation(location.getPitch()));

        sendPacket(packet);
        headRotation(location.getYaw(), location.getPitch());
        this.location = location.clone();
    }


    public void headRotation(float yaw, float pitch) {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));


        sendPacket(packet);
        sendPacket(packetHead);
    }

    public void destroy() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{entityID});
        rmvFromTablist();
        sendPacket(packet);
    }

    public void addToTablist() {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
        @SuppressWarnings("unchecked")
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        setValue(packet, "b", players);

        sendPacket(packet);
    }

    public void rmvFromTablist() {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);
        @SuppressWarnings("unchecked")
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        players.add(data);

        setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        setValue(packet, "b", players);

        sendPacket(packet);
    }

    public int getFixLocation(double pos) {
        return (int) MathHelper.floor(pos * 32.0D);
    }

    public byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

}
package top.speedcubing.fastbuilder.replay.io;

import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.SQL.SQLResult;
import top.speedcubing.lib.utils.SQL.SQLRow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ReplayIO {
    public abstract void write(Replay replay, UUID uuid, boolean TAS);

    public abstract Replay read(String replayId, SQLRow row);

    private static final Path REPLAY_STORAGE_DIR = Path.of("replays");

    private static final Map<Integer, ReplayIO> replayIOMap = new HashMap<>();

    public static void init() {
        replayIOMap.put(1, new ReplayIOv1());
        replayIOMap.put(2, new ReplayIOv2());
    }

    public static ReplayIO getLatest() {
        return replayIOMap.get(2);
    }

    public static Replay readAuto(String replayID) {
        try (SQLConnection connection = Database.getCubing()) {
            SQLResult result = connection.select("replay_ver,datas,name,value,signature,mode").from("fbreplay").where("id=X'" + replayID + "'").executeResult();
            if (result.isEmpty()) {
                throw new RuntimeException();
            }
            SQLRow row = result.get(0);
            int ver = row.getInt("replay_ver");
            return replayIOMap.get(ver).read(replayID, row);
        }
    }

    protected String replayId(byte[] data, long time) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            digest.update(Long.toString(time).getBytes());
            StringBuilder out = new StringBuilder();
            for (byte b : digest.digest()) {
                out.append(String.format("%02x", b & 0xff));
            }
            return out.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 digest is unavailable", e);
        }
    }

    protected String writeReplayFile(String replayId, int version, byte[] data) throws IOException {
        replayId = replayId.toLowerCase();
        String prefix = replayId.substring(0, 2);
        Path directory = REPLAY_STORAGE_DIR.resolve("v" + version).resolve(prefix);
        Files.createDirectories(directory);
        Path file = directory.resolve(replayId + ".fbr");
        Files.write(file, data);
        return toReplayPath(version, replayId);
    }

    protected byte[] readReplayFile(String replayId, int version, SQLRow row) throws Exception {
        if (row.getBlob("datas") != null) {
            return row.getBlob("datas").getBytes(1, (int) row.getBlob("datas").length());
        }
        return Files.readAllBytes(REPLAY_STORAGE_DIR.resolve(toReplayPath(version, replayId)));
    }

    public static void deleteReplayFile(int version, String replayId) throws IOException {
        Files.deleteIfExists(REPLAY_STORAGE_DIR.resolve(toReplayPath(version, replayId)));
    }

    private static String toReplayPath(int version, String replayId) {
        return "v" + version + "/" + replayId.substring(0, 2).toLowerCase() + "/" + replayId.toLowerCase() + ".fbr";
    }
}

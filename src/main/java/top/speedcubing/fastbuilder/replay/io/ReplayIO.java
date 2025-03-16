package top.speedcubing.fastbuilder.replay.io;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.SQL.SQLResult;
import top.speedcubing.lib.utils.SQL.SQLRow;

public abstract class ReplayIO {
    public abstract void write(Replay replay, UUID uuid, boolean TAS);

    public abstract Replay read(SQLRow row);


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
            SQLResult result = connection.select("id,replay_ver,datas,name,value,signature,mode").from("fbreplay").where("id=X'" + replayID + "'").executeResult();
            if (result.isEmpty()) {
                throw new RuntimeException();
            }
            SQLRow row = result.get(0);
            int ver = row.getInt("replay_ver");
            return replayIOMap.get(ver).read(row);
        } catch (Exception e) {
            throw e;
        }
    }
}

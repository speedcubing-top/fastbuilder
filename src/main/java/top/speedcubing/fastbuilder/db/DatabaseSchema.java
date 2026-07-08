package top.speedcubing.fastbuilder.db;

import top.speedcubing.common.database.Database;
import top.speedcubing.lib.utils.SQL.SQLConnection;

public class DatabaseSchema {

    public static void ensureSchema() {
        try (SQLConnection connection = Database.getCubing()) {
            connection.execute("""
                    CREATE TABLE IF NOT EXISTS fbreplay (
                        id           BINARY(16)    NOT NULL,
                        uuid         BINARY(16)    NOT NULL,
                        time         BIGINT        NOT NULL,
                        replay_ver   INT           NOT NULL,
                        datas        LONGBLOB      NULL,
                        name         VARCHAR(16)   NOT NULL DEFAULT '',
                        value        TEXT          NULL,
                        signature    TEXT          NULL,
                        score        INT           NOT NULL DEFAULT 0,
                        mode         INT           NOT NULL DEFAULT 0,
                        tas          TINYINT(1)    NOT NULL DEFAULT 0,
                        fav          TINYINT(1)    NOT NULL DEFAULT 0,
                        PRIMARY KEY (id),
                        INDEX idx_fbreplay_list (uuid, mode, fav, time),
                        INDEX idx_fbreplay_cleanup (fav, time)
                    )
                    """);
            connection.execute("ALTER TABLE fbreplay MODIFY COLUMN datas LONGBLOB NULL");
            connection.execute("ALTER TABLE fbreplay ADD INDEX IF NOT EXISTS idx_fbreplay_list (uuid, mode, fav, time)");
            connection.execute("ALTER TABLE fbreplay ADD INDEX IF NOT EXISTS idx_fbreplay_cleanup (fav, time)");
        }
    }
}

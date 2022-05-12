package speedcubing;

import speedcubing.server.libs.User;

import java.util.Map;
import java.util.UUID;

public class Member extends User {
    public static Map<UUID, Member> users;

    public static Member getMember(UUID uuid) {
        return users.get(uuid);
    }
    public Member(UUID uuid) {
        super.init(this, uuid);
        users.put(uuid, this);
    }
}

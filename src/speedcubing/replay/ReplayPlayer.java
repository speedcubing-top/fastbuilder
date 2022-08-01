package speedcubing.replay;

import speedcubing.Member;

public class ReplayPlayer {
    public final Replay replay;
    public final Member member;

    public ReplayPlayer(Member member, Replay replay) {
        this.replay = replay;
        this.member = member;
    }
}

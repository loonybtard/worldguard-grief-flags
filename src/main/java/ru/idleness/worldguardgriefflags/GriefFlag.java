package ru.idleness.worldguardgriefflags;

import com.sk89q.worldguard.protection.flags.StateFlag;

public enum GriefFlag {
    FALLING("-allow-falling"),
    WITHER("-allow-wither"),
    PISTON("-allow-piston"),
    HOPPER("-allow-hopper"),
    EXPLOSIONS("-allow-explosions");

    public static String prefix;
    public final String wgFlagName;
    private StateFlag stateFlag;

    private GriefFlag(String wgFlagName) {
        this.wgFlagName = wgFlagName;
    }

    public String getName() {
        return prefix + wgFlagName;
    }

    public void setFlag(StateFlag stateFlag) {
        this.stateFlag = stateFlag;
    }

    public StateFlag getFlag() {
        return stateFlag;
    }
}

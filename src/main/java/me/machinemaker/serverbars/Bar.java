package me.machinemaker.serverbars;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class Bar {

    private String title;
    private BarColor color;
    private BarStyle style;
    private long ticks;
    public Bar(String title, BarColor color, BarStyle style, long ticks) {
        this.title = title;
        this.color = color;
        this.style = style;
        this.ticks = ticks;
    }

    String getTitle() {
        return title;
    }

    BarColor getColor() {
        return color;
    }

    BarStyle getStyle() {
        return style;
    }

    long getTicks() {
        return ticks;
    }

    BossBar getBar() {
        return Bukkit.createBossBar(title, color, style);
    }
}

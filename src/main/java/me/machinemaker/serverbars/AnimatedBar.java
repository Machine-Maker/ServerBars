package me.machinemaker.serverbars;

import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class AnimatedBar extends BukkitRunnable {

    private List<Bar> bars;

    private Bar currentBar;
    private long counter = 0;

    private BossBar bossBar;

    AnimatedBar(List<Bar> bars, ServerBars plugin) {
        this.bars = bars;

        this.currentBar = this.bars.get(0);
        this.bossBar = currentBar.getBar();

        this.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        counter++;
        if (currentBar.getTicks() != 0 && counter % currentBar.getTicks() == 0) {
            Collections.rotate(bars, -1);
            currentBar = bars.get(0);
            if (currentBar.getTitle() != null) bossBar.setTitle(currentBar.getTitle());
            if (currentBar.getColor() != null) bossBar.setColor(currentBar.getColor());
            if (currentBar.getStyle() != null) bossBar.setStyle(currentBar.getStyle());
            counter = 0;
        }
    }

    BossBar getBossBar() {
        return bossBar;
    }
}

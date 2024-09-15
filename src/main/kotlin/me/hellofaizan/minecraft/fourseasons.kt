package me.hellofaizan.minecraft

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import net.kyori.adventure.text.Component
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.world.TimeSkipEvent
import org.bukkit.Bukkit
import org.bukkit.block.Biome
import net.kyori.adventure.text.format.NamedTextColor

class fourseasons : JavaPlugin(), Listener {

    private val seasonDuration = 1200L // 30 Minecraft days in ticks 30 * 24000L
    private var currentSeason = Season.SPRING
    private var daysPassed = 0L
    private var ticksPassed = 0L

    enum class Season {
        SPRING, SUMMER, AUTUMN, WINTER
    }

    override fun onEnable() {
        logger.info("SeasonsPlugin has been enabled!")
        server.pluginManager.registerEvents(this, this)
        startSeasonCycle()
        startDayCounter()
        startTickCounter()
    }

    override fun onDisable() {
        logger.info("SeasonsPlugin has been disabled!")
    }

    private fun startSeasonCycle() {
        object : BukkitRunnable() {
            override fun run() {
                changeSeason()
            }
        }.runTaskTimer(this, 0L, seasonDuration)
    }

    private fun changeSeason() {
        currentSeason = Season.entries.toTypedArray()[(currentSeason.ordinal + 1) % 4]
        daysPassed = 0
        ticksPassed = 0
        broadcastSeasonChange()
        applySeasonEffects()
        Bukkit.getOnlinePlayers().forEach { updatePlayerDebugInfo(it) }
    }

    private fun startTickCounter() {
        object : BukkitRunnable() {
            override fun run() {
                ticksPassed++
                if (ticksPassed % 20 == 0L) { // Update every second
                    Bukkit.getOnlinePlayers().forEach { updatePlayerDebugInfo(it) }
                }
            }
        }.runTaskTimer(this, 0L, 1L) // Run every tick
    }

    private fun startDayCounter() {
        object : BukkitRunnable() {
            override fun run() {
                daysPassed++
            }
        }.runTaskTimer(this, 0L, 24000L) // Run every Minecraft day
    }

    private fun broadcastSeasonChange() {
        Bukkit.broadcast(Component.text("The season has changed to ${currentSeason.name}!", NamedTextColor.GREEN))
    }

    private fun applySeasonEffects() {
        for (world in server.worlds) {
            when (currentSeason) {
                Season.SPRING -> SeasonEffects.applySpringEffects(world)
                Season.SUMMER -> SeasonEffects.applySummerEffects(world)
                Season.AUTUMN -> SeasonEffects.applyAutumnEffects(world)
                Season.WINTER -> SeasonEffects.applyWinterEffects(world)
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        updatePlayerDebugInfo(event.player)
    }

    @EventHandler
    fun onTimeSkip(event: TimeSkipEvent) {
        if (event.skipReason == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            daysPassed++
            Bukkit.getOnlinePlayers().forEach { updatePlayerDebugInfo(it) }
        }
    }

    private fun updatePlayerDebugInfo(player: org.bukkit.entity.Player) {
        val biome = player.location.block.biome
        if (biome in listOf(Biome.PLAINS, Biome.SAVANNA, Biome.DESERT)) {
            val nextSeason = Season.entries[(currentSeason.ordinal + 1) % 4]
            val secondsUntilNextSeason = (seasonDuration - ticksPassed) / 20

            val footerText = Component.text()
                .append(Component.text("Current Season: ", NamedTextColor.GOLD))
                .append(Component.text(currentSeason.name, NamedTextColor.YELLOW))
                .append(Component.newline())
                .append(Component.text("Next Season (", NamedTextColor.GOLD))
                .append(Component.text(nextSeason.name, NamedTextColor.YELLOW))
                .append(Component.text(") in ", NamedTextColor.GOLD))
                .append(Component.text("$secondsUntilNextSeason seconds", NamedTextColor.YELLOW))
                .build()

            Bukkit.getServer().sendPlayerListFooter(footerText)
        } else {
            val msg = "Seasons not active in this biome"
            Bukkit.getServer().sendPlayerListFooter(Component.text(msg, NamedTextColor.RED))
        }
    }
}

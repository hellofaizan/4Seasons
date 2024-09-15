package me.hellofaizan.minecraft

import org.bukkit.World
import org.bukkit.block.Biome
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object SeasonEffects {

    private val affectedBiomes = setOf(Biome.PLAINS, Biome.SAVANNA, Biome.DESERT)

    fun applySpringEffects(world: World) {
        world.players.filter { it.location.block.biome in affectedBiomes }.forEach { player ->
            player.addPotionEffect(PotionEffect(PotionEffectType.HASTE, 1200, 0))  // Speed up digging for a day
        }

        // start blooming flowers and trees
    }

    fun applySummerEffects(world: World) {
        world.players.filter { it.location.block.biome in affectedBiomes }.forEach { player ->
            if (player.location.block.biome == Biome.DESERT) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 1200, 0))  // Slowness in desert due to heat
            }


        }
    }

    fun applyAutumnEffects(world: World) {
        world.players.filter { it.location.block.biome in affectedBiomes }.forEach { player ->
            player.addPotionEffect(PotionEffect(PotionEffectType.LUCK, 1200, 0))  // Increased luck for a day

            // start shading the leaves and make them fall and set leaves to orange color
        }
    }

    fun applyWinterEffects(world: World) {
        world.players.filter { it.location.block.biome in affectedBiomes }.forEach { player ->
            if (player.location.block.biome != Biome.DESERT) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 1200, 0))  // Slowness due to cold
            }
            // start snowing and clear leaves from trees
        }
    }
}
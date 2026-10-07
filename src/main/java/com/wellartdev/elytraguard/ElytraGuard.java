package com.wellartdev.elytraguard;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Elytra durability watchdog for pilots who fly too long.
 *
 * An elytra snapping mid-flight is a one-way trip to the void floor.
 * ElytraGuard watches the chest-slot elytra and shows its remaining
 * durability percentage right under the crosshair, so you always know
 * how much airtime you have left. The moment 42 durability or fewer
 * remain (roughly the last seconds of gliding — an elytra loses 1
 * durability per second of flight), the line turns red, the warning
 * names the remaining points, and a deep thud plays once. The warning
 * re-arms when you swap the elytra out or repair it, so it never nags.
 *
 * Client-side only. No mixins, no networking, nothing persisted.
 */
public class ElytraGuard implements ClientModInitializer {
    public static final String MOD_ID = "elytraguard";

    /**
     * Elytra loses 1 durability per second of flight, so 42 remaining
     * is about the last safe window to land and mend.
     */
    private static final int CRITICAL_REMAINING = 42;

    // One-shot warning state. Re-armed whenever the elytra is swapped,
    // removed, or repaired (damage went down).
    private boolean warned = false;
    private Item lastItem = null;
    private int lastDamage = 0;

    // Read by the HUD element every frame; rebuilt on tick below.
    private String hudLine = null;
    private boolean hudCritical = false;
    private int tick = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "elytra_guard"),
                (extractor, deltaTracker) -> {
                    if (hudLine == null) {
                        return;
                    }
                    Minecraft mc = Minecraft.getInstance();
                    int y = mc.getWindow().getGuiScaledHeight() / 2 - 46;
                    int x = (mc.getWindow().getGuiScaledWidth() - mc.font.width(hudLine)) / 2;
                    extractor.text(mc.font, hudLine, x, y, hudCritical ? 0xFF5555 : 0xFFFFFF);
                });
    }

    private void onTick(Minecraft mc) {
        tick++;
        if (mc.player == null || tick % 5 != 0) {
            return;
        }

        hudLine = null;
        hudCritical = false;

        ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty() || !chest.is(Items.ELYTRA) || !chest.isDamageableItem()) {
            warned = false;
            lastItem = null;
            return;
        }

        Item item = chest.getItem();
        int damage = chest.getDamageValue();
        if (item != lastItem || damage < lastDamage) {
            // Fresh elytra, swapped back, or repaired — allow the
            // warning to fire again.
            warned = false;
        }
        lastItem = item;
        lastDamage = damage;

        int max = chest.getMaxDamage();
        int remaining = max - damage;
        int pct = max > 0 ? (int) Math.round(100.0 * remaining / max) : 100;

        if (remaining <= CRITICAL_REMAINING) {
            hudLine = "Elytra critical: " + remaining + " left!";
            hudCritical = true;
            if (!warned) {
                warned = true;
                // Deep thud — alarming without being annoying, and it
                // only plays once per worn-out elytra.
                mc.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS, 1.0f));
            }
        } else {
            hudLine = "Elytra: " + pct + "%";
        }
    }
}

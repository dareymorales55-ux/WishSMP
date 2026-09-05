package com.vessel.smp.items;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Essence implements Listener {

    private static final String IDENTIFIER_KEY = "vessel_essence";

    private static final TextColor LIGHT_PURPLE = TextColor.color(0xE6CCFF);
    private static final TextColor PURPLE = TextColor.color(0xB266FF);
    private static final TextColor DARK_PURPLE = TextColor.color(0x4B0082);

    private final PlayerDataManager dataManager;
    private final Apply apply;

    public Essence(PlayerDataManager dataManager, Apply apply) {
        this.dataManager = dataManager;
        this.apply = apply;
    }

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(gradient("ᴇꜱꜱᴇɴᴄᴇ", PURPLE, DARK_PURPLE, true));
        meta.lore(List.of(gradient("ʀɪɢʜᴛ-ᴄʟɪᴄᴋ ᴛᴏ ɢᴀɪɴ +1 ᴇꜱꜱᴇɴᴄᴇ", LIGHT_PURPLE, PURPLE, false)));

        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(
                new NamespacedKey("vessel", IDENTIFIER_KEY),
                PersistentDataType.BOOLEAN,
                true
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isEssence(ItemStack item) {
        if (item == null || item.getType() != Material.GHAST_TEAR || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey("vessel", IDENTIFIER_KEY),
                PersistentDataType.BOOLEAN
        );
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack held = event.getItem();
        if (!isEssence(held)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerData data = dataManager.get(player);
        if (data == null) {
            return;
        }

        event.setCancelled(true);

        if (data.isFloored()) {
            player.sendMessage(gradient(
                    "ʏᴏᴜ'ʀᴇ ᴅɪꜱᴄᴏɴɴᴇᴄᴛᴇᴅ ꜰʀᴏᴍ ʏᴏᴜʀ ꜱᴘɪʀɪᴛ -- ʀᴇᴄᴏᴠᴇʀ ꜰɪʀꜱᴛ.",
                    LIGHT_PURPLE, PURPLE, true));
            return;
        }

        boolean absorbed = data.absorb();
        if (!absorbed) {
            player.sendMessage(gradient(
                    "ʏᴏᴜ'ʀᴇ ᴀʟʀᴇᴀᴅʏ ᴀᴛ ᴍᴀxɪᴍᴜᴍ ᴇꜱꜱᴇɴᴄᴇ.",
                    LIGHT_PURPLE, PURPLE, true));
            return;
        }

        if (held.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            held.setAmount(held.getAmount() - 1);
        }

        dataManager.save(data);
        apply.refresh(player);

        Component message = gradient("ʏᴏᴜ ɢᴀɪɴᴇᴅ ᴀɴ ᴇꜱꜱᴇɴᴄᴇ, ʏᴏᴜ ᴀʀᴇ ɴᴏᴡ ᴀᴛ (",
                LIGHT_PURPLE, PURPLE, true)
                .append(Component.text(String.valueOf(data.getEssenceLevel())))
                .append(gradient(")", LIGHT_PURPLE, PURPLE, true));
        player.sendMessage(message);
    }

    private static Component gradient(String text, TextColor start, TextColor end, boolean bold) {
        Component result = Component.empty();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);
            TextColor stepColor = TextColor.lerp(ratio, start, end);

            result = result.append(Component.text(String.valueOf(text.charAt(i)))
                    .color(stepColor)
                    .decoration(TextDecoration.BOLD, bold)
                    .decoration(TextDecoration.ITALIC, false));
        }

        return result;
    }
}

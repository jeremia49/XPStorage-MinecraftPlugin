package pw.jere.XPStorage

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.meta.ItemMeta

class CraftRecipe(val main:Main) {

    fun nonMoveXPStorageItemStack() : ItemStack {
        val stack = ItemStack(Material.BARREL,1)

        if(stack.itemMeta !== null){
            val itemMeta : ItemMeta = stack.itemMeta!!
            itemMeta.setDisplayName(main.NONMOVABLEXPSTORAGE)
            itemMeta.isUnbreakable = true
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ATTRIBUTES)

            stack.setItemMeta(itemMeta)
        }

        return stack
    }

    fun nonMoveXPStorageRecipe(X: Material) : ShapedRecipe {
        val rec = ShapedRecipe(NamespacedKey(main,"nonMoveXPStorage"),nonMoveXPStorageItemStack())
        rec.shape("DBD",
                        "OXO",
                        "OOO",
                    )
        rec.setIngredient('D', Material.DIAMOND)
        rec.setIngredient('B', Material.BLAZE_POWDER)
        rec.setIngredient('O', Material.OBSIDIAN)
        rec.setIngredient('X',X)

        return rec
    }

    fun moveXPStorageItemStack() : ItemStack {
        val stack = ItemStack(Material.BARREL,1)

        if(stack.itemMeta !== null){
            val itemMeta : ItemMeta = stack.itemMeta!!
            itemMeta.setDisplayName(main.MOVABLEXPSTORAGE)
            itemMeta.isUnbreakable = true
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_ATTRIBUTES)

            stack.setItemMeta(itemMeta)
        }

        return stack
    }

    fun moveXPStorageRecipe(X: Material) : ShapelessRecipe {
        val rec = ShapelessRecipe(NamespacedKey(main,"moveXPStorage"),moveXPStorageItemStack())
        rec.addIngredient(Material.BARREL)
        rec.addIngredient(X)
        return rec
    }
}
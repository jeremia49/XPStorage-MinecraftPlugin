package pw.jere.XPStorage

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Barrel
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin


class Main : JavaPlugin(){

    public val NONMOVABLEXPSTORAGE : String = ChatColor.AQUA.toString() + "NonMoveable XP Storage"
    public val MOVABLEXPSTORAGE : String = ChatColor.RED.toString() + "Moveable XP Storage"
    public val STORAGEINVENTORYTITLE : String = ChatColor.RED.toString() + "Save % XP"
    public val cR : CraftRecipe = CraftRecipe(this)
    public val nonMoveXPStorageRecipe = cR.nonMoveXPStorageRecipe(Material.BARREL)
    public val moveXPStorageRecipe = cR.moveXPStorageRecipe(Material.DRAGON_EGG)
    public val moveXPStorageItemStack = cR.moveXPStorageItemStack()

    override fun onEnable() {
        super.onEnable()

        server.addRecipe(nonMoveXPStorageRecipe)
        server.addRecipe(moveXPStorageRecipe)

        server.pluginManager.registerEvents(EventListener(this),this)
    }

    override fun onDisable() {
        super.onDisable()
        server.clearRecipes()
    }



}
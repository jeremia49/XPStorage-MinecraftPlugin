package pw.jere.XPStorage

import org.bukkit.*
import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType


class EventListener(val main:Main) : Listener {

    val UUIDtoBlockLocation : HashMap<String, StorageTypeLocation> = HashMap()
    val ExpFix : ExpFix = ExpFix()

    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEvent){
        if(!e.hasBlock()) return

        val block = e.clickedBlock
        if(block != null && block.type == Material.BARREL && e.action == (Action.RIGHT_CLICK_BLOCK)) {
            if(block.state is TileState){
                val state : TileState = block.state as TileState;
                val container : PersistentDataContainer =  state.persistentDataContainer

                if(container.has(NamespacedKey(main, "AUTHOR"), PersistentDataType.STRING)
                            && container.has(NamespacedKey(main, "XP"), PersistentDataType.INTEGER)
                            && container.has(NamespacedKey(main, "TYPE"), PersistentDataType.STRING)
                ){

                    if(e.player.inventory.itemInMainHand == ItemStack(Material.FLINT_AND_STEEL)){
                        block.type =  Material.AIR
                        val xp = container.get(NamespacedKey(main, "XP"), PersistentDataType.INTEGER)
                        if (xp != null) {
                            e.player.world.createExplosion(block.location, 4F * (xp / 3200) , true, true )
                        }
                    }else{
                        e.player.sendMessage("Author : " + container.get(NamespacedKey(main, "AUTHOR"), PersistentDataType.STRING)
                            ?.let { main.server.getPlayer(it)?.displayName })
                        e.player.sendMessage("XP Stored : " + container.get(NamespacedKey(main, "XP"), PersistentDataType.INTEGER).toString()+" XP")
                        e.isCancelled = true
                    }
                }
            }
        }

    }


    @EventHandler
    fun onPlayerClickInventory(e:InventoryClickEvent){
        if(e.view.title == main.STORAGEINVENTORYTITLE){
            if(e.currentItem !== null){
                if(e.currentItem!!.itemMeta !==null){

                    val dName = e.currentItem!!.itemMeta!!.displayName
                    var percentage : Int = 0


                    if (dName == "20%"){
                        percentage = 20
                    }else if (dName == "30%"){
                        percentage = 30
                    }else if (dName == "40%"){
                        percentage = 40
                    }else if (dName == "50%"){
                        percentage = 50
                    }else if (dName == "60%"){
                        percentage = 60
                    }else if (dName == "70%"){
                        percentage = 70
                    }else if (dName == "80%"){
                        percentage = 80
                    }else if (dName == "90%"){
                        percentage = 90
                    }else {
                        percentage = 100
                    }


                    val BlockLocation : Location? = UUIDtoBlockLocation[e.whoClicked.uniqueId.toString()]?.location

                    if(BlockLocation != null){
                        val block = e.whoClicked.world.getBlockAt(BlockLocation)
                        val xp = (ExpFix.getTotalExperience((e.whoClicked as Player)).toFloat() * percentage /100).toInt()

                        ExpFix.setTotalExperience( (e.whoClicked as Player), ExpFix.getTotalExperience((e.whoClicked as Player)) - xp)

                        if(block.state is TileState){
                            val state : TileState = block.state as TileState;
                            val container : PersistentDataContainer =  state.persistentDataContainer
                            container.set(NamespacedKey(main, "AUTHOR"), PersistentDataType.STRING,e.whoClicked.uniqueId.toString())
                            container.set(NamespacedKey(main, "XP"), PersistentDataType.INTEGER,xp)
                            container.set(NamespacedKey(main, "TYPE"), PersistentDataType.STRING, UUIDtoBlockLocation[e.whoClicked.uniqueId.toString()]!!.type)
                            state.update()
                        }

                        (e.whoClicked as Player).sendMessage(ChatColor.BLUE.toString() + "Kamu berhasil menyimpan "+ xp.toString()+" XP !")

                        UUIDtoBlockLocation.remove(e.whoClicked.uniqueId.toString())
                    }

                    e.whoClicked.closeInventory()

                }
            }

            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerExitInventory(e:InventoryCloseEvent){
        if(e.view.title == main.STORAGEINVENTORYTITLE){
            val BlockLocation : Location? = UUIDtoBlockLocation[e.player.uniqueId.toString()]?.location
            if(BlockLocation != null){
                val block = e.player.world.getBlockAt(BlockLocation)
                block.type = Material.AIR
                if(UUIDtoBlockLocation[e.player.uniqueId.toString()]?.type == main.NONMOVABLEXPSTORAGE) {
                    e.player.inventory.addItem(CraftRecipe(main).nonMoveXPStorageItemStack())
                }else if(UUIDtoBlockLocation[e.player.uniqueId.toString()]?.type == main.MOVABLEXPSTORAGE){
                    e.player.inventory.addItem(CraftRecipe(main).moveXPStorageItemStack())
                }
                UUIDtoBlockLocation.remove(e.player.uniqueId.toString())
            }

        }
    }

    @EventHandler
    fun onPlayerPlaceBlock(e:BlockPlaceEvent){
        val block = e.block
        if(block.state.isPlaced){
            if(block.type.isItem && block.type == Material.BARREL){
                val itemMeta = e.player.inventory.itemInMainHand.itemMeta
                if (itemMeta != null) {

                    val inv : Inventory = Bukkit.createInventory(null,9,main.STORAGEINVENTORYTITLE)

                    inv.addItem(createInventoryOption("20%"))
                    inv.addItem(createInventoryOption("30%"))
                    inv.addItem(createInventoryOption("40%"))
                    inv.addItem(createInventoryOption("50%"))
                    inv.addItem(createInventoryOption("60%"))
                    inv.addItem(createInventoryOption("70%"))
                    inv.addItem(createInventoryOption("80%"))
                    inv.addItem(createInventoryOption("90%"))
                    inv.addItem(createInventoryOption("100%"))

                    if(itemMeta.displayName == main.NONMOVABLEXPSTORAGE) {
                        UUIDtoBlockLocation[e.player.uniqueId.toString()] = StorageTypeLocation(main.NONMOVABLEXPSTORAGE, block.location)
                        e.player.openInventory(inv)
                    }else if(itemMeta.displayName == main.MOVABLEXPSTORAGE) {
                        UUIDtoBlockLocation[e.player.uniqueId.toString()] = StorageTypeLocation(main.MOVABLEXPSTORAGE, block.location)
                        e.player.openInventory(inv)

                    }

                }

            }
        }
        return
    }

    @EventHandler
    fun onPlayerDestroyBlock(e:BlockBreakEvent){
        val block = e.block
        if( block.type == Material.BARREL && block.state is TileState){
            val state : TileState = block.state as TileState;
            val container : PersistentDataContainer =  state.persistentDataContainer

            if(container.has(NamespacedKey(main, "AUTHOR"), PersistentDataType.STRING)
                && container.has(NamespacedKey(main, "XP"), PersistentDataType.INTEGER)
                && container.has(NamespacedKey(main, "TYPE"), PersistentDataType.STRING)
            ){
                if( container.get(NamespacedKey(main, "TYPE"), PersistentDataType.STRING) == main.NONMOVABLEXPSTORAGE){

                    container.get(NamespacedKey(main, "XP"), PersistentDataType.INTEGER)?.let { e.player.giveExp(it) }
                    e.player.sendMessage("Anda berhasil mengambil " + container.get(NamespacedKey(main, "XP"), PersistentDataType.INTEGER).toString() +" XP")
                    e.isDropItems = false
                    e.isCancelled = true
                    e.block.type = Material.AIR
                }else if(container.get(NamespacedKey(main, "TYPE"), PersistentDataType.STRING) == main.MOVABLEXPSTORAGE){
                    container.get(NamespacedKey(main, "XP"), PersistentDataType.INTEGER)?.let { e.player.giveExp(it) }
                    e.player.sendMessage("Anda berhasil mengambil " + container.get(NamespacedKey(main, "XP"), PersistentDataType.INTEGER).toString() +" XP")
                    e.isDropItems = true
                }
            }
        }
    }

    fun createInventoryOption(percentage:String):ItemStack{
        val redStone : ItemStack = ItemStack(Material.REDSTONE)
        val redStoneMeta = redStone.itemMeta
        redStoneMeta!!.setDisplayName(percentage)
        redStoneMeta.lore = listOf(ChatColor.LIGHT_PURPLE.toString() +"Simpan "+percentage+" dari XPmu")
        redStone.itemMeta = redStoneMeta
        return redStone
    }

    @EventHandler
    fun onPlayerPrepareCraftItem(e:PrepareItemCraftEvent){
        if(e.recipe?.result == main.moveXPStorageItemStack){
            var isExist = false
            for (itemStack : ItemStack in e.view.topInventory.contents){
                if(itemStack.itemMeta == main.cR.nonMoveXPStorageItemStack().itemMeta){
                    isExist = true
                    break
                }
            }
            if(!isExist){
                e.inventory.result = ItemStack(Material.AIR);
            }
        }
    }
}
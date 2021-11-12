package pw.jere.XPStorage

import org.bukkit.entity.Player

//Source : https://gitlab.com/Flamedek/RPGme/blob/9627237fb3be8bd4fd402ddff1def23f2f7d0208/core/FlameCore-Git/src/nl/flamecore/util/ExpFix.java

class ExpFix {
    /*
     * This class is a straight copy from essentials source code. Bukkits exp part currently doesn't work correctly.
    */
    //This method is used to update both the recorded total experience and displayed total experience.
    //We reset both types to prevent issues.
    fun setTotalExperience(player: Player, exp: Int) {
        require(exp >= 0) { "Experience is negative!" }
        player.exp = 0f
        player.level = 0
        player.totalExperience = 0

        //This following code is technically redundant now, as bukkit now calulcates levels more or less correctly
        //At larger numbers however... player.getExp(3000), only seems to give 2999, putting the below calculations off.
        var amount = exp
        while (amount > 0) {
            val expToLevel: Int = this.getExpAtLevel(player)
            amount -= expToLevel
            if (amount >= 0) {
                // give until next level
                player.giveExp(expToLevel)
            } else {
                // give the rest
                amount += expToLevel
                player.giveExp(amount)
                amount = 0
            }
        }
    }

    private fun getExpAtLevel(player: Player): Int {
        return this.getExpAtLevel(player.level)
    }

    fun getExpAtLevel(level: Int): Int {
        if (level > 29) {
            return 62 + (level - 30) * 7
        }
        return if (level > 15) {
            17 + (level - 15) * 3
        } else 17
    }

    fun getExpToLevel(level: Int): Int {
        var currentLevel = 0
        var exp = 0
        while (currentLevel < level) {
            exp += this.getExpAtLevel(currentLevel)
            currentLevel++
        }
        if (exp < 0) {
            exp = Int.MAX_VALUE
        }
        return exp
    }

    //This method is required because the bukkit player.getTotalExperience() method, shows exp that has been 'spent'.
    //Without this people would be able to use exp and then still sell it.
    fun getTotalExperience(player: Player): Int {
        var exp = Math.round(this.getExpAtLevel(player) * player.exp).toInt()
        var currentLevel = player.level
        while (currentLevel > 0) {
            currentLevel--
            exp += this.getExpAtLevel(currentLevel)
        }
        if (exp < 0) {
            exp = Int.MAX_VALUE
        }
        return exp
    }

    fun getExpUntilNextLevel(player: Player): Int {
        val exp = Math.round(this.getExpAtLevel(player) * player.exp).toInt()
        val nextLevel = player.level
        return this.getExpAtLevel(nextLevel) - exp
    }
}




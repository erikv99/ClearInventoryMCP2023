package clear_inventory.events;

import clear_inventory.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener
{
    private final Plugin _plugin;

    public PlayerDeath(Plugin pl)
    {
        _plugin = pl;
    }

    @EventHandler
    public void OnPlayerDeathEvent(PlayerDeathEvent e)
    {
        var clearInvOnDeath = (boolean) _plugin.Settings.get("clearInventoryOnDeath");
        var dropXpOnDeath = (boolean) _plugin.Settings.get("dropXpOnDeath");
        var dropItemsOnDeath = (boolean) _plugin.Settings.get("dropItemsOnDeath");

        if (_plugin.IsInExcludedWorld(e.getEntity()))
        {
            return;
        }

        if (!dropItemsOnDeath)
        {
            e.getDrops().clear();
        }

        if (clearInvOnDeath)
        {
            e.setKeepInventory(true);
            Player player = e.getEntity();
            player.getInventory().clear();
        }

        if (!dropXpOnDeath)
        {
            e.setDroppedExp(0);
        }
    }
}

package clear_inventory.events;

import clear_inventory.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener
{
    private final Plugin _plugin;

    public PlayerQuit(Plugin pl)
    {
        _plugin = pl;
    }

    // prio lowest will get executed first which means the permissions have not been wiped yet.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        var clearXpOnLeave = (boolean) _plugin.Settings.get("clearXpOnLeave");
        var clearInventoryOnLeave = (boolean) _plugin.Settings.get("clearInventoryOnLeave");

        if (_plugin.IsInExcludedWorld(e.getPlayer()))
        {
            return;
        }

        Player player = e.getPlayer();

        boolean hasXPBypass = player.hasPermission("clearinventory.leave.clearxpbypass");
        boolean hasInvBypass = player.hasPermission("clearinventory.leave.clearinvbypass");

        if (clearInventoryOnLeave && !hasInvBypass)
        {
            player.getInventory().clear();
        }

        if (clearXpOnLeave && !hasXPBypass)
        {
            player.setLevel(0);
            player.setExp(0);
        }
    }
}

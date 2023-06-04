package clear_inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import clear_inventory.events.PlayerDeath;
import clear_inventory.events.PlayerQuit;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin
{
    public Logger Logger;
    public PluginDescriptionFile DescFile;
    public HashMap<String, Object> Settings;

    public Plugin()
    {
        Logger = getLogger();
        DescFile = getDescription();
    }

    @Override
    public void onEnable()
    {
        Metrics metrics = new Metrics(this, 18666);
        _loadConfig();
//        // Checking if a plugin folder exists if not making one.
//        if (!getDataFolder().exists())
//        {
//            getDataFolder().mkdir();
//            _registerConfig();
//        }


        _loadSettings();



        _registerEvents();
    }

    @Override
    public void onDisable()
    {
    }

    private void _registerEvents()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new PlayerQuit(this), this);
    }

    private void _loadSettings()
    {
        var config = getConfig();
        var settings = new HashMap<String, Object>();
        settings.put("clearInventoryOnDeath", config.getBoolean("clearInventoryOnDeath"));
        settings.put("dropXpOnDeath", config.getBoolean("dropXpOnDeath"));
        settings.put("dropItemsOnDeath", config.getBoolean("dropItemsOnDeath"));
        settings.put("clearXpOnLeave", config.getBoolean("clearXpOnLeave"));
        settings.put("clearInventoryOnLeave", config.getBoolean("clearInventoryOnLeave"));

        var excludedWorlds = _loadExcludedWorlds();
        Logger.severe("Excluded world count: " + excludedWorlds.size());
        settings.put("excludedWorlds", new ArrayList<>(excludedWorlds));
        Settings = settings;
    }

    @SuppressWarnings("ConstantConditions")
    private List<World> _loadExcludedWorlds()
    {
        var config = getConfig();

        if (!config.contains("excludedWorlds", true))
        {
            return new ArrayList<>();
        }

        return config.getStringList("excludedWorlds")
              .stream()
              .map(s ->
              {
                  var world = Bukkit.getWorld(s);

                  if (s == null)
                  {
                      Logger.warning("Ignored world '" + s + "' in excluded worlds, world could not be found.");
                  }
                  else
                  {
                      return world;
                  }

                  return null;
              })
              .filter(Objects::nonNull)
              .toList();
    }

    private void _loadConfig()
    {
        var config = getConfig();

        if (config.getKeys(true).isEmpty())
        {
            Logger.warning("No config.yml detected, creating default config.yml now");
            saveDefaultConfig();

            // Reloads config from disk
            reloadConfig();

            saveConfig();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean IsInExcludedWorld(Entity entity)
    {
        List<World> excludedWorlds = new ArrayList<>();

        try
        {
            excludedWorlds = (List<World>) Settings.get("excludedWorlds");
        }
        catch (Exception e)
        {
            Logger.severe("Failed to load excluded worlds");
            return true;
        }

        var entWolrd = entity.getWorld().getName();
        Logger.severe("entity world: " + entWolrd);
        return excludedWorlds.contains(entity.getWorld());
    }
}
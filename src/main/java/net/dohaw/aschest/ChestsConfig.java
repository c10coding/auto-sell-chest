package net.dohaw.aschest;

import net.dohaw.corelib.Config;
import net.dohaw.corelib.serializers.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ChestsConfig extends Config {

    public ChestsConfig() {
        super("chests.yml");
    }

    public Map<UUID, List<Location>> getAutoSellChestLocations(){
        LocationSerializer ls = new LocationSerializer();
        Map<UUID, List<Location>> locations = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("Auto Sell Chest Locations");
        if(section != null){
            for(String uuidStr : section.getKeys(false)){
                List<Location> chestLocations = new ArrayList<>();
                for(String locStr : section.getStringList(uuidStr)){
                    chestLocations.add(ls.toLocationFromLine(locStr));
                }
                locations.put(UUID.fromString(uuidStr), chestLocations);
            }
        }
        return locations;
    }

    public void saveAutoSellChestLocations(Map<UUID, List<Location>> autoSellChestLocations){
        LocationSerializer ls = new LocationSerializer();
        for(Map.Entry<UUID, List<Location>> entry : autoSellChestLocations.entrySet()){
            UUID uuid = entry.getKey();
            List<Location> locations = entry.getValue();
            List<String> locationStrs = new ArrayList<>();
            for(Location location : locations){
                locationStrs.add(ls.toString(location));
            }
            config.set("Auto Sell Chest Locations." + uuid.toString(), locationStrs);
        }
        saveConfig();
    }

}

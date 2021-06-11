package net.dohaw.aschest;

import net.dohaw.corelib.Config;
import net.dohaw.corelib.serializers.LocationSerializer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ChestsConfig extends Config {

    public ChestsConfig() {
        super("chests.yml");
    }

    public List<Location> getAutoSellChestLocations(){
        LocationSerializer ls = new LocationSerializer();
        List<Location> locations = new ArrayList<>();
        for(String locStr : config.getStringList("Auto Sell Chest Locations")){
            locations.add(ls.toLocationFromLine(locStr));
        }
        return locations;
    }

    public void saveAutoSellChestLocations(List<Location> autoSellChestLocations){
        LocationSerializer ls = new LocationSerializer();
        List<String> locationStrs = new ArrayList<>();
        for(Location loc : autoSellChestLocations){
            locationStrs.add(ls.toString(loc));
        }
        config.set("Auto Sell Chest Locations", locationStrs);
        saveConfig();
    }

}

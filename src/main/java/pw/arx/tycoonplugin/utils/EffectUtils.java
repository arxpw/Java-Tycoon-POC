package pw.arx.tycoonplugin.utils;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;

public class EffectUtils {

    public static ArrayList<Location> getCircle(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++)
        {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(center.getWorld(), x, center.getY(), z));
        }
        return locations;
    }

    public static void playerFlightEffect(Location l, Particle p) {
        ArrayList<Location> circleArray = getCircle(l, 0.3, 8);

        for(int i = 0; i < circleArray.size(); i++) {
            l.getWorld().spawnParticle(
                    p,
                    circleArray.get(i).getX(),
                    circleArray.get(i).getY(),
                    circleArray.get(i).getZ(),
                    0, 0, 0, 0, 1);
        }
    }

    public static void spawnFireworks(Location location, int amount){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
       
        fw.setFireworkMeta(fwm);
        fw.detonate();
       
        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }
}

package pw.arx.tycoonplugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public final class LocUtils {
	// prevent this class being constructed
	private LocUtils() { }
	
	public static ArrayList<Location> selectOffset(Location l, Integer BOX_OFFSET) {
		
		// Integer BOX_OFFSET = 2;
		ArrayList<Location> RETURNLOC = new ArrayList<Location>();
			
		RETURNLOC.add(new Location(l.getWorld(), l.getX() + BOX_OFFSET, l.getY(), l.getZ() + BOX_OFFSET));
		RETURNLOC.add(new Location(l.getWorld(), l.getX() - BOX_OFFSET, l.getY(), l.getZ() - BOX_OFFSET));
		
		return RETURNLOC;
	}
	
	public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "NORTH";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NORTH_EAST";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "EAST";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SOUTH_EAST";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "SOUTH";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SOUTH_WEST";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "WEST";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NORTH_WEST";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "NORTH";
        } else {
            return null;
        }
    }
	
	public static String invertFacingDirection(String dir) {
        String return_string = "NORTH";
        
        if(dir == "NORTH")
        	return_string = "SOUTH";

        if(dir == "NORTH_EAST")
        	return_string = "SOUTH_EAST";
        
        if(dir == "NORTH_WEST")
        	return_string = "SOUTH_WEST";
        
        if(dir == "EAST")
        	return_string = "WEST";
        
        if(dir == "SOUTH")
        	return_string = "NORTH";

        if(dir == "SOUTH_WEST")
        	return_string = "NORTH_WEST";

        if(dir == "SOUTH_EAST")
        	return_string = "NORTH_EAST";
        
        if(dir == "WEST")
        	return_string = "EAST";
        
        return return_string;
    }
	
	
	public static Integer schematicRotateAmount(Player p) {
		String Direction = LocUtils.getCardinalDirection(p);
		
		if(Direction == "NORTH" || Direction == "NORTH_WEST" || Direction == "NORTH_EAST")
			return 0;
		
		if(Direction == "EAST")
			return -90;
		
		if(Direction == "SOUTH" || Direction == "SOUTH_EAST" || Direction == "SOUTH_WEST")
			return 180;
		
		if(Direction == "WEST")
			return 90;
		
		return 0;
		
	}
	
}

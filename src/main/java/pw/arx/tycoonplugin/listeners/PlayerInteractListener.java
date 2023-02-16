package pw.arx.tycoonplugin.listeners;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import pw.arx.tycoonplugin.Tycoon;
import pw.arx.tycoonplugin.commandhandlers.CreateCommandHandler;
import pw.arx.tycoonplugin.managers.BuildingManager;
import pw.arx.tycoonplugin.managers.TycoonManager;
import pw.arx.tycoonplugin.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerInteractListener implements Listener {
	Action[] RightClickingActions = {
		Action.RIGHT_CLICK_BLOCK,
		Action.RIGHT_CLICK_AIR
	};
	
	Action[] LeftClickingActions = {
		Action.LEFT_CLICK_AIR,
		Action.LEFT_CLICK_BLOCK
	};

	// combined applicable listeners
	Object[] AllApplicableListeners = Stream.concat(
			Arrays.stream(LeftClickingActions),
			Arrays.stream(RightClickingActions)
	).toArray();
	
	Cooldown cratedown = new Cooldown(2);

	private Player player;
	private Block targetBlock;
	private Block clickedBlock;
	private String activeBuildingName;
	private HashMap<UUID, Location> selectionLocations;

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerClick(PlayerInteractEvent event)
	{
    	if (Arrays.stream(AllApplicableListeners).noneMatch(event.getAction()::equals)) {
			return;
		}
	    
       	EquipmentSlot e = event.getHand();

    	if (e == null || !e.equals(EquipmentSlot.HAND)) {
			return;
		}

		player = event.getPlayer();
		targetBlock = BlockUtils.targetBlock(player);
		clickedBlock = event.getClickedBlock();

		boolean usingActiveLandSelector = player.getItemInHand().equals(ItemUtils.LandSelector(true));

		boolean isLeftClicking = Arrays.asList(LeftClickingActions).contains(event.getAction());
		boolean isRightClicking = Arrays.asList(RightClickingActions).contains(event.getAction());

		if (usingActiveLandSelector) {
			selectionLocations = Tycoon.SELECTION_LOCATIONS;

			if (isLeftClicking) {
				handleLandSelection();
				return;
			}

			if (isRightClicking) {
				handleClearingLandSelection();
				return;
			}
		}

		activeBuildingName = TycoonManager.getBuildingByLocation(event.getPlayer(), event.getClickedBlock().getLocation().add(0,1,0));

		boolean leftClickingBarrel = event.getAction() == Action.LEFT_CLICK_BLOCK && targetBlock.getType() == Material.BARREL;
		boolean usingBlockClicker = activeBuildingName != null && activeBuildingName.contains("crate");

		if (leftClickingBarrel && usingBlockClicker) {
			if (!cratedown.isOnCooldown(event.getPlayer())) {
				BuildingManager.buildingClicker(activeBuildingName, event.getPlayer());
				cratedown.setCooldown(event.getPlayer());
			}

			return;
		}

		// Player is right-clicking a block!
		// - to buy a building
		// - to upgrade an existing building
	    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material clickedBlockType = event.getClickedBlock().getType();

	    	if (clickedBlockType == Material.PLAYER_HEAD) {
				handleBuyingBuilding();
	    	}
	    	
	    	if (clickedBlockType == Material.BARREL) {
	    		if (activeBuildingName != null) {
	    			event.setCancelled(true);
	    			event.getPlayer().openInventory(MenuUtils.getBuildingUpgraderInventory(activeBuildingName, event.getPlayer()));
	    		}
	    	}
	    }
    }

	private void handleBuyingBuilding()
	{
		if (!TycoonManager.hasTycoon(player)) {
			return;
		}

		if (!WorldEditUtils.playerInsideTheirRegion(player)) {
			player.sendMessage("This isn't your tycoon!");
			return;
		}

		for (Hologram holo : HolographicDisplaysAPI.get(Tycoon.getPlugin()).getHolograms()) {
			Block block = clickedBlock;
			String blockWorldName = block.getWorld().getName();
			String worldName = holo.getPosition().getWorldName();

			if (!blockWorldName.equals(worldName)) {
				return;
			}

			if (holo.getPosition().distance(block.getLocation()) > 2) {
				return;
			}

			HologramLine tl = holo.getLines().get(0);

			boolean buyer = tl.toString().contains("BUILDING");

			if (buyer) {
				player.openInventory(MenuUtils.LoadBuildingsMenu(player));
				return;
			}
		}
	}

	private void handleCreatingLandSelection()
	{
		// player has an existing selection?
		if (selectionLocations.containsKey(player.getUniqueId())) {
			// UPDATE OLD BLOCKS...
			ArrayList<Location> OLD_SELECTION = LocUtils.selectOffset(selectionLocations.get(player.getUniqueId()), Tycoon.SEL_OFFSET);
			BlockUtils.blockUpdate(OLD_SELECTION.get(0), OLD_SELECTION.get(1), player);
		}

		TycoonSelector.SelectBlocks(player);
	}

	private void handleClearingLandSelection()
	{
		if (!selectionLocations.containsKey(player.getUniqueId())) {
			player.sendMessage(StringUtils.c("&cYou don't have any land selected!"));
			return;
		}

		// UPDATE BLOCKS...
		if (TycoonSelector.ClearSelection(player)) {
			player.sendMessage(StringUtils.c("Land selection &bcleared&r!"));
		}
	}

	private void handleLandSelection()
	{
		Location targetLocation = targetBlock.getLocation();

		if (TycoonManager.hasTycoon(player)) {
			player.sendMessage(StringUtils.c("&cYou already have a tycoon! Delete this before making a new one with /tycoon delete"));
			return;
		}

		if (!TycoonManager.canPlaceTycoon(targetLocation)) {
			player.sendMessage(StringUtils.c("&cAnother Tycoon is already too close!"));
			return;
		}

		// does the selection contain a player?
		if (Tycoon.SELECTION_LOCATIONS.containsKey(player.getUniqueId())) {
			Location selectedLocation = Tycoon.SELECTION_LOCATIONS.get(player.getUniqueId());
			ArrayList<Location> insideCubeLocation = LocUtils.selectOffset(selectedLocation, Tycoon.SEL_OFFSET);

			boolean blockInside = BlockUtils.insideCuboid(targetLocation, insideCubeLocation.get(0), insideCubeLocation.get(1));

			if (!blockInside) {
				return;
			}

			// is the player updating a block on their client side?
			// will need to update this back to the selection.
			Bukkit.getScheduler().scheduleSyncDelayedTask(Tycoon.getPlugin(), new Runnable() {
				public void run() {
					if (targetBlock.equals(selectedLocation.getBlock())) {
						player.sendBlockChange(targetLocation, Material.GOLD_BLOCK.createBlockData());
					}
				}
			}, 3);

			if (!targetBlock.equals(selectedLocation.getBlock())) {
				player.sendMessage(StringUtils.c("&cArea already selected!"));
				return;
			}

			CreateCommandHandler.Handle(player);
			return;
		}

		handleCreatingLandSelection();
	}
}

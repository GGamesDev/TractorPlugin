package be.openmc.ggames.tractor;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.*;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import be.openmc.ggames.utils.BossBarUtils;

import static be.openmc.ggames.Main.schedulerRun;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class TractorMovement {
    protected Object packet;
    protected Player player;
    protected String license;
    protected ArmorStand standMain;
    protected ArmorStand standSkin;
    protected ArmorStand standMainSeat;
    protected @Nullable ArmorStand standFront;
    protected @Nullable ArmorStand standBack;
    protected @Nullable ArmorStand standVolant;
    protected @Nullable ArmorStand standTrailer;
    protected boolean isFalling = false;

    public void TractorMovement(Player player, Object packet) {

        this.packet = packet;

        this.player = player;
        if (player.getVehicle() == null) return;
        final Entity tractor = player.getVehicle();

        if (!(tractor instanceof ArmorStand)) return;
        if (tractor.getCustomName() == null) return;

        if (tractor.getCustomName().replace("TRACTOR_MAINSEAT_", "").isEmpty()) return;
        this.license = tractor.getCustomName().replace("TRACTOR_MAINSEAT_", "");

        if (TractorData.autostand.get("TRACTOR_MAIN_" + license) == null) return;

        if (TractorData.speed.get(license) == null) {
        	TractorData.speed.put(license, 0.0);
            return;
        }

        if (TractorData.fuel.get(license) < 1) {
            BossBarUtils.setBossBarFuelValue(0 / 100.0D, license);
            BossBarUtils.setBossBarSeedsValue(0 / 1000.0D, license);
            BossBarUtils.setBossBarBonnemealValue(0 / 1000.0D, license);
            return;
        }

        BossBarUtils.setBossBarFuelValue(TractorData.fuel.get(license) / 100.0D, license);
        BossBarUtils.setBossBarSeedsValue(TractorData.seeds.get(license) / 1000.0D, license);
        BossBarUtils.setBossBarBonnemealValue(TractorData.bonnemeal.get(license) / 1000.0D, license);

        standMain = TractorData.autostand.get("TRACTOR_MAIN_" + license);
        standSkin = TractorData.autostand.get("TRACTOR_SKIN_" + license);
        standMainSeat = TractorData.autostand.get("TRACTOR_MAINSEAT_" + license);
        standFront = TractorData.autostand.get("TRACTOR_FRONT_" + license);
        standBack = TractorData.autostand.get("TRACTOR_BACK_" + license);
        standVolant = TractorData.autostand.get("TRACTOR_VOLANT_" + license);
        standTrailer = TractorData.autostand.get("TRACTOR_TRAILER_" + license);
        schedulerRun(() -> {
            standSkin.teleport(new Location(standMain.getLocation().getWorld(), standMain.getLocation().getX(), standMain.getLocation().getY(), standMain.getLocation().getZ(), standMain.getLocation().getYaw(), standMain.getLocation().getPitch()));

            updateStand();
            blockCheck();
            mainSeat();
            front();
            back();
            volant();
            trailer();
            if (TractorData.seatsize.get(license + "addon") != null) {
                for (int i = 1; i <= TractorData.seatsize.get(license + "addon"); i++) {
                    ArmorStand standAddon = TractorData.autostand.get("TRACTOR_ADDON" + i + "_" + license);
                    standAddon.teleport(standMain.getLocation());
                }
            }
            if (player.isInsideVehicle()) {
               if (Tractor.isSeeder(standTrailer)) {
              		startSeeding(standTrailer);
               } else if (Tractor.isTiller(standTrailer)) {
               		startTilling(standTrailer);
               } else if (Tractor.isFertilizer(standTrailer)) {
            	   startFertilizing(standTrailer);
               }
            }
            rotation();
            move();
        });
    }
    
    protected void front() {
        double xOffset = TractorData.frontx.get("TRACTOR_FRONT_" + license);
        double yOffset = TractorData.fronty.get("TRACTOR_FRONT_" + license);
        double zOffset = TractorData.frontz.get("TRACTOR_FRONT_" + license);
        final Location locvp = standMain.getLocation().clone();
        final Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
        final float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(standFront.getLocation().getYaw())));
        final float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(standFront.getLocation().getYaw())));
        final Location loc = new Location(standMain.getWorld(), xvp, standMain.getLocation().getY() + yOffset, zvp, standMain.getLocation().getYaw(), standFront.getLocation().getPitch());
        schedulerRun(() -> standFront.teleport(loc));
    }
    
    protected void back() {
        double xOffset = TractorData.backx.get("TRACTOR_BACK_" + license);
        double yOffset = TractorData.backy.get("TRACTOR_BACK_" + license);
        double zOffset = TractorData.backz.get("TRACTOR_BACK_" + license);
        final Location locvp = standMain.getLocation().clone();
        final Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
        final float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(standBack.getLocation().getYaw())));
        final float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(standBack.getLocation().getYaw())));
        final Location loc = new Location(standMain.getWorld(), xvp, standMain.getLocation().getY() + yOffset, zvp, standMain.getLocation().getYaw(), standBack.getLocation().getPitch());
        schedulerRun(() -> standBack.teleport(loc));
    }
    
    
    protected void volant() {
        double xOffset = TractorData.volantx.get("TRACTOR_VOLANT_" + license);
        double yOffset = TractorData.volanty.get("TRACTOR_VOLANT_" + license);
        double zOffset = TractorData.volantz.get("TRACTOR_VOLANT_" + license);
        final Location locvp = standMain.getLocation().clone();
        final Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
        final float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(standVolant.getLocation().getYaw())));
        final float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(standVolant.getLocation().getYaw())));
        final Location loc = new Location(standMain.getWorld(), xvp, standMain.getLocation().getY() + yOffset, zvp, standMain.getLocation().getYaw(), standVolant.getLocation().getPitch());
        schedulerRun(() -> standVolant.teleport(loc));
    }
    
    protected void trailer() {
        double xOffset = TractorData.trailerx.get("TRACTOR_TRAILER_" + license);
        double yOffset = TractorData.trailery.get("TRACTOR_TRAILER_" + license);
        double zOffset = TractorData.trailerz.get("TRACTOR_TRAILER_" + license);
        final Location locvp = standMain.getLocation().clone();
        final Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
        final float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(standTrailer.getLocation().getYaw())));
        final float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(standTrailer.getLocation().getYaw())));
        final Location loc = new Location(standMain.getWorld(), xvp, standMain.getLocation().getY() + yOffset, zvp, standMain.getLocation().getYaw(), standTrailer.getLocation().getPitch());
        schedulerRun(() -> standTrailer.teleport(loc));
    }
    
    protected void accelerationAnimation() {
        double angle = (System.currentTimeMillis() % 3600) / 3600.0 * 3600;
        EulerAngle leftArmPoseFront = new EulerAngle(0, Math.toRadians(angle), Math.toRadians(270));
        EulerAngle rightArmPoseFront = new EulerAngle(0, Math.toRadians(-angle), Math.toRadians(90));
        EulerAngle leftArmPoseBack = new EulerAngle(0, Math.toRadians(angle), Math.toRadians(270));
        EulerAngle rightArmPoseBack = new EulerAngle(0, Math.toRadians(-angle), Math.toRadians(90));
        schedulerRun(() -> {
        standFront.setLeftArmPose(leftArmPoseFront);
        standFront.setRightArmPose(rightArmPoseFront);
        standBack.setLeftArmPose(leftArmPoseBack);
        standBack.setRightArmPose(rightArmPoseBack);
        });
    }
    
    protected void breakingAnimation() {
        double angle = (System.currentTimeMillis() % 3600) / 3600.0 * 3600;
        EulerAngle leftArmPoseFront = new EulerAngle(0, Math.toRadians(angle), Math.toRadians(270));
        EulerAngle rightArmPoseFront = new EulerAngle(0, Math.toRadians(-angle), Math.toRadians(90));
        EulerAngle leftArmPoseBack = new EulerAngle(0, Math.toRadians(angle), Math.toRadians(270));
        EulerAngle rightArmPoseBack = new EulerAngle(0, Math.toRadians(-angle), Math.toRadians(90));
        schedulerRun(() -> {
        standFront.setLeftArmPose(leftArmPoseFront);
        standFront.setRightArmPose(rightArmPoseFront);
        standBack.setLeftArmPose(leftArmPoseBack);
        standBack.setRightArmPose(rightArmPoseBack);
        });
    }
    
    protected void volantAnimation(String direction) {
    	if (direction == "right") {
            schedulerRun(() -> {
            	standBack.setRotation(standMain.getLocation().getYaw() - 45, standMain.getLocation().getPitch());
            	standVolant.setHeadPose(new EulerAngle(Math.toRadians(-90), 0, Math.toRadians(90)));
            });
    	}
    	
    	else if (direction == "left") {
            schedulerRun(() -> {
            	standBack.setRotation(standMain.getLocation().getYaw() + 45, standMain.getLocation().getPitch());
            	standVolant.setHeadPose(new EulerAngle(Math.toRadians(-90), 0, Math.toRadians(-90)));
            });
    	}
    	
    	else if (direction == "zero") {
            schedulerRun(() -> {
            	standBack.setRotation(standMain.getLocation().getYaw(), standMain.getLocation().getPitch());
            	standVolant.setHeadPose(new EulerAngle(Math.toRadians(-90), 0, 0));
            });
    	}
    }

    protected void rotation(){
        final int rotationSpeed = 8;
        final int rotation = (TractorData.speed.get(license) < 0.1) ? rotationSpeed / 3 : rotationSpeed;
        if (steerGetXxa() > 0) rotateTractor(standMain.getLocation().getYaw() - rotation);
        else if (steerGetXxa() < 0) rotateTractor(standMain.getLocation().getYaw() + rotation);
        if (steerGetXxa() > 0) volantAnimation("right");
        if (steerGetXxa() < 0) volantAnimation("left");
        if (steerGetXxa() == 0) volantAnimation("zero");
    }

    protected void rotateTractor(float yaw){
        schedulerRun(() -> {
            standMain.setRotation(yaw, standMain.getLocation().getPitch());
            standMainSeat.setRotation(yaw, standMain.getLocation().getPitch());
            standSkin.setRotation(yaw, standMain.getLocation().getPitch());
        });
    }
    
    private void startTilling(ArmorStand armorstand) {
        Location location = armorstand.getLocation();
    	schedulerRun(() -> {
    	    for (Location loc : getLocation(location)) {
    	    	addBlock(loc, Material.FARMLAND);
    	    }
        });
    }
    
    private void startSeeding(ArmorStand armorstand) {
    	Location location = armorstand.getLocation();
    	schedulerRun(() -> {
    	    for (Location loc : getLocation(location)) {
    	    	addSeed(loc, Material.WHEAT);
    	    }
        });
    }
    
    private void startFertilizing(ArmorStand armorstand) {
    	Location location = armorstand.getLocation();
    	schedulerRun(() -> {
    	    for (Location loc : getLocation(location)) {
    	    	addBonnemeal(loc);
    	    }
        });
    }
    
    private List<Location> getLocation(Location armorStandLocation) {
        float yaw = armorStandLocation.getYaw();
        double yawInRadians = Math.toRadians(yaw);
        double offsetX = Math.sin(yawInRadians) * 2;
        double offsetZ = -Math.cos(yawInRadians) * 2;
        Location behindLocation = armorStandLocation.clone().add(offsetX, 0, offsetZ);
        double rightYawInRadians = yawInRadians - Math.PI / 2;
        double offsetXRight = Math.sin(rightYawInRadians) * 2;
        double offsetZRight = -Math.cos(rightYawInRadians) * 2;
        Location rightLocation1 = behindLocation.clone().add(offsetXRight, 0, offsetZRight);

        Location intermediateLocation2 = behindLocation.clone().add(
            (rightLocation1.getX() - behindLocation.getX()) / 2,
            0,
            (rightLocation1.getZ() - behindLocation.getZ()) / 2
        );
        double leftYawInRadians = yawInRadians + Math.PI / 2;
        double offsetXLeft = Math.sin(leftYawInRadians) * 2;
        double offsetZLeft = -Math.cos(leftYawInRadians) * 2;

        Location leftLocation1 = behindLocation.clone().add(offsetXLeft, 0, offsetZLeft);
        Location leftLocation2 = leftLocation1.clone().add(offsetXLeft, 0, offsetZLeft);

        double leftYawFromEmeraldInRadians = yawInRadians - Math.PI / 2;
        double offsetXLeftFromEmerald = Math.sin(leftYawFromEmeraldInRadians) * 2;
        double offsetZLeftFromEmerald = -Math.cos(leftYawFromEmeraldInRadians) * 2;

        Location leftOfEmerald = rightLocation1.clone().add(offsetXLeftFromEmerald, 0, offsetZLeftFromEmerald);

        Location intermediateLeftOfEmerald = leftOfEmerald.clone().add(
            (rightLocation1.getX() - leftOfEmerald.getX()) / 2,
            0,
            (rightLocation1.getZ() - leftOfEmerald.getZ()) / 2
        );

        Location intermediateLeft3 = behindLocation.clone().add(
            (leftLocation1.getX() - behindLocation.getX()) / 2,
            0,
            (leftLocation1.getZ() - behindLocation.getZ()) / 2
        );

        Location intermediateLeft1 = leftLocation1.clone().add(
            (leftLocation2.getX() - leftLocation1.getX()) / 2,
            0,
            (leftLocation2.getZ() - leftLocation1.getZ()) / 2
        );
        List<Location> locations = Arrays.asList(
                behindLocation,
                rightLocation1,
                intermediateLocation2,
                leftLocation1,
                intermediateLeft1,
                intermediateLeft3,
                intermediateLeftOfEmerald
            );
        return locations;
    }
    private void addBlock(Location location, Material material) {
        Block block = location.getBlock();
        if (block.getType() == Material.DIRT | block.getType() == Material.GRASS_BLOCK) {
            block.setType(material);
        }
    }
    
    private void addSeed(Location location, Material seedMaterial) {
        Block block = location.getBlock();
        
        if (block.getType() == Material.FARMLAND) {
            Block aboveBlock = block.getRelative(0, 1, 0);
            if (aboveBlock.getType() != seedMaterial) {
                aboveBlock.setType(seedMaterial);
                putSeedsUsage();
            }
        }
    }
    
    private void addBonnemeal(Location location) {
        Block block = location.getBlock();
        
        if (block.getType() == Material.FARMLAND) {
            Block aboveBlock = block.getRelative(0, 1, 0);
    		Ageable crop = (Ageable)aboveBlock.getBlockData();
            Material material = aboveBlock.getType();
        	if (isCrop(material)) {
        		if (crop.getAge() != crop.getMaximumAge()) {
        			crop.setAge(crop.getMaximumAge());
                	aboveBlock.setBlockData(crop);
                	putBonnemealUsage();
        		}
        	}
        }
    }
    
    public static boolean isCrop(Material material) {
    	if (material == Material.WHEAT|material == Material.CARROT|material == Material.POTATO|material == Material.BEETROOT|material == Material.MELON_STEM|material == Material.PUMPKIN_STEM) return true;
    	return false;
    }
    
    public static boolean isCropItems(Material material) {
    	if (material == Material.WHEAT_SEEDS|material == Material.CARROT|material == Material.POTATO|material == Material.BEETROOT_SEEDS|material == Material.MELON_SEEDS|material == Material.PUMPKIN_SEEDS) return true;
    	return false;
    }

    protected void move(){
        final double maxSpeed = 0.1;
        final double accelerationSpeed = 0.012;
        final double brakingSpeed = 0.01;
        final double maxSpeedBackwards = 0.35;
        final Location locBelow = new Location(standMain.getLocation().getWorld(), standMain.getLocation().getX(), standMain.getLocation().getY() - 0.2, standMain.getLocation().getZ(), standMain.getLocation().getYaw(), standMain.getLocation().getPitch());

        if (steerGetZza() == 0.0 && !locBelow.getBlock().getType().equals(Material.AIR)) {
        	TractorData.speed.put(license, 0.0);
        }
        
        if (steerGetZza() > 0.0) {
        	accelerationAnimation();
            if (TractorData.speed.get(license) < 0) {
            	TractorData.speed.put(license, TractorData.speed.get(license) + brakingSpeed);
                return;
            }
            putFuelUsage();

            if (TractorData.speed.get(license) > maxSpeed - accelerationSpeed) return;
            TractorData.speed.put(license, TractorData.speed.get(license) + accelerationSpeed);
        }
        if (steerGetZza() < 0.0) {
        	breakingAnimation();
            if (TractorData.speed.get(license) > 0) {
            	TractorData.speed.put(license, TractorData.speed.get(license) - brakingSpeed);
                return;
            }
            putFuelUsage();

            if (TractorData.speed.get(license) < -maxSpeedBackwards) return;
            TractorData.speed.put(license, TractorData.speed.get(license) - accelerationSpeed);
        }
    }

    protected boolean blockCheck() {
        final Location loc = getLocationOfBlockAhead();
        final String locY = String.valueOf(standMain.getLocation().getY());
        final Location locBlockAbove = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ(), loc.getYaw(), loc.getPitch());
        final Location locBlockBelow = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ(), loc.getYaw(), loc.getPitch());
        final String drivingOnY = locY.substring(locY.length() - 2);

        final boolean isOnGround = drivingOnY.contains(".0");
        final boolean isOnSlab = drivingOnY.contains(".5");
        final boolean isPassable = isPassable(loc.getBlock());
        final boolean isAbovePassable = isPassable(locBlockAbove.getBlock());

        final double difference = Double.parseDouble("0." + locY.split("\\.")[1]);
        final BlockData blockData = loc.getBlock().getBlockData();
        final BlockData blockDataBelow = locBlockBelow.getBlock().getBlockData();


        if (standMain.getLocation().getBlock().getType().toString().contains("PATH") || standMain.getLocation().getBlock().getType().toString().contains("FARMLAND")){

            if (!isAbovePassable){
            	TractorData.speed.put(license, 0.0);
                return false;
            }

            if (!loc.getBlock().getType().toString().contains("PATH") && !loc.getBlock().getType().toString().contains("FARMLAND")) {
                pushTractorUp(0.0625);
                return true;
            }

            return false;

        }

            if (isOnSlab) {

                if (isPassable) {
                	pushTractorDown(0.5);
                    return false;
                }

                if (blockData instanceof Slab) {
                    Slab slab = (Slab) blockData;
                    if (slab.getType().equals(Slab.Type.BOTTOM)) {
                        return false;
                    }
                }

                if (!isAbovePassable) {
                	TractorData.speed.put(license, 0.0);
                    return false;
                }

                pushTractorUp(0.5);
                return true;
            }

            if (!isPassable) {

                if (!isAbovePassable) {
                	TractorData.speed.put(license, 0.0);
                    return false;
                }

                if (blockData instanceof Slab){
                    Slab slab = (Slab) blockData;
                    if (slab.getType().equals(Slab.Type.BOTTOM)){
                        if (isOnGround) {
                        	pushTractorUp(0.5);
                        } else {
                            if ((0.5 - difference) > 0) pushTractorUp(0.5 - difference);
                        }
                        return true;
                    }
                }

                if (isOnGround) {
                	pushTractorUp(1);
                } else {
                    if ((1 - difference) > 0) pushTractorUp(1 - difference);
                }

                return true;

            }

            if (blockDataBelow instanceof Slab){
                Slab slab = (Slab) blockDataBelow;
                if (slab.getType().equals(Slab.Type.BOTTOM)) {
                	pushTractorDown(0.5);
                    return false;
                }
            }

        return false;
    }

    protected double getLayerHeight(int layers){
        switch (layers){
            case 1:
                return 0.125;
            case 2:
                return 0.25;
            case 3:
                return 0.375;
            case 4:
                return 0.5;
            case 5:
                return 0.625;
            case 6:
                return 0.75;
            case 7:
                return 0.875;
            default:
                return 1;
        }
    }
    protected void mainSeat() {
        double xOffset = TractorData.mainx.get("TRACTOR_MAINSEAT_" + license);
        double yOffset = TractorData.mainy.get("TRACTOR_MAINSEAT_" + license);
        double zOffset = TractorData.mainz.get("TRACTOR_MAINSEAT_" + license);
        Location locvp = standMain.getLocation().clone();
        Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
        float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(fbvp.getYaw())));
        float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(fbvp.getYaw())));
        Location loc = new Location(standMain.getWorld(), xvp, standMain.getLocation().getY() + yOffset, zvp, fbvp.getYaw(), fbvp.getPitch());
        teleportSeat(standMainSeat, loc);
    }
    
    protected void teleportSeat(ArmorStand seat, Location loc){
        teleportSeat(((org.bukkit.craftbukkit.v1_20_R4.entity.CraftEntity) seat).getHandle(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    protected void teleportSeat(Object seat, double x, double y, double z, float yaw, float pitch){
        schedulerRun(() -> {
            try {
                Method method = seat.getClass().getSuperclass().getSuperclass().getDeclaredMethod("a", double.class, double.class, double.class, float.class, float.class);
                method.invoke(seat, x, y, z, yaw, pitch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    protected void updateStand() {
        final Location loc = standMain.getLocation();
        final Location locBlockAhead = getLocationOfBlockAhead();
        final Location locBlockAheadAndBelow = new Location(locBlockAhead.getWorld(), locBlockAhead.getX(), locBlockAhead.getY() - 1, locBlockAhead.getZ(), locBlockAhead.getPitch(), locBlockAhead.getYaw());
        final Location locBelow = new Location(loc.getWorld(), loc.getX(), loc.getY() - 0.2, loc.getZ(), loc.getYaw(), loc.getPitch());

        final Material block = locBelow.getBlock().getType();
        final String blockName = block.toString();

        if (blockName.contains("WATER")) {
            standMain.setVelocity(new Vector(loc.getDirection().multiply(TractorData.speed.get(license)).getX(), -0.8, loc.getDirection().multiply(TractorData.speed.get(license)).getZ()));
            return;
        }

        if (isPassable(locBlockAhead.getBlock()) && isPassable(locBlockAheadAndBelow.getBlock())){
            if (isPassable(locBelow.getBlock())){
                standMain.setVelocity(new Vector(loc.getDirection().multiply(TractorData.speed.get(license)).getX(), -0.8, loc.getDirection().multiply(TractorData.speed.get(license)).getZ()));
                return;
            }

            if (blockName.contains("CARPET")){
                standMain.setVelocity(new Vector(loc.getDirection().multiply(TractorData.speed.get(license)).getX(), -0.7375, loc.getDirection().multiply(TractorData.speed.get(license)).getZ()));
                return;
            }
        }

        standMain.setVelocity(new Vector(loc.getDirection().multiply(TractorData.speed.get(license)).getX(), 0.0, loc.getDirection().multiply(TractorData.speed.get(license)).getZ()));
    }

    protected void putFuelUsage() {
        final double newFuel = TractorData.fuel.get(license) - (1 * 0.01);
        if (newFuel < 0) TractorData.fuel.put(license, 0.0);
        else TractorData.fuel.put(license, newFuel);
    }
    
    protected void putSeedsUsage() {
        final double newSeeds = TractorData.seeds.get(license) - (1 * 0.1);
        if (newSeeds < 0) TractorData.seeds.put(license, 0.0);
        else TractorData.seeds.put(license, newSeeds);
    }
    
    protected void putBonnemealUsage() {
        final double newBonnemeal = TractorData.bonnemeal.get(license) - (1 * 0.1);
        if (newBonnemeal < 0) TractorData.bonnemeal.put(license, 0.0);
        else TractorData.bonnemeal.put(license, newBonnemeal);
    }

    protected boolean isPassable(Block block){
        return block.isPassable();
    }

    protected void pushTractorUp(double plus){
        final Location newLoc = new Location(standMain.getLocation().getWorld(), standMain.getLocation().getX(), standMain.getLocation().getY() + plus, standMain.getLocation().getZ(), standMain.getLocation().getYaw(), standMain.getLocation().getPitch());
        schedulerRun(() -> standMain.teleport(newLoc));
    }

    protected void pushTractorDown(double minus){
    	pushTractorUp(-minus);
    }

    protected Location getLocationOfBlockAhead(){
        double xOffset = 0.7;
        double yOffset = 0.4;
        double zOffset = 0.0;
        Location locvp = standMain.getLocation().clone();
        Location fbvp = locvp.add(locvp.getDirection().setY(0).normalize().multiply(xOffset));
        float zvp = (float) (fbvp.getZ() + zOffset * Math.sin(Math.toRadians(fbvp.getYaw())));
        float xvp = (float) (fbvp.getX() + zOffset * Math.cos(Math.toRadians(fbvp.getYaw())));
        return new Location(standMain.getWorld(), xvp, standMain.getLocation().getY() + yOffset, zvp, fbvp.getYaw(), fbvp.getPitch());
    }

    protected boolean steerIsJumping(){
        boolean isJumping = false;
        try {
            Method method = packet.getClass().getDeclaredMethod("f");
            isJumping = (Boolean) method.invoke(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isJumping;
    }

    protected float steerGetXxa(){
        float Xxa = 0;
        try {

            Method method = packet.getClass().getDeclaredMethod("b");
            Xxa = (float) method.invoke(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Xxa;
    }

    protected float steerGetZza(){
        float Zza = 0;
        try {
            Method method = packet.getClass().getDeclaredMethod("e");
            Zza = (float) method.invoke(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Zza;
    }
}

// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops.data;

import static com.gmail.zariust.common.Verbosity.EXTREME;

import org.bukkit.Art;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;
import org.bukkit.material.Button;
import org.bukkit.material.Cake;
import org.bukkit.material.DetectorRail;
import org.bukkit.material.Diode;
import org.bukkit.material.Door;
import org.bukkit.material.Ladder;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.PoweredRail;
import org.bukkit.material.Pumpkin;
import org.bukkit.material.Rails;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.material.Sign;
import org.bukkit.material.Stairs;
import org.bukkit.material.Torch;
import org.bukkit.material.TrapDoor;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;

public class SimpleData implements Data, RangeableData {
    private int data;

    public SimpleData(byte d) {
        data = d;
    }

    public SimpleData(int d) {
        data = d;
    }

    public SimpleData() {
        this(0);
    }

    @SuppressWarnings("deprecation")
	public SimpleData(Painting painting) {
        Art art = painting.getArt();
        data = art.getId() << 4;
        switch (painting.getFacing()) {
        case WEST:
            data |= 1;
        case SOUTH:
            data |= 2;
        case EAST:
            data |= 3;
        default:
            break;
        }
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public void setData(int d) {
        data = d;
    }

    @Override
    public boolean matches(Data d) {
        return data == d.getData();
    }

    @SuppressWarnings("deprecation")
	@Override
    public void setOn(BlockState state) {
        MaterialData mat = new MaterialData(state.getType(), (byte) data);
        state.setData(mat);
    }

    @Override
    public void setOn(Entity entity, Player witness) {
        if (!(entity instanceof Painting)) {
            Log.logWarning("Tried to change a painting, but no painting was found!");
            return;
        }
        Painting painting = (Painting) entity;
        @SuppressWarnings("deprecation")
		Art art = Art.getById(data >> 4);
        painting.setArt(art);
        switch (data & 7) {
        case 0:
            painting.setFacingDirection(BlockFace.NORTH);
        case 1:
            painting.setFacingDirection(BlockFace.WEST);
        case 2:
            painting.setFacingDirection(BlockFace.SOUTH);
        case 3:
            painting.setFacingDirection(BlockFace.EAST);
        }
    }

    @Override
    public String get(Enum<?> mat) {
        if (mat instanceof Material)
            return get((Material) mat);
        return "";
    }

    @SuppressWarnings({ "incomplete-switch", "deprecation" })
    private String get(Material mat) {
        if (mat == null) {
            Log.logWarning(
                    "SimpleData.get() - material is null, this shouldn't happen...",
                    Verbosity.NORMAL);
            return "";
        }
        String result = "";
        try {
            switch (mat) {
            // Simple enum-based blocks
            case CROPS:
            case CARROT:
            case POTATO:
                return CropState.getByData((byte) data).toString();
                // Blocks whose only attribute is direction
            case LADDER:
                Ladder ladder = new Ladder(mat, (byte) data);
                return ladder.getFacing().toString();
            case PUMPKIN:
            case JACK_O_LANTERN:
                Pumpkin pumpkin = new Pumpkin(mat, (byte) data);
                return pumpkin.getFacing().toString();
            case SIGN_POST:
            case WALL_SIGN:
                Sign sign = new Sign(mat, (byte) data);
                return sign.getFacing().toString();
            case WOOD_STAIRS:
            case COBBLESTONE_STAIRS:
                Stairs stairs = new Stairs(mat, (byte) data);
                return stairs.getFacing().toString();
            case TORCH:
                Torch torch = new Torch(mat, (byte) data);
                return torch.getFacing().toString();
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
                RedstoneTorch invert = new RedstoneTorch(mat, (byte) data);
                result += invert.getFacing();
                break;
            // Powerable blocks
            case LEVER:
                Lever lever = new Lever(mat, (byte) data);
                if (lever.isPowered())
                    result += "POWERED/";
                result += lever.getFacing();
                break;
            case STONE_BUTTON:
                Button button = new Button(mat, (byte) data);
                if (button.isPowered())
                    result += "POWERED/";
                result += button.getFacing();
                break;
            // Pistons (overlaps with previous)
            case PISTON_BASE:
            case PISTON_STICKY_BASE:
                PistonBaseMaterial piston = new PistonBaseMaterial(mat,
                        (byte) data);
                if (piston.isPowered())
                    result += "POWERED/";
                result += piston.getFacing();
                break;
            case PISTON_EXTENSION:
                PistonExtensionMaterial pistonHead = new PistonExtensionMaterial(
                        mat, (byte) data);
                if (pistonHead.isSticky())
                    result += "STICKY/";
                result += pistonHead.getFacing();
                break;
            // Rails
            case RAILS:
                Rails rail = new Rails(mat, (byte) data);
                if (rail.isOnSlope())
                    result += "SLOPE/";
                result += rail.getDirection();
                break;
            case POWERED_RAIL:
                PoweredRail booster = new PoweredRail(mat, (byte) data);
                if (booster.isPowered())
                    result += "POWERED/";
                if (booster.isOnSlope())
                    result += "SLOPE/";
                result += booster.getDirection();
                break;
            // Pressable blocks (overlaps with previous)
            case DETECTOR_RAIL:
                DetectorRail detector = new DetectorRail(mat, (byte) data);
                if (detector.isPressed())
                    result += "PRESSED/";
                if (detector.isOnSlope())
                    result += "SLOPE/";
                result += detector.getDirection();
                break;
            // Misc
            case BED_BLOCK:
                Bed bed = new Bed(mat, (byte) data);
                if (bed.isHeadOfBed())
                    result += "HEAD/";
                result += bed.getFacing();
                break;
            case CAKE_BLOCK:
                Cake cake = new Cake(mat, (byte) data);
                result += "EATEN-";
                result += cake.getSlicesEaten();
                break;
            case DIODE_BLOCK_OFF:
            case DIODE_BLOCK_ON:
                Diode diode = new Diode(mat, (byte) data);
                result += diode.getDelay();
                result += "/";
                result += diode.getFacing();
                break;
            case TRAP_DOOR:
                TrapDoor hatch = new TrapDoor(mat, (byte) data);
                if (hatch.isOpen())
                    result += "OPEN/";
                result += hatch.getFacing();
                break;
            case WOODEN_DOOR:
            case IRON_DOOR_BLOCK:
                Door door = new Door(mat, (byte) data);
                if (door.isTopHalf())
                    result += "TOP/";
                if (door.isOpen())
                    result += "OPEN/";
                result += door.getFacing();
                break;
            case MONSTER_EGGS:
                switch (data) {
                case 0:
                    result = "STONE";
                    break;
                case 1:
                    result = "COBBLESTONE";
                    break;
                case 2:
                    result = "SMOOTH_BRICK";
                    break;
                }
                break;
            // Paintings
            case PAINTING:
                Art art = Art.getById(data >> 4);
                result = art.toString();
                switch (data & 7) {
                case 0:
                    result += "/NORTH";
                    break;
                case 1:
                    result += "/WEST";
                    break;
                case 2:
                    result += "/SOUTH";
                    break;
                case 3:
                    result += "/EAST";
                    break;
                }
                break;
            }
        } catch (NullPointerException ex) {
            Log.logWarning(
                    "SimpleData.get - nullpointer exception for material: "
                            + mat.toString(), Verbosity.HIGH);
            // TODO: stacktrace on extreme only? if
            // (OtherDrops.plugin.config.verbosity.exceeds(EXTREME)) {};
        }
        if (result.isEmpty())
            return CommonMaterial.getBlockOrItemData(mat, data);
        return result;
    }

    @SuppressWarnings("deprecation")
	public static Data parse(Material mat, String state)
            throws IllegalArgumentException {
        if (state == null || state.isEmpty())
            return null;
        if (state.matches("[0-9][0-9~.-]+[0-9]") || state.startsWith("RANGE"))
            return RangeData.parse(state);
        state = state.toUpperCase();
        int ret = -1;
        switch (mat) {
        case LOG:
        case LEAVES:
        case SAPLING:
        case WOOL:
        case DOUBLE_STEP:
        case STEP:
        case LONG_GRASS:
        case SMOOTH_BRICK:
        case SKULL:
        case WOOD_STEP:
        case WOOD_DOUBLE_STEP:
        case SANDSTONE:
        case COBBLE_WALL:
            Integer data = CommonMaterial.parseBlockOrItemData(mat, state);
            if (data != null)
                ret = data;
            break;
        case CROPS:
        case CARROT:
        case POTATO:
            CropState crops = CropState.valueOf(state);
            if (crops != null)
                ret = crops.getData();
            break;
        // Blocks whose only attribute is direction
        case LADDER:
            Ladder ladder = new Ladder(mat);
            ladder.setFacingDirection(BlockFace.valueOf(state));
            ret = ladder.getData();
            break;
        case PUMPKIN:
        case JACK_O_LANTERN:
            Pumpkin pumpkin = new Pumpkin(mat);
            pumpkin.setFacingDirection(BlockFace.valueOf(state));
            ret = pumpkin.getData();
            break;
        case SIGN_POST: // TODO: Should we allow sign text matching?
        case WALL_SIGN:
            Sign sign = new Sign(mat);
            sign.setFacingDirection(BlockFace.valueOf(state));
            ret = sign.getData();
            break;
        case WOOD_STAIRS:
        case COBBLESTONE_STAIRS:
            Stairs stairs = new Stairs(mat);
            stairs.setFacingDirection(BlockFace.valueOf(state));
            ret = stairs.getData();
            break;
        case TORCH:
            Torch torch = new Torch(mat);
            torch.setFacingDirection(BlockFace.valueOf(state));
            ret = torch.getData();
            break;
        case REDSTONE_TORCH_OFF:
        case REDSTONE_TORCH_ON:
            RedstoneTorch invert = new RedstoneTorch(mat);
            invert.setFacingDirection(BlockFace.valueOf(state));
            ret = invert.getData();
            break;
        // Powerable blocks
        case LEVER:
            Lever lever = new Lever(mat);
            for (String arg : state.split("/")) {
                if (arg.equals("POWERED"))
                    lever.setPowered(true);
                else
                    lever.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = lever.getData();
            break;
        case STONE_BUTTON:
            Button button = new Button(mat);
            for (String arg : state.split("/")) {
                if (arg.equals("POWERED"))
                    button.setPowered(true);
                else
                    button.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = button.getData();
            break;
        // Pistons (overlaps with previous)
        case PISTON_BASE:
        case PISTON_STICKY_BASE:
            PistonBaseMaterial piston = new PistonBaseMaterial(mat);
            for (String arg : state.split("/")) {
                if (arg.equals("POWERED"))
                    piston.setPowered(true);
                else
                    piston.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = piston.getData();
            break;
        case PISTON_EXTENSION:
            PistonExtensionMaterial pistonHead = new PistonExtensionMaterial(
                    mat);
            for (String arg : state.split("/")) {
                if (arg.equals("STICKY"))
                    pistonHead.setSticky(true);
                else
                    pistonHead.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = pistonHead.getData();
            break;
        // Rails
        case RAILS:
            Rails rail = new Rails(mat);
            {
                boolean slope = false;
                BlockFace face = BlockFace.NORTH;
                for (String arg : state.split("/")) {
                    if (arg.equals("SLOPE"))
                        slope = true;
                    else
                        face = BlockFace.valueOf(arg);
                }
                rail.setDirection(face, slope);
            }
            ret = rail.getData();
            break;
        case POWERED_RAIL:
            PoweredRail booster = new PoweredRail(mat);
            {
                boolean slope = false;
                BlockFace face = BlockFace.NORTH;
                for (String arg : state.split("/")) {
                    if (arg.equals("SLOPE"))
                        slope = true;
                    else if (arg.equals("POWERED"))
                        booster.setPowered(true);
                    else
                        face = BlockFace.valueOf(arg);
                }
                booster.setDirection(face, slope);
            }
            ret = booster.getData();
            break;
        // Pressable blocks (overlaps with previous)
        case DETECTOR_RAIL:
            DetectorRail detector = new DetectorRail(mat);
            {
                boolean slope = false;
                BlockFace face = BlockFace.NORTH;
                for (String arg : state.split("/")) {
                    if (arg.equals("SLOPE"))
                        slope = true;
                    else if (arg.equals("PRESSED"))
                        detector.setPressed(true);
                    else
                        face = BlockFace.valueOf(arg);
                }
                detector.setDirection(face, slope);
            }
            ret = detector.getData();
            break;
        // Misc
        case BED_BLOCK:
            Bed bed = new Bed(mat);
            for (String arg : state.split("/")) {
                if (arg.equals("HEAD"))
                    bed.setHeadOfBed(true);
                else
                    bed.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = bed.getData();
            break;
        case CAKE_BLOCK:
            Cake cake = new Cake(mat);
            if (state.startsWith("EATEN-"))
                cake.setSlicesEaten(Integer.parseInt(state.substring(6)));
            else if (state.startsWith("LEFT-"))
                cake.setSlicesRemaining(Integer.parseInt(state.substring(5)));
            else if (state.equals("FULL"))
                cake.setSlicesEaten(0);
            ret = cake.getData();
            break;
        case DIODE_BLOCK_OFF:
        case DIODE_BLOCK_ON:
            Diode diode = new Diode(mat);
            for (String arg : state.split("/")) {
                if (arg.matches("[1-4]"))
                    diode.setDelay(Integer.parseInt(arg));
                else
                    diode.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = diode.getData();
            break;
        case TRAP_DOOR:
            TrapDoor hatch = new TrapDoor(mat);
            for (String arg : state.split("/")) {
                // TODO: Should use a setOpen method, but there isn't one...
                if (arg.equals("OPEN"))
                    hatch.setData((byte) (hatch.getData() | 0x4));
                else
                    hatch.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = hatch.getData();
            break;
        case WOODEN_DOOR:
        case IRON_DOOR_BLOCK:
            Door door = new Door(mat);
            for (String arg : state.split("/")) {
                if (arg.equals("OPEN"))
                    door.setOpen(true);
                else if (arg.equals("TOP"))
                    door.setTopHalf(true);
                else
                    door.setFacingDirection(BlockFace.valueOf(arg));
            }
            ret = door.getData();
            break;
        case MONSTER_EGGS:
            Material step = CommonMaterial.matchMaterial(state);
            if (step == null)
                throw new IllegalArgumentException("Unknown material " + state);
            switch (step) {
            case STONE:
                ret = 0;
                break;
            case COBBLESTONE:
                ret = 1;
                break;
            case SMOOTH_BRICK:
                ret = 2;
                break;
            default:
                throw new IllegalArgumentException(
                        "Illegal monster egg material " + state);
            }
            break;
        // Tile entities
        case FURNACE:
        case BURNING_FURNACE:
        case DISPENSER:
        case CHEST:
            return ContainerData.parse(mat, state);
        case MOB_SPAWNER:
            return SpawnerData.parse(state);
        case NOTE_BLOCK:
            return NoteData.parse(state);
        case JUKEBOX:
            return RecordData.parse(state);
            // Paintings and vehicles
        case PAINTING:
            BlockFace facing = null;
            Art painting = null;
            for (String arg : state.split("/")) {
                Art tmp = Art.getByName(arg);
                if (tmp == null) {
                    facing = BlockFace.valueOf(arg);
                } else
                    painting = tmp;
            }
            ret = painting == null ? 0 : painting.getId() << 4;
            if (facing == null)
                ret |= 4;
            else
                switch (facing) {
                case NORTH:
                    break;
                case SOUTH:
                    ret |= 2;
                    break;
                case EAST:
                    ret |= 3;
                    break;
                case WEST:
                    ret |= 1;
                    break;
                default:
                    throw new IllegalArgumentException("Paintings cannot face "
                            + facing);
                }
            break;
        case BOAT:
        case MINECART:
        case POWERED_MINECART:
            return VehicleData.parse(mat, state);
        case STORAGE_MINECART:
            return ContainerData.parse(mat, state);
        default:
            if (!state.isEmpty())
                throw new IllegalArgumentException("Illegal data for " + mat
                        + ": " + state);
        }
        return new SimpleData((byte) ret);
    }

    @Override
    public String toString() {
        // TODO: Should probably make sure this is not used, and always use the
        // get method instead
        Log.logWarning("SimpleData.toString() was called! Is this right?",
                EXTREME);
        Log.stackTrace();
        return String.valueOf(data);
    }

    @Override
    public int hashCode() {
        return data;
    }

    @Override
    public Boolean getSheared() {
        // TODO Auto-generated method stub
        return null;
    }
}

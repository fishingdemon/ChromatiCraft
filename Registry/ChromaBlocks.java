/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.Base.BlockModelledChromaTile;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Block.BlockCaveCrystal;
import Reika.ChromatiCraft.Block.BlockChromaPlantTile;
import Reika.ChromatiCraft.Block.BlockCrystalHive;
import Reika.ChromatiCraft.Block.BlockCrystalLamp;
import Reika.ChromatiCraft.Block.BlockCrystalPlant;
import Reika.ChromatiCraft.Block.BlockCrystalPylon;
import Reika.ChromatiCraft.Block.BlockCrystalRune;
import Reika.ChromatiCraft.Block.BlockCrystalTile;
import Reika.ChromatiCraft.Block.BlockCrystalTileNonCube;
import Reika.ChromatiCraft.Block.BlockLiquidChroma;
import Reika.ChromatiCraft.Block.BlockLiquidEnder;
import Reika.ChromatiCraft.Block.BlockPylonStructure;
import Reika.ChromatiCraft.Block.BlockRift;
import Reika.ChromatiCraft.Block.BlockSuperCrystal;
import Reika.ChromatiCraft.Block.Dye.BlockDye;
import Reika.ChromatiCraft.Block.Dye.BlockDyeFlower;
import Reika.ChromatiCraft.Block.Dye.BlockDyeGrass;
import Reika.ChromatiCraft.Block.Dye.BlockDyeLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowLeaf;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowSapling;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockChromaFlower;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystal;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalHive;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockCrystalPlant;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeColors;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockDyeTypes;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockMultiType;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockRainbowLeaf;
import Reika.ChromatiCraft.Items.ItemBlock.ItemBlockRainbowSapling;
import Reika.DragonAPI.Base.BlockCustomLeaf;
import Reika.DragonAPI.Interfaces.BlockEnum;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;

public enum ChromaBlocks implements BlockEnum {

	TILEPLANT(BlockChromaPlantTile.class, 		ItemBlockChromaFlower.class, 	"Chromatic Plant"),
	TILEENTITY(BlockChromaTile.class, 											"Chromatic Tile"),
	TILEMODELLED(BlockModelledChromaTile.class, 								"Modelled Chromatic Tile"),
	RUNE(BlockCrystalRune.class, 				ItemBlockDyeColors.class, 		"block.crystalrune"),
	CHROMA(BlockLiquidChroma.class, 			ChromatiCraft.chroma, 			"Liquid Chroma"),
	RIFT(BlockRift.class, 														"Rift"),
	CRYSTAL(BlockCaveCrystal.class, 			ItemBlockCrystal.class, 		"crystal.cave"), //Cave Crystal
	LAMP(BlockCrystalLamp.class, 				ItemBlockCrystal.class, 		"crystal.lamp"),
	SUPER(BlockSuperCrystal.class, 				ItemBlockCrystal.class, 		"crystal.super"),
	PLANT(BlockCrystalPlant.class, 				ItemBlockCrystalPlant.class, 	"crystal.plant"),
	HIVE(BlockCrystalHive.class, 				ItemBlockCrystalHive.class, 	"block.crystalhive"),
	TILECRYSTAL(BlockCrystalTile.class,											"Crystal Tile"),
	TILECRYSTALNONCUBE(BlockCrystalTileNonCube.class,							"Crystal Tile Non-Cube"),
	DECAY(BlockDyeLeaf.class, 					ItemBlockDyeTypes.class, 		"dye.leaf"),
	DYELEAF(BlockDyeLeaf.class, 				ItemBlockDyeTypes.class, 		"dye.leaf"),
	DYESAPLING(BlockDyeSapling.class, 			ItemBlockDyeTypes.class, 		"dye.sapling"),
	DYE(BlockDye.class, 						ItemBlockDyeTypes.class, 		"dye.block"),
	RAINBOWLEAF(BlockRainbowLeaf.class, 		ItemBlockRainbowLeaf.class, 	"rainbow.leaf"),
	RAINBOWSAPLING(BlockRainbowSapling.class, 	ItemBlockRainbowSapling.class, 	"rainbow.sapling"),
	DYEFLOWER(BlockDyeFlower.class, 			ItemBlockDyeTypes.class, 		"dye.flower"),
	ENDER(BlockLiquidEnder.class, 				ChromatiCraft.ender,			"Liquid Ender"),
	DYEGRASS(BlockDyeGrass.class,				ItemBlockDyeTypes.class,		"dye.grass"),
	PYLONSTRUCT(BlockPylonStructure.class,		ItemBlockMultiType.class,		"block.pylon"),
	PYLON(BlockCrystalPylon.class,				ItemBlockMultiType.class,		"crystal.pylon");

	private Class blockClass;
	private String blockName;
	private Class itemBlock;
	private Fluid fluid;

	public static final ChromaBlocks[] blockList = values();
	private static final HashMap<Block, ChromaBlocks> blockMap = new HashMap();

	private ChromaBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, Fluid f, String n) {
		blockClass = cl;
		blockName = n;
		itemBlock = ib;
		fluid = f;
	}

	private ChromaBlocks(Class <? extends Block> cl, Fluid f, String n) {
		this(cl, null, f, n);
	}

	private ChromaBlocks(Class <? extends Block> cl, Class<? extends ItemBlock> ib, String n) {
		this(cl, ib, null, n);
	}

	private ChromaBlocks(Class <? extends Block> cl, String n) {
		this(cl, null, null, n);
	}

	public Material getBlockMaterial() {
		if (this.isCrystal())
			return Material.glass;
		switch(this) {
		case TILEPLANT:
			return Material.plants;
		case CHROMA:
			return Material.water;
		case TILECRYSTAL:
			return Material.glass;
		default:
			return Material.rock;
		}
	}

	public boolean isFluid() {
		return fluid != null;
	}

	public Fluid getFluid() {
		return fluid;
	}

	public boolean isDye() {
		switch(this) {
		case DYE:
		case DYELEAF:
		case DECAY:
		case DYESAPLING:
		case DYEFLOWER:
		case DYEGRASS:
			return true;
		default:
			return false;
		}
	}

	public boolean isLeaf() {
		return BlockCustomLeaf.class.isAssignableFrom(blockClass);
	}

	public boolean isDyePlant() {
		return this == DYESAPLING || this == DYEGRASS || this == DYEFLOWER;
	}

	public boolean isSapling() {
		return BlockSapling.class.isAssignableFrom(blockClass);
	}

	@Override
	public Class[] getConstructorParamTypes() {
		if (this.isFluid())
			return new Class[]{Fluid.class, Material.class};
		if (this.isLeaf() || this.isDyePlant() || this.isSapling())
			return new Class[0];
		return new Class[]{Material.class};
	}

	@Override
	public Object[] getConstructorParams() {
		if (this.isFluid())
			return new Object[]{this.getFluid(), this.getBlockMaterial()};
		if (this.isLeaf() || this.isDyePlant() || this.isSapling())
			return new Object[0];
		return new Object[]{this.getBlockMaterial()};
	}

	@Override
	public String getUnlocalizedName() {
		return ReikaStringParser.stripSpaces(blockName);
	}

	@Override
	public Class getObjectClass() {
		return blockClass;
	}

	@Override
	public String getBasicName() {
		return StatCollector.translateToLocal(blockName);
	}

	@Override
	public String getMultiValuedName(int meta) {
		if (this.isCrystal() || this.isDye())
			return ReikaDyeHelper.dyes[meta].colorName+" "+this.getBasicName();
		switch(this) {
		case RUNE:
		case PLANT: //"Crystal Bloom"
			return ReikaDyeHelper.dyes[meta].colorName+" "+this.getBasicName();
		case HIVE:
			return meta == 0 ? "Crystal Hive" : "Pure Hive";
		case PYLON:
			return this.getBasicName();
		case PYLONSTRUCT:
			return StatCollector.translateToLocal("chromablock.pylon."+meta);
		default:
			return "";
		}
	}

	@Override
	public boolean hasMultiValuedName() {
		return true;
	}

	public boolean isCrystal() {
		return CrystalBlock.class.isAssignableFrom(blockClass);
	}

	@Override
	public int getNumberMetadatas() {
		if (this.isCrystal() || this.isDye())
			return ReikaDyeHelper.dyes.length;
		switch(this) {
		case RUNE:
		case PLANT:
			return 16;
		case HIVE:
			return 2;
		case PYLON:
			return 2;
		case PYLONSTRUCT:
			return 10;
		default:
			return 1;
		}
	}

	@Override
	public Class<? extends ItemBlock> getItemBlock() {
		return itemBlock;
	}

	@Override
	public boolean hasItemBlock() {
		return itemBlock != null;
	}

	public boolean isDummiedOut() {
		return blockClass == null;
	}

	public Block getBlockInstance() {
		return ChromatiCraft.blocks[this.ordinal()];
	}

	public static ChromaBlocks getEntryByID(Block id) {
		return blockMap.get(id);
	}

	public Item getItem() {
		return Item.getItemFromBlock(this.getBlockInstance());
	}

	public static void loadMappings() {
		for (int i = 0; i < blockList.length; i++) {
			blockMap.put(blockList[i].getBlockInstance(), blockList[i]);
		}
	}

	public boolean match(ItemStack is) {
		return is.getItem() == Item.getItemFromBlock(this.getBlockInstance());
	}

}
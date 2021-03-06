/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.awt.Color;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLiquidChroma extends BlockFluidClassic {

	public IIcon[] theIcon = new IIcon[2];

	public BlockLiquidChroma(Fluid fluid, Material material) {
		super(fluid, material);
		this.setCreativeTab(null);

		this.setHardness(100F);
		this.setLightOpacity(0);
		this.setResistance(500);
		renderPass = 1;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		renderPass = 1;
		this.setLightLevel(1);
		return s <= 1 ? theIcon[0] : theIcon[1];
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		String icon = this.getIcon();
		theIcon = new IIcon[]{ico.registerIcon("chromaticraft:fluid/"+icon), ico.registerIcon("chromaticraft:fluid/"+icon+"_flowing")};
	}

	protected String getIcon() {
		return "chroma";
	}

	@Override
	public Fluid getFluid() {
		return FluidRegistry.getFluid("chroma");
	}

	@Override
	public FluidStack drain(World world, int x, int y, int z, boolean doDrain) {
		world.setBlockToAir(x, y, z);
		return new FluidStack(FluidRegistry.getFluid("chroma"), 1000);
	}

	@Override
	public boolean canDrain(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		Color c = ReikaDyeHelper.getRandomColor().getJavaColor().brighter().brighter().brighter().brighter();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, c.getRed()/255F, c.getGreen()/255F, c.getBlue()/255F, 2);
		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode");
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		super.onEntityCollidedWithBlock(world, x, y, z, e);
		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			ProgressStage.CHROMA.stepPlayerTo(ep);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, y, dz);
			if (b == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
				((BlockPylonStructure)b).triggerAddCheck(world, dx, y, dz);
			}
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldm) {

		super.breakBlock(world, x, y, z, old, oldm);

		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, y, dz);
			if (b == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
				((BlockPylonStructure)b).triggerBreakCheck(world, dx, y, dz);
			}
		}
	}

}

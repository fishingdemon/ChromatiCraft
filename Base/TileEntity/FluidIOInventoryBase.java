/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import Reika.DragonAPI.Interfaces.InertIInv;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class FluidIOInventoryBase extends FluidIOChromaticBase implements ISidedInventory {

	protected ItemStack[] inv = new ItemStack[this.getSizeInventory()];

	public final ItemStack getStackInSlot(int par1) {
		return inv[par1];
	}

	public final void setInventorySlotContents(int par1, ItemStack is) {
		inv[par1] = is;
	}

	public int getInventoryStackLimit()
	{
		return 64;
	}

	public void openInventory() {}

	public void closeInventory() {}

	@Override
	public final boolean hasCustomInventoryName() {
		return true;
	}

	public final String getInventoryName() {
		return this.getTEName();
	}

	@Override
	public final void markDirty() {
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);

		if (this.getBlockType() != Blocks.air)
		{
			worldObj.func_147453_f(xCoord, yCoord, zCoord, this.getBlockType());
		}
	}

	public abstract boolean isItemValidForSlot(int slot, ItemStack is);

	public final ItemStack decrStackSize(int par1, int par2) {
		return ReikaInventoryHelper.decrStackSize(this, par1, par2);
	}

	public final ItemStack getStackInSlotOnClosing(int par1) {
		return ReikaInventoryHelper.getStackInSlotOnClosing(this, par1);
	}

	public int[] getAccessibleSlotsFromSide(int var1) {
		if (this instanceof InertIInv)
			return new int[0];
		return ReikaInventoryHelper.getWholeInventoryForISided(this);
	}

	public boolean canInsertItem(int i, ItemStack is, int side) {
		if (this instanceof InertIInv)
			return false;
		return ((IInventory)this).isItemValidForSlot(i, is);
	}

	public boolean isUseableByPlayer(EntityPlayer var1) {
		return this.isPlayerAccessible(var1);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte)i);
				inv[i].writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}

		NBT.setTag("Items", nbttaglist);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);

		NBTTagList nbttaglist = NBT.getTagList("Items", NBT.getId());
		inv = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound.getByte("Slot");

			if (byte0 >= 0 && byte0 < inv.length) {
				inv[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound);
			}
		}
	}

}
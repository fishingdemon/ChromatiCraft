/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedCrystalReceiver;
import Reika.ChromatiCraft.Items.ItemStorageCrystal;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Base.OneSlotMachine;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityCrystalCharger extends InventoriedCrystalReceiver implements OneSlotMachine {

	private float angle;

	public static final int CAPACITY = 120000;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (!world.isRemote && this.getCooldown() == 0 && checkTimer.checkCap()) {
			this.checkAndRequest();
		}

		if (this.hasItem()) {
			for (CrystalElement e : energy.elementSet()) {
				int max = this.getMaxTransfer(e);
				int amt = this.getEnergy(e);
				ItemStorageCrystal cry = this.item();
				int put = Math.min(max, Math.min(amt, cry.getSpace(e, inv[0])));
				if (put > 0) {
					cry.addEnergy(inv[0], e, put);
					this.drainEnergy(e, put);
				}
			}
		}
	}

	public float getAngle() {
		return angle;
	}

	private ItemStorageCrystal item() {
		return ((ItemStorageCrystal)inv[0].getItem());
	}

	private int getMaxTransfer(CrystalElement e) {
		int max = 10+(int)Math.sqrt(this.getEnergy(e));
		return this.hasSpeedUpgrade() ? 8*max : max;
	}

	public boolean hasSpeedUpgrade() {
		return ReikaItemHelper.matchStacks(inv[1], ChromaStacks.speedUpgrade);
	}

	private void checkAndRequest() {
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			int capacity = this.getMaxStorage(e);
			int space = capacity-this.getEnergy(e);
			if (space > 0) {
				this.requestEnergy(e, space);
			}
		}
	}

	@Override
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public int getReceiveRange() {
		return 20;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e != null;
	}

	@Override
	public int maxThroughput() {
		return 250;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		switch(slot) {
		case 0:
			return ChromaItems.STORAGE.matchWith(is);
		case 1:
			return ReikaItemHelper.matchStacks(is, ChromaStacks.speedUpgrade);
		default:
			return false;
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return slot == 0 && ChromaItems.STORAGE.matchWith(is) && this.item().isFull(is);
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.CHARGER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {
		if (world == null) {
			angle = 0;
			return;
		}
		int energy = this.energy.getTotalEnergy();
		if (this.hasItem()) {
			ElementTagCompound tag = this.item().getStoredTags(inv[0]);
			energy += tag.getTotalEnergy();
		}
		if (energy > 0) {
			angle += ReikaMathLibrary.logbase(energy, 2);
			if (angle >= 180) {
				//ReikaSoundHelper.playSound(ChromaSounds., x+0.5, y+0.5, z+0.5, 1, 1);
				angle -= 180;
			}
		}
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return CAPACITY;
	}

	public boolean hasItem() {
		return inv[0] != null && ChromaItems.STORAGE.matchWith(inv[0]);
	}

}

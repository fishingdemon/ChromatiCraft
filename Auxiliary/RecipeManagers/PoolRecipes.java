/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayList;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class PoolRecipes {

	public static final PoolRecipes instance = new PoolRecipes();

	private final ItemHashMap<Collection<PoolRecipe>> recipes = new ItemHashMap().setOneWay();

	private PoolRecipes() {

		this.addRecipe(ChromaStacks.magicIngot, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.chromaDust, 16));
		this.addRecipe(ChromaStacks.magicIngot2, new ItemStack(Items.gold_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.firaxite, 16), new ItemStack(Items.blaze_powder, 8), new ItemStack(Items.coal, 2, 0));
		this.addRecipe(ChromaStacks.magicIngot3, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.enderDust, 16), new ItemStack(Items.ender_pearl, 4, 0));
		this.addRecipe(ChromaStacks.magicIngot4, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.waterDust, 16), new ItemStack(Items.gold_ingot, 2, 0));
		this.addRecipe(ChromaStacks.magicIngot5, new ItemStack(Items.gold_ingot), new ItemStack(Items.redstone, 8, 0), ReikaItemHelper.getSizedItemStack(ChromaStacks.beaconDust, 16));
		this.addRecipe(ChromaStacks.magicIngot6, new ItemStack(Items.iron_ingot), ReikaItemHelper.getSizedItemStack(ChromaStacks.auraDust, 8), new ItemStack(Items.glowstone_dust, 8, 0), new ItemStack(Items.redstone, 16, 0), new ItemStack(Items.quartz, 4, 0));

	}

	private void addRecipe(ItemStack out, ItemStack main, ItemStack... ingredients) {
		Collection<PoolRecipe> c = recipes.get(main);
		if (c == null) {
			c = new OneWayList();
			recipes.put(main, c);
		}
		c.add(new PoolRecipe(out, main, ingredients));
	}

	public PoolRecipe getPoolRecipe(EntityItem ei) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		if (ei.worldObj.getBlock(x, y, z) == ChromaBlocks.CHROMA.getBlockInstance() && ei.worldObj.getBlockMetadata(x, y, z) == 0) {
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
			Collection<EntityItem> li = ei.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
			Collection<PoolRecipe> prs = recipes.get(ei.getEntityItem());
			if (prs != null) {
				for (PoolRecipe pr : prs) {
					if (pr.canBeMadeFrom(li)) {
						return pr;
					}
				}
			}
		}
		return null;
	}

	public void makePoolRecipe(EntityItem ei, PoolRecipe pr) {
		int x = MathHelper.floor_double(ei.posX);
		int y = MathHelper.floor_double(ei.posY);
		int z = MathHelper.floor_double(ei.posZ);
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		Collection<EntityItem> li = ei.worldObj.getEntitiesWithinAABB(EntityItem.class, box);
		pr.makeFrom(li);
		ReikaEntityHelper.decrEntityItemStack(ei, 1);
		EntityItem newitem = ReikaItemHelper.dropItem(ei, pr.getOutput());
		newitem.lifespan = Integer.MAX_VALUE;
		ei.worldObj.setBlock(x, y, z, Blocks.air);
		ReikaWorldHelper.causeAdjacentUpdates(ei.worldObj, x, y, z);
	}

	public static class PoolRecipe {

		private final ItemStack main;
		private final ItemHashMap<Integer> inputs = new ItemHashMap().setOneWay();
		private final ItemStack output;

		private PoolRecipe(ItemStack out, ItemStack m, ItemStack... input) {
			output = out.copy();
			main = m;

			for (int i = 0; i < input.length; i++) {
				inputs.put(input[i], input[i].stackSize);
			}
		}

		private void makeFrom(Collection<EntityItem> li) {
			ItemHashMap<Integer> map = inputs.clone();
			for (EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				Integer get = map.get(is);
				if (get != null && get > 0) {
					int rem = Math.min(is.stackSize, get);
					get -= rem;
					ReikaEntityHelper.decrEntityItemStack(ei, rem);
					if (get <= 0) {
						map.remove(is);
						if (map.isEmpty())
							return;
					}
				}
			}
		}

		private boolean canBeMadeFrom(Collection<EntityItem> li) {
			ItemHashMap<Integer> map = inputs.clone();
			for (EntityItem ei : li) {
				ItemStack is = ei.getEntityItem();
				Integer get = map.get(is);
				if (get != null && get > 0) {
					int rem = Math.min(is.stackSize, get);
					get -= rem;
					if (get <= 0) {
						map.remove(is);
						if (map.isEmpty())
							return true;
					}
				}
			}
			return false;
		}

		public ItemStack getMainInput() {
			return main.copy();
		}

		public Collection<ItemStack> getInputs() {
			Collection<ItemStack> c = new ArrayList();
			for (ItemStack is : inputs.keySet()) {
				c.add(ReikaItemHelper.getSizedItemStack(is, inputs.get(is)));
			}
			return c;
		}

		public ItemStack getOutput() {
			return output.copy();
		}

	}

	public Collection<PoolRecipe> getAllPoolRecipes() {
		return Collections.unmodifiableCollection(ReikaJavaLibrary.getCompoundCollection(recipes.values()));
	}

	public Collection<ItemStack> getAllOutputItems() {
		Collection<ItemStack> c = new ArrayList();
		for (PoolRecipe pr : this.getAllPoolRecipes()) {
			if (!ReikaItemHelper.collectionContainsItemStack(c, pr.output))
				c.add(pr.output.copy());
		}
		return c;
	}

}

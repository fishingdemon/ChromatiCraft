/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.monster.EntityWisp;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes.PoolRecipe;
import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityCrystalBase;
import Reika.ChromatiCraft.Block.Dye.BlockDyeSapling;
import Reika.ChromatiCraft.Block.Dye.BlockRainbowSapling;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Items.ItemInfoFragment;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Magic.CrystalPotionController;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer;
import Reika.ChromatiCraft.ModInterface.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.TileEntityLifeEmitter;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityChromaFluidFX;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityAIShutdown;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityChromaLamp;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityCrystalBeacon;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityHeatLily;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ClassDependent;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Event.IceFreezeEvent;
import Reika.DragonAPI.Instantiable.Event.ItemUpdateEvent;
import Reika.DragonAPI.Interfaces.ActivatedInventoryItem;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.ModInteract.ReikaTwilightHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.FrameBlacklist.FrameUsageEvent;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import WayofTime.alchemicalWizardry.api.event.ItemDrainNetworkEvent;
import WayofTime.alchemicalWizardry.api.event.PlayerDrainNetworkEvent;
import WayofTime.alchemicalWizardry.api.event.TeleposeEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaticEventManager {

	public static final ChromaticEventManager instance = new ChromaticEventManager();

	private final Random rand = new Random();

	//
	//private final Collection<TileEntityItemCollector> collectors = new ArrayList();

	private ChromaticEventManager() {

	}

	@SubscribeEvent
	public void harvestSpawner(BlockEvent.BreakEvent evt) {
		if (evt.block == Blocks.mob_spawner) {
			ProgressStage.BREAKSPAWNER.stepPlayerTo(evt.getPlayer());
		}
	}

	@SubscribeEvent
	public void doPoolRecipes(ItemUpdateEvent evt) {
		EntityItem ei = evt.entityItem;
		if (rand.nextInt(5) == 0) {
			PoolRecipe out = PoolRecipes.instance.getPoolRecipe(ei);
			if (out != null) {
				if (ei.worldObj.isRemote) {
					this.poolRecipeParticles(ei);
				}
				if (!ei.worldObj.isRemote && ei.ticksExisted > 20 && rand.nextInt(20) == 0 && (ei.ticksExisted >= 600 || rand.nextInt(600-ei.ticksExisted) == 0)) {
					PoolRecipes.instance.makePoolRecipe(ei, out);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void poolRecipeParticles(EntityItem ei) {
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1, 0.5);
		EntityFX fx = new EntityChromaFluidFX(ei.worldObj, ei.posX, ei.posY, ei.posZ, vx, vy, vz).setGravity(0.125F).setScale(s);
		fx.noClip = true;
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SubscribeEvent
	public void stealHealth(LivingAttackEvent evt) {
		DamageSource src = evt.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();;
			if (Chromabilities.LEECH.enabledOn(ep)) {
				ep.heal(evt.ammount*0.1F);
			}
		}
	}

	@SubscribeEvent
	public void cancelFramez(FrameUsageEvent evt) {
		if (!this.isMovable(evt.tile)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.THAUMCRAFT)
	public void wispEnergy(LivingDropsEvent evt) {
		if (evt.entityLiving instanceof EntityWisp) {
			EntityWisp e = (EntityWisp)evt.entityLiving;
			if (evt.source.getEntity() instanceof EntityPlayer) {
				EntityPlayer ep = (EntityPlayer)evt.source.getEntity();
				String type = e.getType();
				Aspect a = Aspect.getAspect(type);
				if (a != null) {
					int s = 4+rand.nextInt(8);
					ElementTagCompound tag = ChromaAspectManager.instance.getElementCost(a, 1+rand.nextInt(2)).scale(s);
					PlayerElementBuffer.instance.addToPlayer(ep, tag);
					PlayerElementBuffer.instance.checkUpgrade(ep, true);
				}
			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.VOIDMONSTER)
	public void voidMonsterBonus(LivingDropsEvent evt) {
		if (evt.source instanceof PylonDamage && evt.entityLiving.getClass().getSimpleName().equals("EntityVoidMonster")) {
			for (int i = 0; i < 4+rand.nextInt(4); i++) {
				ReikaItemHelper.dropItem(evt.entityLiving, ChromaItems.FRAGMENT.getStackOf());
			}
			try {
				for (int i = 0; i < 2; i++)
					ReikaObfuscationHelper.getMethod("dropFewItems").invoke(evt.entityLiving, false, 0);
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not perform pylon-void monster bonus drops interaction!");
				e.printStackTrace();
			}
			ChromaSounds.POWERDOWN.playSound(evt.entityLiving, 2, 1);
			ChromaSounds.POWERDOWN.playSound(evt.entityLiving, 2, 0.5F);
			ReikaParticleHelper.EXPLODE.spawnAt(evt.entityLiving);
		}
	}

	@SubscribeEvent
	public void deathProgress(LivingDeathEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)evt.entityLiving;
			if (PlayerElementBuffer.instance.getPlayerTotalEnergy(ep) >= 90000)
				ProgressStage.DIE.stepPlayerTo(ep);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ModDependent(ModList.BLOODMAGIC)
	@ClassDependent("WayofTime.alchemicalWizardry.api.event.TeleposeEvent")
	public void noTelepose(TeleposeEvent evt) {
		if (!this.isMovable(evt.getInitialTile()) || !this.isMovable(evt.getFinalTile()))
			evt.setCanceled(true);
	}

	private boolean isMovable(TileEntity te) {
		return !(te instanceof TileEntityCrystalBase);
	}

	@SubscribeEvent
	@ModDependent(ModList.BLOODMAGIC)
	public void interceptSoulNet(PlayerDrainNetworkEvent evt) {
		EntityPlayer ep = evt.player;
		if (Chromabilities.LIFEPOINT.enabledOn(ep)) {
			float amt = evt.drainAmount;
			ElementTagCompound tag1 = TileEntityLifeEmitter.getLumensPerHundredLP().scale(amt/100F);
			ElementTagCompound tag2 = PlayerElementBuffer.instance.getPlayerBuffer(ep);
			tag2.intersectWith(tag1);
			float ratio = tag2.getSmallestRatio(tag1);
			if (ratio >= 1) {
				PlayerElementBuffer.instance.removeFromPlayer(ep, tag1);
				evt.drainAmount = 0;
				if (evt instanceof ItemDrainNetworkEvent) {
					ItemDrainNetworkEvent ev = (ItemDrainNetworkEvent)evt;
					ev.damageAmount = 0;
					ev.shouldDamage = false;
				}
				evt.setCanceled(true);
			}
			else if (ratio > 0) {
				ElementTagCompound rem = tag1.copy();
				rem.scale(ratio);
				float rat = 1-ratio;
				PlayerElementBuffer.instance.removeFromPlayer(ep, rem);
				evt.drainAmount *= rat;
				if (evt instanceof ItemDrainNetworkEvent) {
					ItemDrainNetworkEvent ev = (ItemDrainNetworkEvent)evt;
					ev.damageAmount *= rat;
					if (rat < 0.25)
						ev.shouldDamage = false;
				}
			}
			else {

			}
		}
	}

	@SubscribeEvent
	@ModDependent(ModList.BLOODMAGIC)
	public void onUseSacrificeOrb(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_AIR) {
			EntityPlayer ep = evt.entityPlayer;
			ItemStack is = ep.getCurrentEquippedItem();
			if (is != null) {
				if (is.getItem() == BloodMagicHandler.getInstance().orbID || BloodMagicHandler.getInstance().isBloodOrb(is.getItem())) {
					if (Chromabilities.LIFEPOINT.enabledOn(ep)) {
						ElementTagCompound tag = AbilityHelper.instance.getUsageElementsFor(Chromabilities.LIFEPOINT);
						tag.maximizeWith(TileEntityLifeEmitter.getLumensPerHundredLP());
						if (PlayerElementBuffer.instance.playerHas(ep, tag)) {
							Chromabilities.LIFEPOINT.trigger(ep, 3);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerChangedDimensionEvent evt) {
		if (evt.toDim == -1) {
			ProgressStage.NETHER.stepPlayerTo(evt.player);
		}
		else if (evt.toDim == 1) {
			ProgressStage.END.stepPlayerTo(evt.player);
		}
		else if (evt.toDim == ReikaTwilightHelper.getDimensionID()) {
			ProgressStage.TWILIGHT.stepPlayerTo(evt.player);
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void rangedInvincibility(LivingAttackEvent evt) {
		if (evt.entityLiving instanceof EntityPlayer && !((EntityPlayer)evt.entityLiving).capabilities.isCreativeMode && evt.ammount > 0) {
			if (TileEntityCrystalBeacon.isPlayerInvincible((EntityPlayer)evt.entityLiving, evt.ammount)) {
				evt.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void lampSpawnLimits(CheckSpawn evt) {
		if (TileEntityChromaLamp.findLampFromXYZ(evt.world, evt.x, evt.z)) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void stopSomeTicks(LivingUpdateEvent evt) {
		if (TileEntityAIShutdown.stopUpdate(evt.entityLiving)) {
			evt.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void preSpawnItemXP(EntityJoinWorldEvent evt) {
		if (!evt.world.isRemote) {
			Entity e = evt.entity;
			if (e instanceof EntityItem || e instanceof EntityXPOrb) {
				if (TileEntityItemCollector.absorbItem(e)) {
					evt.setCanceled(true);
					return;
				}
			}
		}
	}

	@SubscribeEvent
	public void preventLilyFreeze(IceFreezeEvent evt) {
		if (TileEntityHeatLily.stopFreeze(evt.world, evt.x, evt.y, evt.z)) {
			evt.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void convertCrystals(EntityJoinWorldEvent evt) {
		if (!evt.world.isRemote) {
			if (evt.entity.getClass() == EntityEnderCrystal.class) {
				EntityChromaEnderCrystal cry = new EntityChromaEnderCrystal(evt.world, (EntityEnderCrystal)evt.entity);
				evt.entity.setDead();
				cry.worldObj.spawnEntityInWorld(cry);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void sendLinkedItems(EntityItemPickupEvent ev) {
		this.fillFragments(ev);
		EntityPlayer ep = ev.entityPlayer;
		this.parseInventoryForLinking(ev, ep.inventory.mainInventory);
	}

	private void parseInventoryForLinking(EntityItemPickupEvent ev, ItemStack[] inv) {
		EntityItem e = ev.item;
		ItemStack picked = e.getEntityItem();
		for (int i = 0; i < inv.length; i++) {
			ItemStack in = inv[i];
			if (in != null && in.getItem() == ChromaItems.LINK.getItemInstance()) {
				ItemInventoryLinker iil = (ItemInventoryLinker)in.getItem();
				if (iil.linksItem(in, picked)) {
					if (iil.processItem(ev.entityPlayer.worldObj, in, picked)) {
						e.playSound("random.pop", 0.5F, 1);
						e.setDead();
						ev.setCanceled(true);
						return;
					}
				}
			}
			else if (in != null && in.getItem() instanceof ActivatedInventoryItem) {
				this.parseInventoryForLinking(ev, ((ActivatedInventoryItem)in.getItem()).getInventory(in));
			}
		}
	}

	@SubscribeEvent
	public void onBucketUse(FillBucketEvent event) {
		World world = event.world;
		MovingObjectPosition pos = event.target;
		int x = pos.blockX;
		int y = pos.blockY;
		int z = pos.blockZ;
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			event.result = ChromaItems.BUCKET.getStackOf();
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}/*
		else if (b == ChromaBlocks.ACTIVECHROMA.getBlockInstance()) {
			TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(2+te.getElement().ordinal());
			event.result.stackTagCompound = new NBTTagCompound();
			event.result.stackTagCompound.setInteger("berry", te.getBerryCount());
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}*/
		else if (b == ChromaBlocks.ENDER.getBlockInstance()) {
			world.setBlockToAir(x, y, z);
			event.setResult(Result.ALLOW);
			event.result = ChromaItems.BUCKET.getStackOfMetadata(1);
			//event.entityPlayer.setCurrentItemOrArmor(0, event.result);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void carryPotionEffect(AttackEntityEvent ev) {
		EntityPlayer ep = ev.entityPlayer;
		Entity tg = ev.target;
		if (tg instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)tg;
			this.parseInventoryForPendantCarry(ev, elb, ep.inventory.mainInventory);
		}
	}

	private void parseInventoryForPendantCarry(AttackEntityEvent ev, EntityLivingBase elb, ItemStack[] inv) {
		for (int i = 0; i < inv.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				if (is.getItem() == ChromaItems.PENDANT3.getItemInstance()) {
					CrystalBlock.applyEffectFromColor(100, 3, elb, CrystalElement.elements[is.getItemDamage()]);
				}
				else if (is.getItem() == ChromaItems.PENDANT.getItemInstance()) {
					CrystalBlock.applyEffectFromColor(100, 1, elb, CrystalElement.elements[is.getItemDamage()]);
				}
				else if (is.getItem() instanceof ActivatedInventoryItem) {
					this.parseInventoryForPendantCarry(ev, elb, ((ActivatedInventoryItem)is.getItem()).getInventory(is));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void extraXP(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		DamageSource src = ev.source;
		if (src.getEntity() instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)src.getEntity();
			int meta = CrystalElement.PURPLE.ordinal();
			int val = e instanceof EntityPlayer ? 25 : e instanceof EntityLiving ? ((EntityLiving)e).experienceValue : 5;
			if (val == 0)
				val = 5;
			if (e instanceof EntityDragon)
				val = 10000;
			if (CrystalPotionController.shouldBeHostile(e.worldObj)) {
				if (e instanceof EntityLiving)
					((EntityLiving)e).experienceValue = 0;
				else if (e instanceof EntityPlayer) {
					ReikaPlayerAPI.clearExperience((EntityPlayer)e);
				}
			}
			else {
				if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT3.getStackOfMetadata(meta), ep.inventory, false)) {
					for (int i = 0; i < 3; i++) {
						double px = e.posX;
						double pz = e.posZ;
						EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
						if (!e.worldObj.isRemote)
							e.worldObj.spawnEntityInWorld(xp);
					}
				}
				else if (ReikaInventoryHelper.checkForItemStack(ChromaItems.PENDANT.getStackOfMetadata(meta), ep.inventory, false)) {
					double px = e.posX;
					double pz = e.posZ;
					EntityXPOrb xp = new EntityXPOrb(e.worldObj, px, e.posY, pz, val);
					if (!e.worldObj.isRemote)
						e.worldObj.spawnEntityInWorld(xp);
				}
			}
		}
	}

	@SubscribeEvent
	public void bonemealEvent (BonemealEvent event)
	{
		if (!event.world.isRemote)  {
			if (event.block == ChromaBlocks.DYESAPLING.getBlockInstance()) {
				BlockDyeSapling sap = (BlockDyeSapling)event.block;
				World world = event.world;
				int x = event.x;
				int y = event.y;
				int z = event.z;
				if (sap.canGrowAt(world, x, y, z, true)) {
					sap.func_149878_d(world, x, y, z, rand);
					event.setResult(Event.Result.ALLOW);
				}
			}
			else if (event.block == ChromaBlocks.RAINBOWSAPLING.getBlockInstance()) {
				BlockRainbowSapling sap = (BlockRainbowSapling)event.block;
				World world = event.world;
				int x = event.x;
				int y = event.y;
				int z = event.z;
				if (sap.canGrowAt(world, x, y, z)) {
					sap.func_149878_d(world, x, y, z, rand);
					event.setResult(Event.Result.ALLOW);
				}
			}
		}
	}

	@SubscribeEvent
	public void colorSheep(LivingSpawnEvent ev) {
		if (ev.getClass() != LivingSpawnEvent.class) //do not allow subclasses
			return;
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (e instanceof EntitySheep) {
				EntitySheep es = (EntitySheep)e;
				es.setFleeceColor(rand.nextInt(16));
			}
		}
	}

	/** Not functional due to BlockLeaves being the <i>only</i> block not to fire the event */
	@SubscribeEvent
	public void addLeafColors(HarvestDropsEvent evt) {/*
		World world = evt.world;
		int x = evt.x;
		int y = evt.y;
		int z = evt.z;
		ArrayList<ItemStack> li = evt.drops;
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.leaves || b == Blocks.leaves2) {
			int meta = rand.nextInt(16);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, meta);
			if (ReikaRandomHelper.doWithChance(0.04)) { //4% chance per leaf block
				li.add(sapling);
			}
		}*/
	}

	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void controlSlimes(CheckSpawn ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (!BiomeRainbowForest.isMobAllowed(e)) {
				ev.setResult(Result.DENY);
				e.setDead();
			}
		}
		//ReikaJavaLibrary.pConsole(b.biomeName+":"+e.getCommandSenderName()+":"+ReikaEntityHelper.isHostile(e)+":"+ev.getResult());
	}

	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void killSpawns(LivingSpawnEvent ev) {
		World world = ev.world;
		if (world.isRemote)
			return;
		int x = (int)Math.floor(ev.x);
		int y = (int)Math.floor(ev.y);
		int z = (int)Math.floor(ev.z);
		EntityLivingBase e = ev.entityLiving;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (!BiomeRainbowForest.isMobAllowed(e)) {
				e.setDead();
			}
		}
	}


	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void killSpawns(EntityJoinWorldEvent ev) {
		World world = ev.world;
		if (ev.entity instanceof EntityLiving) {
			int x = (int)Math.floor(ev.entity.posX);
			int y = (int)Math.floor(ev.entity.posY);
			int z = (int)Math.floor(ev.entity.posZ);
			EntityLiving e = (EntityLiving)ev.entity;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (ChromatiCraft.isRainbowForest(b)) {
				if (!BiomeRainbowForest.isMobAllowed(e) && ChromaAux.requiresSpecialSpawnEnforcement(e)) {
					//e.setDead();
					ev.setCanceled(true);
				}
			}
		}
	}
	/*
	@ModDependent(ModList.LYCANITE)
	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void specialEnforce(LivingUpdateEvent ev) {
		if (ev.entityLiving.ticksExisted < 5) {
			World world = ev.entityLiving.worldObj;
			if (world.isRemote)
				return;
			int x = (int)Math.floor(ev.entityLiving.posX);
			int y = (int)Math.floor(ev.entityLiving.posY);
			int z = (int)Math.floor(ev.entityLiving.posZ);
			EntityLivingBase e = ev.entityLiving;
			BiomeGenBase b = world.getBiomeGenForCoords(x, z);
			if (ChromatiCraft.isRainbowForest(b)) {
				if (!BiomeRainbowForest.isMobAllowed(e)) {
					e.setDead();
				}
			}
		}
	}
	 */

	@SubscribeEvent
	@ModDependent(ModList.THAUMCRAFT)
	public void friendlyWisps(LivingUpdateEvent ev) {
		if (!ChromaOptions.HOSTILEFOREST.getState()) {
			if (ev.entityLiving instanceof EntityWisp) {
				World world = ev.entityLiving.worldObj;
				if (world.isRemote)
					return;
				int x = (int)Math.floor(ev.entityLiving.posX);
				int y = (int)Math.floor(ev.entityLiving.posY);
				int z = (int)Math.floor(ev.entityLiving.posZ);
				EntityLivingBase e = ev.entityLiving;
				BiomeGenBase b = world.getBiomeGenForCoords(x, z);
				if (ChromatiCraft.isRainbowForest(b)) {
					ReikaThaumHelper.setWispHostility((EntityWisp)ev.entityLiving, null);
				}
			}
		}
	}

	@SubscribeEvent
	public void pylonDrops(LivingDropsEvent ev) {
		if (ev.source instanceof PylonDamage) {
			EntityLivingBase e = ev.entityLiving;
			int n = 0;
			if (e instanceof EntityPlayer) {
				NBTTagCompound nbt = ReikaPlayerAPI.getDeathPersistentNBT((EntityPlayer)e);
				String tag = "pylondeath";
				long time = nbt.getLong(tag);
				long cur = e.worldObj.getTotalWorldTime();
				if (cur-time > 24000) {
					n = 1;
					if (rand.nextInt(8) == 0)
						n = 2;
				}
				nbt.setLong(tag, cur);
			}
			else if (e instanceof EntityVillager || e instanceof EntityWitch)
				n = rand.nextInt(8) == 0 ? 1 : 0;
			else if (e instanceof EntityEnderman)
				;//n = rand.nextInt(32) == 0 ? 1 : 0;
			if (n > 0) {
				World world = e.worldObj;
				for (int i = 0; i < n; i++) {
					ReikaItemHelper.dropItem(e, ChromaItems.FRAGMENT.getStackOf());
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void fillFragments(EntityItemPickupEvent ev) {
		ItemStack is = ev.item.getEntityItem();
		if (ChromaItems.FRAGMENT.matchWith(is) && ItemInfoFragment.isBlank(is) && !ev.entityPlayer.capabilities.isCreativeMode) {
			ItemInfoFragment.programShardAndGiveData(is, ev.entityPlayer);
		}
	}

	@SubscribeEvent
	public void biomeDrops(LivingDropsEvent ev) {
		EntityLivingBase e = ev.entityLiving;
		ArrayList<EntityItem> drops = ev.drops;
		int x = (int)Math.floor(e.posX);
		int y = (int)Math.floor(e.posY);
		int z = (int)Math.floor(e.posZ);
		World world = e.worldObj;
		BiomeGenBase b = world.getBiomeGenForCoords(x, z);
		if (ChromatiCraft.isRainbowForest(b)) {
			if (e instanceof EntitySlime) {
				int dmg = e.getEntityId()%16;
				int size = 1+rand.nextInt(3);
				ItemStack dye = new ItemStack(Items.dye, size, dmg);
				ReikaItemHelper.dropItem(world, e.posX, e.posY, e.posZ, dye);
			}

			int spawn = ChromaOptions.ANIMALSPAWN.getValue();
			int def = ChromaOptions.ANIMALSPAWN.getDefaultValue();
			if (spawn < def) {
				int mult = def-spawn;
				if (e instanceof EntityAnimal) {
					for (int i = 0; i < mult; i++) {
						for (EntityItem is : drops)
							ReikaItemHelper.dropItem(world, e.posX, e.posY, e.posZ, is.getEntityItem());
					}
				}
			}
		}
	}



}

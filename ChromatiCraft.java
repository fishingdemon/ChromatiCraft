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

import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import ttftcuts.atg.api.ATGBiomes;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper;
import Reika.ChromatiCraft.Auxiliary.AbilityHelper.LoginApplier;
import Reika.ChromatiCraft.Auxiliary.ChromaBookSpawner;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaFontRenderer;
import Reika.ChromatiCraft.Auxiliary.ChromaHelpHUD;
import Reika.ChromatiCraft.Auxiliary.ChromaLock;
import Reika.ChromatiCraft.Auxiliary.ChromaOverlays;
import Reika.ChromatiCraft.Auxiliary.ChromaResearchCommand;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ChromabilityCommand;
import Reika.ChromatiCraft.Auxiliary.CrystalMaterial;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.NetworkLoggerCommand;
import Reika.ChromatiCraft.Auxiliary.ExplorationMonitor;
import Reika.ChromatiCraft.Auxiliary.FragmentTab;
import Reika.ChromatiCraft.Auxiliary.GuardianCommand;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionStageCommand;
import Reika.ChromatiCraft.Auxiliary.PylonDamage;
import Reika.ChromatiCraft.Auxiliary.PylonFinderOverlay;
import Reika.ChromatiCraft.Auxiliary.TabChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionBetterSaturation;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionCustomRegen;
import Reika.ChromatiCraft.Auxiliary.Potions.PotionGrowthHormone;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Magic.PlayerElementBuffer.PlayerEnergyCommand;
import Reika.ChromatiCraft.Magic.Network.CrystalNetworker;
import Reika.ChromatiCraft.ModInterface.ChromaAspectManager;
import Reika.ChromatiCraft.ModInterface.CrystalWand;
import Reika.ChromatiCraft.ModInterface.TieredOreCap;
import Reika.ChromatiCraft.ModInterface.TreeCapitatorHandler;
import Reika.ChromatiCraft.ModInterface.Bees.CrystalBees;
import Reika.ChromatiCraft.ModInterface.Lua.ChromaLuaMethods;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ChromaResearchDebugCommand;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.ChromatiCraft.World.BiomeEnderForest;
import Reika.ChromatiCraft.World.BiomeRainbowForest;
import Reika.ChromatiCraft.World.ColorTreeGenerator;
import Reika.ChromatiCraft.World.CrystalGenerator;
import Reika.ChromatiCraft.World.DungeonGenerator;
import Reika.ChromatiCraft.World.PylonGenerator;
import Reika.ChromatiCraft.World.TieredWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.ChromaDimensionManager;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.CreativeTabSorter;
import Reika.DragonAPI.Auxiliary.Trackers.BiomeCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.IntegrityChecker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerFirstTimeTracker;
import Reika.DragonAPI.Auxiliary.Trackers.PlayerHandler;
import Reika.DragonAPI.Auxiliary.Trackers.PotionCollisionTracker;
import Reika.DragonAPI.Auxiliary.Trackers.RetroGenController;
import Reika.DragonAPI.Auxiliary.Trackers.SuggestedModsTracker;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Base.DragonAPIMod.LoadProfiler.LoadPhase;
import Reika.DragonAPI.Instantiable.EnhancedFluid;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.ModInteract.BannedItemReader;
import Reika.DragonAPI.ModInteract.ReikaEEHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MTInteractionManager;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.TimeTorchHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.ThermalHandler;
import Reika.RotaryCraft.API.BlockColorInterface;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@Mod( modid = "ChromatiCraft", name="ChromatiCraft", version="Alpha", certificateFingerprint = "@GET_FINGERPRINT@", dependencies="required-after:DragonAPI")

public class ChromatiCraft extends DragonAPIMod {
	public static final String packetChannel = "ChromaData";

	public static final TabChromatiCraft tabChroma = new TabChromatiCraft("ChromatiCraft");
	public static final TabChromatiCraft tabChromaTools = new TabChromatiCraft("ChromatiCraft Tools");
	public static final TabChromatiCraft tabChromaItems = new TabChromatiCraft("ChromatiCraft Items");
	public static final FragmentTab tabChromaFragments = new FragmentTab("ChromatiCraft Fragments");

	static final Random rand = new Random();

	private boolean isLocked = false;

	public static final EnhancedFluid chroma = (EnhancedFluid)new EnhancedFluid("chroma").setColor(0x00aaff).setViscosity(300).setTemperature(400).setDensity(300);
	//public static final EnhancedFluid activechroma = (EnhancedFluid)new EnhancedFluid("activechroma").setColor(0x00aaff).setViscosity(300).setTemperature(500).setDensity(300);
	public static EnhancedFluid crystal = (EnhancedFluid)new EnhancedFluid("potion crystal").setColor(0x66aaff).setLuminosity(15).setTemperature(500).setUnlocalizedName("potioncrystal");
	public static final Fluid ender = new Fluid("ender").setViscosity(2000).setDensity(1500).setTemperature(270).setUnlocalizedName("endere");

	public static final Block[] blocks = new Block[ChromaBlocks.blockList.length];
	public static final Item[] items = new Item[ChromaItems.itemList.length];

	public static Achievement[] achievements;

	public static final Material crystalMat = new CrystalMaterial();

	public static PotionGrowthHormone growth;
	public static PotionBetterSaturation betterSat;
	public static PotionCustomRegen betterRegen;

	public static final PylonDamage pylon = new PylonDamage("got too close to a Crystal Pylon");

	@Instance("ChromatiCraft")
	public static ChromatiCraft instance = new ChromatiCraft();

	public static final ChromaConfig config = new ChromaConfig(instance, ChromaOptions.optionList, ExtraChromaIDs.idList, 0);

	public static ModLogger logger;

	public static BiomeRainbowForest rainbowforest;
	public static BiomeEnderForest enderforest;

	@SidedProxy(clientSide="Reika.ChromatiCraft.ChromaClient", serverSide="Reika.ChromatiCraft.ChromaCommon")
	public static ChromaCommon proxy;

	public final boolean isLocked() {
		return isLocked;
	}

	private final boolean checkForLock() {
		for (int i = 0; i < ChromaItems.itemList.length; i++) {
			ChromaItems r = ChromaItems.itemList[i];
			if (!r.isDummiedOut()) {
				Item id = r.getItemInstance();
				if (BannedItemReader.instance.containsID(id))
					return true;
			}
		}
		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks r = ChromaBlocks.blockList[i];
			if (!r.isDummiedOut()) {
				Block id = r.getBlockInstance();
				if (BannedItemReader.instance.containsID(id))
					return true;
			}
		}
		return false;
	}

	@Override
	@EventHandler
	public void preload(FMLPreInitializationEvent evt) {
		this.startTiming(LoadPhase.PRELOAD);
		this.verifyInstallation();
		MinecraftForge.EVENT_BUS.register(GuardianStoneManager.instance);
		MinecraftForge.EVENT_BUS.register(ChromaticEventManager.instance);
		FMLCommonHandler.instance().bus().register(ChromaticEventManager.instance);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(ChromaClientEventController.instance);
			MinecraftForge.EVENT_BUS.register(ChromaHelpHUD.instance);
			MinecraftForge.EVENT_BUS.register(ChromaOverlays.instance);
			MinecraftForge.EVENT_BUS.register(PylonFinderOverlay.instance);
		}

		config.loadSubfolderedConfigFile(evt);
		config.initProps(evt);
		proxy.registerSounds();

		isLocked = this.checkForLock();
		if (this.isLocked()) {
			ReikaJavaLibrary.pConsole("");
			ReikaJavaLibrary.pConsole("\t========================================= ChromatiCraft ===============================================");
			ReikaJavaLibrary.pConsole("\tNOTICE: It has been detected that third-party plugins are being used to disable parts of ChromatiCraft.");
			ReikaJavaLibrary.pConsole("\tBecause this is frequently done to sell access to mod content, which is against the Terms of Use");
			ReikaJavaLibrary.pConsole("\tof both Mojang and the mod, the mod has been functionally disabled. No damage will occur to worlds,");
			ReikaJavaLibrary.pConsole("\tand all machines (including contents) and items already placed or in inventories will remain so,");
			ReikaJavaLibrary.pConsole("\tbut its machines will not function, recipes will not load, and no renders or textures will be present.");
			ReikaJavaLibrary.pConsole("\tAll other mods in your installation will remain fully functional.");
			ReikaJavaLibrary.pConsole("\tTo regain functionality, unban the ChromatiCraft content, and then reload the game. All functionality");
			ReikaJavaLibrary.pConsole("\twill be restored. You may contact Reika for further information on his forum thread.");
			ReikaJavaLibrary.pConsole("\t=====================================================================================================");
			ReikaJavaLibrary.pConsole("");
		}

		logger = new ModLogger(instance, false);

		int id = ExtraChromaIDs.GROWTHID.getValue();
		PotionCollisionTracker.instance.addPotionID(instance, id, PotionGrowthHormone.class);
		growth = new PotionGrowthHormone(id);

		id = ExtraChromaIDs.SATID.getValue();
		PotionCollisionTracker.instance.addPotionID(instance, id, PotionBetterSaturation.class);
		betterSat = new PotionBetterSaturation(id);

		id = ExtraChromaIDs.REGENID.getValue();
		PotionCollisionTracker.instance.addPotionID(instance, id, PotionCustomRegen.class);
		betterRegen = new PotionCustomRegen(id);

		BiomeCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.RAINBOWFOREST.getValue(), BiomeRainbowForest.class);
		BiomeCollisionTracker.instance.addBiomeID(instance, ExtraChromaIDs.ENDERFOREST.getValue(), BiomeEnderForest.class);

		this.setupClassFiles();
		ChromaTiles.loadMappings();
		ChromaBlocks.loadMappings();
		ChromaItems.loadMappings();
		//ChromaResearch.loadCache();

		ReikaPacketHelper.registerPacketHandler(instance, packetChannel, new ChromatiPackets());

		proxy.registerKeys();

		tabChroma.setIcon(ChromaItems.RIFT.getStackOf());
		tabChromaTools.setIcon(ChromaItems.TOOL.getStackOf());
		tabChromaItems.setIcon(ChromaStacks.getShard(CrystalElement.RED));
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaItems, tabChroma);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaTools, tabChroma);
		CreativeTabSorter.instance.registerCreativeTabAfter(tabChromaFragments, tabChroma);

		if (!this.isLocked()) {
			//if (ConfigRegistry.ACHIEVEMENTS.getState()) {
			//		achievements = new Achievement[RotaryAchievements.list.length];
			//		RotaryAchievements.registerAchievements();
			//	}
		}

		//CompatibilityTracker.instance.registerIncompatibility(ModList.CHROMATICRAFT, ModList.OPTIFINE, CompatibilityTracker.Severity.GLITCH, "Optifine is known to break some rendering and cause framerate drops.");

		FMLInterModComms.sendMessage("zzzzzcustomconfigs", "blacklist-mod-as-output", this.getModContainer().getModId());

		this.basicSetup(evt);
		this.finishTiming();
	}

	@Override
	@EventHandler
	public void load(FMLInitializationEvent event) {
		this.startTiming(LoadPhase.LOAD);
		ChromaRecipes.loadDictionary();
		if (this.isLocked())
			PlayerHandler.instance.registerTracker(ChromaLock.instance);
		if (!this.isLocked()) {
			proxy.addArmorRenders();
			proxy.registerRenderers();
		}

		rainbowforest = new BiomeRainbowForest(ExtraChromaIDs.RAINBOWFOREST.getValue());
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(rainbowforest, ChromaOptions.getRainbowForestWeight()));
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(rainbowforest, ChromaOptions.getRainbowForestWeight()));
		BiomeManager.addSpawnBiome(rainbowforest);
		BiomeDictionary.registerBiomeType(rainbowforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.HILLS);

		enderforest = new BiomeEnderForest(ExtraChromaIDs.ENDERFOREST.getValue());
		BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(enderforest, ChromaOptions.getEnderForestWeight()));
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(enderforest, ChromaOptions.getEnderForestWeight()));
		BiomeManager.addSpawnBiome(enderforest);
		BiomeDictionary.registerBiomeType(enderforest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.MAGICAL);

		ChromaDimensionManager.initialize();

		RetroGenController.instance.addHybridGenerator(CrystalGenerator.instance, 0, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(ColorTreeGenerator.instance, -10, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(PylonGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(DungeonGenerator.instance, Integer.MAX_VALUE, ChromaOptions.RETROGEN.getState());
		RetroGenController.instance.addHybridGenerator(TieredWorldGenerator.instance, Integer.MIN_VALUE, ChromaOptions.RETROGEN.getState());

		ReikaEntityHelper.overrideEntity(EntityChromaEnderCrystal.class, "EnderCrystal", 0);

		ChromaChests.addToChests();

		if (!this.isLocked())
			;//RotaryNames.addNames();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ChromaGuiHandler());
		this.addTileEntities();
		this.addEntities();

		if (!this.isLocked()) {
			ChromaRecipes.addRecipes();
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			ChromaDescriptions.loadData();
		}

		if (!this.isLocked())
			IntegrityChecker.instance.addMod(instance, ChromaBlocks.blockList, ChromaItems.itemList);

		if (!this.isLocked()) {
			TickRegistry.instance.registerTickHandler(ChromabilityHandler.instance, Side.SERVER);
			TickRegistry.instance.registerTickHandler(CrystalNetworker.instance, Side.SERVER);
			TickRegistry.instance.registerTickHandler(ExplorationMonitor.instance, Side.SERVER);
			MinecraftForge.EVENT_BUS.register(AbilityHelper.instance);
			PlayerHandler.instance.registerTracker(LoginApplier.instance);
		}

		if (ChromaOptions.HANDBOOK.getState())
			PlayerFirstTimeTracker.addTracker(new ChromaBookSpawner());

		ReikaMystcraftHelper.disableFluidPage("chroma");

		for (int i = 0; i < 16; i++) {
			ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
			ItemStack used = ChromaOptions.isVanillaDyeMoreCommon() ? dye.getStackOf() : ChromaItems.DYE.getStackOfMetadata(i);
			ItemStack sapling = new ItemStack(ChromaBlocks.DYESAPLING.getBlockInstance(), 1, i);
			ItemStack flower = new ItemStack(ChromaBlocks.DYEFLOWER.getBlockInstance(), 1, i);
			GameRegistry.addRecipe(new ItemStack(ChromaBlocks.DYE.getBlockInstance(), 1, i), "ddd", "ddd", "ddd", 'd', used);
			GameRegistry.addShapelessRecipe(used, flower);
			OreDictionary.registerOre(dye.getOreDictName(), ChromaItems.DYE.getStackOfMetadata(i));
			OreDictionary.registerOre("treeSapling", sapling);
			OreDictionary.registerOre("plant"+dye.colorNameNoSpaces, flower);
			OreDictionary.registerOre("flower"+dye.colorNameNoSpaces, flower);
		}

		if (ChromaOptions.doesVanillaDyeDrop()) {

		}
		else {/*
			logger.log("Configs were set such that trees do not drop vanilla dyes! Loading interface recipes to ensure farmability!");
			for (int i = 0; i < 16; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				Object[] in = this.getIntercraft(dye);
				Object[] sub = new Object[in.length-1];
				System.arraycopy(in, 1, sub, 0, sub.length);
				boolean shaped = (Boolean)in[0];
				if (shaped) {
					GameRegistry.addRecipe(dye.getStackOf(), sub);
				}
				else {
					GameRegistry.addShapelessRecipe(dye.getStackOf(), sub);
				}
			}*/
		}

		this.addDyeCompat();

		if (ModList.ATG.isLoaded()) {
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Forest", rainbowforest, 1.0);
			ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Forest", enderforest, 1.0);
		}

		if (ModList.BLUEPOWER.isLoaded()) { //prevent what is nearly an exploit by uncrafting gold apples
			try {
				Class c = Class.forName("com.bluepowermod.recipe.AlloyFurnaceRegistry");
				Field inst = c.getDeclaredField("INSTANCE");
				inst.setAccessible(true);
				Object instance = inst.get(null);
				Field list = c.getDeclaredField("blacklist");
				list.setAccessible(true);
				List li = (List)list.get(instance);
				li.add("minecraft:golden_apple");
			}
			catch (Exception e) {
				logger.logError("Unable to blacklist golden apple recycling");
				e.printStackTrace();
			}
		}

		ReikaJavaLibrary.initClass(ChromaLuaMethods.class);

		//ModCropList.addCustomCropType(new CrystalPlantHandler());

		//ReikaEEHelper.blacklistRegistry(ChromaBlocks.blockList);
		//ReikaEEHelper.blacklistRegistry(ChromaItems.itemList);

		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.FORESTRY, "Access to crystal bees which have valuable genetics");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.TWILIGHT, "Dense crystal generation");
		SuggestedModsTracker.instance.addSuggestedMod(instance, ModList.THAUMCRAFT, "High crystal aspect values and mod interaction");

		if (MTInteractionManager.isMTLoaded()) {
			for (int i = 0; i < ChromaItems.itemList.length; i++) {
				ChromaItems ir = ChromaItems.itemList[i];
				if (!ir.isDummiedOut() && ir != ChromaItems.TOOL) {
					MTInteractionManager.instance.blacklistNewRecipesFor(ir.getItemInstance());
				}
			}

			for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
				ChromaBlocks ir = ChromaBlocks.blockList[i];
				MTInteractionManager.instance.blacklistNewRecipesFor(ir.getBlockInstance());
			}

			MTInteractionManager.instance.blacklistRecipeRemovalFor(ChromaTiles.TABLE.getCraftedProduct());
		}

		ReikaEEHelper.blacklistEntry(ChromaItems.TIERED);
		ReikaEEHelper.blacklistEntry(ChromaItems.SHARD);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.TIEREDORE);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.TIEREDPLANT);
		ReikaEEHelper.blacklistEntry(ChromaBlocks.CRYSTAL);

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			ReikaJavaLibrary.initClassWithSubs(ChromaFontRenderer.class);

		this.finishTiming();
	}

	private void addEntities() {
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerModEntity(EntityBallLightning.class, "Ball Lightning", id, ChromatiCraft.instance, 64, 20, true);
		EntityRegistry.registerGlobalEntityID(EntityBallLightning.class, "Ball Lightning", id);
		EntityList.entityEggs.put(id, new EntityList.EntityEggInfo(id, 0xbbbbbb, 0xffffff));

		//id = EntityRegistry.findGlobalUniqueEntityId();
		//EntityRegistry.registerModEntity(EntityGluon.class, "Gluon", id, ChromatiCraft.instance, 64, 20, true);
		//EntityRegistry.registerGlobalEntityID(EntityGluon.class, "Gluon", id);
	}

	private void addDyeCompat() {
		if (ModList.TREECAPITATOR.isLoaded()) {
			TreeCapitatorHandler.register();
		}

		if (ModList.ROTARYCRAFT.isLoaded()) {
			for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
				ReikaDyeHelper dye = ReikaDyeHelper.dyes[i];
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DECAY.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYELEAF.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYE.getBlockInstance(), i, dye.color);
				BlockColorInterface.addGPRBlockColor(ChromaBlocks.DYESAPLING.getBlockInstance(), i, dye.color);
			}
		}

		for (int i = 0; i < ReikaDyeHelper.dyes.length; i++) {
			ItemStack is = new ItemStack(ChromaBlocks.DYE.getBlockInstance(), 1, i);
			FMLInterModComms.sendMessage("ForgeMicroblock", "microMaterial", is);
		}
	}

	@Override
	@EventHandler
	public void postload(FMLPostInitializationEvent evt) {
		this.startTiming(LoadPhase.POSTLOAD);

		if (!this.isLocked()) {
			ChromaRecipes.addPostLoadRecipes();
		}

		proxy.addDonatorRender();

		TileEntityBiomePainter.buildBiomeList();

		if (ModList.THAUMCRAFT.isLoaded()) {
			ChromaAspectManager.instance.register();

			new CrystalWand().setGlowing(true);
			TieredOreCap.registerAll();
		}

		if (ModList.FORESTRY.isLoaded()) {
			CrystalBees.register();
		}

		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles m = ChromaTiles.TEList[i];
			TimeTorchHelper.blacklistTileEntity(m.getTEClass());
		}

		this.finishTiming();
	}

	public static boolean isRainbowForest(BiomeGenBase b) {
		return b instanceof BiomeRainbowForest || b.biomeName.equals("Rainbow Forest");
	}

	@EventHandler
	public void registerCommands(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new GuardianCommand());
		evt.registerServerCommand(new ProgressionStageCommand());
		evt.registerServerCommand(new ChromaResearchCommand());
		evt.registerServerCommand(new NetworkLoggerCommand());
		evt.registerServerCommand(new ChromabilityCommand());
		evt.registerServerCommand(new PlayerEnergyCommand());
		evt.registerServerCommand(new ChromaResearchDebugCommand());
	}

	@EventHandler
	public void overrideRecipes(FMLServerStartedEvent evt) {
		if (!this.isLocked()) {

		}
	}

	private void setupClassFiles() {
		setupLiquids();
		ReikaRegistryHelper.instantiateAndRegisterBlocks(instance, ChromaBlocks.blockList, blocks);
		ReikaRegistryHelper.instantiateAndRegisterItems(instance, ChromaItems.itemList, items);
		setupLiquidContainers();

		//Block b = Blocks.mob_spawner;
		//Items.itemsList[b.blockID] = new ItemNBTSpawner(b.blockID).setUnlocalizedName(Items.itemsList[b.blockID].getUnlocalizedName());
	}

	private static void setupLiquids() {
		logger.log("Loading And Registering Liquids");
		FluidRegistry.registerFluid(chroma);
		//FluidRegistry.registerFluid(activechroma);
		FluidRegistry.registerFluid(crystal);
		FluidRegistry.registerFluid(ender);
	}

	private static void setupLiquidContainers() {
		logger.log("Loading And Registering Liquid Containers");
		FluidContainerRegistry.registerFluidContainer(new FluidStack(chroma, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOf(), new ItemStack(Items.bucket));
		if (!ModList.THERMALFOUNDATION.isLoaded())
			FluidContainerRegistry.registerFluidContainer(new FluidStack(ender, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(1), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(crystal, FluidContainerRegistry.BUCKET_VOLUME), ChromaItems.BUCKET.getStackOfMetadata(2), new ItemStack(Items.bucket));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void setupExtraIcons(TextureStitchEvent.Pre event) {
		if (!this.isLocked()) {
			logger.log("Loading Additional Icons");

			if (event.map.getTextureType() == 0) {
				IIcon sicon = event.map.registerIcon("ChromatiCraft:fluid/chroma");
				IIcon ficon = event.map.registerIcon("ChromatiCraft:fluid/chroma_flowing");
				chroma.setIcons(sicon, ficon);

				IIcon cry = event.map.registerIcon("ChromatiCraft:fluid/liqcrystal3");
				crystal.setIcons(cry);

				IIconRegister ico = event.map;
				ender.setStillIcon(ico.registerIcon("ChromatiCraft:fluid/ender"));
				ender.setFlowingIcon(ico.registerIcon("ChromatiCraft:fluid/flowingender"));
				ender.setBlock(ChromaBlocks.ENDER.getBlockInstance());

				for (int i = 0; i < CrystalElement.elements.length; i++) {
					CrystalElement.elements[i].setIcons(event.map);
				}
				ChromaIcons.registerAll(event.map);
			}
		}
	}

	public Block getEnderBlockToGenerate() {
		if (ModList.THERMALFOUNDATION.isLoaded() && ThermalHandler.getInstance().enderID != null) {
			return ThermalHandler.getInstance().enderID;
		}
		return ChromaBlocks.ENDER.getBlockInstance();
	}

	private void addTileEntities() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			String label = "CC"+ChromaTiles.TEList[i].getUnlocalizedName().toLowerCase().replaceAll("\\s","");
			GameRegistry.registerTileEntity(ChromaTiles.TEList[i].getTEClass(), label);
			ReikaJavaLibrary.initClass(ChromaTiles.TEList[i].getTEClass());
		}
		for (int i = 0; i < ChromaBlocks.blockList.length; i++) {
			ChromaBlocks b = ChromaBlocks.blockList[i];
			Class c = b.getObjectClass();
			Class[] cs = c.getClasses();
			if (cs != null) {
				for (int k = 0; k < cs.length; k++) {
					Class in = cs[k];
					if (TileEntity.class.isAssignableFrom(in)) {
						String s = "CC"+in.getSimpleName();
						GameRegistry.registerTileEntity(in, s);
					}
				}
			}
		}
		GameRegistry.registerTileEntity(TileEntityCrystalPlant.class, "CCCrystalPlant");
	}

	@Override
	public String getDisplayName() {
		return "ChromatiCraft";
	}

	@Override
	public String getModAuthorName() {
		return "Reika";
	}

	@Override
	public URL getDocumentationSite() {
		return DragonAPICore.getReikaForumPage();
	}

	@Override
	public String getWiki() {
		return "http://ChromatiCraft.wikia.com/wiki/ChromatiCraft_Wiki";
	}

	@Override
	public ModLogger getModLogger() {
		return logger;
	}

	@Override
	public String getUpdateCheckURL() {
		return CommandableUpdateChecker.reikaURL;
	}
}

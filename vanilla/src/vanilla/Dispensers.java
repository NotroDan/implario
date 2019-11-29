package vanilla;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.Registrar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.*;
import net.minecraft.world.World;
import vanilla.entity.VBlockPumpkin;
import vanilla.entity.VanillaEntity;
import vanilla.entity.VItemSkull;
import vanilla.item.ItemMonsterPlacer;
import vanilla.item.VItemDye;
import vanilla.item.VanillaItems;

import java.util.List;
import java.util.Random;

import static net.minecraft.block.BlockDispenser.getDispensePosition;
import static net.minecraft.block.BlockDispenser.getFacing;

public class Dispensers {
	private static final IBehaviorDispenseItem armor = new BehaviorDefaultDispenseItem() {
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));
			int i = blockpos.getX();
			int j = blockpos.getY();
			int k = blockpos.getZ();
			AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1));
			List<EntityLivingBase> list = source.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb,
					Predicates.and(EntitySelectors.NOT_SPECTATING, new ArmoredMob(stack)));

			if (list.size() > 0) {
				EntityLivingBase entitylivingbase = list.get(0);
				int l = entitylivingbase instanceof Player ? 1 : 0;
				int i1 = VanillaEntity.getArmorPosition(stack);
				ItemStack itemstack = stack.copy();
				itemstack.stackSize = 1;
				entitylivingbase.setCurrentItemOrArmor(i1 - l, itemstack);

				if (entitylivingbase instanceof VanillaEntity) {
					((VanillaEntity) entitylivingbase).setEquipmentDropChance(i1, 2.0F);
				}

				--stack.stackSize;
				return stack;
			}
			return super.dispenseStack(source, stack);
		}
	};


	public static class ArmoredMob implements Predicate<Entity> {

		private final ItemStack armor;

		public ArmoredMob(ItemStack armor) {
			this.armor = armor;
		}

		public boolean apply(Entity e) {
			if (!e.isEntityAlive()) {
				return false;
			}
			if (!(e instanceof EntityLivingBase)) {
				return false;
			}
			EntityLivingBase entitylivingbase = (EntityLivingBase) e;
			return entitylivingbase.getEquipmentInSlot(
					VanillaEntity.getArmorPosition(this.armor)) == null && (entitylivingbase instanceof VanillaEntity ?
					((VanillaEntity) entitylivingbase).canPickUpLoot() :
					entitylivingbase instanceof EntityArmorStand || entitylivingbase instanceof Player
			);
		}

	}


	public void load(Registrar r) {


		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.minecart, new BehaviorDefaultDispenseItem() {
			private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();

			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				EnumFacing enumfacing = getFacing(source.getBlockMetadata());
				World world = source.getWorld();
				double d0 = source.getX() + (double) enumfacing.getFrontOffsetX() * 1.125D;
				double d1 = Math.floor(source.getY()) + (double) enumfacing.getFrontOffsetY();
				double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ() * 1.125D;
				BlockPos blockpos = source.getBlockPos().offset(enumfacing);
				IBlockState iblockstate = world.getBlockState(blockpos);
				BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = iblockstate.getBlock() instanceof BlockRailBase ? iblockstate.getValue(
						((BlockRailBase) iblockstate.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
				double d3;

				if (BlockRailBase.isRailBlock(iblockstate)) {
					if (blockrailbase$enumraildirection.isAscending()) {
						d3 = 0.6D;
					} else {
						d3 = 0.1D;
					}
				} else {
					if (iblockstate.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(world.getBlockState(blockpos.down()))) {
						return this.behaviourDefaultDispenseItem.dispense(source, stack);
					}

					IBlockState iblockstate1 = world.getBlockState(blockpos.down());
					BlockRailBase.EnumRailDirection blockrailbase$enumraildirection1 = iblockstate1.getBlock() instanceof BlockRailBase ? iblockstate1.getValue(
							((BlockRailBase) iblockstate1.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;

					if (enumfacing != EnumFacing.DOWN && blockrailbase$enumraildirection1.isAscending()) d3 = -0.4D;
					else d3 = -0.9D;
				}

				EntityMinecart entityminecart = EntityMinecart.func_180458_a(world, d0, d1 + d3, d2, ((ItemMinecart) stack.getItem()).minecartType);

				if (stack.hasDisplayName()) {
					entityminecart.setCustomNameTag(stack.getDisplayName());
				}

				world.spawnEntityInWorld(entityminecart);
				stack.splitStack(1);
				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
			}
		});

		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense() {
			protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
				EntityArrow entityarrow = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
				entityarrow.canBePickedUp = 1;
				return entityarrow;
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense() {
			protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
				return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense() {
			protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
				return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense() {
			protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
				return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
			}

			protected float magicOne() {
				return super.magicOne() * 0.5F;
			}

			protected float func_82500_b() {
				return super.magicTwo() * 1.25F;
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem() {
			private final BehaviorDefaultDispenseItem field_150843_b = new BehaviorDefaultDispenseItem();

			public ItemStack dispense(IBlockSource source, final ItemStack stack) {
				return ItemPotion.isSplash(stack.getMetadata()) ? new BehaviorProjectileDispense() {
					protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
						return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
					}

					protected float magicOne() {
						return super.magicOne() * 0.5F;
					}

					protected float magicTwo() {
						return super.magicTwo() * 1.25F;
					}
				}.dispense(source, stack) : this.field_150843_b.dispense(source, stack);
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(VanillaItems.spawn_egg, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				EnumFacing enumfacing = getFacing(source.getBlockMetadata());
				double d0 = source.getX() + (double) enumfacing.getFrontOffsetX();
				double d1 = (double) ((float) source.getBlockPos().getY() + 0.2F);
				double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ();
				Entity entity = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), d0, d1, d2);

				if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
					entity.setCustomNameTag(stack.getDisplayName());
				}

				stack.splitStack(1);
				return stack;
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				EnumFacing enumfacing = getFacing(source.getBlockMetadata());
				double d0 = source.getX() + (double) enumfacing.getFrontOffsetX();
				double d1 = (double) ((float) source.getBlockPos().getY() + 0.2F);
				double d2 = source.getZ() + (double) enumfacing.getFrontOffsetZ();
				EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(source.getWorld(), d0, d1, d2, stack);
				source.getWorld().spawnEntityInWorld(entityfireworkrocket);
				stack.splitStack(1);
				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				EnumFacing enumfacing = getFacing(source.getBlockMetadata());
				IPosition iposition = getDispensePosition(source);
				double d0 = iposition.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 0.3F);
				double d1 = iposition.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 0.3F);
				double d2 = iposition.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 0.3F);
				World world = source.getWorld();
				Random random = world.rand;
				double d3 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetX();
				double d4 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetY();
				double d5 = random.nextGaussian() * 0.05D + (double) enumfacing.getFrontOffsetZ();
				world.spawnEntityInWorld(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
				stack.splitStack(1);
				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				source.getWorld().playAuxSFX(1009, source.getBlockPos(), 0);
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {
			private final BehaviorDefaultDispenseItem field_150842_b = new BehaviorDefaultDispenseItem();

			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				EnumFacing enumfacing = getFacing(source.getBlockMetadata());
				World world = source.getWorld();
				double d0 = source.getX() + (double) ((float) enumfacing.getFrontOffsetX() * 1.125F);
				double d1 = source.getY() + (double) ((float) enumfacing.getFrontOffsetY() * 1.125F);
				double d2 = source.getZ() + (double) ((float) enumfacing.getFrontOffsetZ() * 1.125F);
				BlockPos blockpos = source.getBlockPos().offset(enumfacing);
				Material material = world.getBlockState(blockpos).getBlock().getMaterial();
				double d3;

				if (Material.water.equals(material)) {
					d3 = 1.0D;
				} else {
					if (!Material.air.equals(material) || !Material.water.equals(world.getBlockState(blockpos.down()).getBlock().getMaterial())) {
						return this.field_150842_b.dispense(source, stack);
					}

					d3 = 0.0D;
				}

				EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
				world.spawnEntityInWorld(entityboat);
				stack.splitStack(1);
				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
			}
		});
		IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {
			private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				ItemBucket itembucket = (ItemBucket) stack.getItem();
				BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));

				if (itembucket.tryPlaceContainedLiquid(source.getWorld(), blockpos)) {
					stack.setItem(Items.bucket);
					stack.stackSize = 1;
					return stack;
				}
				return this.field_150841_b.dispense(source, stack);
			}
		};
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, ibehaviordispenseitem);
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, ibehaviordispenseitem);
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {
			private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

			public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				World world = source.getWorld();
				BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				Material material = block.getMaterial();
				Item item;

				if (Material.water.equals(material) && block instanceof BlockLiquid && iblockstate.getValue(BlockLiquid.LEVEL) == 0) {
					item = Items.water_bucket;
				} else {
					if (!Material.lava.equals(material) || !(block instanceof BlockLiquid) || iblockstate.getValue(BlockLiquid.LEVEL) != 0) {
						return super.dispenseStack(source, stack);
					}

					item = Items.lava_bucket;
				}

				world.setBlockToAir(blockpos);

				if (--stack.stackSize == 0) {
					stack.setItem(item);
					stack.stackSize = 1;
				} else if (((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(new ItemStack(item)) < 0) {
					this.field_150840_b.dispense(source, new ItemStack(item));
				}

				return stack;
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {
			private boolean field_150839_b = true;

			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				World world = source.getWorld();
				BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));

				if (world.isAirBlock(blockpos)) {
					world.setBlockState(blockpos, Blocks.fire.getDefaultState());

					if (stack.attemptDamageItem(1, world.rand)) {
						stack.stackSize = 0;
					}
				} else if (world.getBlockState(blockpos).getBlock() == Blocks.tnt) {
					Blocks.tnt.onBlockDestroyedByPlayer(world, blockpos, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, Boolean.TRUE));
					world.setBlockToAir(blockpos);
				} else {
					this.field_150839_b = false;
				}

				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				if (this.field_150839_b) {
					source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
				} else {
					source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
				}
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {
			private boolean field_150838_b = true;

			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
					World world = source.getWorld();
					BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));

					if (VItemDye.applyBonemeal(stack, world, blockpos)) {
						if (!world.isClientSide) {
							world.playAuxSFX(2005, blockpos, 0);
						}
					} else {
						this.field_150838_b = false;
					}

					return stack;
				}
				return super.dispenseStack(source, stack);
			}

			protected void playDispenseSound(IBlockSource source) {
				if (this.field_150838_b) {
					source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
				} else {
					source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
				}
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {
			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				World world = source.getWorld();
				BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));
				EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockpos.getX() + 0.5D, (double) blockpos.getY(), (double) blockpos.getZ() + 0.5D, null);
				world.spawnEntityInWorld(entitytntprimed);
				world.playSoundAtEntity(entitytntprimed, "game.tnt.primed", 1.0F, 1.0F);
				--stack.stackSize;
				return stack;
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem() {
			private boolean field_179240_b = true;

			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				World world = source.getWorld();
				EnumFacing enumfacing = getFacing(source.getBlockMetadata());
				BlockPos blockpos = source.getBlockPos().offset(enumfacing);
				BlockSkull blockskull = Blocks.skull;

				if (world.isAirBlock(blockpos) && VItemSkull.canDispenserPlace(world, blockpos, stack)) {
					if (!world.isClientSide) {
						world.setBlockState(blockpos, blockskull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
						TileEntity tileentity = world.getTileEntity(blockpos);

						if (tileentity instanceof TileEntitySkull) {
							if (stack.getMetadata() == 3) {
								GameProfile gameprofile = null;

								if (stack.hasTagCompound()) {
									NBTTagCompound nbttagcompound = stack.getTagCompound();

									if (nbttagcompound.hasKey("SkullOwner", 10)) {
										gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
									} else if (nbttagcompound.hasKey("SkullOwner", 8)) {
										String s = nbttagcompound.getString("SkullOwner");

										if (!StringUtils.isNullOrEmpty(s)) {
											gameprofile = new GameProfile(null, s);
										}
									}
								}

								((TileEntitySkull) tileentity).setPlayerProfile(gameprofile);
							} else {
								((TileEntitySkull) tileentity).setType(stack.getMetadata());
							}

							((TileEntitySkull) tileentity).setSkullRotation(enumfacing.getOpposite().getHorizontalIndex() * 4);
							VItemSkull.checkWitherSpawn(world, blockpos, (TileEntitySkull) tileentity);
						}

						--stack.stackSize;
					}
				} else {
					this.field_179240_b = false;
				}

				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				if (this.field_179240_b) {
					source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
				} else {
					source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
				}
			}
		});
		BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem() {
			private boolean field_179241_b = true;

			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				World world = source.getWorld();
				BlockPos blockpos = source.getBlockPos().offset(getFacing(source.getBlockMetadata()));
				BlockPumpkin blockpumpkin = (BlockPumpkin) Blocks.pumpkin;
				if (world.isAirBlock(blockpos) && VBlockPumpkin.canDispenserPlace(world, blockpos)) {
					if (!world.isClientSide) {
						world.setBlockState(blockpos, blockpumpkin.getDefaultState(), 3);
					}

					--stack.stackSize;
				} else {
					this.field_179241_b = false;
				}

				return stack;
			}

			protected void playDispenseSound(IBlockSource source) {
				if (this.field_179241_b) {
					source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
				} else {
					source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
				}
			}
		});

		for (Item item : Item.itemRegistry.getRegistryObjects().values()) {
			if (item instanceof ItemArmor)
				BlockDispenser.dispenseBehaviorRegistry.putObject(item, armor);
		}
	}

}

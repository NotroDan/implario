package net.minecraft.entity;

import com.google.common.collect.Sets;
import net.minecraft.Logger;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EntityTracker {

	private static final Logger logger = Logger.getInstance();
	private final WorldServer theWorld;
	private Set<EntityTrackerEntry> trackedEntities = Sets.newHashSet();
	private IntHashMap<EntityTrackerEntry> trackedEntityHashTable = new IntHashMap();
	private int maxTrackingDistanceThreshold;

	public EntityTracker(WorldServer theWorldIn) {
		this.theWorld = theWorldIn;
		this.maxTrackingDistanceThreshold = theWorldIn.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
	}

	public void trackEntity(Entity e) {
		if (e instanceof MPlayer) {
			this.trackEntity(e, 512, 2);
			MPlayer entityplayermp = (MPlayer) e;

			for (EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
				if (entitytrackerentry.trackedEntity != entityplayermp) {
					entitytrackerentry.updatePlayerEntity(entityplayermp);
				}
			}
		} else if (e.doTracking()) {
			this.addEntityToTracker(e, e.getTrackingRange(), e.getUpdateFrequency(), e.sendVelocityUpdates());
		}
	}

	public void trackEntity(Entity entityIn, int trackingRange, int updateFrequency) {
		this.addEntityToTracker(entityIn, trackingRange, updateFrequency, false);
	}

	/**
	 * Args : Entity, trackingRange, updateFrequency, sendVelocityUpdates
	 */
	public void addEntityToTracker(Entity entityIn, int trackingRange, final int updateFrequency, boolean sendVelocityUpdates) {
		if (trackingRange > this.maxTrackingDistanceThreshold) {
			trackingRange = this.maxTrackingDistanceThreshold;
		}

		try {
			if (this.trackedEntityHashTable.containsItem(entityIn.getEntityId())) {
				throw new IllegalStateException("Entity is already tracked!");
			}

			EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entityIn, trackingRange, updateFrequency, sendVelocityUpdates);
			this.trackedEntities.add(entitytrackerentry);
			this.trackedEntityHashTable.addKey(entityIn.getEntityId(), entitytrackerentry);
			entitytrackerentry.updatePlayerEntities(this.theWorld.playerEntities);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding entity to track");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity To Track");
			crashreportcategory.addCrashSection("Tracking range", trackingRange + " blocks");
			crashreportcategory.addCrashSectionCallable("Update interval", () -> {
				String s = "Once per " + updateFrequency + " ticks";

				if (updateFrequency == Integer.MAX_VALUE) s = "Maximum (" + s + ")";

				return s;
			});
			entityIn.addEntityCrashInfo(crashreportcategory);
			CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Entity That Is Already Tracked");
			this.trackedEntityHashTable.lookup(entityIn.getEntityId()).trackedEntity.addEntityCrashInfo(crashreportcategory1);

			try {
				throw new ReportedException(crashreport);
			} catch (ReportedException reportedexception) {
				logger.error("\"Silently\" catching entity tracking error.", reportedexception);
			}
		}
	}

	public void untrackEntity(Entity entityIn) {
		if (entityIn instanceof MPlayer) {
			MPlayer entityplayermp = (MPlayer) entityIn;

			for (EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
				entitytrackerentry.removeFromTrackedPlayers(entityplayermp);
			}
		}

		EntityTrackerEntry entitytrackerentry1 = this.trackedEntityHashTable.removeObject(entityIn.getEntityId());

		if (entitytrackerentry1 != null) {
			this.trackedEntities.remove(entitytrackerentry1);
			entitytrackerentry1.sendDestroyEntityPacketToTrackedPlayers();
		}
	}

	public void updateTrackedEntities() {
		List<MPlayer> list = new ArrayList<>();

		for (EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
			entitytrackerentry.updatePlayerList(this.theWorld.playerEntities);

			if (entitytrackerentry.playerEntitiesUpdated && entitytrackerentry.trackedEntity instanceof MPlayer) {
				list.add((MPlayer) entitytrackerentry.trackedEntity);
			}
		}

		for (int i = 0; i < list.size(); ++i) {
			MPlayer entityplayermp = list.get(i);

			for (EntityTrackerEntry entitytrackerentry1 : this.trackedEntities) {
				if (entitytrackerentry1.trackedEntity != entityplayermp) {
					entitytrackerentry1.updatePlayerEntity(entityplayermp);
				}
			}
		}
	}

	public void func_180245_a(MPlayer p_180245_1_) {
		for (EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
			if (entitytrackerentry.trackedEntity == p_180245_1_) {
				entitytrackerentry.updatePlayerEntities(this.theWorld.playerEntities);
			} else {
				entitytrackerentry.updatePlayerEntity(p_180245_1_);
			}
		}
	}

	public void sendToAllTrackingEntity(Entity entityIn, Packet p_151247_2_) {
		EntityTrackerEntry entitytrackerentry = this.trackedEntityHashTable.lookup(entityIn.getEntityId());

		if (entitytrackerentry != null) {
			entitytrackerentry.sendPacketToTrackedPlayers(p_151247_2_);
		}
	}

	public void func_151248_b(Entity entityIn, Packet p_151248_2_) {
		EntityTrackerEntry entitytrackerentry = this.trackedEntityHashTable.lookup(entityIn.getEntityId());

		if (entitytrackerentry != null) {
			entitytrackerentry.func_151261_b(p_151248_2_);
		}
	}

	public void removePlayerFromTrackers(MPlayer p_72787_1_) {
		for (EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
			entitytrackerentry.removeTrackedPlayerSymmetric(p_72787_1_);
		}
	}

	public void func_85172_a(MPlayer p_85172_1_, Chunk p_85172_2_) {
		for (EntityTrackerEntry entitytrackerentry : this.trackedEntities) {
			if (entitytrackerentry.trackedEntity != p_85172_1_ && entitytrackerentry.trackedEntity.chunkCoordX == p_85172_2_.xPosition && entitytrackerentry.trackedEntity.chunkCoordZ == p_85172_2_.zPosition) {
				entitytrackerentry.updatePlayerEntity(p_85172_1_);
			}
		}
	}

}

package vanilla.world;

import net.minecraft.logging.IProfiler;
import net.minecraft.resources.event.E;
import net.minecraft.resources.event.ServerEvents;
import net.minecraft.resources.event.events.world.WorldServerInitEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;
import vanilla.world.gen.feature.village.VillageCollection;
import vanilla.world.gen.feature.village.VillageSiege;

/**
 * Серверная сторона мира, использующаяся для ада и энда (DIM-1 и DIM1)
 * Делегирует информацию о мире, хранилище карты, скорборд и границу мира из DIM0
 */
public class WorldServerExtra extends VanillaWorldServer {

	private VanillaWorldServer delegate;

	public WorldServerExtra(MinecraftServer server, ISaveHandler saveHandlerIn, int dimensionId, VanillaWorldServer delegate, IProfiler profilerIn) {
		super(server, saveHandlerIn, new DerivedWorldInfo(delegate.getWorldInfo()), dimensionId, profilerIn);
		this.delegate = delegate;
		delegate.getWorldBorder().addListener(new IBorderListener() {
			public void onSizeChanged(WorldBorder border, double newSize) {
				getWorldBorder().setTransition(newSize);
			}

			public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
				WorldServerExtra.this.getWorldBorder().setTransition(oldSize, newSize, time);
			}

			public void onCenterChanged(WorldBorder border, double x, double z) {
				WorldServerExtra.this.getWorldBorder().setCenter(x, z);
			}

			public void onWarningTimeChanged(WorldBorder border, int newTime) {
				WorldServerExtra.this.getWorldBorder().setWarningTime(newTime);
			}

			public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
				WorldServerExtra.this.getWorldBorder().setWarningDistance(newDistance);
			}

			public void onDamageAmountChanged(WorldBorder border, double newAmount) {
				WorldServerExtra.this.getWorldBorder().setDamageAmount(newAmount);
			}

			public void onDamageBufferChanged(WorldBorder border, double newSize) {
				WorldServerExtra.this.getWorldBorder().setDamageBuffer(newSize);
			}
		});
	}

	/**
	 * Saves the chunks to disk.
	 */
	protected void saveLevel() throws MinecraftException {}

	public World init() {
		this.mapStorage = this.delegate.getMapStorage();
		this.worldScoreboard = this.delegate.getScoreboard();

		villageSiege = new VillageSiege(this);

		String s = VillageCollection.fileNameForProvider(this.provider);
		VillageCollection villagecollection = (VillageCollection) this.mapStorage.loadData(VillageCollection.class, s);

		if (villagecollection == null) {
			this.villageCollection = new VillageCollection(this);
			this.mapStorage.setData(s, this.villageCollection);
		} else {
			this.villageCollection = villagecollection;
			this.villageCollection.setWorldsForAll(this);
		}

		if(ServerEvents.worldInit.isUseful())
			ServerEvents.worldInit.call(new WorldServerInitEvent(this));

		return this;
	}

}

package net.minecraft.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.Getter;
import net.minecraft.logging.IProfiler;
import net.minecraft.logging.Profiler;
import net.minecraft.network.net.Packet;
import org.apache.commons.lang3.Validate;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public abstract class MinecraftCore {

	@Getter
	private final IProfiler profiler = new Profiler();

	private final Queue<Packet> queuedPackets = new ArrayDeque<>();

	private final Queue<FutureTask<?>> queuedTasks = new ArrayDeque<>();

	public void queue(Packet packet) {
		synchronized (this.queuedPackets) {
			this.queuedPackets.add(packet);
		}
	}

	public void executeQueued() {
		profiler.startSection("jobs");
		synchronized (this.queuedTasks) {
			while (!this.queuedTasks.isEmpty()) {
				Util.schedule(queuedTasks.poll());
			}
		}
		profiler.endStartSection("packets");
		synchronized (this.queuedPackets) {
			while (!this.queuedPackets.isEmpty()) {
				Packet packet = queuedPackets.poll();
				// ToDo: stub
//				packet.getConcept().getListeners();
			}
		}
		profiler.endSection();
	}


	public <V> ListenableFuture<V> queue(Callable<V> callable) {
		Validate.notNull(callable);

		if (doTaskForwarding()) try {
			return Futures.immediateFuture(callable.call());
		} catch (Exception exception) {
			return Futures.immediateFailedCheckedFuture(exception);
		}

		ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);
		synchronized (this.queuedTasks) {
			this.queuedTasks.add(listenablefuturetask);
			return listenablefuturetask;
		}
	}

	protected boolean doTaskForwarding() {
		return false;
	}


	public abstract boolean isCallingFromMinecraftThread();

}

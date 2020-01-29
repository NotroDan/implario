package net.minecraft.util;

import net.minecraft.network.net.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

public class CraftCore {

	private final BlockingQueue<Packet> queuedPackets = new ArrayBlockingQueue<>(16);
	private final BlockingQueue<FutureTask<?>> queuedTasks = new ArrayBlockingQueue<>(16);





}

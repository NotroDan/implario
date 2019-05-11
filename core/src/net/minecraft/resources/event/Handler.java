package net.minecraft.resources.event;

public interface Handler<Global, Local extends Global> {

	void handle(Local data);

}

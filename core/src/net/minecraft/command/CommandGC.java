package net.minecraft.command;

public class CommandGC extends CommandBase{
    @Override
    public String getCommandName() {
        return "gc";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        long start = System.currentTimeMillis();
        System.gc();
        long end = System.currentTimeMillis();
        sender.sendMessage(end - start + "ms to gc");
    }
}

package net.minecraft.command;

import net.minecraft.entity.player.Player;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3d;
import net.minecraft.util.chat.ChatComponentTranslation;

public class CommandFly extends CommandBase{
    @Override
    public String getCommandName() {
        return "fly";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.fly.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean clientProcessSupported() {
        return false;
    }

    @Override
    public void processClientCommand(ICommandSender sender, String args[]) {
        sender.sendMessage("LoL");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Player player;
        boolean fly;
        switch (args.length){
            case 0:
                player = getCommandSenderAsPlayer(sender);
                fly = !player.capabilities.allowFlying;
                break;
            case 1:
                switch (args[0]){
                    case "on":
                        fly = true;
                        player = getCommandSenderAsPlayer(sender);
                        break;
                    case "off":
                        player = getCommandSenderAsPlayer(sender);
                        fly = false;
                        break;
                    default:
                        player = getPlayer(sender, args[0]);
                        fly = !player.capabilities.allowFlying;
                        break;
                }
                break;
            case 2:
                player = getPlayer(sender, args[0]);
                fly = args[1].equals("on");
                break;
            default:
                throw new WrongUsageException("commands.fly.usage");
        }
        player.capabilities.allowFlying = fly;
        if(!fly)player.capabilities.isFlying = false;
        player.sendPlayerAbilities();
        IChatComponent chat;
        if(player == sender)
            chat = new ChatComponentTranslation(fly ? "commands.fly.myself.on" : "commands.fly.myself.off");
        else
            chat = new ChatComponentTranslation(fly ? "commands.fly.himself.on" : "commands.fly.himself.off", player.getDisplayName());
        chat.getChatStyle().setColor(fly ? EnumChatFormatting.GREEN : EnumChatFormatting.RED);
        sender.sendMessage(chat);
    }
}

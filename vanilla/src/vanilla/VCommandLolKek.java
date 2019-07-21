package vanilla;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.chat.ChatComponentText;

public class VCommandLolKek extends CommandBase {
    @Override
    public String getCommandName() {
        return "lolkek";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "LolKek";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new ChatComponentText("LolKek"));
    }
}

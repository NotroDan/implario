package net.minecraft.resources.mapping;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;

public class MappingCommand extends Mapping<ICommand> {
    public MappingCommand(ICommand overriden, ICommand actual){
        super(actual.getCommandName(), overriden, actual);
    }

    @Override
    protected void map(ICommand element) {
        if(element == null) CommandHandler.unregisterCommand(actual);
        else {
            CommandHandler.unregisterCommand(element);
            CommandHandler.registerCommand(element);
        }
    }
}

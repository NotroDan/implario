package net.minecraft.command.impl.server;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.impl.core.WrongUsageException;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.crypt.SHA;

import java.util.Collections;
import java.util.List;

public class CommandRegister extends CommandBase {
    @Override
    public String getCommandName() {
        return "register";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("reg");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        MPlayer player = (MPlayer)sender;
        if(player.isLogined()){
            sender.sendMessage("§cТы уже авторизован");
            return;
        }
        if(player.registered()){
            sender.sendMessage("§cАвторизируйся командой /login [Пароль]");
            return;
        }
        if(args.length == 0)
            throw new WrongUsageException("юзаге когда 0 аргументов");
        if(player.register(SHA.SHA_256(args[0].getBytes())))
            sender.sendMessage("§6Регистрация завершена успешло");
        else sender.sendMessage("§cНеверный пароль");
    }
}

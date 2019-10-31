package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.crypt.SHA;

import java.util.Collections;
import java.util.List;

public class CommandLogin extends CommandBase {
    @Override
    public String getCommandName() {
        return "login";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("l");
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
        if(!player.registered()){
            sender.sendMessage("§cЗагегиструйся командой /reg [Пароль]");
            return;
        }
        if(args.length == 0)
            throw new WrongUsageException("юзаге когда 0 аргументов");
        if(player.login(SHA.SHA_256(args[0].getBytes())))
            sender.sendMessage("§6Авторизация завершена успешло");
        else sender.sendMessage("§cНеверный пароль");
    }
}

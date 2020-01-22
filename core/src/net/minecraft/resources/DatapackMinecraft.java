package net.minecraft.resources;

public class DatapackMinecraft extends Datapack{
    public static final String MINECRAFT = "minecraft";

    public DatapackMinecraft() {
        super(MINECRAFT);
    }

    @Override
    public void preinit() {
        registrar.registerCommands(
//                new CommandTime(), new CommandGameMode(), new CommandDifficulty(),
//                new CommandDefaultGameMode(), new CommandKill(),
//                new CommandToggleDownfall(), new CommandWeather(),
//                new CommandXP(), new CommandTeleport(),
//                new CommandGive(), new CommandReplaceItem(),
//                new CommandStats(), new CommandEffect(),
//                new CommandEnchant(), new CommandParticle(),
//                new CommandEmote(), new CommandShowSeed(),
//                new CommandFly(), new CommandHelp(),
//                new CommandDebug(), new CommandMessage(),
//                new CommandBroadcast(), new CommandSetSpawnpoint(),
//                new CommandSetDefaultSpawnpoint(), new CommandGameRule(),
//                new CommandClearInventory(), new CommandTestFor(),
//                new CommandSpreadPlayers(), new CommandPlaySound(),
//                new CommandScoreboard(), new CommandExecuteAt(),
//                new CommandTrigger(), new CommandAchievement(),
//                new CommandSummon(), new CommandSetBlock(),
//                new CommandFill(), new CommandClone(),
//                new CommandCompare(), new CommandBlockData(),
//                new CommandTestForBlock(), new CommandMessageRaw(),
//                new CommandWorldBorder(), new CommandTitle(),
//                new CommandEntityData(), new CommandSet(),
//                new CommandMemory(), new CommandGC(),
//                new CommandLogin(), new CommandRegister(),
//                new CommandDatapack(), new CommandOp(),
//                new CommandDeOp(), new CommandStop(),
//                new CommandSaveAll(), new CommandSaveOff(),
//                new CommandSaveOn(), new CommandBanIp(),
//                new CommandUnbanIp(), new CommandBanPlayer(),
//                new CommandListBans(), new CommandUnbanPlayer(),
//                new CommandServerKick(), new CommandListPlayers(),
//                new CommandWhitelist(), new CommandSetPlayerTimeout(),
//                new CommandPublishLocalServer()
        );
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    protected void unload() {
        super.unload();
    }
}

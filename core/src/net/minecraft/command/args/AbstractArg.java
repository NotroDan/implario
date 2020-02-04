package net.minecraft.command.args;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.command.Arg;
import net.minecraft.command.ArgsParser;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.entity.player.Player;
import net.minecraft.util.BlockPos;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractArg<T> implements Arg<T> {
	private final String caption;

	@Getter
	private final String description;

	private Function<ArgsParser, T> filler;
	private TabCompleter tabCompleter = (player, pos) -> player.getServerForPlayer().getMinecraftServer().getConfigurationManager().getPlayers().stream().map(Player::getName).collect(Collectors.toList());

	@Override
	public Collection<String> performTabCompletion(MPlayer player, BlockPos pos, String arg) {
		return tabCompleter.tabComplete(player, pos);
	}

	@Override
	public Arg<T> tabCompleter(TabCompleter tabCompleter) {
		this.tabCompleter = tabCompleter;
		return this;
	}

	@Override
	public String getCaption() {
		return "<" + caption + ">";
	}

	@Override
	public T getDefaultValue(ArgsParser parser) {
		return filler.apply(parser);
	}

	@Override
	public Arg<T> defaults(Function<ArgsParser, T> filler) {
		this.filler = filler;
		return this;
	}

	@Override
	public boolean isEssential() {
		return filler == null;
	}

	@Override
	public Arg<T> defaults(final T value) {
		this.filler = parser -> value;
		return this;
	}

	@Override
	public int getEssentialPartsAmount() {
		return 1;
	}
}

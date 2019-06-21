package net.minecraft.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IStringSerializable;

import static net.minecraft.block.material.MapColor.*;

@RequiredArgsConstructor
public enum EnumDyeColor implements IStringSerializable {
	WHITE      ("white",      "white",     snowColor,      EnumChatFormatting.WHITE        ),
	ORANGE     ("orange",     "orange",    adobeColor,     EnumChatFormatting.GOLD         ),
	MAGENTA    ("magenta",    "magenta",   magentaColor,   EnumChatFormatting.AQUA         ),
	LIGHT_BLUE ("light_blue", "lightBlue", lightBlueColor, EnumChatFormatting.BLUE         ),
	YELLOW     ("yellow",     "yellow",    yellowColor,    EnumChatFormatting.YELLOW       ),
	LIME       ("lime",       "lime",      limeColor,      EnumChatFormatting.GREEN        ),
	PINK       ("pink",       "pink",      pinkColor,      EnumChatFormatting.LIGHT_PURPLE ),
	GRAY       ("gray",       "gray",      grayColor,      EnumChatFormatting.DARK_GRAY    ),
	SILVER     ("silver",     "silver",    silverColor,    EnumChatFormatting.GRAY         ),
	CYAN       ("cyan",       "cyan",      cyanColor,      EnumChatFormatting.DARK_AQUA    ),
	PURPLE     ("purple",     "purple",    purpleColor,    EnumChatFormatting.DARK_PURPLE  ),
	BLUE       ("blue",       "blue",      blueColor,      EnumChatFormatting.DARK_BLUE    ),
	BROWN      ("brown",      "brown",     brownColor,     EnumChatFormatting.GOLD         ),
	GREEN      ("green",      "green",     greenColor,     EnumChatFormatting.DARK_GREEN   ),
	RED        ("red",        "red",       redColor,       EnumChatFormatting.DARK_RED     ),
	BLACK      ("black",      "black",     blackColor,     EnumChatFormatting.BLACK        );

	@Getter private final String name;
	@Getter private final String unlocalizedName;
	@Getter private final MapColor mapColor;
	@Getter private final EnumChatFormatting chatColor;

	public static EnumDyeColor byDyeDamage(int damage) {
		return byMetadata(15 - damage);
	}

	public static EnumDyeColor byMetadata(int meta) {
		if (meta < 0 || meta >= 15) meta = 0;
		return values()[meta];
	}

	public int getMetadata() {
		return ordinal();
	}

	public int getDyeDamage() {
		return 15 - ordinal();
	}

	public String toString() {
		return this.unlocalizedName;
	}
}

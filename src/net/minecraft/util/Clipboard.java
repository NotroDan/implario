package net.minecraft.util;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

public final class Clipboard {

	private Clipboard() {throw null;}


	public static void push(String string) {
		if (StringUtils.isEmpty(string)) {return;}
		try {
			StringSelection stringselection = new StringSelection(string);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
		} catch (Exception ignored) {}
	}

	public static String pullString() {
		try {
			Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
				return (String) transferable.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception ignored) {}
		return "";
	}

	public static void push(BufferedImage image) {

		TransferableImage timage = new TransferableImage(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(timage, (cb, t) -> {});
	}

	private static class TransferableImage implements Transferable {

		Image i;

		TransferableImage(Image i) {
			this.i = i;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (flavor.equals(DataFlavor.imageFlavor) && i != null) return i;
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {DataFlavor.imageFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			DataFlavor[] flavors = getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) if (flavor.equals(flavors[i])) return true;
			return false;
		}

	}


}

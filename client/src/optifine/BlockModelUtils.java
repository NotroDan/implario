package optifine;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

public class BlockModelUtils {
	public static IBakedModel makeModelCube(String name, int tintIndexIn) {
		TextureAtlasSprite textureatlassprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(name);
		return makeModelCube(textureatlassprite, tintIndexIn);
	}

	public static IBakedModel makeModelCube(TextureAtlasSprite atlas, int tintIndexIn) {
		EnumFacing[] aenumfacing = EnumFacing.VALUES;
		List<List<BakedQuad>> list = new ArrayList<>(aenumfacing.length);

		for (EnumFacing facing : aenumfacing) {
			List<BakedQuad> local = new ArrayList<>();
			local.add(makeBakedQuad(facing, atlas, tintIndexIn));
			list.add(local);
		}

		return new SimpleBakedModel(new ArrayList<>(), list,
				true, true, atlas, ItemCameraTransforms.DEFAULT);
	}

	private static BakedQuad makeBakedQuad(EnumFacing facing, TextureAtlasSprite atlas, int tintIndexIn) {
		Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
		Vector3f vector3f1 = new Vector3f(16.0F, 16.0F, 16.0F);
		BlockFaceUV blockfaceuv = new BlockFaceUV(new float[] {0.0F, 0.0F, 16.0F, 16.0F}, 0);
		BlockPartFace blockpartface = new BlockPartFace(facing, tintIndexIn, "#" + facing.getName(), blockfaceuv);
		ModelRotation modelrotation = ModelRotation.X0_Y0;
		BlockPartRotation blockpartrotation = null;
		boolean flag = false;
		boolean flag1 = true;
		return new FaceBakery().makeBakedQuad(vector3f, vector3f1, blockpartface, atlas,
				facing, modelrotation, blockpartrotation, flag, flag1);
	}
}

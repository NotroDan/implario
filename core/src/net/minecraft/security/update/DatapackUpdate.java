package net.minecraft.security.update;

import __google_.util.FileIO;
import net.minecraft.logging.Log;
import net.minecraft.network.services.Method;
import net.minecraft.network.services.Request;
import net.minecraft.network.services.github.GitHubAPI;
import net.minecraft.network.services.github.GitHubAsset;
import net.minecraft.network.services.github.Release;
import net.minecraft.util.byteable.SlowDecoder;
import net.minecraft.util.crypt.SecurityKey;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DatapackUpdate {
    public static final DatapackUpdate minecraft = new DatapackUpdate(SecurityKeys.root, "v0.9", "DelfikPro", "Implario",  true);

    private final SecurityKey key;
    private final String tagRelease, owner, repo;
    private final boolean preRelease;

    public DatapackUpdate(SecurityKey publicKey, String tagRelease, String owner, String repo, boolean preRelease){
        this.key = publicKey;
        this.tagRelease = tagRelease;
        this.owner = owner;
        this.repo = repo;
        this.preRelease = preRelease;
    }

    public List<Release> checkUpdate() {
        List<Release> releases = GitHubAPI.getReleases(owner, repo);
        for(Release release : releases){
            if(release.isPrerelease())
                if(!preRelease)continue;
            if(release.getTag().equals(tagRelease))return null;
        }
        return releases;
    }

    public void update(File datapack, List<Release> list) throws IOException {
        JarFile jar = new JarFile(datapack);
        int release = 1;
        for(; release < list.size(); release++)
            if(list.get(release).getTag().equals(tagRelease) && (list.get(release).isPrerelease() && preRelease))break;
        if(release == 1){
            Release rlz = null;
            for(Release rel : list){
                if(rel.isPrerelease())
                    if(!preRelease)continue;
                rlz = rel;
                break;
            }
            if(rlz == null)throw new Error("шта");
            for(GitHubAsset asset : rlz.getAssets()){
                if(asset.getName().equals("client.jar"))
                    FileIO.writeBytes(datapack, new Request(asset.getPath(), Method.GET).execute((int)asset.getSize()));
            }
        }

        for(; release > -1; release--){
            Release r = list.get(release);
            if(!preRelease && r.isPrerelease())continue;
            for(GitHubAsset asset : r.getAssets()){
                if(asset.getName().equals("update.patch")){
                    byte patch[] = new Request(asset.getPath(), Method.GET).execute((int)asset.getSize());
                    update(jar, new SignedUpdate(SlowDecoder.defaultDecoder(patch)));
                    break;
                }
            }
        }
        jar.writeToJar(datapack);
    }

    public boolean update(JarFile file, SignedUpdate signed){
        Update update = signed.getUpdate();
        if(!signed.check(key)){
            Log.MAIN.info("Update can't write");
            return false;
        }
        update.writeTo(file);
        return true;
    }
}

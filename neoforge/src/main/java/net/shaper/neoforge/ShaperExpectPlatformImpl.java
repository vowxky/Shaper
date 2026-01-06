package net.shaper.neoforge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ShaperExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}

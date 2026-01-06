package net.shaper.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ShapesReader extends SimplePreparableReloadListener<Void> {
    public ShapesReader() {}

    @Override
    protected Void prepare(ResourceManager rm, ProfilerFiller pf) { return null; }

    @Override
    protected void apply(Void prep, ResourceManager rm, ProfilerFiller pf) {
        ShaperAPI.clear();

        Map<ResourceLocation, Resource> files = rm.listResources("", rl -> rl.getPath().endsWith(".json"));

        for (Map.Entry<ResourceLocation, Resource> e : files.entrySet()) {
            ResourceLocation fileLoc = e.getKey();
            if (!fileLoc.getNamespace().equals(ShaperAPI.NAMESPACE)) continue;

            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(e.getValue().open(), StandardCharsets.UTF_8))) {

                JsonElement root = JsonParser.parseReader(r);
                if (!root.isJsonArray()) continue;

                VoxelShape shape = ShaperAPI.parseBoxes(root.getAsJsonArray());

                String path = fileLoc.getPath();
                String name = path.substring(0, path.length() - 5);

                ResourceLocation publicId = ResourceLocation.fromNamespaceAndPath(ShaperAPI.NAMESPACE, name);
                ShaperAPI.put(publicId, shape);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
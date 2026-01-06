package net.shaper.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class ShaperAPI {
    private static final Map<ResourceLocation, VoxelShape> CACHE = new ConcurrentHashMap<>();
    public static final String NAMESPACE = "shaper";

    private ShaperAPI() {}

    public static VoxelShape get(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, name);
        return CACHE.getOrDefault(id, Shapes.empty());
    }

    public static void clear() { CACHE.clear(); }

    public static void put(ResourceLocation id, VoxelShape shape) {
        CACHE.put(id, shape);
    }

    public static void putFromJson(String name, JsonElement json) {
        if (!json.isJsonArray()) return;
        VoxelShape shape = parseBoxes(json.getAsJsonArray());
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(NAMESPACE, name);
        CACHE.put(id, shape);
    }

    public static VoxelShape parseBoxes(JsonArray arr) {
        VoxelShape out = Shapes.empty();
        for (JsonElement el : arr) {
            if (!el.isJsonArray()) continue;
            JsonArray a = el.getAsJsonArray();
            if (a.size() != 6) continue;

            double x1=a.get(0).getAsDouble(), y1=a.get(1).getAsDouble(), z1=a.get(2).getAsDouble();
            double x2=a.get(3).getAsDouble(), y2=a.get(4).getAsDouble(), z2=a.get(5).getAsDouble();
            if (x1 > x2 || y1 > y2 || z1 > z2) continue;

            out = Shapes.or(out, Shapes.box(x1, y1, z1, x2, y2, z2));
        }
        return out;
    }
}
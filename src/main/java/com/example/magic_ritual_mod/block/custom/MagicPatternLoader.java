package com.example.magic_ritual_mod.block.custom;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class MagicPatternLoader {

    private static final Gson GSON = new Gson();

    public static double[][] load(ResourceManager manager, ResourceLocation id) {
        try {
            var resource = manager.getResource(id).orElseThrow();

            try (InputStreamReader reader = new InputStreamReader(resource.open())) {

                Type type = new TypeToken<List<List<Double>>>() {}.getType();
                List<List<Double>> raw = GSON.fromJson(reader, type);

                double[][] result = new double[raw.size()][2];

                for (int i = 0; i < raw.size(); i++) {
                    result[i][0] = raw.get(i).get(0);
                    result[i][1] = raw.get(i).get(1);
                }

                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new double[0][0];
        }
    }
}
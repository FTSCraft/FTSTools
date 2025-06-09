package de.ftscraft.ftstools.environments;

import java.util.HashMap;
import java.util.Map;

public class CraftingEnvManager {

    private static final Map<String, CraftingEnvironment> craftingEnvironments = new HashMap<>();

    public static CraftingEnvironment getCraftingEnv(String id) {
        return craftingEnvironments.get(id);
    }

    public static void addCraftingEnv(String id, CraftingEnvironment env) {
        craftingEnvironments.put(id, env);
    }

}

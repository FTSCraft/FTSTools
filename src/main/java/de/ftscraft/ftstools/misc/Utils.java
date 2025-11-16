package de.ftscraft.ftstools.misc;

import java.util.List;

public class Utils {

    public static final String MM_PREFIX = "<gray>[<gradient:green:blue>FTS-Tools</gradient>] ";

    public static final String PDC_OPEN_CRAFTING_ENV = "open-crafting-env";
    public static final String PDC_CRAFTING_ENVS = "crafting-envs";

    public static String transformStringListToString(List<String> strings) {
        StringBuilder builder = new StringBuilder("[");
        for (String string : strings) {
            builder.append(string).append(',');
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(']');
        return builder.toString();
    }

}

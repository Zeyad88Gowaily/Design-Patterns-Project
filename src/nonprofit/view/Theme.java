package nonprofit.view;

import java.util.HashMap;
import java.util.Map;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

/** Look and feel of the Swing windows: a flat theme with a teal accent, light or dark. */
public final class Theme {
    private static boolean dark;

    private Theme() {
    }

    /** Call once before creating any window. */
    public static void install() {
        Map<String, String> defaults = new HashMap<String, String>();
        defaults.put("@accentColor", "#1F8A70");
        defaults.put("Component.arc", "10");
        defaults.put("Button.arc", "10");
        defaults.put("TextComponent.arc", "8");
        defaults.put("TabbedPane.showTabSeparators", "true");
        defaults.put("TabbedPane.tabHeight", "38");
        defaults.put("TabbedPane.tabInsets", "4,16,4,16");
        defaults.put("ScrollBar.thumbArc", "999");
        defaults.put("ScrollBar.width", "12");
        FlatLaf.setGlobalExtraDefaults(defaults);
        FlatLightLaf.setup();
    }

    public static boolean isDark() {
        return dark;
    }

    /** Switches between light and dark and repaints every open window. */
    public static void toggle() {
        dark = !dark;
        if (dark) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        FlatLaf.updateUI();
    }
}

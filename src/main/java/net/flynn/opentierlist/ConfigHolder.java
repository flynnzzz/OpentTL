package net.flynn.opentierlist;

public class ConfigHolder {

    public final static int DEFAULT_CELL_SIZE = 64 + 32;

    public final static int DEFAULT_TIERED_BAR_WIDTH = (8 + 1) * DEFAULT_CELL_SIZE + 2;
    public final static int DEFAULT_UNTIERED_BAR_WIDTH = (8 + 1) * DEFAULT_CELL_SIZE + (8 + 4);

    public final static int DEFAULT_BAR_MIN_HEIGHT = DEFAULT_CELL_SIZE;
    public final static double DEFAULT_UNRANKED_PANE_HEIGHT = DEFAULT_CELL_SIZE * 1.6;

    public final static double DEFAULT_EXPANDED_IMAGE_SIZE = DEFAULT_CELL_SIZE * (0.8 + 0.4);
    public final static int DEFAULT_TIERS_VBOX_PADDING = 8;

    public final static String DEFAULT_BAR_BORDER_COLOR = "#696969";
    public final static String DEFAULT_BAR_HIGHLIGHT_COLOR = "#00bfff";

    public final static int COLOR_MENU_WIDTH = 256;
    public final static int COLOR_MENU_HEIGHT = 128 + 32;
    public final static int COLOR_PADDING_TOP = 4 + 1,
            COLOR_PADDING_RIGHT = 16 + 4,
            COLOR_PADDING_BOTTOM = 4 + 1,
            COLOR_PADDING_LEFT = 16 + 4;
    public final static int COLOR_SPACING = 16;

    public enum Theme {
        LIGHT, DARK
    }

    private static Theme currentTheme = Theme.LIGHT;

    public static void setCurrentTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public final static double SCREENSHOT_WIDTH = DEFAULT_TIERED_BAR_WIDTH + 128 + DEFAULT_CELL_SIZE;

    public static final String DEFAULT_ACCENT_COLOR_LIGHT = "#dadfe7";
    public static final String DEFAULT_ACCENT_COLOR_DARK = "#3b4252";

    public static final String DEFAULT_S_COLOR = "#bf616a";
    public static final String DEFAULT_A_COLOR = "#d08770";
    public static final String DEFAULT_B_COLOR = "#ebcb8b";
    public static final String DEFAULT_C_COLOR = "#a3be8c";
    public static final String DEFAULT_D_COLOR = "#88c0d0";
    public static final String DEFAULT_E_COLOR = "#81a1c1";
    public static final String DEFAULT_F_COLOR = "#434c5e";
    public static final String DEFAULT_UNTIERED_COLOR = "#ffffff";
    public static final String DEFAULT_NEW_TIER_COLOR = "#808080";

    public final static int DEFAULT_UNRANKED_PADDING_TOP = 16;
    public final static int DEFAULT_UNRANKED_PADDING_RIGHT = 16;
    public final static int DEFAULT_UNRANKED_PADDING_BOTTOM = 16;
    public final static int DEFAULT_UNRANKED_PADDING_LEFT = 16;

    public final static int DEFAULT_TIER_SPACING = 8;
    public final static int DEFAULT_TIER_PADDING_TOP = 8;
    public final static int DEFAULT_TIER_PADDING_RIGHT = 8;
    public final static int DEFAULT_TIER_PADDING_BOTTOM = 8;
    public final static int DEFAULT_TIER_PADDING_LEFT = 8;

    public final static int DEFAULT_TITLE_PADDING_TOP = 16;
    public final static int DEFAULT_TITLE_PADDING_RIGHT = 16;
    public final static int DEFAULT_TITLE_PADDING_BOTTOM = 16;
    public final static int DEFAULT_TITLE_PADDING_LEFT = 16;

    public final static int DEFAULT_BUTTON_PADDING = 8;
    public final static double DEFAULT_BUTTON_SPACING = DEFAULT_BUTTON_PADDING / 1.6;
}

package util.icons;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;
import java.util.Locale;

/**
 * Иконки GtT плагина
 * Created by v.nosov on 20.06.2017.
 */
public class GtTIcons {
    public static final Icon ICON_GTT = IconLoader.getIcon("/icons/gherkin.png");

    private GtTIcons() {
        // only static
    }

    public static Icon icon(String name) {
        String nameWithExtension = name + ".png";
        return IconLoader.getIcon("/images/" + nameWithExtension);
    }

    public static Icon toDisabled(Icon icon) {
        return IconLoader.getDisabledIcon(icon);
    }

    public static Icon priority12(String priority) {
        return priority(priority + "12");
    }

    public static Icon priority(String priority) {
        String name = priority.toLowerCase(Locale.ENGLISH) + ".png";
        return IconLoader.getIcon("/icons/priority/" + name);
    }
}
package util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import exceptions.GtTOperationCanceledException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import static util.GtTUtil.getErrorTextFromException;

/**
 * Класс с методами для генерации и вывода нотификаций
 * Created by v.nosov on 08.06.2017.
 */
public class GtTNotifications {

    private static boolean isOperationCanceled(@NotNull Exception e) {
        return e instanceof GtTOperationCanceledException ||
                e instanceof ProcessCanceledException;
    }

    public static void showInfoDialog(@NotNull Component component, @NotNull String title, @NotNull String message) {
        //todo LOG.info(title + "; " + message);
        Messages.showInfoMessage(component, message, title);
    }

    public static void showErrorDialog(@Nullable Project project, @NotNull String title, @NotNull Exception e) {
        //todo  LOG.warn(title, e);
        if (isOperationCanceled(e)) return;
        Messages.showErrorDialog(project, getErrorTextFromException(e), title);
    }

    public static void showErrorDialog(@NotNull Component component, @NotNull String title, @NotNull Exception e) {
        //todo LOG.info(title, e);
        if (isOperationCanceled(e)) return;
        Messages.showErrorDialog(component, getErrorTextFromException(e), title);
    }

    public static void showErrorDialog(@NotNull Component component, @NotNull String title, @NotNull String prefix, @NotNull Exception e) {
        //todo LOG.info(title, e);
        if (isOperationCanceled(e)) return;
        Messages.showErrorDialog(component, prefix + getErrorTextFromException(e), title);
    }

    public static void notify(String header, String text, NotificationType type) {
        Notifications.Bus.notify(new Notification("GtT", header, text, type));
    }
}

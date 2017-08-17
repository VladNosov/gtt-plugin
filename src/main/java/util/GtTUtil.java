package util;

import api.testrail.TestRailAPIUtil;
import api.testrail.TestRailConnection;
import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.DisposeAwareRunnable;
import com.intellij.util.ThrowableConvertor;
import exceptions.GtTAuthenticationException;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by v.nosov on 08.06.2017.
 */
public class GtTUtil {

    /**
     *Подождать, пока идея проинициализирует проект, а затем выполнить действие
     */
    public static void runWhenInitialized(final Project project, final Runnable r) {
        if (project.isDisposed()) return;

        if (!project.isInitialized()) {
            StartupManager.getInstance(project).registerPostStartupActivity(DisposeAwareRunnable.create(r, project));
            return;
        }

        runDumbAware(project, r);
    }

    public static void runDumbAware(final Project project, final Runnable r) {
        if (DumbService.isDumbAware(r)) {
            r.run();
        }
        else {
            DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
        }
    }

    @NotNull
    public static String getErrorTextFromException(@NotNull Exception e) {
        if (e instanceof UnknownHostException) {
            return "Unknown host: " + e.getMessage();
        }
        return StringUtil.notNullize(e.getMessage(), "Unknown error");
    }

    /**
     * Проверяем данные для авторизации
     */
    @NotNull
    public static Object checkAuthData(@NotNull GtTAuthData authData, @NotNull ProgressIndicator indicator) throws IOException {
        if (StringUtil.isEmptyOrSpaces(authData.getHost())) {
            throw new GtTAuthenticationException("Target host not defined");
        }

        try {
            new URI(authData.getHost());
        }
        catch (URISyntaxException e) {
            throw new GtTAuthenticationException("Invalid host URL");
        }

        switch (authData.getAuthType()) {
            case BASIC:
                GtTAuthData.BasicAuth basicAuth = authData.getBasicAuth();
                assert basicAuth != null;
                if (StringUtil.isEmptyOrSpaces(basicAuth.getLogin()) || StringUtil.isEmptyOrSpaces(basicAuth.getPassword())) {
                    throw new GtTAuthenticationException("Empty login or password");
                }
                break;
            case ANONYMOUS:
                throw new GtTAuthenticationException("Anonymous connection not allowed");
        }

        return testConnection(authData, indicator);
    }

    public static <T> T computeValueInModalIO(@NotNull Project project,
                                              @NotNull String caption,
                                              @NotNull final ThrowableConvertor<ProgressIndicator, T, IOException> task) throws IOException {
        return ProgressManager.getInstance().run(new Task.WithResult<T, IOException>(project, caption, true) {
            @Override
            protected T compute(@NotNull ProgressIndicator indicator) throws IOException {
                return task.convert(indicator);
            }
        });
    }

    //======================================== private methods =========================================================

    @NotNull
    private static Object testConnection(@NotNull GtTAuthData auth,
                                         @NotNull final ProgressIndicator indicator) throws IOException {
        ScheduledFuture<?> future = null;

        try(final TestRailConnection connection = new TestRailConnection(auth, true)) {
            future = addCancellationListener(indicator, connection);
            return TestRailAPIUtil.getProjectList(connection);
        }
        finally {
            if (future != null) future.cancel(true);
        }
    }

    @NotNull
    private static ScheduledFuture<?> addCancellationListener(@NotNull Runnable run) {
        return JobScheduler.getScheduler().scheduleWithFixedDelay(run, 1000, 300, TimeUnit.MILLISECONDS);
    }

    @NotNull
    private static ScheduledFuture<?> addCancellationListener(@NotNull final ProgressIndicator indicator,
                                                              @NotNull final TestRailConnection connection) {
        return addCancellationListener(() -> {
            if (indicator.isCanceled()) connection.abort();
        });
    }
}
package util;

import api.testrail.TestRailAPIUtil;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.GtTAuthData.AuthType;

/**
 * Класс для работы с настройками плагина (сохранение/загрузка)
 * Created by v.nosov on 07.06.2017.
 */
@State(name = "GtTSettings", storages = @Storage("gtt_settings.xml"))
public class GtTSettings implements PersistentStateComponent<GtTSettings.State> {
    private static final String GTT_SETTINGS_PASSWORD_KEY = "GTT_SETTINGS_PASSWORD_KEY";

    State myState = new State();

    /**
     * вызывается каждый раз при попытке сохранить настройки
     */
    public State getState() {
        return myState;
    }


    /**
     * вызывается каждый раз при создании компонента или изменении xml извне
     */
    public void loadState(State state) {
        myState = state;
    }

    public static GtTSettings getInstance() {
        return ServiceManager.getService(GtTSettings.class);
    }

    //=================================== get and set ==================================================================

    private static boolean isValidGitAuth(@NotNull GtTAuthData auth) {
        switch (auth.getAuthType()) {
            case BASIC:
                assert auth.getBasicAuth() != null;
                return true;
            case ANONYMOUS:
                return false;
            default:
                throw new IllegalStateException("GtTSettings: setAuthData - wrong AuthType: " + auth.getAuthType());
        }
    }

    @NotNull
    public GtTAuthData getAuthData() {
        switch (getAuthType()) {
            case BASIC:
                //noinspection ConstantConditions
                return GtTAuthData.createBasicAuth(getHost(), getLogin(), getPassword());
            case ANONYMOUS:
                return GtTAuthData.createAnonymous(getHost());
            default:
                throw new IllegalStateException(String.format("GtTSettings: getAuthData - wrong AuthType: %s", getAuthType()));
        }
    }

    @NotNull
    public String getHost() {
        return myState.HOST;
    }

    @Nullable
    public String getLogin() {
        return myState.LOGIN;
    }

    @NotNull
    private String getPassword() {
        //todo переписать потом
        return StringUtil.notNullize(PasswordSafe.getInstance().getPassword(GtTSettings.class, GTT_SETTINGS_PASSWORD_KEY));
    }

    @NotNull
    public AuthType getAuthType() {
        return myState.AUTH_TYPE;
    }

    public int getConnectionTimeout() {
        return myState.CONNECTION_TIMEOUT;
    }

    public void setConnectionTimeout(int timeout) {
        myState.CONNECTION_TIMEOUT = timeout;
    }

    /**
     * если подключиться удалось - true
     */
    public boolean getValidTestRailAuth() {
        return myState.VALID_TESTRAIL_AUTH;
    }

    private void setHost(@NotNull String host) {
        myState.HOST = StringUtil.notNullize(host, TestRailAPIUtil.DEFAULT_TESTRAIL_HOST);
    }

    private void setLogin(@Nullable String login) {
        myState.LOGIN = login;
    }

    private void setPassword(@NotNull String password, boolean rememberPassword) {
        if (!rememberPassword) return;
        PasswordSafe.getInstance().setPassword(GtTSettings.class, GTT_SETTINGS_PASSWORD_KEY, password);
    }

    private void setAuthType(@NotNull AuthType authType) {
        myState.AUTH_TYPE = authType;
    }

    public void setAuthData(@NotNull GtTAuthData auth, boolean rememberPassword) {
        setValidTestRailAuth(isValidGitAuth(auth));

        setAuthType(auth.getAuthType());
        setHost(auth.getHost());

        switch (auth.getAuthType()) {
            case BASIC:
                assert auth.getBasicAuth() != null;
                setLogin(auth.getBasicAuth().getLogin());
                setPassword(auth.getBasicAuth().getPassword(), rememberPassword);
                break;
            case ANONYMOUS:
                setLogin("");
                setPassword("", rememberPassword);
                break;
            default:
                throw new IllegalStateException(String.format("GtTSettings: setAuthData - wrong AuthType: %s", auth.getAuthType()));
        }
    }

    /**
     * если подключиться удалось - true
     */
    public void setValidTestRailAuth(final boolean validTestRailAuth) {
        myState.VALID_TESTRAIL_AUTH = validTestRailAuth;
    }

    //==================================================================================================================

    public static class State {
        @NotNull public String HOST = TestRailAPIUtil.DEFAULT_TESTRAIL_HOST;
        @Nullable public String LOGIN = null;
        @NotNull public AuthType AUTH_TYPE = AuthType.ANONYMOUS;
        public int CONNECTION_TIMEOUT = 5000;
        public boolean VALID_TESTRAIL_AUTH = true;
    }
}

package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TO для коннекшна к TestRail
 * Created by v.nosov on 07.06.2017.
 */
public class GtTAuthData {
    public enum AuthType {BASIC, ANONYMOUS}

    @NotNull private final AuthType myAuthType;
    @NotNull private final String myHost;
    @Nullable private final BasicAuth myBasicAuth;

    private GtTAuthData(@NotNull AuthType authType,
                        @NotNull String host,
                        @Nullable BasicAuth basicAuth) {
        myAuthType = authType;
        myHost = host;
        myBasicAuth = basicAuth;
    }

    //=========================== get and set ==========================================================================

    @NotNull
    public AuthType getAuthType() {
        return myAuthType;
    }

    @NotNull
    public String getHost() {
        return myHost;
    }

    @Nullable
    public BasicAuth getBasicAuth() {
        return myBasicAuth;
    }

    //==================================================================================================================


    public static GtTAuthData createBasicAuth(@NotNull String host, @NotNull String login, @NotNull String password) {
        return new GtTAuthData(AuthType.BASIC, host, new BasicAuth(login, password));
    }

    public static GtTAuthData createAnonymous(@NotNull String host) {
        return new GtTAuthData(AuthType.ANONYMOUS, host, new BasicAuth("", ""));
    }

    /**
     * Тип подключения - базовая авторизация по логин/пароль
     */
    public static class BasicAuth {
        @NotNull private final String myLogin;
        @NotNull private final String myPassword;

        private BasicAuth(@NotNull String login, @NotNull String password) {
            myLogin = login;
            myPassword = password;
        }

        @NotNull
        public String getLogin() {
            return myLogin;
        }

        @NotNull
        public String getPassword() {
            return myPassword;
        }
    }
}

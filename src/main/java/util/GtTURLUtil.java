package util;

import api.testrail.TestRailAPIUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Util-класс для работы с URL
 * Created by v.nosov on 07.06.2017.
 */
public class GtTURLUtil {

    /**
     * Получаем финальный урл для коннекта к API
     */
    @NotNull
    public static String getApiUrl(@NotNull String urlFromSettings) {
        return getApiProtocolFromUrl(urlFromSettings) + getApiUrlWithoutProtocol(urlFromSettings);
    }

    /**
     * https://host.com/suffix/ -> https://
     */
    @NotNull
    public static String getApiProtocolFromUrl(@NotNull String urlFromSettings) {
        if (StringUtil.startsWithIgnoreCase(urlFromSettings.trim(), "http://")) return "http://";
        return "https://";
    }

    /**
     * https://host.com/suffix/ -> host.com/suffix/index.php?/api/v2/
     */
    @NotNull
    public static String getApiUrlWithoutProtocol(@NotNull String urlFromSettings) {
        String url = removeTrailingSlash(removeProtocolPrefix(urlFromSettings.toLowerCase()));
        url = removeProtocolPrefix(url);
        final String ENTERPRISE_API_SUFFIX = "/index.php?/api/v2/";

        if (url.equals(TestRailAPIUtil.DEFAULT_TESTRAIL_HOST + ENTERPRISE_API_SUFFIX)) {
            return url;
        } else {
            return url + ENTERPRISE_API_SUFFIX;
        }
    }

    /**
     * https://host.com/suffix/ -> https://host.com/suffix
     */
    @NotNull
    public static String removeTrailingSlash(@NotNull String s) {
        if (s.endsWith("/")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * https://usr:pswd@host.com/suffix/ -> host.com/suffix/
     */
    @NotNull
    public static String removeProtocolPrefix(String url) {
        int index = url.indexOf('@');
        if (index != -1) {
            return url.substring(index + 1).replace(':', '/');
        }
        index = url.indexOf("://");
        if (index != -1) {
            return url.substring(index + 3);
        }
        return url;
    }
}

package api.testrail;

import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.jetbrains.annotations.NotNull;
import util.GtTAuthData;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс-билдер коннекшна к TestRail:
 * задает тайминг для подключения, добавляет креденшны в хидер
 * Created by v.nosov on 07.06.2017.
 */
public class TestRailConnectionBuilder {
    @NotNull private final GtTAuthData myAuth;
    @NotNull private final String myApiURL;

    public TestRailConnectionBuilder(@NotNull GtTAuthData auth, @NotNull String apiURL) {
        myAuth = auth;
        myApiURL = apiURL;
    }

    @NotNull
    public CloseableHttpClient createClient() {
        HttpClientBuilder builder = HttpClients.custom();

        builder.setDefaultRequestConfig(createRequestConfig())
                .setDefaultConnectionConfig(createConnectionConfig())
                .setDefaultHeaders(createHeaders());
                //todo удалить, если не нужно .setSslcontext(CertificateManager.getInstance().getSslContext());

        return builder.build();
    }

    /**
     * задаем настройки подключения
     */
    @NotNull
    private RequestConfig createRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();

        int timeout = 3333; //todo заглушка, вернуть после отладки GtTSettings.getInstance().getConnectionTimeout();
        builder.setConnectTimeout(timeout)
                .setSocketTimeout(timeout);

        return builder.build();
    }

    @NotNull
    private Collection<? extends Header> createHeaders() {
        List<Header> headers = new ArrayList<>();
        GtTAuthData.BasicAuth basicAuth = myAuth.getBasicAuth();
        if (basicAuth != null) {
            String encodedAuth  = TestRailAPIUtil.encodedToString(
                    String.format("%s:%s", basicAuth.getLogin(), basicAuth.getPassword())
            );
            headers.add(new BasicHeader("Authorization", "Basic " + encodedAuth));
        }
        return headers;
    }

    @NotNull
    private ConnectionConfig createConnectionConfig() {
        return ConnectionConfig.custom()
                .setCharset(Consts.UTF_8)
                .build();
    }

    @NotNull
    private AuthScope getBasicAuthScope() {
        try {
            URIBuilder builder = new URIBuilder(myApiURL);
            return new AuthScope(builder.getHost(), builder.getPort(), AuthScope.ANY_REALM, AuthSchemes.BASIC);
        }
        catch (URISyntaxException e) {
            return AuthScope.ANY;
        }
    }

    private static class PreemptiveBasicAuthInterceptor implements HttpRequestInterceptor {
        @NotNull private final AuthScope myBasicAuthScope;

        public PreemptiveBasicAuthInterceptor(@NotNull AuthScope basicAuthScope) {
            myBasicAuthScope = basicAuthScope;
        }

        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            CredentialsProvider provider = (CredentialsProvider)context.getAttribute(HttpClientContext.CREDS_PROVIDER);
            Credentials credentials = provider.getCredentials(myBasicAuthScope);
            if (credentials != null) {
                request.addHeader(new BasicScheme(Consts.UTF_8).authenticate(credentials, request, context));
            }
        }
    }
}

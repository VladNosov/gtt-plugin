package api.testrail;

import api.testrail.data.TestRailErrorMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.CharsetToolkit;
import exceptions.*;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.GtTAuthData;
import util.GtTURLUtil;
import javax.net.ssl.SSLHandshakeException;
import java.awt.*;
import java.io.*;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.List;
import static api.testrail.TestRailAPIUtil.fromJson;

/**
 * API Testrail
 * Created by v.nosov on 07.06.2017.
 */
public class TestRailConnection implements Closeable {

    @NotNull private final String myApiURL;
    @NotNull private final CloseableHttpClient myClient;
    private final boolean myReusable;

    private volatile HttpUriRequest myRequest;
    private volatile boolean myAborted;

    public TestRailConnection(@NotNull GtTAuthData auth, boolean reusable) {
        myApiURL = GtTURLUtil.getApiUrl(auth.getHost());
        myClient = new TestRailConnectionBuilder(auth, myApiURL).createClient();
        myReusable = reusable;
    }

    @NotNull
    String getApiURL() {
        return myApiURL;
    }

    public void close() throws IOException {
        myClient.close();
    }

    /**
     * Прерываем попытку отправить запрос
     */
    public void abort() {
        if (myAborted) return;
        myAborted = true;

        HttpUriRequest request = myRequest;
        if (request != null) request.abort();
    }

    //============================================ requests ============================================================

    /**
     * Отправить GET запрос
     */
    @Nullable
    public JsonElement getRequest(@NotNull String path, @NotNull Header... headers) throws IOException {
        return request(path, null, Arrays.asList(headers), HttpVerb.GET).getJsonElement();
    }

    //======================================= private methods ==========================================================

    @NotNull
    private ResponsePage request(@NotNull String path, @Nullable String requestBody, @NotNull Collection<Header> headers,
                                 @NotNull HttpVerb verb) throws IOException {
        return doRequest(myApiURL + path, requestBody, headers, verb);
    }

    @NotNull
    private ResponsePage doRequest(@NotNull String uri, @Nullable String requestBody, @NotNull Collection<Header> headers,
                                   @NotNull HttpVerb verb) throws IOException {
        if (myAborted) throw new GtTOperationCanceledException();

        if (EventQueue.isDispatchThread() && !ApplicationManager.getApplication().isUnitTestMode()) {
           // LOG.warn("Network operation in EDT"); // TODO: fix
        }

        CloseableHttpResponse response = null;
        try {
            response = doREST(uri, requestBody, headers, verb);

            if (myAborted) throw new GtTOperationCanceledException();

            checkStatusCode(response, requestBody);

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return createResponse(response);
            }

            JsonElement ret = parseResponse(entity.getContent());
            if (ret.isJsonNull()) {
                return createResponse(response);
            }

            String nextPage = null;
            Header pageHeader = response.getFirstHeader("Link");
            if (pageHeader != null) {
                for (HeaderElement element : pageHeader.getElements()) {
                    NameValuePair rel = element.getParameterByName("rel");
                    if (rel != null && "next".equals(rel.getValue())) {
                        String urlString = element.toString();
                        int begin = urlString.indexOf('<');
                        int end = urlString.lastIndexOf('>');
                        if (begin == -1 || end == -1) {
                            //todo LOG.error("Invalid 'Link' header", "{" + pageHeader.toString() + "}");
                            break;
                        }

                        nextPage = urlString.substring(begin + 1, end);
                        break;
                    }
                }
            }

            return createResponse(ret, nextPage, response);
        }
        catch (SSLHandshakeException e) { // User canceled operation from CertificateManager
            if (e.getCause() instanceof CertificateException) {
                //todo LOG.info("Host SSL certificate is not trusted", e);
                throw new GtTOperationCanceledException("Host SSL certificate is not trusted", e);
            }
            throw e;
        }
        catch (IOException e) {
            if (myAborted) throw new GtTOperationCanceledException("Operation canceled", e);
            throw e;
        }
        finally {
            myRequest = null;
            if (response != null) {
                response.close();
            }
            if (!myReusable) {
                myClient.close();
            }
        }
    }

    @NotNull
    private CloseableHttpResponse doREST(@NotNull final String uri, @Nullable final String requestBody,
                                         @NotNull final Collection<Header> headers, @NotNull final HttpVerb verb) throws IOException {
        HttpRequestBase request;
        switch (verb) {
            case POST:
                request = new HttpPost(uri);
                if (requestBody != null) {
                    ((HttpPost)request).setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
                }
                break;
            case GET:
                request = new HttpGet(uri);
                break;
            default:
                throw new IllegalStateException("Unknown HttpVerb: " + verb.toString());
        }

        for (Header header : headers) {
            request.addHeader(header);
        }

        myRequest = request;
        return myClient.execute(request);
    }

    //-------------------------------------- работа с response ---------------------------------------------------------

    private ResponsePage createResponse(@NotNull CloseableHttpResponse response) throws GtTOperationCanceledException {
        if (myAborted) throw new GtTOperationCanceledException();

        return new ResponsePage(null, null, response.getAllHeaders());
    }

    private ResponsePage createResponse(@NotNull JsonElement ret, @Nullable String path, @NotNull CloseableHttpResponse response)
            throws GtTOperationCanceledException {
        if (myAborted) throw new GtTOperationCanceledException();

        return new ResponsePage(ret, path, response.getAllHeaders());
    }

    @NotNull
    private static JsonElement parseResponse(@NotNull InputStream githubResponse) throws IOException {
        Reader reader = new InputStreamReader(githubResponse, CharsetToolkit.UTF8_CHARSET);
        try {
            return new JsonParser().parse(reader);
        }
        catch (JsonParseException jse) {
            throw new GtTJsonException("Couldn't parse GitHub response", jse);
        }
        finally {
            reader.close();
        }
    }

    //--------------------------------------- status code --------------------------------------------------------------

    private static void checkStatusCode(@NotNull CloseableHttpResponse response, @Nullable String body) throws IOException {
        int code = response.getStatusLine().getStatusCode();
        switch (code) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
            case HttpStatus.SC_ACCEPTED:
            case HttpStatus.SC_NO_CONTENT:
                return;
            case HttpStatus.SC_UNAUTHORIZED:
            case HttpStatus.SC_PAYMENT_REQUIRED:
            case HttpStatus.SC_FORBIDDEN:
                //noinspection ThrowableResultOfMethodCallIgnored
                GtTStatusCodeException error = getStatusCodeException(response);

                if (error.getError() != null && error.getError().containsReasonMessage("API rate limit exceeded")) {
                    //todo throw new GithubRateLimitExceededException(error.getMessage());
                }

                throw new GtTAuthenticationException("Request response: " + error.getMessage());
            case HttpStatus.SC_BAD_REQUEST:
            case HttpStatus.SC_UNPROCESSABLE_ENTITY:
                //todo LOG.info("body message:" + body);
                throw getStatusCodeException(response);
            default:
                throw getStatusCodeException(response);
        }
    }

    /**
     * Возвращает error message по статус коду
     */
    @NotNull
    private static GtTStatusCodeException getStatusCodeException(@NotNull CloseableHttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reason = statusCode + " " + statusLine.getReasonPhrase();
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                TestRailErrorMessage error = fromJson(parseResponse(entity.getContent()), TestRailErrorMessage.class);
                String message = reason + " - " + error.getMessage();
                return new GtTStatusCodeException(message, error, statusCode);
            }
        }
        catch (IOException e) {
            //todo LOG.info(e);
        }
        return new GtTStatusCodeException(reason, statusCode);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Тип запроса
     */
    private enum HttpVerb {
        GET, POST
    }

    /**
     * Класс с ответом из одного объекта
     */
    private static class ResponsePage {
        @Nullable private final JsonElement myResponse;
        @Nullable private final String myNextPage;
        @NotNull private final Header[] myHeaders;

        public ResponsePage(@Nullable JsonElement response, @Nullable String next, @NotNull Header[] headers) {
            myResponse = response;
            myNextPage = next;
            myHeaders = headers;
        }

        @Nullable
        public JsonElement getJsonElement() {
            return myResponse;
        }

        @Nullable
        public String getNextPage() {
            return myNextPage;
        }

        @NotNull
        public Header[] getHeaders() {
            return myHeaders;
        }
    }

    /**
     * Класс с ответом из массива объектов
     */
    public static class ArrayPagedRequest<T> extends PagedRequestBase<T> {
        @NotNull private final Class<? extends T[]> myTypeArray;

        public ArrayPagedRequest(@NotNull String path,
                                 @NotNull Class<? extends T[]> typeArray,
                                 @NotNull Header... headers) {
            super(path, headers);
            myTypeArray = typeArray;
        }

        @Override
        protected List<T> parse(@NotNull JsonElement response) throws IOException {
            if (!response.isJsonArray()) {
                throw new GtTJsonException("Wrong json type: expected JsonArray", new Exception(response.toString()));
            }

            T[] result = fromJson(response.getAsJsonArray(), myTypeArray);
            return Arrays.asList(result);
        }
    }

    public static abstract class PagedRequestBase<T> implements PagedRequest<T> {
        @NotNull private String myPath;
        @NotNull private final Collection<Header> myHeaders;

        private boolean myFirstRequest = true;
        @Nullable private String myNextPage;

        public PagedRequestBase(@NotNull String path, @NotNull Header... headers) {
            myPath = path;
            myHeaders = Arrays.asList(headers);
        }

        @NotNull
        public List<T> next(@NotNull TestRailConnection connection) throws IOException {
            String url;
            if (myFirstRequest) {
                url = connection.getApiURL() + myPath;
                myFirstRequest = false;
            }
            else {
                if (myNextPage == null) throw new NoSuchElementException();
                url = myNextPage;
                myNextPage = null;
            }

            ResponsePage response = connection.doRequest(url, null, myHeaders, HttpVerb.GET);
            myNextPage = response.getNextPage();

            if (response.getJsonElement() == null) {
                throw new GtTConfusingException("Empty response");
            }

            return parse(response.getJsonElement());
        }

        public boolean hasNext() {
            return myFirstRequest || myNextPage != null;
        }

        protected abstract List<T> parse(@NotNull JsonElement response) throws IOException;
    }

    public interface PagedRequest<T> {
        @NotNull
        List<T> next(@NotNull TestRailConnection connection) throws IOException;

        boolean hasNext();

        @NotNull
        default List<T> getAll(@NotNull TestRailConnection connection) throws IOException {
            List<T> result = new ArrayList<>();
            while (hasNext()) {
                result.addAll(next(connection));
            }
            return result;
        }
    }
}

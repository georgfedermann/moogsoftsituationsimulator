package org.poormanscastle.moogsoft;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkState;

public class DataUplink {

    String protocol = "https://";
    String baseUrlString = ".moogsoft.io/events/";

    MoogsoftCredentials credentials = null;
    String moogEngineName = null;
    String webHookName = null;

    final static Logger logger = Logger.getLogger(DataUplink.class);

    public void configureUplink(String moogEngineName, String webHookName, MoogsoftCredentials credentials) {
        this.moogEngineName = moogEngineName;
        this.webHookName = webHookName;
        this.credentials = credentials;
    }

    /**
     * if this DataUplink was configured using the configureUplink method, this method can be used as a shorthand
     * to upload an event to the configured Moogsoft engine.
     *
     * @param event
     * @throws AuthenticationException
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public void sendEvent(Event event) throws AuthenticationException, IOException, UnsupportedEncodingException {
        checkState(credentials != null && !StringUtils.isBlank(moogEngineName) && !StringUtils.isBlank(webHookName),
                "Please configure DataUplink object before using the shortcut method.");
        sendEvent(this.moogEngineName, this.webHookName, this.credentials, event);
    }

    /**
     * @param moogEngineName gets assigned by MoogSoft as part of the registration process.
     *                       It's your subdomain of the moogsoft domain as in
     *                       minky.moogsoft.io, badpush.moogsoft.io or ruderest.moogsoft.io.
     * @param webHookName    is the name you've assigned to your webhook when creating it
     * @param event
     */
    public void sendEvent(String moogEngineName, String webHookName, MoogsoftCredentials credentials, Event event) throws
            AuthenticationException, IOException, UnsupportedEncodingException {
        String url = StringUtils.join(protocol, moogEngineName, baseUrlString, webHookName);
        if (logger.isDebugEnabled()) {
            logger.debug(StringUtils.join("This is the url: ", url));
            logger.debug(StringUtils.join("This is the JSON POST body content: ", event.getJson()));
        }

        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
                RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        HttpPost httpPost = new HttpPost(url);
        StringEntity json = new StringEntity(event.getJson());
        httpPost.setEntity(json);
        httpPost.setHeader("Content-Type", "application/json");
        UsernamePasswordCredentials cred = new UsernamePasswordCredentials(credentials.getUsername(), credentials.getPassword());
        httpPost.addHeader(new BasicScheme().authenticate(cred, httpPost, null));
        CloseableHttpResponse response = client.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            byte[] serverReply = IOUtils.toByteArray(response.getEntity().getContent());
            UniversalDetector encodingDetector = new UniversalDetector(null);
            encodingDetector.handleData(serverReply, 0, serverReply.length);
            encodingDetector.dataEnd();
            String detectedEncoding = encodingDetector.getDetectedCharset();
            if (detectedEncoding != null) {
                logger.warn(StringUtils.join("Statuscode ", statusCode, " for url ", url, " and json ", json,
                        "\nanswer from server encoded in ", detectedEncoding, " is: \n",
                        IOUtils.toString(response.getEntity().getContent(), Charset.forName(detectedEncoding))));
            } else {
                logger.warn(StringUtils.join("Statuscode ", statusCode, " for url ", url, " and json ", json,
                        "\nanswer from server is: \n", new String(serverReply)));
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug(StringUtils.join("Statuscode 200 for url ", url, " and json ", json));
        }
    }

}

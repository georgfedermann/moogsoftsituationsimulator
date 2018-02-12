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

public class DataUplink {

    String protocol = "https://";
    String baseUrlString = ".moogsoft.io/events/";

    final static Logger logger = Logger.getLogger(DataUplink.class);

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
            logger.warn(StringUtils.join("Statuscode ", statusCode, " for url ", url, " and json ", json,
                    "\nanswer from server encoded in ", detectedEncoding, " is: \n",
                    IOUtils.toString(response.getEntity().getContent(), Charset.forName(detectedEncoding))));
        } else if (logger.isDebugEnabled()) {
            logger.debug(StringUtils.join("Statuscode 200 for url ", url, " and json ", json));
        }
    }

    public static void main(String[] args) throws Exception {
        DataUplink uplink = new DataUplink();
        for (int severity = 0; severity < 10; severity++) {
            logger.info(StringUtils.join("Generating 10 events at severity level ", severity));
            for (int loopCount = 0; loopCount < 10; loopCount++) {
                // ruderest, webhook_testwebhook
                // badpush, webhook_webhook_testwebhook
                uplink.sendEvent("badpush", "webhook_webhook_testwebhook",
                        MoogsoftCredentials.getKevinCredentials(), Event.getStandardEventAtSeverityLevel(severity));
                logger.info(StringUtils.join("Sent event # ", loopCount));
                Thread.sleep(5000);
            }
        }
    }
}

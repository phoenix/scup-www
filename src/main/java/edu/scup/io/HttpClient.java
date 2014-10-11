package edu.scup.io;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springside.modules.utils.Encodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Map;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.13 Safari/537.36";
    private static final int socketReadTimeout = 30000;

    public static String getResponse(String url) throws IOException {
        try {
            HttpURLConnection conn = openConnection(url);
            String contentType = conn.getContentType();
            String encoding = "utf-8";
            if (contentType != null && contentType.indexOf("charset=") > 0) {
                encoding = contentType.split("charset=")[1];
            }

            InputStream is = conn.getInputStream();
            byte[] resp = IOUtils.toByteArray(is);
            conn.disconnect();
            return new String(resp, encoding);
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error("", e);
            return "";
        }
    }

    public static String post(String url, Map<String, String> keyValueMap) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> kv : keyValueMap.entrySet()) {
            if (kv.getValue() == null) {
                continue;
            }
            sb.append(Encodes.urlEncode(kv.getKey())).append("=").append(Encodes.urlEncode(kv.getValue())).append("&");
        }
        String body = sb.toString();
        if (body.endsWith("&")) {
            body = body.substring(0, body.length() - 1);
        }
        return post(url, body.getBytes(), null);
    }

    public static String post(String url, byte[] body, String contentType) throws IOException {
        try {
            HttpURLConnection conn = openConnection(url);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            if (contentType != null) {
                conn.setRequestProperty("Content-Type", contentType);
            }
            conn.getOutputStream().write(body);

            if (conn.getResponseCode() != 200) {
                logger.error("response error {},request {}", IOUtils.toString(conn.getErrorStream()), body);
                return "";
            }

            contentType = conn.getContentType();
            String encoding = "utf-8";
            if (contentType != null && contentType.indexOf("charset=") > 0) {
                encoding = contentType.split("charset=")[1];
            }

            InputStream is = conn.getInputStream();
            byte[] resp = IOUtils.toByteArray(is);
            conn.disconnect();
            return new String(resp, encoding);
        } catch (MalformedURLException | URISyntaxException e) {
            logger.error("", e);
            return "";
        }
    }

    public static String getRedirectUrl(String url) throws URISyntaxException, IOException {
        HttpURLConnection conn = openConnection(url);
        conn.setInstanceFollowRedirects(false);
        if (conn.getResponseCode() != 302) {
            return null;
        }
        String result = conn.getHeaderField("Location");
        conn.disconnect();
        return result;
    }

    public static int getContentLength(String httpUrl) throws IOException, URISyntaxException {
        HttpURLConnection conn = openConnection(httpUrl);
        conn.setRequestMethod("HEAD");
        int length = conn.getContentLength();
        conn.disconnect();
        return length;
    }

    public static int downloadFile(String fileUrl, File saveTo) throws IOException, URISyntaxException {
        HttpURLConnection conn = openConnection(fileUrl);
        FileOutputStream out = new FileOutputStream(saveTo);
        int fileSize = IOUtils.copy(conn.getInputStream(), out);
        out.close();
        conn.disconnect();
        return fileSize;
    }

    public static HttpURLConnection openConnection(String url) throws URISyntaxException, IOException {
        URLConnection conn = new URI(url).toURL().openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setReadTimeout(socketReadTimeout);
        return (HttpURLConnection) conn;
    }
}

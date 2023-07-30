package id.tog.oauth2.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class SimpleHttpUtils {
    public static <T> HttpEntity<T> createHeaders(T request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//        headers.add("Accept-Encoding", "gzip");
//        headers.add("Accept-Encoding", "deflate");
        return new HttpEntity<>(request, headers);
    }
}

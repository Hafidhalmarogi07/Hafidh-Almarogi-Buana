package id.tog.oauth2.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SuppressWarnings("SpellCheckingInspection")
@Component
public class PropertiesUtils {
    public static String TZ;

    public static String SPRING_DATASOURCE_URL;

    public static String SPRING_DATASOURCE_USERNAME;

    public static String SPRING_DATASOURCE_PASSWORD;

    public static String SPRING_PROFILES_ACTIVE;

    public static String SPRING_CACHE_TYPE;

    public static String SPRING_REDIS_HOST;

    public static Integer SPRING_REDIS_PORT;

    public static String SPRING_CACHE_REDIS_TIMETOLIVE;

    public static String CDN_PATH;

    public static String CDN_BASEURL;

    public static Boolean SECURITY_JWT_ENABLED;

    public static String SERVER_SERVLET_CONTEXT_PATH;

    public static String MAVEN_CONFIG;

    public static String SPRING_MAIL_HOST;

    public static String SPRING_MAIL_PORT;

    public static String SPRING_MAIL_USERNAME;

    public static String SPRING_MAIL_PASSWORD;

    public static String SPRING_MAIL_SENDER_NAME;

    public static String SPRING_MAIL_SENDER_MAIL;

    public static String SOCKET_IO_URL;

    @Value("${tz:-}")
    public void setT(String value) {
        PropertiesUtils.TZ = value;
    }

    @Value("${spring.dataSource.url:-}")
    public void setSpringDatasourceUrl(String value) {
        PropertiesUtils.SPRING_DATASOURCE_URL = value;
    }

    @Value("${spring.dataSource.username:-}")
    public void setSpringDatasourceUsername(String value) {
        PropertiesUtils.SPRING_DATASOURCE_USERNAME = value;
    }

    @Value("${spring.dataSource.password:-}")
    public void setSpringDatasourcePassword(String value) {
        PropertiesUtils.SPRING_DATASOURCE_PASSWORD = value;
    }

    @Value("${spring.profiles.active:-}")
    public void setSpringProfilesActive(String value) {
        PropertiesUtils.SPRING_PROFILES_ACTIVE = value;
    }

    @Value("${spring.cache.type:-}")
    public void setSpringCacheType(String value) {
        PropertiesUtils.SPRING_CACHE_TYPE = value;
    }

    @Value("${spring.redis.host:-}")
    public void setSpringRedisHost(String value) {
        PropertiesUtils.SPRING_REDIS_HOST = value;
    }

    @Value("${spring.redis.port:-}")
    public void setSpringRedisPort(Integer value) {
        PropertiesUtils.SPRING_REDIS_PORT = value;
    }

    @Value("${spring.cache.redis.timeToLive:-}")
    public void setSpringCacheRedisTimetolive(String value) {
        PropertiesUtils.SPRING_CACHE_REDIS_TIMETOLIVE = value;
    }

    @Value("${cdn.path:-}")
    public void setCdnPath(String value) {
        PropertiesUtils.CDN_PATH = value;
    }

    @Value("${cdn.baseUrl:-}")
    public void setCdnBaseurl(String value) {
        PropertiesUtils.CDN_BASEURL = value;
    }

    @Value("${security.jwt.enabled:-}")
    public void setSecurityJwtEnabled(Boolean value) {
        PropertiesUtils.SECURITY_JWT_ENABLED = value;
    }

    @Value("${server.servlet.context.path:-}")
    public void setServerServletContextPath(String value) {
        PropertiesUtils.SERVER_SERVLET_CONTEXT_PATH = value;
    }

    @Value("${maven.config:-}")
    public void setMavenConfig(String value) {
        PropertiesUtils.MAVEN_CONFIG = value;
    }

    @Value("${spring.mail.host:-}")
    public void setSpringMailHost(String value) {
        PropertiesUtils.SPRING_MAIL_HOST = value;
    }

    @Value("${spring.mail.port:-}")
    public void setSpringMailPort(String value) {
        PropertiesUtils.SPRING_MAIL_PORT = value;
    }

    @Value("${spring.mail.username:-}")
    public void setSpringMailUsername(String value) {
        PropertiesUtils.SPRING_MAIL_USERNAME = value;
    }

    @Value("${spring.mail.password:-}")
    public void setSpringMailPassword(String value) {
        PropertiesUtils.SPRING_MAIL_PASSWORD = value;
    }

    @Value("${spring.mail.sender.name:-}")
    public void setSpringMailSenderName(String value) {
        PropertiesUtils.SPRING_MAIL_SENDER_NAME = value;
    }

    @Value("${spring.mail.sender.mail:-}")
    public void setSpringMailSenderMail(String value) {
        PropertiesUtils.SPRING_MAIL_SENDER_MAIL = value;
    }
}

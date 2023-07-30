package id.tog.oauth2.util;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class UsernameUtils {
    public static String getUsername(HttpServletRequest request) throws UsernameNotFoundException {
        String username = null;
        if (null != request.getUserPrincipal()) {
            username = request.getUserPrincipal().getName();
        }

        if (StringUtils.isEmpty(username)) {
            username = request.getHeader("X-User");
        }

        if (StringUtils.isEmpty(username)) {
            throw new UsernameNotFoundException("Unauthorized user");
        }

        return username;
    }
}

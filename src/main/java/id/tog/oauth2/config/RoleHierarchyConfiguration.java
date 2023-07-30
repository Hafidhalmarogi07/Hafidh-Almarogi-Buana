package id.tog.oauth2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("security")
class RoleHierarchyConfiguration {
    Logger logger = LoggerFactory.getLogger(RoleHierarchyConfiguration.class);
    private Map<String, List<String>> roles;
    public void setRoleHierarchy(Map<String, List<String>> roles) {
        this.roles = roles;
    }
    @Bean
    public RoleHierarchy roleHierarchy() {
        if (null == this.roles) {
            return null;
        }
        StringWriter roleHierarchyDescriptionBuffer = new StringWriter();
        PrintWriter roleHierarchyDescriptionWriter = new PrintWriter(roleHierarchyDescriptionBuffer);
        for (Map.Entry<String, List<String>> entry : this.roles.entrySet()) {
            String currentRole = entry.getKey();
            List<String> impliedRoles = entry.getValue();
            for (String impliedRole : impliedRoles) {
                String roleMapping = currentRole + " > " + impliedRole;
                roleHierarchyDescriptionWriter.println(roleMapping);
            }
            logger.info("Apply roles for {} as {}, {}", currentRole, currentRole, String.join(", ", impliedRoles));
        }
        RoleHierarchyImpl roles = new RoleHierarchyImpl();
        roles.setHierarchy(roleHierarchyDescriptionBuffer.toString());
        return roles;
    }
}

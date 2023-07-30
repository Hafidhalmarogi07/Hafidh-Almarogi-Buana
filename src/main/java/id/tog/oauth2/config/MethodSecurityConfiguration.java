package id.tog.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    @Autowired
    private RoleHierarchy roleHierarchy;
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        if (null != roleHierarchy) {
            DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
            expressionHandler.setRoleHierarchy(roleHierarchy);
            return expressionHandler;
        }
        return super.createExpressionHandler();
    }
    public AffirmativeBased getAccessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<>();
        if (null != roleHierarchy) {
            RoleHierarchyVoter roleVoter = new RoleHierarchyVoter(roleHierarchy);
            voters.add(roleVoter);
        }
        return new AffirmativeBased(voters);
    }
    @Override
    protected AccessDecisionManager accessDecisionManager() {
        if (null != roleHierarchy) {
            return getAccessDecisionManager();
        }
        return super.accessDecisionManager();
    }
}

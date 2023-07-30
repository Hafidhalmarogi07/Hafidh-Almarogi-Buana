package id.tog.oauth2.service;

import id.tog.oauth2.repository.ClientRepository;
import id.tog.oauth2.util.CacheObjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
@EnableCaching
public class Oauth2ClientDetailsService implements ClientDetailsService, CacheObjectInterface {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    @Cacheable(value = "oauth_client_id", unless = "#result == null")
    public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
        ClientDetails client = clientRepository.findOneByClientId(s);
        if (null == client) {
            throw new ClientRegistrationException("Client not found");
        }

        return client;
    }

    @CacheEvict("oauth_client_id")
    public void clearCache(String s) {
    }

    @CacheEvict(value = "oauth_client_id", allEntries = true)
    @Override
    public void clearCache() {

    }
}

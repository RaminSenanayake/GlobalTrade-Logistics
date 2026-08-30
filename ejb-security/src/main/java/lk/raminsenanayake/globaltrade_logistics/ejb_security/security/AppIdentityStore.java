package lk.raminsenanayake.globaltrade_logistics.ejb_security.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import lk.raminsenanayake.globaltrade_logistics.persistence.service.UserPersistenceService;

import java.util.Set;

@ApplicationScoped
public class AppIdentityStore implements IdentityStore {

    @Inject
    private UserPersistenceService userPersistenceService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential usc) {
            if (userPersistenceService.validate(usc.getCaller(), usc.getPasswordAsString())) {
                String role = userPersistenceService.getUser(usc.getCaller()).get().getRole().toString();
                return new CredentialValidationResult(usc.getCaller(), Set.of(role));
            }
        }

        return CredentialValidationResult.INVALID_RESULT;
    }
}

package lk.raminsenanayake.globaltrade_logistics.web;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DeclareRoles({"CUSTOMER", "ADMIN", "LOGISTIC_PERSONNEL", "VENDOR", "CUSTOM_OFFICIAL"})
public class RestApplication extends Application {
}

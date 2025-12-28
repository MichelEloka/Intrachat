package fr.intrachat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/api/auth/health")
  public Map<String, Object> health() {
    return Map.of("status", "ok", "service", "auth-service");
  }

  @GetMapping("/api/auth/me")
  public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
    return Map.of(
      "sub", jwt.getSubject(),
      "username", jwt.getClaimAsString("preferred_username"),
      "email", jwt.getClaimAsString("email"),
      "name", jwt.getClaimAsString("name"),
      "roles", extractRealmRoles(jwt),
      "issuer", jwt.getIssuer().toString()
    );
  }

  private List<String> extractRealmRoles(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess == null) {
      return List.of();
    }
    Object roles = realmAccess.get("roles");
    if (roles instanceof Collection<?> roleCollection) {
      return roleCollection.stream().map(Object::toString).toList();
    }
    return List.of();
  }
}

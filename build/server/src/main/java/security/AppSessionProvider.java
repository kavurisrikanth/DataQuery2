package security;

import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.GraphQLContext;
import java.util.Collections;
import java.util.List;
import models.AnonymousUser;
import models.User;
import org.apache.tomcat.websocket.WsSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import store.EntityMutator;

@Component
@Service
public class AppSessionProvider {
  @Autowired private EntityMutator mutator;
  @Autowired private JwtTokenUtil jwtTokenUtil;

  public User getCurrentUser() {
    UserProxy user = getCurrentUserProxy();
    if (user == null) return new AnonymousUser();
    return null;
  }

  public void set(DataFetchingEnvironment env) {
    if (env == null) {
      return;
    }
    WsSession session = ((WsSession) ((GraphQLContext) env.getContext()).getSession().get());
    List<String> list = session.getRequestParameterMap().get("token");
    if (list == null || list.isEmpty()) {
      return;
    }
    String token = list.get(0);
    setToken(token);
  }

  public void setToken(String token) {
    if (token == null) {
      return;
    }
    UserProxy proxy = jwtTokenUtil.validateToken(token);
    if (proxy == null) {
      return;
    }
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(proxy, proxy.sessionId, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  public UserProxy getCurrentUserProxy() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null
        || auth
            instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
      return null;
    }
    Object principal = auth.getPrincipal();
    return ((UserProxy) principal);
  }

  public AnonymousUser getAnonymousUser() {
    User user = getCurrentUser();
    if (user instanceof AnonymousUser) {
      return ((AnonymousUser) user);
    }
    return null;
  }
}

package rest;

import d3e.core.D3ELogger;
import gqltosql.GqlToSql;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lists.AllStudentsImpl;
import lists.LimitAndOffsetStudentsImpl;
import lists.LimitedStudentsImpl;
import models.AnonymousUser;
import models.OneTimePassword;
import models.User;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.StudentRepository;
import repository.jpa.UserRepository;
import repository.jpa.UserSessionRepository;
import security.AppSessionProvider;
import security.JwtTokenUtil;
import security.UserProxy;
import store.EntityHelperService;
import store.EntityMutator;

@RestController
@RequestMapping("native/")
public class NativeQuery extends AbstractQueryService {
  @Autowired private EntityMutator mutator;
  @Autowired private ObjectFactory<EntityHelperService> helperService;
  @Autowired private GqlToSql gqlToSql;
  @Autowired private IModelSchema schema;
  @Autowired private JwtTokenUtil jwtTokenUtil;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private ObjectFactory<AppSessionProvider> provider;
  @Autowired private AllStudentsImpl allStudentsImpl;
  @Autowired private LimitAndOffsetStudentsImpl limitAndOffsetStudentsImpl;
  @Autowired private LimitedStudentsImpl limitedStudentsImpl;

  @PostMapping(path = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
  public String run(@RequestBody String query) throws Exception {
    JSONObject req = new JSONObject(query);
    List<Field> fields = parseFields(req);
    JSONObject variables = req.getJSONObject("variables");
    return executeFields(fields, variables);
  }

  public String executeFields(List<Field> fields, JSONObject variables) throws Exception {
    JSONObject data = new JSONObject();
    for (Field s : fields) {
      String name = s.getAlias() == null ? s.getName() : s.getAlias();
      data.put(name, executeOperation(s, variables));
    }
    JSONObject output = new JSONObject();
    output.put("data", data);
    return output.toString();
  }

  protected Object executeOperation(Field field, JSONObject variables) throws Exception {
    GraphQLInputContext ctx =
        new ArgumentInputContext(
            field.getArguments(),
            helperService.getObject(),
            new HashMap<>(),
            new HashMap<>(),
            variables);
    D3ELogger.info("Query: " + field.getName());
    switch (field.getName()) {
      case "getAnonymousUserById":
        {
          return gqlToSql.execute("AnonymousUser", field, ctx.readLong("id"));
        }
      case "getOneTimePasswordById":
        {
          return gqlToSql.execute("OneTimePassword", field, ctx.readLong("id"));
        }
      case "getStudentById":
        {
          return gqlToSql.execute("Student", field, ctx.readLong("id"));
        }
      case "getAllStudents":
        {
          return allStudentsImpl.getAsJson(inspect(field, "items"));
        }
      case "getLimitedStudents":
        {
          return limitedStudentsImpl.getAsJson(inspect(field, "items"));
        }
      case "loginWithOTP":
        {
          String token = ctx.readString("token");
          String code = ctx.readString("code");
          return loginWithOTP(field, token, code);
        }
      case "currentAnonymousUser":
        {
          return currentAnonymousUser(field);
        }
    }
    D3ELogger.info("Query Not found");
    return null;
  }

  private JSONObject loginWithOTP(Field field, String token, String code) throws Exception {
    OneTimePassword otp = oneTimePasswordRepository.getByToken(token);
    JSONObject loginResult = new JSONObject();
    if (otp == null) {
      loginResult.put("success", false);
      loginResult.put("loginResult", "Wrong password.");
      return loginResult;
    }
    if (otp.getExpiry().isBefore(java.time.LocalDateTime.now())) {
      loginResult.put("success", false);
      loginResult.put("loginResult", "Wrong password.");
      return loginResult;
    }
    if (!(code.equals(otp.getCode()))) {
      loginResult.put("success", false);
      loginResult.put("loginResult", "Wrong password.");
      return loginResult;
    }
    User user = otp.getUser();
    if (user == null) {
      loginResult.put("success", false);
      loginResult.put("loginResult", "Wrong password.");
      return loginResult;
    }
    loginResult.put("success", true);
    JSONObject userObject = gqlToSql.execute("User", inspect(field, "userObject"), user.getId());
    loginResult.put("userObject", userObject);
    String type = ((String) userObject.get("__typename"));
    String id = String.valueOf(user.getId());
    String finalToken =
        jwtTokenUtil.generateToken(
            id, new UserProxy(type, user.getId(), UUID.randomUUID().toString()));
    loginResult.put("token", finalToken);
    return loginResult;
  }

  private JSONObject currentAnonymousUser(Field field) throws Exception {
    AnonymousUser user = provider.getObject().getAnonymousUser();
    return gqlToSql.execute("AnonymousUser", field, user.getId());
  }
}

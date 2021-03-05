package rest;

import gqltosql.GqlToSql;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import java.util.List;
import java.util.Map;
import lists.AllStudentsImpl;
import lists.LimitedStudentsImpl;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("native/")
public class NativeQuery extends AbstractQueryService {
  @Autowired private GqlToSql gqlToSql;
  @Autowired private IModelSchema schema;
  @Autowired private AllStudentsImpl allStudentsImpl;
  @Autowired private LimitedStudentsImpl limitedStudentsImpl;

  @PostMapping(path = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
  public String run(@RequestBody String query) throws Exception {
    JSONObject req = new JSONObject(query);
    List<Field> operations = parseOperations(req);
    JSONObject variables = req.getJSONObject("variables");
    JSONObject data = new JSONObject();
    for (Field s : operations) {
      String name = s.getAlias() == null ? s.getName() : s.getAlias();
      data.put(name, executeOperation(s, variables));
    }
    JSONObject output = new JSONObject();
    output.put("data", data);
    return output.toString();
  }

  protected Object executeOperation(Field field, JSONObject variables) throws Exception {
    Map<String, Object> args = parseArguments(field.getArguments(), variables);
    switch (field.getName()) {
      case "getAnonymousUserById":
        {
          return gqlToSql.execute("AnonymousUser", field, ((Long) args.get("id")));
        }
      case "getOneTimePasswordById":
        {
          return gqlToSql.execute("OneTimePassword", field, ((Long) args.get("id")));
        }
      case "getStudentById":
        {
          return gqlToSql.execute("Student", field, ((Long) args.get("id")));
        }
      case "getAllStudents":
        {
          return allStudentsImpl.getAsJson(gqlToSql, field);
        }
      case "getLimitedStudents":
        {
          return limitedStudentsImpl.getAsJson(gqlToSql, field);
        }
    }
    return null;
  }
}

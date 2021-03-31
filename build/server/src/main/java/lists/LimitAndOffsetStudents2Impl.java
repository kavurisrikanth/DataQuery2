package lists;

import classes.LimitAndOffsetStudents2;
import classes.LimitAndOffsetStudents2In;
import classes.LimitAndOffsetStudents2Request;
import gqltosql.GqlToSql;
import gqltosql.SqlRow;
import graphql.language.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Student;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.AbstractQueryService;

@Service
public class LimitAndOffsetStudents2Impl extends AbsDataQueryImpl {
  @Autowired private EntityManager em;
  @Autowired private GqlToSql gqlToSql;

  public LimitAndOffsetStudents2Request inputToRequest(LimitAndOffsetStudents2In inputs) {
    LimitAndOffsetStudents2Request request = new LimitAndOffsetStudents2Request();
    request.limit = inputs.limit;
    request.offset = inputs.offset;
    return request;
  }

  public LimitAndOffsetStudents2 get(LimitAndOffsetStudents2In inputs) {
    LimitAndOffsetStudents2Request request = inputToRequest(inputs);
    return get(request);
  }

  public LimitAndOffsetStudents2 get(LimitAndOffsetStudents2Request request) {
    List<NativeObj> rows = getNativeResult(request);
    List<Student> result = new ArrayList<>();
    for (NativeObj _r1 : rows) {
      result.add(NativeSqlUtil.get(em, _r1.getRef(0), Student.class));
    }
    LimitAndOffsetStudents2 wrap = new LimitAndOffsetStudents2();
    wrap.items = result;
    return wrap;
  }

  public JSONObject getAsJson(Field field, LimitAndOffsetStudents2In inputs) throws Exception {
    LimitAndOffsetStudents2Request request = inputToRequest(inputs);
    return getAsJson(field, request);
  }

  public JSONObject getAsJson(Field field, LimitAndOffsetStudents2Request request)
      throws Exception {
    List<NativeObj> rows = getNativeResult(request);
    return getAsJson(field, rows);
  }

  public JSONObject getAsJson(Field field, List<NativeObj> rows) throws Exception {
    JSONArray array = new JSONArray();
    List<SqlRow> sqlDecl0 = new ArrayList<>();
    for (NativeObj _r1 : rows) {
      array.put(NativeSqlUtil.getJSONObject(_r1, sqlDecl0));
    }
    gqlToSql.execute("Student", AbstractQueryService.inspect(field, ""), sqlDecl0);
    JSONObject result = new JSONObject();
    result.put("items", array);
    return result;
  }

  public List<NativeObj> getNativeResult(LimitAndOffsetStudents2Request request) {
    assertLimitNotNegative(request.limit - request.offset);
    Query query =
        em.createNativeQuery(
            "select a._id a0 from _student a order by a._name limit :param_0 - :param_1 offset :param_1");
    setParameter(query, "param_0", request.limit);
    setParameter(query, "param_1", request.offset);
    List<NativeObj> result = NativeSqlUtil.createNativeObj(query.getResultList(), 0);
    return result;
  }
}

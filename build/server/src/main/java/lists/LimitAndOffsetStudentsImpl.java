package lists;

import classes.LimitAndOffsetStudents;
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
public class LimitAndOffsetStudentsImpl extends AbsDataQueryImpl {
  @Autowired private EntityManager em;
  @Autowired private GqlToSql gqlToSql;

  public LimitAndOffsetStudents get() {
    List<NativeObj> rows = getNativeResult();
    List<Student> result = new ArrayList<>();
    for (NativeObj _r1 : rows) {
      result.add(NativeSqlUtil.get(em, _r1.getRef(0), Student.class));
    }
    LimitAndOffsetStudents wrap = new LimitAndOffsetStudents();
    wrap.items = result;
    return wrap;
  }

  public JSONObject getAsJson(Field field) throws Exception {
    List<NativeObj> rows = getNativeResult();
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

  public void assertLimitNotNegative(long limit) {
    if (limit < 0) {
      throw new RuntimeException("Limit is negative.");
    }
  }

  public List<NativeObj> getNativeResult() {
    assertLimitNotNegative(5l - 10l);
    Query query =
        em.createNativeQuery(
            "select a._id a0 from _student a order by a._name limit 5 - 10 offset 10");
    List<NativeObj> result = NativeSqlUtil.createNativeObj(query.getResultList(), 0);
    return result;
  }
}

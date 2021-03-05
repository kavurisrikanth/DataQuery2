package lists;

import gqltosql.GqlToSql;
import gqltosql.SqlRow;
import graphql.language.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Student;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.AbstractQueryService;

@Service
public class LimitedStudentsImpl {
  @Autowired private EntityManager em;

  public List<Student> get() {
    List<NativeObj> rows = getNativeResult();
    List<Student> result = new ArrayList<>();
    for (NativeObj _r1 : rows) {
      result.add(NativeSqlUtil.get(em, _r1.getRef(0), Student.class));
    }
    return result;
  }

  public JSONArray getAsJson(GqlToSql gqlToSql, Field field) throws Exception {
    List<NativeObj> rows = getNativeResult();
    return getAsJson(gqlToSql, field, rows);
  }

  public JSONArray getAsJson(GqlToSql gqlToSql, Field field, List<NativeObj> rows)
      throws Exception {
    JSONArray result = new JSONArray();
    List<SqlRow> sqlDecl0 = new ArrayList<>();
    for (NativeObj _r1 : rows) {
      result.put(NativeSqlUtil.getJSONObject(_r1.getRef(0), sqlDecl0));
    }
    gqlToSql.execute("Student", AbstractQueryService.inspect(field, ""), sqlDecl0);
    return result;
  }

  public List<NativeObj> getNativeResult() {
    Query query = em.createNativeQuery("select a._id a0 from _student a");
    List<NativeObj> result = NativeSqlUtil.createNativeObj(query.getResultList(), 0);
    return result;
  }
}

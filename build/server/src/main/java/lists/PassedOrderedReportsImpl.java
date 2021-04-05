package lists;

import classes.PassedOrderedReports;
import gqltosql.GqlToSql;
import gqltosql.SqlRow;
import graphql.language.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import models.Report;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.AbstractQueryService;

@Service
public class PassedOrderedReportsImpl extends AbsDataQueryImpl {
  @Autowired private EntityManager em;
  @Autowired private GqlToSql gqlToSql;

  public PassedOrderedReports get() {
    List<NativeObj> rows = getNativeResult();
    List<Report> result = new ArrayList<>();
    for (NativeObj _r1 : rows) {
      result.add(NativeSqlUtil.get(em, _r1.getRef(3), Report.class));
    }
    PassedOrderedReports wrap = new PassedOrderedReports();
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
    gqlToSql.execute("Report", AbstractQueryService.inspect(field, ""), sqlDecl0);
    JSONObject result = new JSONObject();
    result.put("items", array);
    return result;
  }

  public List<NativeObj> getNativeResult() {
    Query query =
        em.createNativeQuery(
            "select a._marks a0, b._name a1, a._student_id a2, a._id a3 from _report a left join _student b on b._id = a._student_id where a._marks >= 40.0 order by b._name");
    List<NativeObj> result = NativeSqlUtil.createNativeObj(query.getResultList(), 3);
    return result;
  }
}

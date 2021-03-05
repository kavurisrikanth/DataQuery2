package rest;

import d3e.core.D3ESubscription;
import d3e.core.D3ESubscriptionEvent;
import gqltosql.GqlToSql;
import gqltosql.schema.GrpahQLDataFetcher;
import gqltosql.schema.IModelSchema;
import graphql.language.Field;
import io.reactivex.rxjava3.core.Flowable;
import java.util.List;
import java.util.Map;
import lists.DataQueryDataChange;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import store.DatabaseObject;

@Component
public class NativeSubscription extends AbstractQueryService {
  @Autowired private D3ESubscription subscription;
  @Autowired private IModelSchema schema;
  @Autowired private GqlToSql gqltosql;

  public Flowable<JSONObject> subscribe(JSONObject req) throws Exception {
    List<Field> operations = parseOperations(req);
    Field field = operations.get(0);
    JSONObject variables = req.getJSONObject("variables");
    return executeOperation(field, variables);
  }

  private JSONObject fromDataQueryDataChange(DataQueryDataChange event, Field field) {
    JSONObject data = new JSONObject();
    JSONObject opData = new JSONObject();
    try {
      opData.put("changeType", event.changeType.name());
      opData.put("path", event.path);
      opData.put("data", event.data);
      data.put(field.getName(), opData);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return data;
  }

  private <T> JSONObject fromD3ESubscriptionEventExternal(
      D3ESubscriptionEvent<T> event, Field field, String type) {
    JSONObject data = new JSONObject();
    JSONObject opData = new JSONObject();
    try {
      opData.put("changeType", event.changeType.name());
      opData.put(
          "data", new GrpahQLDataFetcher(schema).fetch(inspect(field, "data"), type, event.model));
      data.put(field.getName(), opData);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return data;
  }

  private <T> JSONObject fromD3ESubscriptionEvent(
      D3ESubscriptionEvent<T> event, Field field, String type) {
    JSONObject data = new JSONObject();
    JSONObject opData = new JSONObject();
    try {
      opData.put("changeType", event.changeType.name());
      if (event.model instanceof DatabaseObject) {
        long id = ((DatabaseObject) event.model).getId();
        opData.put("data", gqltosql.execute(type, inspect(field, "data"), id));
      }
      data.put(field.getName(), opData);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return data;
  }

  protected Flowable<JSONObject> executeOperation(Field field, JSONObject variables)
      throws Exception {
    Map<String, Object> args = parseArguments(field.getArguments(), variables);
    switch (field.getName()) {
      case "onAnonymousUserChangeEvent":
        {
          return subscription
              .onAnonymousUserChangeEvent()
              .map((e) -> fromD3ESubscriptionEvent(e, field, "AnonymousUser"));
        }
      case "onAnonymousUserChangeEventById":
        {
          List<Long> ids = parseObjectValueList(variables, args.get("in"), Long.class);
          return subscription
              .onAnonymousUserChangeEvent()
              .filter((e) -> ids.contains(e.model.getId()))
              .map((e) -> fromD3ESubscriptionEvent(e, field, "AnonymousUser"));
        }
      case "onOneTimePasswordChangeEvent":
        {
          return subscription
              .onOneTimePasswordChangeEvent()
              .map((e) -> fromD3ESubscriptionEvent(e, field, "OneTimePassword"));
        }
      case "onOneTimePasswordChangeEventById":
        {
          List<Long> ids = parseObjectValueList(variables, args.get("in"), Long.class);
          return subscription
              .onOneTimePasswordChangeEvent()
              .filter((e) -> ids.contains(e.model.getId()))
              .map((e) -> fromD3ESubscriptionEvent(e, field, "OneTimePassword"));
        }
      case "onStudentChangeEvent":
        {
          return subscription
              .onStudentChangeEvent()
              .map((e) -> fromD3ESubscriptionEvent(e, field, "Student"));
        }
      case "onStudentChangeEventById":
        {
          List<Long> ids = parseObjectValueList(variables, args.get("in"), Long.class);
          return subscription
              .onStudentChangeEvent()
              .filter((e) -> ids.contains(e.model.getId()))
              .map((e) -> fromD3ESubscriptionEvent(e, field, "Student"));
        }
      case "onUserChangeEvent":
        {
          return subscription
              .onUserChangeEvent()
              .map((e) -> fromD3ESubscriptionEvent(e, field, "User"));
        }
      case "onUserChangeEventById":
        {
          List<Long> ids = parseObjectValueList(variables, args.get("in"), Long.class);
          return subscription
              .onUserChangeEvent()
              .filter((e) -> ids.contains(e.model.getId()))
              .map((e) -> fromD3ESubscriptionEvent(e, field, "User"));
        }
      case "onUserSessionChangeEvent":
        {
          return subscription
              .onUserSessionChangeEvent()
              .map((e) -> fromD3ESubscriptionEvent(e, field, "UserSession"));
        }
      case "onUserSessionChangeEventById":
        {
          List<Long> ids = parseObjectValueList(variables, args.get("in"), Long.class);
          return subscription
              .onUserSessionChangeEvent()
              .filter((e) -> ids.contains(e.model.getId()))
              .map((e) -> fromD3ESubscriptionEvent(e, field, "UserSession"));
        }
    }
    return null;
  }
}

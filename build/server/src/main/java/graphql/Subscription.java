package graphql;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import d3e.core.D3ESubscription;
import graphql.events.AnonymousUserChangeEvent;
import graphql.events.ChangeEventType;
import graphql.events.OneTimePasswordChangeEvent;
import graphql.events.StudentChangeEvent;
import graphql.events.UserChangeEvent;
import graphql.events.UserSessionChangeEvent;
import graphql.schema.DataFetchingEnvironment;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import java.util.List;
import javax.annotation.PostConstruct;
import models.AnonymousUser;
import models.OneTimePassword;
import models.Student;
import models.User;
import models.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import security.AppSessionProvider;
import store.DataStoreEvent;
import store.StoreEventType;

@org.springframework.stereotype.Component
public class Subscription implements GraphQLSubscriptionResolver {
  @Autowired private D3ESubscription subscription;
  private ConnectableFlowable<DataStoreEvent> flowable;
  @Autowired private AppSessionProvider provider;

  @PostConstruct
  public void init() {
    this.flowable = subscription.flowable;
  }

  public ChangeEventType from(StoreEventType type) {
    switch (type) {
      case Insert:
        {
          return ChangeEventType.Insert;
        }
      case Update:
        {
          return ChangeEventType.Update;
        }
      case Delete:
        {
          return ChangeEventType.Delete;
        }
      default:
        {
          return null;
        }
    }
  }

  public Object onAnonymousUserChangeEvent(DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter((e) -> e.getEntity() instanceof AnonymousUser)
        .map(
            (e) -> {
              AnonymousUserChangeEvent event = new AnonymousUserChangeEvent();
              event.model = ((AnonymousUser) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onAnonymousUserChangeEventById(List<Long> ids, DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter(
            (e) ->
                e.getEntity() instanceof AnonymousUser
                    && ids.contains(((AnonymousUser) e.getEntity()).getId()))
        .map(
            (e) -> {
              AnonymousUserChangeEvent event = new AnonymousUserChangeEvent();
              event.model = ((AnonymousUser) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onOneTimePasswordChangeEvent(DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter((e) -> e.getEntity() instanceof OneTimePassword)
        .map(
            (e) -> {
              OneTimePasswordChangeEvent event = new OneTimePasswordChangeEvent();
              event.model = ((OneTimePassword) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onOneTimePasswordChangeEventById(List<Long> ids, DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter(
            (e) ->
                e.getEntity() instanceof OneTimePassword
                    && ids.contains(((OneTimePassword) e.getEntity()).getId()))
        .map(
            (e) -> {
              OneTimePasswordChangeEvent event = new OneTimePasswordChangeEvent();
              event.model = ((OneTimePassword) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onStudentChangeEvent(DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter((e) -> e.getEntity() instanceof Student)
        .map(
            (e) -> {
              StudentChangeEvent event = new StudentChangeEvent();
              event.model = ((Student) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onStudentChangeEventById(List<Long> ids, DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter(
            (e) ->
                e.getEntity() instanceof Student && ids.contains(((Student) e.getEntity()).getId()))
        .map(
            (e) -> {
              StudentChangeEvent event = new StudentChangeEvent();
              event.model = ((Student) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onUserChangeEvent(DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter((e) -> e.getEntity() instanceof User)
        .map(
            (e) -> {
              UserChangeEvent event = new UserChangeEvent();
              event.model = ((User) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onUserChangeEventById(List<Long> ids, DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter(
            (e) -> e.getEntity() instanceof User && ids.contains(((User) e.getEntity()).getId()))
        .map(
            (e) -> {
              UserChangeEvent event = new UserChangeEvent();
              event.model = ((User) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onUserSessionChangeEvent(DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter((e) -> e.getEntity() instanceof UserSession)
        .map(
            (e) -> {
              UserSessionChangeEvent event = new UserSessionChangeEvent();
              event.model = ((UserSession) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }

  public Object onUserSessionChangeEventById(List<Long> ids, DataFetchingEnvironment env) {
    provider.set(env);
    return this.flowable
        .filter(
            (e) ->
                e.getEntity() instanceof UserSession
                    && ids.contains(((UserSession) e.getEntity()).getId()))
        .map(
            (e) -> {
              UserSessionChangeEvent event = new UserSessionChangeEvent();
              event.model = ((UserSession) e.getEntity());
              event.changeType = from(e.getType());
              return event;
            });
  }
}

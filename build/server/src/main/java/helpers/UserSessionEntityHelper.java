package helpers;

import graphql.input.UserSessionEntityInput;
import models.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.UserSessionRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("UserSession")
public class UserSessionEntityHelper<T extends UserSession, I extends UserSessionEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private UserSessionRepository userSessionRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    return null;
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("userSessionId")) {
      entity.setUserSessionId(input.userSessionId);
    }
    entity.updateMasters((o) -> {});
    return entity;
  }

  public UserSessionEntityInput toInput(T entity) {
    return null;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldUserSessionId(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getUserSessionId();
    if (it == null) {
      validationContext.addFieldError("userSessionId", "userSessionId is required.");
      return;
    }
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldUserSessionId(entity, validationContext, onCreate, onUpdate);
  }

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, true, false);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, false, true);
  }

  @Override
  public T clone(T entity) {
    return null;
  }

  @Override
  public T getById(long id) {
    return id == 0l ? null : ((T) userSessionRepository.findById(id).orElse(null));
  }

  @Override
  public void setDefaults(T entity) {}

  @Override
  public void compute(T entity) {}

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    return true;
  }

  public T getOld(long id) {
    return ((T) getById(id).clone());
  }
}

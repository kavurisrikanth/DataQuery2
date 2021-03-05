package helpers;

import graphql.input.AnonymousUserEntityInput;
import models.AnonymousUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AnonymousUserRepository;
import store.EntityValidationContext;
import store.InputHelper;

@Service("AnonymousUser")
public class AnonymousUserEntityHelper<T extends AnonymousUser, I extends AnonymousUserEntityInput>
    extends UserEntityHelper<T, I> {
  @Autowired private AnonymousUserRepository anonymousUserRepository;

  public AnonymousUser newInstance() {
    return new AnonymousUser();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newAnonymousUser = ((T) new AnonymousUser());
    newAnonymousUser.setId(input.getId());
    return fromInput(input, newAnonymousUser, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("isActive")) {
      entity.setIsActive(input.isActive);
    }
    entity.updateMasters((o) -> {});
    return entity;
  }

  public AnonymousUserEntityInput toInput(T entity) {
    I input = ((I) new AnonymousUserEntityInput());
    input.setId(entity.getId());
    input.isActive = entity.isIsActive();
    return input;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    super.validateInternal(entity, validationContext, onCreate, onUpdate);
  }

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    super.validateOnCreate(entity, validationContext);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    super.validateOnUpdate(entity, validationContext);
  }

  @Override
  public T clone(T entity) {
    return null;
  }

  @Override
  public T getById(long id) {
    return id == 0l ? null : ((T) anonymousUserRepository.findById(id).orElse(null));
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

package helpers;

import graphql.input.OneTimePasswordEntityInput;
import models.OneTimePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.OneTimePasswordRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("OneTimePassword")
public class OneTimePasswordEntityHelper<
        T extends OneTimePassword, I extends OneTimePasswordEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public OneTimePassword newInstance() {
    return new OneTimePassword();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newOneTimePassword = ((T) new OneTimePassword());
    newOneTimePassword.setId(input.getId());
    return fromInput(input, newOneTimePassword, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("success")) {
      entity.setSuccess(input.success);
    }
    if (helper.has("errorMsg")) {
      entity.setErrorMsg(input.errorMsg);
    }
    if (helper.has("token")) {
      entity.setToken(input.token);
    }
    if (helper.has("expiry")) {
      entity.setExpiry(input.expiry);
    }
    entity.updateMasters((o) -> {});
    return entity;
  }

  public OneTimePasswordEntityInput toInput(T entity) {
    I input = ((I) new OneTimePasswordEntityInput());
    input.setId(entity.getId());
    input.success = entity.isSuccess();
    input.errorMsg = entity.getErrorMsg();
    input.token = entity.getToken();
    input.expiry = entity.getExpiry();
    return input;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldSuccess(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    boolean it = entity.isSuccess();
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldSuccess(entity, validationContext, onCreate, onUpdate);
    validateFieldTokenUnique(entity, validationContext);
    isErrorMsgExists(entity);
  }

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, true, false);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, false, true);
  }

  public boolean isErrorMsgExists(T entity) {
    try {
      if (!entity.isSuccess()) {
        return true;
      } else {
        entity.setErrorMsg("");
        return false;
      }
    } catch (RuntimeException e) {
      return false;
    }
  }

  public void validateFieldTokenUnique(T entity, EntityValidationContext validationContext) {
    if (!(oneTimePasswordRepository.checkTokenUnique(entity.getId(), entity.getToken()))) {
      validationContext.addFieldError("token", "OneTimePassword with given token already exists");
    }
  }

  @Override
  public T clone(T entity) {
    return null;
  }

  @Override
  public T getById(long id) {
    return id == 0l ? null : ((T) oneTimePasswordRepository.findById(id).orElse(null));
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

package helpers;

import graphql.input.SMSMessageEntityInput;
import java.util.stream.Collectors;
import models.SMSMessage;
import org.springframework.stereotype.Service;
import store.EntityValidationContext;
import store.InputHelper;

@Service("SMSMessage")
public class SMSMessageEntityHelper<T extends SMSMessage, I extends SMSMessageEntityInput>
    extends D3EMessageEntityHelper<T, I> {
  public SMSMessage newInstance() {
    return new SMSMessage();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newSMSMessage = ((T) new SMSMessage());
    newSMSMessage.setId(input.getId());
    return fromInput(input, newSMSMessage, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("from")) {
      entity.setFrom(input.from);
    }
    if (helper.has("to")) {
      entity.setTo(input.to.stream().collect(Collectors.toList()));
    }
    if (helper.has("body")) {
      entity.setBody(input.body);
    }
    if (helper.has("createdOn")) {
      entity.setCreatedOn(input.createdOn);
    }
    entity.updateMasters((o) -> {});
    return entity;
  }

  public SMSMessageEntityInput toInput(T entity) {
    I input = ((I) new SMSMessageEntityInput());
    input.setId(entity.getId());
    input.from = entity.getFrom();
    input.to = entity.getTo().stream().collect(java.util.stream.Collectors.toList());
    input.body = entity.getBody();
    input.createdOn = entity.getCreatedOn();
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
    return null;
  }

  @Override
  public void setDefaults(T entity) {
    this.setDefaultCreatedOn(entity);
  }

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

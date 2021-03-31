package helpers;

import graphql.input.ReportConfigOptionEntityInput;
import models.ReportConfigOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ReportConfigOptionRepository;
import repository.jpa.ReportConfigRepository;
import rest.GraphQLInputContext;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("ReportConfigOption")
public class ReportConfigOptionEntityHelper<
        T extends ReportConfigOption, I extends ReportConfigOptionEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ReportConfigOptionRepository reportConfigOptionRepository;
  @Autowired private ReportConfigRepository reportConfigRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public ReportConfigOption newInstance() {
    return new ReportConfigOption();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newReportConfigOption = ((T) new ReportConfigOption());
    newReportConfigOption.setId(input.getId());
    return fromInput(input, newReportConfigOption, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("identity")) {
      entity.setIdentity(input.identity);
    }
    if (helper.has("value")) {
      entity.setValue(input.value);
    }
    return entity;
  }

  @Override
  public void fromInput(T entity, GraphQLInputContext ctx) {
    if (ctx.has("identity")) {
      entity.setIdentity(ctx.readString("identity"));
    }
    if (ctx.has("value")) {
      entity.setValue(ctx.readString("value"));
    }
  }

  public ReportConfigOptionEntityInput toInput(T entity) {
    I input = ((I) new ReportConfigOptionEntityInput());
    input.setId(entity.getId());
    input.identity = entity.getIdentity();
    input.value = entity.getValue();
    return input;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldIdentity(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getIdentity();
    if (it == null) {
      validationContext.addFieldError("identity", "identity is required.");
      return;
    }
  }

  public void validateFieldValue(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    String it = entity.getValue();
    if (it == null) {
      validationContext.addFieldError("value", "value is required.");
      return;
    }
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldIdentity(entity, validationContext, onCreate, onUpdate);
    validateFieldValue(entity, validationContext, onCreate, onUpdate);
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
    return id == 0l ? null : ((T) reportConfigOptionRepository.findById(id).orElse(null));
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
}

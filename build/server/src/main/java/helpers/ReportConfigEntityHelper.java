package helpers;

import d3e.core.IterableExt;
import d3e.core.ListExt;
import graphql.input.ReportConfigEntityInput;
import models.ReportConfig;
import models.ReportConfigOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ReportConfigRepository;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("ReportConfig")
public class ReportConfigEntityHelper<T extends ReportConfig, I extends ReportConfigEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ReportConfigRepository reportConfigRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public ReportConfig newInstance() {
    return new ReportConfig();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newReportConfig = ((T) new ReportConfig());
    newReportConfig.setId(input.getId());
    return fromInput(input, newReportConfig, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("identity")) {
      entity.setIdentity(input.identity);
    }
    if (helper.has("values")) {
      entity.setValues(
          IterableExt.toList(
              ListExt.map(
                  input.values,
                  (objId) -> {
                    ReportConfigOptionEntityHelper valuesHelper =
                        this.mutator.getHelper(objId._type());
                    return ((ReportConfigOption) helper.readChild(objId, "values"));
                  })));
    }
    return entity;
  }

  public ReportConfigEntityInput toInput(T entity) {
    I input = ((I) new ReportConfigEntityInput());
    input.setId(entity.getId());
    input.identity = entity.getIdentity();
    input.values =
        entity.getValues().stream()
            .map(
                (one) -> {
                  ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(one);
                  return helper.toInput(one);
                })
            .collect(java.util.stream.Collectors.toList());
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

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldIdentity(entity, validationContext, onCreate, onUpdate);
    long valuesIndex = 0l;
    for (ReportConfigOption obj : entity.getValues()) {
      ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(obj);
      if (onCreate) {
        helper.validateOnCreate(
            obj, validationContext.child("values", obj.getIdentity(), valuesIndex++));
      } else {
        helper.validateOnUpdate(
            obj, validationContext.child("values", obj.getIdentity(), valuesIndex++));
      }
    }
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
    return id == 0l ? null : ((T) reportConfigRepository.findById(id).orElse(null));
  }

  @Override
  public void setDefaults(T entity) {
    for (ReportConfigOption obj : entity.getValues()) {
      ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(obj);
      helper.setDefaults(obj);
    }
  }

  @Override
  public void compute(T entity) {
    for (ReportConfigOption obj : entity.getValues()) {
      ReportConfigOptionEntityHelper helper = mutator.getHelperByInstance(obj);
      helper.compute(obj);
    }
  }

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

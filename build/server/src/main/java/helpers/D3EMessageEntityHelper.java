package helpers;

import graphql.input.D3EMessageEntityInput;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import models.D3EMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rest.GraphQLInputContext;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("D3EMessage")
public class D3EMessageEntityHelper<T extends D3EMessage, I extends D3EMessageEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    return null;
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

  @Override
  public void fromInput(T entity, GraphQLInputContext ctx) {
    if (ctx.has("from")) {
      entity.setFrom(ctx.readString("from"));
    }
    if (ctx.has("to")) {
      entity.setTo(ctx.readStringColl("to"));
    }
    if (ctx.has("body")) {
      entity.setBody(ctx.readString("body"));
    }
    if (ctx.has("createdOn")) {
      entity.setCreatedOn(ctx.readDateTime("createdOn"));
    }
    entity.updateMasters((o) -> {});
  }

  public D3EMessageEntityInput toInput(T entity) {
    return null;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {}

  public void validateOnCreate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, true, false);
  }

  public void validateOnUpdate(T entity, EntityValidationContext validationContext) {
    validateInternal(entity, validationContext, false, true);
  }

  public void setDefaultCreatedOn(T entity) {
    if (entity.getCreatedOn() != null) {
      return;
    }
    entity.setCreatedOn(LocalDateTime.now());
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

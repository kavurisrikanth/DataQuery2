package helpers;

import d3e.core.DFile;
import graphql.input.D3EImageEntityInput;
import models.D3EImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AvatarRepository;
import repository.jpa.DFileRepository;
import rest.GraphQLInputContext;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("D3EImage")
public class D3EImageEntityHelper<T extends D3EImage, I extends D3EImageEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private AvatarRepository avatarRepository;
  @Autowired private DFileRepository dFileRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public D3EImage newInstance() {
    return new D3EImage();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newD3EImage = ((T) new D3EImage());
    return fromInput(input, newD3EImage, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("size")) {
      entity.setSize(input.size);
    }
    if (helper.has("width")) {
      entity.setWidth(input.width);
    }
    if (helper.has("height")) {
      entity.setHeight(input.height);
    }
    if (helper.has("file")) {
      DFile existing = dFileRepository.findById(input.file.getId()).orElse(null);
      if (existing == null) {
        existing = helper.readDFile(input.file, "file");
      }
      entity.setFile(existing);
    }
    return entity;
  }

  @Override
  public void fromInput(T entity, GraphQLInputContext ctx) {
    if (ctx.has("size")) {
      entity.setSize(ctx.readInteger("size"));
    }
    if (ctx.has("width")) {
      entity.setWidth(ctx.readInteger("width"));
    }
    if (ctx.has("height")) {
      entity.setHeight(ctx.readInteger("height"));
    }
    if (ctx.has("file")) {
      entity.setFile(ctx.readDFile("file"));
    }
  }

  public D3EImageEntityInput toInput(T entity) {
    I input = ((I) new D3EImageEntityInput());
    input.size = entity.getSize();
    input.width = entity.getWidth();
    input.height = entity.getHeight();
    return input;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldSize(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long it = entity.getSize();
  }

  public void validateFieldWidth(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long it = entity.getWidth();
  }

  public void validateFieldHeight(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long it = entity.getHeight();
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldSize(entity, validationContext, onCreate, onUpdate);
    validateFieldWidth(entity, validationContext, onCreate, onUpdate);
    validateFieldHeight(entity, validationContext, onCreate, onUpdate);
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
    return null;
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

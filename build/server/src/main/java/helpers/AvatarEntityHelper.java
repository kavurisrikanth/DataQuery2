package helpers;

import d3e.core.D3EResourceHandler;
import d3e.core.ListExt;
import graphql.input.AvatarEntityInput;
import models.Avatar;
import models.D3EImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AvatarRepository;
import rest.GraphQLInputContext;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("Avatar")
public class AvatarEntityHelper<T extends Avatar, I extends AvatarEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private AvatarRepository avatarRepository;
  @Autowired private D3EResourceHandler resourceHandler;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public Avatar newInstance() {
    return new Avatar();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newAvatar = ((T) new Avatar());
    newAvatar.setId(input.getId());
    return fromInput(input, newAvatar, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("image")) {
      helper.readEmbedded(entity.getImage(), input.image, "image");
    }
    if (helper.has("createFrom")) {
      entity.setCreateFrom(input.createFrom);
    }
    return entity;
  }

  @Override
  public void fromInput(T entity, GraphQLInputContext ctx) {
    if (ctx.has("image")) {
      entity.setImage(ctx.readEmbedded("image", "D3EImage", entity.getImage()));
    }
    if (ctx.has("createFrom")) {
      entity.setCreateFrom(ctx.readString("createFrom"));
    }
  }

  public AvatarEntityInput toInput(T entity) {
    I input = ((I) new AvatarEntityInput());
    input.setId(entity.getId());
    {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      input.image = helper.toInput(entity.getImage());
    }
    input.createFrom = entity.getCreateFrom();
    return input;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    long imageIndex = 0l;
    if (entity.getImage() != null) {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      if (onCreate) {
        helper.validateOnCreate(entity.getImage(), validationContext.child("image", null, 0l));
      } else {
        helper.validateOnUpdate(entity.getImage(), validationContext.child("image", null, 0l));
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
    return id == 0l ? null : ((T) avatarRepository.findById(id).orElse(null));
  }

  @Override
  public void setDefaults(T entity) {
    if (entity.getImage() != null) {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      helper.setDefaults(entity.getImage());
    }
  }

  @Override
  public void compute(T entity) {
    if (entity.getImage() != null) {
      D3EImageEntityHelper helper = mutator.getHelperByInstance(entity.getImage());
      helper.compute(entity.getImage());
    }
  }

  public Boolean onDelete(T entity, boolean internal, EntityValidationContext deletionContext) {
    return true;
  }

  public void performImageAction(T entity) {
    if (entity.getImage() != null && entity.getImage().getFile() != null) {
      entity
          .getImage()
          .setFile(resourceHandler.saveImage(entity.getImage().getFile(), ListExt.List()));
    }
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    performImageAction(entity);
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    performImageAction(entity);
    return true;
  }
}

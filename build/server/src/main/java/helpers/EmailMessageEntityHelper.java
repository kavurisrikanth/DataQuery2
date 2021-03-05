package helpers;

import d3e.core.D3EResourceHandler;
import d3e.core.DFile;
import graphql.input.EmailMessageEntityInput;
import java.util.stream.Collectors;
import models.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.DFileRepository;
import store.EntityValidationContext;
import store.InputHelper;

@Service("EmailMessage")
public class EmailMessageEntityHelper<T extends EmailMessage, I extends EmailMessageEntityInput>
    extends D3EMessageEntityHelper<T, I> {
  @Autowired private DFileRepository dFileRepository;
  @Autowired private D3EResourceHandler resourceHandler;

  public EmailMessage newInstance() {
    return new EmailMessage();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newEmailMessage = ((T) new EmailMessage());
    newEmailMessage.setId(input.getId());
    return fromInput(input, newEmailMessage, helper);
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
    if (helper.has("bcc")) {
      entity.setBcc(input.bcc.stream().collect(Collectors.toList()));
    }
    if (helper.has("cc")) {
      entity.setCc(input.cc.stream().collect(Collectors.toList()));
    }
    if (helper.has("subject")) {
      entity.setSubject(input.subject);
    }
    if (helper.has("html")) {
      entity.setHtml(input.html);
    }
    if (helper.has("inlineAttachments")) {
      entity.setInlineAttachments(
          input.inlineAttachments.stream()
              .map(
                  (one) -> {
                    DFile existing = dFileRepository.findById(one.getId()).orElse(null);
                    if (existing == null) {
                      existing = helper.readDFile(one, "inlineAttachments");
                    }
                    return existing;
                  })
              .collect(Collectors.toList()));
    }
    if (helper.has("attachments")) {
      entity.setAttachments(
          input.attachments.stream()
              .map(
                  (one) -> {
                    DFile existing = dFileRepository.findById(one.getId()).orElse(null);
                    if (existing == null) {
                      existing = helper.readDFile(one, "attachments");
                    }
                    return existing;
                  })
              .collect(Collectors.toList()));
    }
    entity.updateMasters((o) -> {});
    return entity;
  }

  public EmailMessageEntityInput toInput(T entity) {
    I input = ((I) new EmailMessageEntityInput());
    input.setId(entity.getId());
    input.from = entity.getFrom();
    input.to = entity.getTo().stream().collect(java.util.stream.Collectors.toList());
    input.body = entity.getBody();
    input.createdOn = entity.getCreatedOn();
    input.bcc = entity.getBcc().stream().collect(java.util.stream.Collectors.toList());
    input.cc = entity.getCc().stream().collect(java.util.stream.Collectors.toList());
    input.subject = entity.getSubject();
    input.html = entity.isHtml();
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

  public void performFileAction(T entity) {
    entity.setInlineAttachments(
        entity.getInlineAttachments().stream()
            .filter((one) -> one != null)
            .map((one) -> resourceHandler.save(one))
            .collect(Collectors.toList()));
    entity.setAttachments(
        entity.getAttachments().stream()
            .filter((one) -> one != null)
            .map((one) -> resourceHandler.save(one))
            .collect(Collectors.toList()));
  }

  @Override
  public Boolean onCreate(T entity, boolean internal) {
    performFileAction(entity);
    return true;
  }

  @Override
  public Boolean onUpdate(T entity, boolean internal) {
    performFileAction(entity);
    return true;
  }

  public T getOld(long id) {
    return ((T) getById(id).clone());
  }
}

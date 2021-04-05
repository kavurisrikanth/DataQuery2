package helpers;

import graphql.input.ReportEntityInput;
import models.Report;
import models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.ReportRepository;
import rest.GraphQLInputContext;
import store.EntityHelper;
import store.EntityMutator;
import store.EntityValidationContext;
import store.InputHelper;

@Service("Report")
public class ReportEntityHelper<T extends Report, I extends ReportEntityInput>
    implements EntityHelper<T, I> {
  @Autowired protected EntityMutator mutator;
  @Autowired private ReportRepository reportRepository;

  public void setMutator(EntityMutator obj) {
    mutator = obj;
  }

  public Report newInstance() {
    return new Report();
  }

  @Override
  public T fromInput(I input, InputHelper helper) {
    if (input == null) {
      return null;
    }
    T newReport = ((T) new Report());
    newReport.setId(input.getId());
    return fromInput(input, newReport, helper);
  }

  @Override
  public T fromInput(I input, T entity, InputHelper helper) {
    if (helper.has("marks")) {
      entity.setMarks(input.marks);
    }
    if (helper.has("student")) {
      entity.setStudent(helper.readRef("Student", input.student));
    }
    entity.updateMasters((o) -> {});
    return entity;
  }

  @Override
  public void fromInput(T entity, GraphQLInputContext ctx) {
    if (ctx.has("marks")) {
      entity.setMarks(ctx.readDouble("marks"));
    }
    if (ctx.has("student")) {
      entity.setStudent(ctx.readRef("student", "Student"));
    }
    entity.updateMasters((o) -> {});
  }

  public ReportEntityInput toInput(T entity) {
    I input = ((I) new ReportEntityInput());
    input.setId(entity.getId());
    input.marks = entity.getMarks();
    input.student = entity.getStudent().getId();
    return input;
  }

  public void referenceFromValidations(T entity, EntityValidationContext validationContext) {}

  public void validateFieldStudent(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    Student it = entity.getStudent();
    if (it == null) {
      validationContext.addFieldError("student", "student is required.");
      return;
    }
  }

  public void validateInternal(
      T entity, EntityValidationContext validationContext, boolean onCreate, boolean onUpdate) {
    validateFieldStudent(entity, validationContext, onCreate, onUpdate);
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
    return id == 0l ? null : ((T) reportRepository.findById(id).orElse(null));
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

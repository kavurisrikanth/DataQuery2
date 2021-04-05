package graphql;

import classes.MutateResultStatus;
import classes.MutateStudentResult;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import d3e.core.CloneContext;
import d3e.core.CurrentUser;
import d3e.core.ListExt;
import graphql.input.StudentEntityInput;
import graphql.schema.DataFetchingEnvironment;
import helpers.StudentEntityHelper;
import java.util.ArrayList;
import java.util.Random;
import models.AnonymousUser;
import models.Student;
import models.User;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.ReportRepository;
import repository.jpa.StudentRepository;
import repository.jpa.UserRepository;
import repository.jpa.UserSessionRepository;
import security.AppSessionProvider;
import store.EntityMutator;
import store.InputHelper;
import store.InputHelperImpl;
import store.ValidationFailedException;

@org.springframework.stereotype.Component
public class Mutation implements GraphQLMutationResolver {
  @Autowired private EntityMutator mutator;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private ReportRepository reportRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private ObjectFactory<AppSessionProvider> provider;

  public Mutation() {}

  public MutateStudentResult createStudent(
      StudentEntityInput gqlInput, DataFetchingEnvironment env) {
    User currentUser = CurrentUser.get();
    if (!(currentUser instanceof AnonymousUser)) {
      return new MutateStudentResult(
          MutateResultStatus.AuthFail,
          null,
          ListExt.asList("Current user type does not have create permissions for this model."));
    }
    InputHelper helper = new InputHelperImpl(mutator, env.getArguments());
    try {
      Student newStudent = ((Student) helper.readChild(gqlInput, "input"));
      this.mutator.save(newStudent, false);
      return new MutateStudentResult(MutateResultStatus.Success, newStudent, new ArrayList<>());
    } catch (ValidationFailedException exp) {
      return new MutateStudentResult(
          exp.hasStatus() ? exp.getStatus() : MutateResultStatus.ValidationFail,
          null,
          exp.getErrors());
    }
  }

  public MutateStudentResult updateStudent(
      StudentEntityInput gqlInput, DataFetchingEnvironment env) {
    User currentUser = CurrentUser.get();
    if (!(currentUser instanceof AnonymousUser)) {
      return new MutateStudentResult(
          MutateResultStatus.AuthFail,
          null,
          ListExt.asList("Current user type does not have update permissions for this model."));
    }
    InputHelper helper = new InputHelperImpl(mutator, env.getArguments());
    StudentEntityHelper studentHelper = this.mutator.getHelper(gqlInput._type());
    Student currentStudent = studentRepository.findById(gqlInput.getId()).orElse(null);
    if (currentStudent == null) {
      return new MutateStudentResult(
          MutateResultStatus.BadRequest, null, ListExt.asList("Invalid ID."));
    }
    try {
      currentStudent.recordOld(CloneContext.forCloneable(currentStudent, false));
      Student newStudent =
          ((Student) helper.readUpdate(studentHelper, gqlInput.getId(), gqlInput, "input"));
      this.mutator.update(newStudent, false);
      return new MutateStudentResult(MutateResultStatus.Success, newStudent, null);
    } catch (ValidationFailedException exp) {
      return new MutateStudentResult(
          exp.hasStatus() ? exp.getStatus() : MutateResultStatus.ValidationFail,
          null,
          exp.getErrors());
    } finally {
      currentStudent.recordOld(new CloneContext(false));
    }
  }

  public MutateStudentResult deleteStudent(long gqlInputId, DataFetchingEnvironment env) {
    User currentUser = CurrentUser.get();
    if (!(currentUser instanceof AnonymousUser)) {
      return new MutateStudentResult(
          MutateResultStatus.AuthFail,
          null,
          ListExt.asList("Current user type does not have delete permissions for this model."));
    }
    StudentEntityHelper studentHelper = this.mutator.getHelper("Student");
    Student currentStudent = studentRepository.findById(gqlInputId).orElse(null);
    if (currentStudent == null) {
      return new MutateStudentResult(
          MutateResultStatus.BadRequest, null, ListExt.asList("Invalid ID"));
    }
    try {
      this.mutator.delete(currentStudent, false);
      return new MutateStudentResult(MutateResultStatus.Success, null, null);
    } catch (ValidationFailedException exp) {
      return new MutateStudentResult(MutateResultStatus.ValidationFail, null, exp.getErrors());
    }
  }

  private String generateToken() {
    char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    return generateRandomString(chars, 32);
  }

  private String generateCode() {
    char[] digits = "1234567890".toCharArray();
    return generateRandomString(digits, 4);
  }

  private String generateRandomString(char[] array, int length) {
    StringBuilder sb = new StringBuilder(length);
    Random rnd = new Random();
    for (int i = 0; i < length; i++) {
      char c = array[rnd.nextInt(array.length)];
      sb.append(c);
    }
    return sb.toString();
  }
}

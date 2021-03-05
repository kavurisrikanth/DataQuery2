package classes;

import java.util.List;
import models.Student;

public class MutateStudentResult extends MutateResult<Student> {
  public MutateStudentResult() {}

  public MutateStudentResult(MutateResultStatus status, Student value, List<String> errors) {
    this.status = status;
    this.value = value;
    this.errors = errors;
  }
}

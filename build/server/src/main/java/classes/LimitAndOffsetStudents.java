package classes;

import java.util.List;
import models.Student;

public class LimitAndOffsetStudents {
  public List<Student> items;

  public LimitAndOffsetStudents() {}

  public LimitAndOffsetStudents(List<Student> items) {
    this.items = items;
  }
}

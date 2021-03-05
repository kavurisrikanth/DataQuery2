package classes;

import java.util.List;
import models.Student;

public class LimitedStudents {
  public List<Student> items;

  public LimitedStudents() {}

  public LimitedStudents(List<Student> items) {
    this.items = items;
  }
}

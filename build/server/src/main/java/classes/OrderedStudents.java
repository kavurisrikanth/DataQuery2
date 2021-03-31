package classes;

import java.util.List;
import models.Student;

public class OrderedStudents {
  public List<Student> items;

  public OrderedStudents() {}

  public OrderedStudents(List<Student> items) {
    this.items = items;
  }
}

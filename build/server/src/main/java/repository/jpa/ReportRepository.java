package repository.jpa;

import java.util.List;
import models.Report;
import models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
  public List<Report> getByStudent(Student student);
}

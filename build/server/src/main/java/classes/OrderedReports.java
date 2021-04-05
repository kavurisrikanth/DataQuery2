package classes;

import java.util.List;
import models.Report;

public class OrderedReports {
  public List<Report> items;

  public OrderedReports() {}

  public OrderedReports(List<Report> items) {
    this.items = items;
  }
}

package classes;

import java.util.List;
import models.Report;

public class MyOrderedReports {
  public List<Report> items;

  public MyOrderedReports() {}

  public MyOrderedReports(List<Report> items) {
    this.items = items;
  }
}

package classes;

import java.util.List;
import models.Report;

public class MutateReportResult extends MutateResult<Report> {
  public MutateReportResult() {}

  public MutateReportResult(MutateResultStatus status, Report value, List<String> errors) {
    this.status = status;
    this.value = value;
    this.errors = errors;
  }
}

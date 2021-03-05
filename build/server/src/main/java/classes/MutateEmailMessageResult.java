package classes;

import java.util.List;
import models.EmailMessage;

public class MutateEmailMessageResult extends MutateResult<EmailMessage> {
  public MutateEmailMessageResult() {}

  public MutateEmailMessageResult(
      MutateResultStatus status, EmailMessage value, List<String> errors) {
    this.status = status;
    this.value = value;
    this.errors = errors;
  }
}

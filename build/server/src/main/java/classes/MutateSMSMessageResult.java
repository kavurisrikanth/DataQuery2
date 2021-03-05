package classes;

import java.util.List;
import models.SMSMessage;

public class MutateSMSMessageResult extends MutateResult<SMSMessage> {
  public MutateSMSMessageResult() {}

  public MutateSMSMessageResult(MutateResultStatus status, SMSMessage value, List<String> errors) {
    this.status = status;
    this.value = value;
    this.errors = errors;
  }
}

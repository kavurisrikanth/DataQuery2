package classes;

import java.util.List;
import models.OneTimePassword;

public class MutateOneTimePasswordResult extends MutateResult<OneTimePassword> {
  public MutateOneTimePasswordResult() {}

  public MutateOneTimePasswordResult(
      MutateResultStatus status, OneTimePassword value, List<String> errors) {
    this.status = status;
    this.value = value;
    this.errors = errors;
  }
}

package classes;

import java.util.List;
import models.AnonymousUser;

public class MutateAnonymousUserResult extends MutateResult<AnonymousUser> {
  public MutateAnonymousUserResult() {}

  public MutateAnonymousUserResult(
      MutateResultStatus status, AnonymousUser value, List<String> errors) {
    this.status = status;
    this.value = value;
    this.errors = errors;
  }
}

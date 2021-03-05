package classes;

import java.util.List;

public class MutateResult<T> {
  public MutateResultStatus status;
  public T value;
  public List<String> errors;
}

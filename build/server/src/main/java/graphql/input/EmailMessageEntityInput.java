package graphql.input;

import d3e.core.DFileEntityInput;
import java.util.List;

public class EmailMessageEntityInput extends D3EMessageEntityInput {
  public List<String> bcc;
  public List<String> cc;
  public String subject;
  public boolean html;
  public List<DFileEntityInput> inlineAttachments;
  public List<DFileEntityInput> attachments;

  public String _type() {
    return "EmailMessage";
  }
}

package graphql.input;

public class D3EMessageEntityInputUnionX implements store.IEntityInput {
  public String type;
  public EmailMessageEntityInput valueEmailMessage;
  public SMSMessageEntityInput valueSMSMessage;

  public String _type() {
    return type;
  }

  public long getId() {
    return 0l;
  }

  public D3EMessageEntityInput getValue() {
    switch (type) {
      case "EmailMessage":
        {
          return this.valueEmailMessage;
        }
      case "SMSMessage":
        {
          return this.valueSMSMessage;
        }
      default:
        {
          return null;
        }
    }
  }
}

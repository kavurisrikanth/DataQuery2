package rest;

import gqltosql.schema.DModel;
import gqltosql.schema.DModelType;
import gqltosql.schema.IModelSchema;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import models.AnonymousUser;
import models.Avatar;
import models.D3EImage;
import models.D3EMessage;
import models.EmailMessage;
import models.OneTimePassword;
import models.Report;
import models.ReportConfig;
import models.ReportConfigOption;
import models.SMSMessage;
import models.Student;
import models.User;
import models.UserSession;

@org.springframework.stereotype.Service
public class ModelSchema implements IModelSchema {
  private Map<String, DModel<?>> allTypes = new HashMap<>();

  @PostConstruct
  public void init() {
    createAllTables();
  }

  public DModel<?> getType(String type) {
    return allTypes.get(type);
  }

  public <T> DModel<T> getType2(String type) {
    return ((DModel<T>) allTypes.get(type));
  }

  private void addTable(DModel<?> model) {
    allTypes.put(model.getType(), model);
  }

  private void createAllTables() {
    addTable(new DModel<AnonymousUser>("AnonymousUser", "_anonymous_user", DModelType.ENTITY));
    addTable(new DModel<Avatar>("Avatar", "_avatar", DModelType.ENTITY));
    addTable(new DModel<D3EImage>("D3EImage", "_d3eimage", DModelType.EMBEDDED));
    addTable(new DModel<D3EMessage>("D3EMessage", "_d3emessage", DModelType.TRANSIENT));
    addTable(new DModel<EmailMessage>("EmailMessage", "_email_message", DModelType.TRANSIENT));
    addTable(
        new DModel<OneTimePassword>("OneTimePassword", "_one_time_password", DModelType.ENTITY));
    addTable(new DModel<Report>("Report", "_report", DModelType.ENTITY));
    addTable(new DModel<ReportConfig>("ReportConfig", "_report_config", DModelType.ENTITY));
    addTable(
        new DModel<ReportConfigOption>(
            "ReportConfigOption", "_report_config_option", DModelType.ENTITY));
    addTable(new DModel<SMSMessage>("SMSMessage", "_smsmessage", DModelType.TRANSIENT));
    addTable(new DModel<Student>("Student", "_student", DModelType.ENTITY));
    addTable(new DModel<User>("User", "_user", DModelType.ENTITY));
    addTable(new DModel<UserSession>("UserSession", "_user_session", DModelType.ENTITY));
    addAnonymousUserFields();
    addAvatarFields();
    addD3EImageFields();
    addD3EMessageFields();
    addEmailMessageFields();
    addOneTimePasswordFields();
    addReportFields();
    addReportConfigFields();
    addReportConfigOptionFields();
    addSMSMessageFields();
    addStudentFields();
    addUserFields();
    addUserSessionFields();
  }

  private void addAnonymousUserFields() {
    DModel<AnonymousUser> m = getType2("AnonymousUser");
    m.setParent(getType("User"));
    m.addPrimitive("id", "_id", (s) -> s.getId());
  }

  private void addAvatarFields() {
    DModel<Avatar> m = getType2("Avatar");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addReference("image", "_image_id", getType("D3EImage"), (s) -> s.getImage());
    m.addPrimitive("createFrom", "_create_from", (s) -> s.getCreateFrom());
  }

  private void addD3EImageFields() {
    DModel<D3EImage> m = getType2("D3EImage");
    m.addPrimitive("size", "_size", (s) -> s.getSize());
    m.addPrimitive("width", "_width", (s) -> s.getWidth());
    m.addPrimitive("height", "_height", (s) -> s.getHeight());
    m.addPrimitive("file", "_file_id", (s) -> s.getFile());
  }

  private void addD3EMessageFields() {
    DModel<D3EMessage> m = getType2("D3EMessage");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("from", "_from", (s) -> s.getFrom());
    m.addPrimitiveCollection("to", "_to", "_d3emessage_to", (s) -> s.getTo());
    m.addPrimitive("body", "_body", (s) -> s.getBody());
    m.addPrimitive("createdOn", "_created_on", (s) -> s.getCreatedOn());
  }

  private void addEmailMessageFields() {
    DModel<EmailMessage> m = getType2("EmailMessage");
    m.setParent(getType("D3EMessage"));
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitiveCollection("bcc", "_bcc", "_email_message_bcc", (s) -> s.getBcc());
    m.addPrimitiveCollection("cc", "_cc", "_email_message_cc", (s) -> s.getCc());
    m.addPrimitive("subject", "_subject", (s) -> s.getSubject());
    m.addPrimitive("html", "_html", (s) -> s.isHtml());
    m.addPrimitiveCollection(
        "inlineAttachments",
        "_inline_attachments_id",
        "_email_message_inline_attachments_id",
        (s) -> s.getInlineAttachments());
    m.addPrimitiveCollection(
        "attachments",
        "_attachments_id",
        "_email_message_attachments_id",
        (s) -> s.getAttachments());
  }

  private void addOneTimePasswordFields() {
    DModel<OneTimePassword> m = getType2("OneTimePassword");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("success", "_success", (s) -> s.isSuccess());
    m.addPrimitive("errorMsg", "_error_msg", (s) -> s.getErrorMsg());
    m.addPrimitive("token", "_token", (s) -> s.getToken());
    m.addPrimitive("code", "_code", (s) -> s.getCode());
    m.addReference("user", "_user_id", getType("User"), (s) -> s.getUser());
    m.addPrimitive("expiry", "_expiry", (s) -> s.getExpiry());
  }

  private void addReportFields() {
    DModel<Report> m = getType2("Report");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("marks", "_marks", (s) -> s.getMarks());
    m.addReference("student", "_student_id", getType("Student"), (s) -> s.getStudent());
  }

  private void addReportConfigFields() {
    DModel<ReportConfig> m = getType2("ReportConfig");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("identity", "_identity", (s) -> s.getIdentity());
    m.addReferenceCollection(
        "values",
        "_values_id",
        "_report_config_values",
        getType("ReportConfigOption"),
        (s) -> s.getValues());
  }

  private void addReportConfigOptionFields() {
    DModel<ReportConfigOption> m = getType2("ReportConfigOption");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("identity", "_identity", (s) -> s.getIdentity());
    m.addPrimitive("value", "_value", (s) -> s.getValue());
  }

  private void addSMSMessageFields() {
    DModel<SMSMessage> m = getType2("SMSMessage");
    m.setParent(getType("D3EMessage"));
    m.addPrimitive("id", "_id", (s) -> s.getId());
  }

  private void addStudentFields() {
    DModel<Student> m = getType2("Student");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("name", "_name", (s) -> s.getName());
  }

  private void addUserFields() {
    DModel<User> m = getType2("User");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("isActive", "_is_active", (s) -> s.isIsActive());
  }

  private void addUserSessionFields() {
    DModel<UserSession> m = getType2("UserSession");
    m.addPrimitive("id", "_id", (s) -> s.getId());
    m.addPrimitive("userSessionId", "_user_session_id", (s) -> s.getUserSessionId());
  }
}

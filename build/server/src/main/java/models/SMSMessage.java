package models;

import d3e.core.CloneContext;
import java.util.function.Consumer;
import store.DatabaseObject;

public class SMSMessage extends D3EMessage {
  private transient SMSMessage old;

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public SMSMessage getOld() {
    return this.old;
  }

  public void setOld(SMSMessage old) {
    this.old = old;
  }

  public void setOld(CloneContext ctx) {
    this.setOld(ctx.getFromCache(this));
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof SMSMessage && super.equals(a);
  }

  public SMSMessage deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public SMSMessage cloneInstance(SMSMessage cloneObj) {
    if (cloneObj == null) {
      cloneObj = new SMSMessage();
    }
    super.cloneInstance(cloneObj);
    return cloneObj;
  }

  public boolean transientModel() {
    return true;
  }

  public SMSMessage createNewInstance() {
    return new SMSMessage();
  }
}

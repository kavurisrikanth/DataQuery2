package models;

import d3e.core.CloneContext;
import java.util.function.Consumer;
import javax.persistence.Entity;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DatabaseObject;

@SolrDocument(collection = "AnonymousUser")
@Entity
public class AnonymousUser extends User {
  private transient AnonymousUser old;

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public AnonymousUser getOld() {
    return this.old;
  }

  public void setOld(AnonymousUser old) {
    this.old = old;
  }

  public void setOld(CloneContext ctx) {
    this.setOld(ctx.getFromCache(this));
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof AnonymousUser && super.equals(a);
  }

  public AnonymousUser deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public AnonymousUser cloneInstance(AnonymousUser cloneObj) {
    if (cloneObj == null) {
      cloneObj = new AnonymousUser();
    }
    super.cloneInstance(cloneObj);
    return cloneObj;
  }

  public AnonymousUser createNewInstance() {
    return new AnonymousUser();
  }
}

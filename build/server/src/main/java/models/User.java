package models;

import d3e.core.CloneContext;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "User")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends CreatableObject {
  @Field
  @ColumnDefault("false")
  private boolean isActive = false;

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public boolean isIsActive() {
    return this.isActive;
  }

  public void setIsActive(boolean isActive) {
    onPropertySet();
    this.isActive = isActive;
  }

  public String displayName() {
    return "";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof User && super.equals(a);
  }

  public User deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    User _obj = ((User) dbObj);
    _obj.setIsActive(isActive);
  }

  public User cloneInstance(User cloneObj) {
    super.cloneInstance(cloneObj);
    cloneObj.setIsActive(this.isIsActive());
    return cloneObj;
  }

  @Override
  public String toString() {
    return displayName();
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

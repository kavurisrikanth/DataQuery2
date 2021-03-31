package models;

import d3e.core.CloneContext;
import java.util.function.Consumer;
import javax.persistence.Entity;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "Student")
@Entity
public class Student extends CreatableObject {
  @Field private String name;
  private transient Student old;

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    onPropertySet();
    this.name = name;
  }

  public Student getOld() {
    return this.old;
  }

  public void setOld(DatabaseObject old) {
    this.old = ((Student) old);
  }

  public String displayName() {
    return "Student";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof Student && super.equals(a);
  }

  public Student deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    Student _obj = ((Student) dbObj);
    _obj.setName(name);
  }

  public Student cloneInstance(Student cloneObj) {
    if (cloneObj == null) {
      cloneObj = new Student();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setName(this.getName());
    return cloneObj;
  }

  public Student createNewInstance() {
    return new Student();
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

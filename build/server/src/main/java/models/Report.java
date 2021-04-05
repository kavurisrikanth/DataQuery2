package models;

import d3e.core.CloneContext;
import java.util.List;
import java.util.function.Consumer;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.solr.core.mapping.SolrDocument;
import store.DatabaseObject;
import store.ICloneable;

@SolrDocument(collection = "Report")
@Entity
public class Report extends CreatableObject {
  @Field
  @ColumnDefault("0.0")
  private double marks = 0.0d;

  @Field
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  private Student student;

  private transient Report old;

  public void updateMasters(Consumer<DatabaseObject> visitor) {
    super.updateMasters(visitor);
  }

  public double getMarks() {
    return this.marks;
  }

  public void setMarks(double marks) {
    onPropertySet();
    this.marks = marks;
  }

  public Student getStudent() {
    return this.student;
  }

  public void setStudent(Student student) {
    onPropertySet();
    this.student = student;
  }

  public Report getOld() {
    return this.old;
  }

  public void setOld(DatabaseObject old) {
    this.old = ((Report) old);
  }

  public String displayName() {
    return "Report";
  }

  @Override
  public boolean equals(Object a) {
    return a instanceof Report && super.equals(a);
  }

  public Report deepClone(boolean clearId) {
    CloneContext ctx = new CloneContext(clearId);
    return ctx.startClone(this);
  }

  public void deepCloneIntoObj(ICloneable dbObj, CloneContext ctx) {
    super.deepCloneIntoObj(dbObj, ctx);
    Report _obj = ((Report) dbObj);
    _obj.setMarks(marks);
    _obj.setStudent(student);
  }

  public Report cloneInstance(Report cloneObj) {
    if (cloneObj == null) {
      cloneObj = new Report();
    }
    super.cloneInstance(cloneObj);
    cloneObj.setMarks(this.getMarks());
    cloneObj.setStudent(this.getStudent());
    return cloneObj;
  }

  public Report createNewInstance() {
    return new Report();
  }

  public void collectCreatableReferences(List<Object> _refs) {
    super.collectCreatableReferences(_refs);
    _refs.add(this.student);
  }

  @Override
  public boolean _isEntity() {
    return true;
  }
}

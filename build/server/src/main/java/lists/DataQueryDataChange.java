package lists;

import java.util.List;

import org.json.JSONArray;

import classes.SubscriptionChangeType;

public class DataQueryDataChange {
  public SubscriptionChangeType changeType;
  public String path;
  public String oldPath;
  public List<NativeObj> nativeData;
  public JSONArray data;
}

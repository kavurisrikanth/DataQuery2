package classes;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import d3e.core.IntegerExt;
import d3e.core.IterableExt;
import d3e.core.ListExt;
import d3e.core.StringExt;

public class AutoGenerateUtil {
  private static String REPLACE_REGEX = "\\s+";
  
  private static enum CaseType {
    None,
    LowerCase,
    UpperCase,
    CamelCaseStartLower,
    CamelCaseStartUpper;
    
    private static CaseType fromNumber(long x) {
      int val = (int) x;
      switch (val) {
        case 0:
          return None;
        case 1:
          return LowerCase;
        case 2:
          return UpperCase;
        case 3:
          return CamelCaseStartLower;
        case 4:
          return CamelCaseStartUpper;
        default:
          return null;
      }
    }
  }

  public AutoGenerateUtil() {}

  public static String generateIdentity(String src, long caseType, String sanitizeWith, String prefix, String suffix) {
    if (src == null) {
      return null;
    }
    if (prefix == null) {
      prefix = "";
    }
    if (suffix == null) {
      suffix = "";
    }
    CaseType ct = CaseType.fromNumber(caseType);
    sanitizeWith = AutoGenerateUtil._modifyString(sanitizeWith, ct);
    List<String> modifiedPieces =
        IterableExt.toList(
            ListExt.map(
                StringExt.split(src, AutoGenerateUtil.REPLACE_REGEX),
                (str) -> {
                  if (ct == CaseType.CamelCaseStartLower || ct == CaseType.CamelCaseStartUpper) {
                    return AutoGenerateUtil._modifyString(str, CaseType.CamelCaseStartUpper);
                  }
                  return AutoGenerateUtil._modifyString(str, ct);
                }),
            false);
    String modifiedStr = ListExt.join(modifiedPieces, sanitizeWith);
    if (ct == CaseType.CamelCaseStartLower) {
      modifiedStr = AutoGenerateUtil._caseFirstChar(modifiedStr, true);
    }
    return prefix + modifiedStr + suffix;
  }

  public static String generateNextSequenceString(
      long startsFrom, long step, String prefix, String suffix, String old) {
    if (prefix == null) {
      prefix = "";
    }
    if (suffix == null) {
      suffix = "";
    }
    if (old == null) {
      return IntegerExt.toString(startsFrom);
    }
    String stripped = StringExt.substring(old, StringExt.length(prefix), 0l);
    stripped =
        StringExt.substring(stripped, 0l, StringExt.length(stripped) - StringExt.length(suffix));
    long oldInt = IntegerExt.tryParse(old, 0l);
    long newInt = AutoGenerateUtil.generateNextSequence(startsFrom, step, oldInt, false);
    return new StringBuilder(prefix).append(newInt).append(suffix).toString();
  }

  public static long generateNextSequence(long startsFrom, long step, long old, boolean hasOld) {
    if (!hasOld) {
      return startsFrom;
    }
    return old + step;
  }

  public static String toName(String str) {
    String[] pieces = unCamelCase(str);
    if (pieces.length > 0) {
      pieces[0] = StringUtils.capitalize(pieces[0]);
      for (int i = 1; i < pieces.length; i++) {
        pieces[i] = StringUtils.lowerCase(pieces[i]);
      }
    }
    return String.join(" ", pieces);
  }
  
  private static String[] unCamelCase(String str) {
    int i = 0, len = str.length();
    int prevType = -1;
    StringBuilder sb = new StringBuilder();
    List<String> pieces = ListExt.asList();
    while (i < len) {
      char currentChar = str.charAt(i);
      int type = Character.getType(currentChar);
      boolean upperNextToLower = prevType == Character.LOWERCASE_LETTER && type == Character.UPPERCASE_LETTER;
      if (prevType == -1 || type == prevType || !upperNextToLower) {
        sb.append(currentChar);
      } else {
        if (upperNextToLower) {
          pieces.add(sb.toString());
          sb = new StringBuilder();
        }
      }
      i++;
    }
    if (sb.length() != 0) {
      pieces.add(sb.toString());
    }
    return pieces.stream().toArray(String[]::new);
  }

  private static String _modifyString(String str, CaseType caseType) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    switch (caseType) {
      case CamelCaseStartLower:
        return _caseFirstChar(str, true);
      case CamelCaseStartUpper:
        return _caseFirstChar(str, false);
      case LowerCase:
        return str.toLowerCase();
      case UpperCase:
        return str.toUpperCase();
      default:
        return str;
    }
  }
  
  private static String _caseFirstChar(String str, boolean lower) {
    return lower ? StringUtils.uncapitalize(str) : StringUtils.capitalize(str);
  }
}

package d3e.core;

import classes.AllStudents;
import classes.LimitAndOffsetStudents;
import classes.LimitAndOffsetStudents2;
import classes.LimitAndOffsetStudents2Request;
import classes.LimitAndOffsetStudents3;
import classes.LimitAndOffsetStudents3Request;
import classes.LimitedStudents;
import classes.LoginResult;
import classes.MyReports;
import classes.OrderedReports;
import classes.OrderedStudents;
import classes.PassedOrderedReports;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lists.AllStudentsImpl;
import lists.LimitAndOffsetStudents2Impl;
import lists.LimitAndOffsetStudents3Impl;
import lists.LimitAndOffsetStudentsImpl;
import lists.LimitedStudentsImpl;
import lists.MyReportsImpl;
import lists.OrderedReportsImpl;
import lists.OrderedStudentsImpl;
import lists.PassedOrderedReportsImpl;
import models.AnonymousUser;
import models.OneTimePassword;
import models.Report;
import models.Student;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.AvatarRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.ReportConfigOptionRepository;
import repository.jpa.ReportConfigRepository;
import repository.jpa.ReportRepository;
import repository.jpa.StudentRepository;
import repository.jpa.UserRepository;
import repository.jpa.UserSessionRepository;
import security.AppSessionProvider;
import security.JwtTokenUtil;

@Service
public class QueryProvider {
  public static QueryProvider instance;
  @Autowired private JwtTokenUtil jwtTokenUtil;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private AvatarRepository avatarRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private ReportRepository reportRepository;
  @Autowired private ReportConfigRepository reportConfigRepository;
  @Autowired private ReportConfigOptionRepository reportConfigOptionRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private AllStudentsImpl allStudentsImpl;
  @Autowired private LimitAndOffsetStudentsImpl limitAndOffsetStudentsImpl;
  @Autowired private LimitAndOffsetStudents2Impl limitAndOffsetStudents2Impl;
  @Autowired private LimitAndOffsetStudents3Impl limitAndOffsetStudents3Impl;
  @Autowired private LimitedStudentsImpl limitedStudentsImpl;
  @Autowired private MyReportsImpl myReportsImpl;
  @Autowired private OrderedReportsImpl orderedReportsImpl;
  @Autowired private OrderedStudentsImpl orderedStudentsImpl;
  @Autowired private PassedOrderedReportsImpl passedOrderedReportsImpl;
  @Autowired private ObjectFactory<AppSessionProvider> provider;

  @PostConstruct
  public void init() {
    instance = this;
  }

  public static QueryProvider get() {
    return instance;
  }

  public AnonymousUser getAnonymousUserById(long id) {
    Optional<AnonymousUser> findById = anonymousUserRepository.findById(id);
    return findById.orElse(null);
  }

  public OneTimePassword getOneTimePasswordById(long id) {
    Optional<OneTimePassword> findById = oneTimePasswordRepository.findById(id);
    return findById.orElse(null);
  }

  public boolean checkTokenUniqueInOneTimePassword(long oneTimePasswordId, String token) {
    return oneTimePasswordRepository.checkTokenUnique(oneTimePasswordId, token);
  }

  public Report getReportById(long id) {
    Optional<Report> findById = reportRepository.findById(id);
    return findById.orElse(null);
  }

  public Student getStudentById(long id) {
    Optional<Student> findById = studentRepository.findById(id);
    return findById.orElse(null);
  }

  public AllStudents getAllStudents() {
    return allStudentsImpl.get();
  }

  public LimitAndOffsetStudents getLimitAndOffsetStudents() {
    return limitAndOffsetStudentsImpl.get();
  }

  public LimitAndOffsetStudents2 getLimitAndOffsetStudents2(LimitAndOffsetStudents2Request inputs) {
    return limitAndOffsetStudents2Impl.get(inputs);
  }

  public LimitAndOffsetStudents3 getLimitAndOffsetStudents3(LimitAndOffsetStudents3Request inputs) {
    return limitAndOffsetStudents3Impl.get(inputs);
  }

  public LimitedStudents getLimitedStudents() {
    return limitedStudentsImpl.get();
  }

  public MyReports getMyReports() {
    return myReportsImpl.get();
  }

  public OrderedReports getOrderedReports() {
    return orderedReportsImpl.get();
  }

  public OrderedStudents getOrderedStudents() {
    return orderedStudentsImpl.get();
  }

  public PassedOrderedReports getPassedOrderedReports() {
    return passedOrderedReportsImpl.get();
  }

  public LoginResult loginWithOTP(String token, String code) {
    OneTimePassword otp = oneTimePasswordRepository.getByToken(token);
    LoginResult loginResult = new LoginResult();
    loginResult.success = true;
    loginResult.userObject = otp.getUser();
    loginResult.token = token;
    return loginResult;
  }

  public AnonymousUser currentAnonymousUser() {
    return provider.getObject().getAnonymousUser();
  }
}

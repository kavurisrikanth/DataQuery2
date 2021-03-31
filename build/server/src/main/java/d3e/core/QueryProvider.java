package d3e.core;

import classes.AllStudents;
import classes.LimitedStudents;
import classes.LoginResult;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lists.AllStudentsImpl;
import lists.LimitedStudentsImpl;
import models.AnonymousUser;
import models.OneTimePassword;
import models.Student;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.AvatarRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.ReportConfigOptionRepository;
import repository.jpa.ReportConfigRepository;
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
  @Autowired private ReportConfigRepository reportConfigRepository;
  @Autowired private ReportConfigOptionRepository reportConfigOptionRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private AllStudentsImpl allStudentsImpl;
  @Autowired private LimitedStudentsImpl limitedStudentsImpl;
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

  public Student getStudentById(long id) {
    Optional<Student> findById = studentRepository.findById(id);
    return findById.orElse(null);
  }

  public AllStudents getAllStudents() {
    return allStudentsImpl.get();
  }

  public LimitedStudents getLimitedStudents() {
    return limitedStudentsImpl.get();
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

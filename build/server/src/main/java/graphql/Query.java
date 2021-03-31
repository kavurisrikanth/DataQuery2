package graphql;

import classes.LoginResult;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;
import java.util.UUID;
import models.AnonymousUser;
import models.OneTimePassword;
import models.Student;
import models.User;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.jpa.AnonymousUserRepository;
import repository.jpa.OneTimePasswordRepository;
import repository.jpa.StudentRepository;
import repository.jpa.UserRepository;
import repository.jpa.UserSessionRepository;
import security.AppSessionProvider;
import security.JwtTokenUtil;
import security.UserProxy;
import store.DatabaseObject;

@org.springframework.stereotype.Component
public class Query implements GraphQLQueryResolver {
  @Autowired private JwtTokenUtil jwtTokenUtil;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private AnonymousUserRepository anonymousUserRepository;
  @Autowired private OneTimePasswordRepository oneTimePasswordRepository;
  @Autowired private StudentRepository studentRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserSessionRepository userSessionRepository;
  @Autowired private ObjectFactory<AppSessionProvider> provider;

  public DatabaseObject getObject() {
    return null;
  }

  public AnonymousUser getAnonymousUserById(long id, DataFetchingEnvironment env) {
    Optional<AnonymousUser> findById = anonymousUserRepository.findById(id);
    return findById.orElse(null);
  }

  public OneTimePassword getOneTimePasswordById(long id, DataFetchingEnvironment env) {
    Optional<OneTimePassword> findById = oneTimePasswordRepository.findById(id);
    return findById.orElse(null);
  }

  public boolean checkTokenUniqueInOneTimePassword(
      long oneTimePasswordId, String token, DataFetchingEnvironment env) {
    return oneTimePasswordRepository.checkTokenUnique(oneTimePasswordId, token);
  }

  public Student getStudentById(long id, DataFetchingEnvironment env) {
    Optional<Student> findById = studentRepository.findById(id);
    return findById.orElse(null);
  }

  public LoginResult loginWithOTP(String token, String code) {
    OneTimePassword otp = oneTimePasswordRepository.getByToken(token);
    LoginResult loginResult = new classes.LoginResult();
    if (otp == null) {
      loginResult.success = false;
      loginResult.failureMessage = "Invalid token.";
      return loginResult;
    }
    if (otp.getExpiry().isBefore(java.time.LocalDateTime.now())) {
      loginResult.success = false;
      loginResult.failureMessage = "OTP validity has expired.";
      return loginResult;
    }
    if (!(code.equals(otp.getCode()))) {
      loginResult.success = false;
      loginResult.failureMessage = "Invalid code.";
      return loginResult;
    }
    User user = otp.getUser();
    if (user == null) {
      loginResult.success = false;
      loginResult.failureMessage = "Invalid user.";
      return loginResult;
    }
    loginResult.success = true;
    loginResult.userObject = ((User) Hibernate.unproxy(user));
    String type = null;
    String email = null;
    loginResult.token =
        jwtTokenUtil.generateToken(
            email, new UserProxy(type, user.getId(), UUID.randomUUID().toString()));
    return loginResult;
  }
}

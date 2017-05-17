package br.com.intro.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.intro.utils.ServiceMap;

@RestController
@RequestMapping(path = "/api/user")
public class UserService implements ServiceMap {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserVerificationRepository userVerificationRepository;

	private UserVerificationEmailSender emailSender = new UserVerificationEmailSender();

	@RequestMapping(method = RequestMethod.GET)
	public List<UserEntity> findAll() {
		return userRepository.findAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public void insert(@RequestBody UserEntity userEntity) {
		userRepository.save(userEntity);
		UserVerificationEntity userVerificationEntity = new UserVerificationEntity(userEntity.getUserName(),
				userEntity.getToken());
		userVerificationRepository.save(userVerificationEntity);
		emailSender.send(userVerificationEntity, userEntity);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public UserEntity update(@RequestBody UserEntity userEntity) {
		return userRepository.save(userEntity);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<UserEntity> delete(@PathVariable Long id) {
		UserEntity userFound = userRepository.findById(id);
		userRepository.delete(userFound);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(path = "/activateUser/{verificationKey}", method = RequestMethod.GET)
	public void activateUser(@PathVariable String verificationKey) {
		UserVerificationEntity userVerificationEntityFound = userVerificationRepository.findByVerificationKey(verificationKey);
		if (userVerificationEntityFound == null) throw new IllegalArgumentException("Conta de usuário já está ativada!");
		UserEntity userFound = userRepository.findByUserName(userVerificationEntityFound.getUserName());
		userFound.setVerified(true);
		userRepository.save(userFound);
		userVerificationRepository.delete(userVerificationEntityFound);
	}
}

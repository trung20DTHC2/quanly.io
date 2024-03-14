package vn.fs.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vn.fs.entities.Role;
import vn.fs.entities.User;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;

/**
 *
 *
 */
@Controller
public class RegisterController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	SendMailService sendMailService;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	HttpSession session;

	@GetMapping("/register")
	public ModelAndView registerForm(ModelMap model) {
		model.addAttribute("user", new User());
		return new ModelAndView("web/register", model);
	}

	@PostMapping("/register")
	public String register(ModelMap model, @Validated @ModelAttribute("user") User dto, BindingResult result,
						   @RequestParam("password") String password) {
		if (result.hasErrors()) {
			return "web/register";
		}
		if (!checkEmail(dto.getEmail())) {
			model.addAttribute("error", "Email này đã được sử dụng!");
			return "web/register";
		}

		dto.setPassword(bCryptPasswordEncoder.encode(password));
		dto.setRegisterDate(new Date());
		dto.setStatus(true);
		dto.setAvatar("user.png");
		dto.setRoles(Arrays.asList(new Role("ROLE_USER")));
		userRepository.save(dto);

		model.addAttribute("message", "Đăng kí thành công");
		return "web/login";
	}

	// check email
	public boolean checkEmail(String email) {
		List<User> list = userRepository.findAll();
		for (User c : list) {
			if (c.getEmail().equalsIgnoreCase(email)) {
				return false;
			}
		}
		return true;
	}


}

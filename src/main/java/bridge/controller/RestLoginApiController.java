package bridge.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import bridge.dto.UserDto;
import bridge.security.JwtTokenUtil;
import bridge.service.LoginService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RestLoginApiController {
	@Autowired
	private LoginService loginService;

	@ApiOperation(value="회원가입")
	@PostMapping("/api/regist")
	public ResponseEntity<Object> regist(@RequestBody UserDto userDto) throws Exception {
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>" + userDto);
		int registedCount = loginService.registUser(userDto);
		log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>" + userDto);
		if (registedCount > 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body(registedCount);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(registedCount);
		}

	}

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@GetMapping("/api/user")
	public ResponseEntity<UserDto> currentUserName(Authentication authentication) {
		try {
			UserDto userDto = (UserDto) authentication.getPrincipal();
			return ResponseEntity.status(HttpStatus.OK).body(userDto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(null);
		}
	}

	// 외부 로그인
	@PostMapping("/api/bridge/pass/login")
	public void loginPass(@RequestBody UserDto usersDto, HttpServletResponse response) throws Exception {
		UserDto usersDto1 = loginService.passInformation(usersDto);
		String jwtToken = jwtTokenUtil.generateToken(usersDto1);
		response.setHeader("token", jwtToken);
		response.getWriter().write(jwtToken);
	}

	@ApiOperation(value="아이디 중복 확인")
	@PostMapping("/api/idlist/{userId}")
	public int userIdCheck(@PathVariable("userId") String userId) throws Exception {
		int result = loginService.userIdCheck(userId);
		return result;
	}
	@ApiOperation(value="아이디 찾기")
	@PostMapping("/api/findid/{email}")
	public String findId(@PathVariable("email") String email) {
		String result = loginService.findId(email);
		return result;
	}
	@ApiOperation(value="비밀번호 찾기")
	@PutMapping("api/findPassword/{email}/{password}")
	public void findPassword(@PathVariable("email")String email,@PathVariable("password")String password) {
		loginService.findPassword(email,password);	
	}
	
}

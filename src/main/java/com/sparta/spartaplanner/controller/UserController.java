package com.sparta.spartaplanner.controller;

import com.sparta.spartaplanner.dto.UserRequestDto;
import com.sparta.spartaplanner.dto.UserResponseDto;
import com.sparta.spartaplanner.entity.User;
import com.sparta.spartaplanner.exception.FailedRequestException;
import com.sparta.spartaplanner.exception.FieldOverlapException;
import com.sparta.spartaplanner.exception.IdNotFoundException;
import com.sparta.spartaplanner.exception.PasswordFailException;
import com.sparta.spartaplanner.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository repository;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.repository = new UserRepository(jdbcTemplate);
    }

    // 사용자 생성 API
    @PostMapping("")
    public UserResponseDto createUser(@RequestBody UserRequestDto requestDto) {
        // 요청 Dto 에 포함된 내용으로 Entity 생성
        User user = new User(requestDto);

        // 요청에 포함된 이메일 주소가 중복되지는 않는 지 repository 를 통해 검사 후, 중복 시 예외 전달
        if (repository.ExistsByEmail(user.getEmail())) {throw new FieldOverlapException("email");}

        // Entity 를 DB 에 저장함과 동시에, id 및 시간을 부여받은 새 Entity 획득
        User resUser = repository.saveUser(user);

        // 해당 Entity 를 응답 Dto 로 반환
        return new UserResponseDto(resUser);
    }

    // 사용자 전체 조회 API
    @GetMapping("")
    public List<UserResponseDto> getAllUsers() {
        // 읽어들인 Entity 리스트 -> 응답 Dto 리스트 반환
        return repository.readAllUsers().stream().map(UserResponseDto::new).toList();
    }

    // 사용자 조회 API
    @GetMapping("{id}")
    public UserResponseDto getUser(@PathVariable Long id) {
        // 기본 키로 nullable 데이터 조회
        User user = repository.readUser(id);
        if (!Objects.isNull(user)) {
            // 존재할 시, 응답 Dto 로 반환
            return new UserResponseDto(user);
        } else {
            // 아닐 시, 예외 전달
            throw new IdNotFoundException("User", id);
        }
    }

    // 사용자 수정 API
    @PutMapping("{id}")
    public Long updateUser(@PathVariable Long id, @RequestBody UserRequestDto requestDto) {
        // id 에 해당하는 사용자 존재 확인
        User targetUser = repository.readUser(id);
        if (Objects.isNull(targetUser)) {throw new IdNotFoundException("User", id);}

        // 등록된 사용자와 요청 Dto 비밀번호 일치 여부 확인
        User requestUser = new User(requestDto);
        if (!requestUser.getPassword().equals(targetUser.getPassword())) {throw new PasswordFailException();}

        // 사용자 갱신 및 id 반환
        return repository.updateUser(id, requestUser);
    }

    // 사용자 삭제 API
    @DeleteMapping("{id}")
    public Long deleteUser(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        // id 에 해당하는 사용자 존재 확인
        User user = repository.readUser(id);
        if (Objects.isNull(user)) {throw new IdNotFoundException("User", id);}

        // 등록된 사용자와 요청 헤더 비밀번호 일치 여부 확인
        String password = headers.get("password");
        if (!password.equals(user.getPassword())) {throw new PasswordFailException();}

        // 사용자 삭제 및 id 반환
        return repository.deleteUser(id);
    }

    // 발생한 예외를 적절한 응답으로 처리하는 Handler
    @ExceptionHandler(FailedRequestException.class)
    public ResponseEntity<String> handleFailedRequestException(FailedRequestException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }

}

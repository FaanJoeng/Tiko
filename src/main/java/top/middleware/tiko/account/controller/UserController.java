package top.middleware.tiko.account.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.middleware.tiko.account.dto.User;
import top.middleware.tiko.account.service.UserService;
import top.middleware.tiko.core.dto.ResponseData;

@Api(value = "用户操作", tags = {"用户操作"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping(value = {"/sys/user"})
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/{userId}")
    public User find(@PathVariable(value = "userId") Long userId) {
        return userService.findById(userId);
    }

    @PostMapping
    public User save(@Validated @RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping(value = "{userId}")
    public ResponseData delete(@PathVariable(value = "userId") Long userId) {
        userService.delete(userId);

        return new ResponseData();
    }
}

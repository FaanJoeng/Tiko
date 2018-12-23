package top.middleware.tiko.account.mapper;

import org.apache.ibatis.annotations.Param;
import top.middleware.tiko.account.dto.User;

public interface UserMapper {
    User selectUserByName(@Param("username") String username);
}

package top.middleware.tiko.account.dto;

import lombok.Data;
import top.middleware.tiko.core.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Table(name = "sys_user_role")
public class UserRole extends BaseDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private Long roleId;
}

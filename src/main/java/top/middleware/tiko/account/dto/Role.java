package top.middleware.tiko.account.dto;

import lombok.Data;
import top.middleware.tiko.core.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Data
@Table(name = "sys_role")
public class Role extends BaseDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String roleCode;

    @NotBlank
    private String roleName;
}

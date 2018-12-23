package top.middleware.tiko.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO {
    private Long createBy;

    private Long modifiedBy;

    @Column(name = "gmt_create")
    private Date createTime;

    @Column(name = "gmt_modified")
    private Date modifiedTime;
}

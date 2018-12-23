package top.middleware.tiko.test.entity;

import lombok.Data;
import top.middleware.tiko.quick.annotation.Quick;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_table")
@Quick(requestMapping = "/sys/person")
@Data
public class Person {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
}

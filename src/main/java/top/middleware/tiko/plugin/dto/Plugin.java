package top.middleware.tiko.plugin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Data
@Entity
@Table(name = "sys_plugin")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plugin {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)    private Long id;

    private String name;
    private String description;
    private String version;
    private String author;
    @Column(name = "package")
    @JsonProperty("package")
    private String relatedPackage;

    @JsonIgnore
    private String tables;

    @JsonProperty("tables")
    @Transient
    private List<String> tablesList;

    // TODO 此方法与插件升级相关 暂时只对name字段进行了比对，升级还涉及版本号等字段
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Plugin plugin = (Plugin) o;

        return new EqualsBuilder()
                .append(name, plugin.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }
}

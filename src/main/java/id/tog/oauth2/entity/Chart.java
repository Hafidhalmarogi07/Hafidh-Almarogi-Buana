package id.tog.oauth2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import id.tog.oauth2.util.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Data
@Where(clause = "deleted IS NULL")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"created","updated", "deleted", "hibernate_lazy_initializer"})
@Table(name = "chart")
public class Chart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(targetEntity = User.class,fetch = FetchType.LAZY)
    private User user;

    @OneToOne(targetEntity = Item.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Item item;

    private Long itemFee;

    private Integer amount;

    private Long totalItemFee;




}

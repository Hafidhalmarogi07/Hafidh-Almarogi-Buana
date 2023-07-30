package id.tog.oauth2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import id.tog.oauth2.util.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Where(clause = "deleted IS NULL")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"updated", "deleted", "hibernate_lazy_initializer"})
@Table(name = "transaction")
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionCode;


    private Integer totalAmount;

    private Long totalCost;

    private String status;


    @OneToMany(targetEntity = TransactionDetail.class,fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "transaction")
    private List<TransactionDetail> detail;

    @JsonIgnore
    @ManyToOne(targetEntity = User.class,fetch = FetchType.LAZY)
    private User user;
}




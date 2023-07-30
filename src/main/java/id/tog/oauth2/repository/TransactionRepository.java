package id.tog.oauth2.repository;

import id.tog.oauth2.entity.Transaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Integer countAllByDeletedIsNull();
}

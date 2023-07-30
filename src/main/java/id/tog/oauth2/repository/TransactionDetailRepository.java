package id.tog.oauth2.repository;

import id.tog.oauth2.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionDetailRepository extends PagingAndSortingRepository<TransactionDetail, Long>, JpaSpecificationExecutor<TransactionDetail> {

}

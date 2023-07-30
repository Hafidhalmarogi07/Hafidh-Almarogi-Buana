package id.tog.oauth2.repository;

import id.tog.oauth2.entity.Chart;
import id.tog.oauth2.entity.Item;
import id.tog.oauth2.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ChartRepository extends PagingAndSortingRepository<Chart, Long>, JpaSpecificationExecutor<Chart> {

    List<Chart> findAllByUser(User user);

    Chart findByUserAndItem(User user, Item item);

    Chart findByIdAndUser(Long id, User user);
}

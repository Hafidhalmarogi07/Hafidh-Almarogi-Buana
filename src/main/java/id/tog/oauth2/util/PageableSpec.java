package id.tog.oauth2.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class PageableSpec<T> {
    private Specification<T> specification;

    private Pageable pageable;

    public Specification<T> getSpecification() {
        return specification;
    }

    public void setSpecification(Specification<T> specification) {
        this.specification = specification;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}

package id.tog.oauth2.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.Map;

public class PageableUtils {
    public static Pageable of(Map<String, String> params) {
        return of(params, 10);
    }

    public static Pageable of(Map<String, String> params, Integer defaultSize) {
        String strSize = "" + defaultSize;
        String strPage = "0";
        String strSort = "";
        if (params.containsKey("page")) {
            strPage = params.get("page");
            if (StringUtils.isEmpty(strPage)) {
                strPage = "0";
            }
        }

        if (params.containsKey("size")) {
            strSize = params.get("size");
            if (StringUtils.isEmpty(strSize)) {
                strSize = "0";
            }
        }

        if (params.containsKey("sort")) {
            strSort = params.get("sort");
            if (StringUtils.isEmpty(strSort)) {
                strSort = "";
            }
        }

        Integer page = Integer.valueOf(strPage);
        Integer size = Integer.valueOf(strSize);
        String sort = strSort;

        return of(page, size, sort);
    }

    public static Pageable of(@NonNull Integer page,
                              @NonNull Integer size,
                              @NonNull String sort) {
        page = (page < 1 ? 1 : page) - 1;

        boolean ascending = true;
        if (!StringUtils.isEmpty(sort) && !"-".equals(sort)) {
            if (sort.startsWith("-")) {
                ascending = false;
                sort = sort.substring(1);
            }
        } else {
            sort = null;
        }

        Pageable filter = PageRequest.of(page, size);
        if (null != sort) {
            Sort sorting;
            if (!ascending) {
                sorting = Sort.by(sort).descending();
            } else {
                sorting = Sort.by(sort);
            }
            filter = PageRequest.of(page, size, sorting);
        }

        return filter;
    }
}

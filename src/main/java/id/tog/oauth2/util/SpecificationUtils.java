package id.tog.oauth2.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import id.tog.oauth2.responseException.BadRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"unchecked", "ArraysAsListWithZeroOrOneArgument"})
public class SpecificationUtils {
    public static final List<String> OPERATORS = Arrays.asList(
            "=", ">", "<", ">=", "<=", "!=", "<>", "between", "like", "in", "!in"
    );

    public static final List<String> COMBINERS = Arrays.asList(
            "and", "or"
    );

    public static final List<String> digits = new ArrayList<>(Arrays.asList("long", "double", "integer", "float"));

    public static Map<String, Object> qs(Map<String, String> params) {
        return qs(params, null);
    }

    public static Map<String, Object> qs(Map<String, String> params, Integer defaultOffset) {
        Map<String, Object> result = new HashMap<>();
        String sort = null;
        Sort.Direction direction = Sort.Direction.ASC;
        int page = 1;
        int start = null == defaultOffset ? 20 : defaultOffset;
        String filters = "[]";

        if (params.containsKey("sort") && !StringUtils.isEmpty(params.get("sort"))) {
            sort = params.get("sort");
            if (sort.startsWith("-")) {
                sort = params.get("sort").substring(1);
                direction = Sort.Direction.DESC;
            }
        }

        if (params.containsKey("page") && !StringUtils.isEmpty(params.get("page"))) {
            Pattern pattern = Pattern.compile("^[\\d]+$");
            if (pattern.matcher(params.get("page")).matches()) {
                page = Integer.valueOf(params.get("page"));
            }
        }

        if (params.containsKey("size") && !StringUtils.isEmpty(params.get("size"))) {
            Pattern pattern = Pattern.compile("^[\\d]+$");
            if (pattern.matcher(params.get("size")).matches()) {
                start = Integer.valueOf(params.get("size"));
            }
        }

        if (params.containsKey("filters") && !StringUtils.isEmpty(params.get("filters"))) {
            filters = params.get("filters");
        }

        if (null != sort) {
            List<String> sorts = new ArrayList<>();
            for (String sortField: sort.split("\\.")) {
                sorts.add(CaseUtils.toCamelCase(sortField));
            }
            sort = String.join(".", sorts);
        }

        result.put("page", page - 1 < 0 ? 0 : page - 1);
        result.put("size", start);
        result.put("sort", sort);
        result.put("direction", direction);
        result.put("filters", createFilter(filters));

        return result;
    }

    private static List<Spec> createFilter(String filters) throws BadRequest {
        List<Object[]> parsedFilters = SpecificationUtils.fromJson(filters, new TypeReference<List<Object[]>>() {});

        List<Spec> result = new ArrayList<>();

        if (null == parsedFilters) {
            return result;
        }

        for (Object[] objects: parsedFilters) {
            if (objects.length > 3) {
                throw new BadRequest("Parameter too long");
            }
            if (objects.length == 2) {
                objects = new Object[] {objects[0], "=", objects[1]};
            }

            if (objects.length == 3) {
                Spec spec = new Spec();
                try {
                    spec.setField((String) objects[0]);
                } catch (Exception e) {
                    throw new BadRequest("Invalid field name");
                }

                try {
                    spec.setOperator((String) objects[1]);
                } catch (Exception e) {
                    throw new BadRequest("Invalid operator format");
                }

                if (!OPERATORS.contains(spec.getOperator())) {
                    throw new BadRequest("Invalid operator "+ spec.getOperator());
                }

                if ("in".equals(spec.getOperator()) || "between".equals(spec.getOperator())) {
                    try {
                        if (objects[2] instanceof String) {
                            objects = new Object[] {objects[0], objects[1], Arrays.asList(objects[2])};
                        }
                        List<Object> objectValues = (List<Object>) objects[2];
                        List<Object> values = new ArrayList<>();
                        for (Object o: objectValues) {
                            int previousIndex = objectValues.size() - 1;
                            if (objectValues.size() > 0 &&
                                    !objectValues.get(previousIndex).getClass().getTypeName().equals(o.getClass().getTypeName()) &&
                                    !digits.contains(objectValues.get(previousIndex).getClass().getSimpleName().toLowerCase())
                            ) {
                                throw new BadRequest("operator value does not match previous");
                            }
                            if (DateFormat.isDate(o)) {
                                values.add(DateFormat.parse((String) o));
                            } else {
                                values.add(o);
                            }
                        }

                        spec.setListValue(values);
                    } catch (Exception e) {
                        throw new BadRequest("Invalid array values "+spec.getOperator()+"(): " + e.getMessage());
                    }
                } else {
                    try {
                        if (DateFormat.isDate(objects[2])) {
                            spec.setValue(DateFormat.parse((String) objects[2]));
                        } else {
                            spec.setValue(objects[2]);
                        }
                    } catch (Exception e) {
                        throw new BadRequest("Invalid value "+objects[2]+": "+e.getMessage());
                    }
                }

                if (Pattern.compile("^(>|<|>=|<=)$").matcher(spec.getOperator()).matches() &&
                        null != spec.getValue() &&
                        (!digits.contains(spec.getType()) && !"date".equals(spec.getOperator()))) {
                    throw new BadRequest("Invalid arithmetic value");
                } else if ("between".equals(spec.getOperator()) && spec.getListValue().size() != 2) {
                    throw new BadRequest("Between operator must be contain two values");
                }

                result.add(spec);
            } else if (objects.length == 1) {
                Spec spec = new Spec();
                try {
                    spec.setOperator((String) objects[0]);
                    spec.setOperator(spec.getOperator().toLowerCase());
                } catch (Exception e) {
                    throw new BadRequest("Invalid operator "+e.getMessage());
                }
                if (!COMBINERS.contains(spec.getOperator())) {
                    throw new BadRequest("Invalid operator, only and or allowed");
                }

                spec.setType("operator");
                result.add(spec);
                if (result.size() == parsedFilters.size()) {
                    throw new BadRequest("Filter cannot ends with operator");
                }
            }
        }

        return result;
    }

    private static <T> T fromJson(String filters, TypeReference<T> type) {
        if (null == filters) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        T result = null;
        try {
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
            result = objectMapper.readValue(((String)decodeValue(filters)), type);
        } catch (IOException e) {
            System.out.println("ERROR QUERY FILTERS: "+e.getMessage());
        }

        return result;
    }

    private static Object decodeValue(Object value) {
        if (!(value instanceof String)) {
            return value;
        }
        try {
            return URLDecoder.decode((String) value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static <T> PageableSpec<T> of(Map<String, String> params) {
        return of(params, 10);
    }

    public static <T> PageableSpec<T> of(Map<String, String> params, Integer defaultOffset) {
        PageableSpec<T> pageableSpec = new PageableSpec<>();
        Map<String, Object> map = qs(params, defaultOffset);
        pageableSpec.setPageable(pageOf(map));
        pageableSpec.setSpecification(specOf((List<Spec>) map.get("filters")));

        return pageableSpec;
    }

    public static Pageable pageOf(final Map<String, Object> qs) {
        Integer page = (Integer) qs.get("page");
        Integer start = (Integer) qs.get("size");
        if (null == qs.get("sort")) {
            return PageRequest.of(page, start);
        }
        Sort sort = Sort.by((Sort.Direction) qs.get("direction"), (String) qs.get("sort"));
        return PageRequest.of(page, start, sort);
    }

    public static <T> Specification<T> specOf(final List<Spec> specs) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                if (null == specs) {
                    return null;
                }

                Predicate predicate = null;
                String previousOperator = null;

                for (Spec spec: specs) {
                    Predicate p = null;
                    Path<Object> path = null;
                    if (null != spec.getField()) {
                        String[] fields = spec.getField().split("\\.");
                        String firstField = CaseUtils.toCamelCase(fields[0]);
                        path = root.get(firstField);
                        if (fields.length > 1) {
                            Join<Object, Object> join = root.join(fields[0]);
                            for (int c = 1; c < fields.length; c++) {
                                String fieldName = CaseUtils.toCamelCase(fields[c]);
                                if (c + 1 == fields.length) {
                                    path = join.get(fieldName);
                                    break;
                                }
                                join = join.join(fieldName);
                            }
                        }
                    }
                    if ("operator".equals(spec.getType()) || null == path) {
                        previousOperator = spec.getOperator();
                        continue;
                    } else if ("list".equals(spec.getType())) {
                        List<Object> values = spec.getListValue();
                        if ("between".equals(spec.getOperator())) {
                            String bType = values.get(0).getClass().getSimpleName().toLowerCase();
                            if ("double".equals(bType)) {
                                p = criteriaBuilder.between(path.as(Double.class),
                                        (Double) values.get(0),
                                        (Double) values.get(1));
                            } else if ("long".equals(bType)) {
                                p = criteriaBuilder.between(path.as(Long.class),
                                        (Long) values.get(0),
                                        (Long) values.get(1));
                            } else if ("float".equals(bType)) {
                                p = criteriaBuilder.between(path.as(Float.class),
                                        (Float) values.get(0),
                                        (Float) values.get(1));
                            } else if ("integer".equals(bType)) {
                                p = criteriaBuilder.between(path.as(Integer.class),
                                        (Integer) values.get(0),
                                        (Integer) values.get(1));
                            } else if ("date".equals(bType)) {
                                p = criteriaBuilder.between(path.as(Date.class),
                                        (Date) values.get(0),
                                        (Date) values.get(1));
                            } else if ("boolean".equals(bType)) {
                                p = criteriaBuilder.between(path.as(Boolean.class),
                                        (Boolean) values.get(0),
                                        (Boolean) values.get(1));
                            }
                        } else if("in".equals(spec.getOperator())) {
                            CriteriaBuilder.In<Object> ins = criteriaBuilder.in(path);
                            for (Object obj: values) {
                                ins.value(obj);
                            }
                            p = ins;
                        }
                    } else {
                        Long longValue = null;
                        Double doubleValue = null;
                        Integer integerValue = null;
                        Float floatValue = null;
                        Boolean booleanValue = null;
                        String stringValue = null;
                        Date dateValue = null;

                        if ("double".equals(spec.getType())) {
                            doubleValue = (Double) spec.getValue();
                        } else if ("long".equals(spec.getType())) {
                            longValue = (Long) spec.getValue();
                        } else if ("float".equals(spec.getType())) {
                            floatValue = (Float) spec.getValue();
                        } else if ("integer".equals(spec.getType())) {
                            integerValue = (Integer) spec.getValue();
                        } else if ("boolean".equals(spec.getType())) {
                            booleanValue = (Boolean) spec.getValue();
                        } else if ("string".equals(spec.getType())) {
                            stringValue = (String) spec.getValue();
                        } else if ("date".equals(spec.getType())) {
                            dateValue = (Date) spec.getValue();
                        }

                        if (">".equals(spec.getOperator())) {
                            if (null != doubleValue)
                                p = criteriaBuilder.greaterThan(path.as(Double.class), doubleValue);
                            if (null != longValue)
                                p = criteriaBuilder.greaterThan(path.as(Long.class), longValue);
                            if (null != floatValue)
                                p = criteriaBuilder.greaterThan(path.as(Float.class), floatValue);
                            if (null != integerValue)
                                p = criteriaBuilder.greaterThan(path.as(Integer.class), integerValue);
                            if (null != dateValue)
                                p = criteriaBuilder.greaterThan(path.as(Date.class), dateValue);
                        } else if ("<".equals(spec.getOperator())) {
                            if (null != doubleValue)
                                p = criteriaBuilder.lessThan(path.as(Double.class), doubleValue);
                            if (null != longValue)
                                p = criteriaBuilder.lessThan(path.as(Long.class), longValue);
                            if (null != floatValue)
                                p = criteriaBuilder.lessThan(path.as(Float.class), floatValue);
                            if (null != integerValue)
                                p = criteriaBuilder.lessThan(path.as(Integer.class), integerValue);
                            if (null != dateValue)
                                p = criteriaBuilder.lessThan(path.as(Date.class), dateValue);
                        } else if (">=".equals(spec.getOperator())) {
                            if (null != doubleValue)
                                p = criteriaBuilder.greaterThanOrEqualTo(path.as(Double.class), doubleValue);
                            if (null != longValue)
                                p = criteriaBuilder.greaterThanOrEqualTo(path.as(Long.class), longValue);
                            if (null != floatValue)
                                p = criteriaBuilder.greaterThanOrEqualTo(path.as(Float.class), floatValue);
                            if (null != integerValue)
                                p = criteriaBuilder.greaterThanOrEqualTo(path.as(Integer.class), integerValue);
                            if (null != dateValue)
                                p = criteriaBuilder.greaterThanOrEqualTo(path.as(Date.class), dateValue);
                        } else if ("<=".equals(spec.getOperator())) {
                            if (null != doubleValue)
                                p = criteriaBuilder.lessThanOrEqualTo(path.as(Double.class), doubleValue);
                            if (null != longValue)
                                p = criteriaBuilder.lessThanOrEqualTo(path.as(Long.class), longValue);
                            if (null != floatValue)
                                p = criteriaBuilder.lessThanOrEqualTo(path.as(Float.class), floatValue);
                            if (null != integerValue)
                                p = criteriaBuilder.lessThanOrEqualTo(path.as(Integer.class), integerValue);
                            if (null != dateValue)
                                p = criteriaBuilder.lessThanOrEqualTo(path.as(Date.class), dateValue);
                        } else if ("!=".equals(spec.getOperator()) || "<>".equals(spec.getOperator())) {
                            if (null != doubleValue)
                                p = criteriaBuilder.notEqual(path.as(Double.class), doubleValue);
                            if (null != longValue)
                                p = criteriaBuilder.notEqual(path.as(Long.class), longValue);
                            if (null != floatValue)
                                p = criteriaBuilder.notEqual(path.as(Float.class), floatValue);
                            if (null != integerValue)
                                p = criteriaBuilder.notEqual(path.as(Integer.class), integerValue);
                            if (null != booleanValue)
                                p = criteriaBuilder.notEqual(path.as(Boolean.class), booleanValue);
                            if (null != stringValue)
                                p = criteriaBuilder.notEqual(path.as(String.class), stringValue);
                            if (null != dateValue)
                                p = criteriaBuilder.notEqual(path.as(Date.class), dateValue);
                        } else if ("=".equals(spec.getOperator())) {
                            if (null != doubleValue)
                                p = criteriaBuilder.equal(path.as(Double.class), doubleValue);
                            if (null != longValue)
                                p = criteriaBuilder.equal(path.as(Long.class), longValue);
                            if (null != floatValue)
                                p = criteriaBuilder.equal(path.as(Float.class), floatValue);
                            if (null != integerValue)
                                p = criteriaBuilder.equal(path.as(Integer.class), integerValue);
                            if (null != booleanValue)
                                p = criteriaBuilder.equal(path.as(Boolean.class), booleanValue);
                            if (null != stringValue)
                                p = criteriaBuilder.equal(path.as(String.class), stringValue);
                            if (null != dateValue)
                                p = criteriaBuilder.equal(path.as(Date.class), dateValue);
                        } else if ("like".equals(spec.getOperator()) && null != stringValue) {
                            p = criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                    "%"+stringValue.toLowerCase()+"%");
                        }
                    }

                    if (null != p) {
                        if (null == predicate) {
                            predicate = p;
                        } else {
                            if ("and".equals(previousOperator)) {
                                predicate = criteriaBuilder.and(predicate, p);
                            } else {
                                predicate = criteriaBuilder.or(predicate, p);
                            }
                            previousOperator = null;
                        }
                    }
                }

                return predicate;
            }
        };
    }
}

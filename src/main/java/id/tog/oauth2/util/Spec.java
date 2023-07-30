package id.tog.oauth2.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Spec {
    private String type;

    private String field;

    private String operator;

    private Object value;

    private List<Object> listValue;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<Object> getListValue() {
        return listValue;
    }

    public void setListValue(List<Object> listValue) {
        this.type = "list";
        this.listValue = listValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (null == value) {
            this.type = "null";
            return;
        }
        this.type = value.getClass().getSimpleName().toLowerCase();
        this.value = value;
    }
}

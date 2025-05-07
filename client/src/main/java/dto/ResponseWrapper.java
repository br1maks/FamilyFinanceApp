package dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResponseWrapper<T> {
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    @JsonIgnore
    private Map<String, Object> additionalFields = new HashMap<>();

    public ResponseWrapper() {
    }

    public ResponseWrapper(int code, String message, T data, String error) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    @JsonAnySetter
    public void setAdditionalField(String key, Object value) {
        this.additionalFields.put(key, value);
    }
}
package ru.t1.java.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricDto {
    @JsonProperty("method_name")
    String methodName;
    @JsonProperty("method_params")
    Object[] methodParams;
    @JsonProperty("execution_time")
    Long executionTime;
}

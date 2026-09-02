package lk.raminsenanayake.globaltrade_logistics.ejb_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchDispatchResult implements Serializable {
    private int totalProcessed;
    private int totalSucceeded;
    private int totalFailed;
    private List<String> generatedTrackingNumbers;
    private List<String> errors;
}

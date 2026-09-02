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
public class BookingSummary implements Serializable {
    private String senderUsername;
    private String origin;
    private String destination;
    private String carrierCode;
    private String serviceLevel;
    private List<BookingItemDto> items;
    private double totalWeightKg;
    private double totalDeclaredValue;
    private double estimatedCostUSD;
}

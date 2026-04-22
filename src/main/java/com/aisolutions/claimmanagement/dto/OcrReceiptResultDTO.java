package com.aisolutions.claimmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Shape of an OCR parse result exchanged with the frontend.
 * Input to applyTraining() and the "ocr" half of recordCorrection().
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrReceiptResultDTO {
    private String merchantName;
    private String receiptNumber;
    private String receiptDate;      // ISO yyyy-MM-dd
    private BigDecimal receiptAmount;
    private String rawText;
}

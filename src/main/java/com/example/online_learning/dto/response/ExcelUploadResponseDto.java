package com.example.online_learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelUploadResponseDto {
    private Integer successCount;
    private Integer errorCount;
    private List<ExcelErrorDto> errors;
    private String message;
}

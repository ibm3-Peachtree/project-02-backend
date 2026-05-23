package com.ruttu.project_02_backend.dto.routine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OdsayXYDto {
    private BigDecimal sx; // start x
    private BigDecimal sy; // start y
    private BigDecimal ex; // end x
    private BigDecimal ey;  // end y
}

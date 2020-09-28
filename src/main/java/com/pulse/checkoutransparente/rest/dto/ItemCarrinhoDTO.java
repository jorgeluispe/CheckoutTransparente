package com.pulse.checkoutransparente.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCarrinhoDTO {
    private Integer produto;
    private Integer quantidade;
}

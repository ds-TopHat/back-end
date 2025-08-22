package com.mathfusion.domain.wrongnote.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PdfRequest {
    private List<String> problemImageUrls;
}

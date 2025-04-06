package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Label {
    private int id;
    private String name;
    private ImageUrls iconUrls;
}

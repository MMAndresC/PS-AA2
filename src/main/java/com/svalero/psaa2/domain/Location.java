package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int id;
    private String name;
    private boolean isCountry;
    private String countryCode;
    private String localizedName;
}

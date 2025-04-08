package com.svalero.psaa2.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WarFrequencyStructure {
    private final String value;
    private final String translate;

    @Override
    public String toString() {
        return translate;
    }
}

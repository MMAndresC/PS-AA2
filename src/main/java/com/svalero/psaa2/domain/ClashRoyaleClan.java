package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClashRoyaleClan {

    private String tag;

    private String name;

    private int rank;

    private int previousRank;

    private Location location;

    private int clanScore;

    private int members;

    private int badgeId;

}

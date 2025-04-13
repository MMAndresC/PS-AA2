package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    private String tag;
    private String name;
    private String role;
    private int townHallLevel;
    private int expLevel;
    private League league;
    private int trophies;
    private int builderBaseTrophies;
    private int clanRank;
    private int previousClanRank;
    private int donations;
    private int donationsReceived;
    private PlayerHouse playerHouse;
    private BasicInfo builderBaseLeague;
}

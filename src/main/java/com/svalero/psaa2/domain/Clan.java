package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clan {

    private String tag;

    private int clanLevel;

    private String warFrequency;

    private int warWinStreak;

    private int warWins;

    private int warTies;

    private Language chatLanguage;

    private int clanBuilderBasePoints;

    private int clanCapitalPoints;

    private int requiredTrophies;

    private int requiredBuilderBaseTrophies;

    private int requiredTownhallLevel;

    private boolean isFamilyFriendly;

    private boolean isWarLogPublic;

    private int warLosses;

    private int clanPoints;

    private List<Label> labels;

    private String name;

    private Location location;

    private String type;

    private int members;

    private String description;

    private ImageUrls badgeUrls;

    private BasicInfo warLeague;

    private BasicInfo capitalLeague;

    // Test api response
    @Override
    public String toString() {
        return name + " (" + tag + ")";
    }
}

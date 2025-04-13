package com.svalero.psaa2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopClanMembers {
    private String clanName;
    private String locationName;
    private String badgeUrl;
    private int rank;
    private int previousRank;
    private List<Member> members;
}

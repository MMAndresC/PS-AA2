package com.svalero.psaa2.utils;


import com.svalero.psaa2.domain.Clan;
import com.svalero.psaa2.domain.Language;
import com.svalero.psaa2.domain.Location;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CreateCsv {

    // Replace null & commas
    public static String sanitizeStrings(String str) {
        if(str == null) return "";
        if(str.contains(","))
            str = str.replaceAll(",", " ");
        if(str.contains("\"")) //Comillas dobles cerrarlas
            str =  "\"" + str.replace("\"", "\"\"") + "\"";
        return str;
    }

    public static File exportClan(ObservableList<Clan> clans, String filePath) throws IOException {
        String header = "Tag,Name,Level,Type,War Frequency,War Wins,War Ties,War Losses,War Win Streak,Clan Points," +
                "Clan Builder Base Points,Clan Capital Points,Required Trophies,Required Builder Base Trophies," +
                "Required Townhall Level,Family Friendly,War Log Public,Members,Location,Country Code," +
                "Chat language,Language code\n";

        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            //Header csv
            writer.write(header);
            for (Clan clan : clans) {
                Location location = clan.getLocation();
                Language chatLanguage = clan.getChatLanguage();
                writer.write(String.join(",", List.of(
                        sanitizeStrings(clan.getTag()),
                        sanitizeStrings(clan.getName()),
                        String.valueOf(clan.getClanLevel()),
                        sanitizeStrings(clan.getType()),
                        sanitizeStrings(clan.getWarFrequency()),
                        String.valueOf(clan.getWarWins()),
                        String.valueOf(clan.getWarTies()),
                        String.valueOf(clan.getWarLosses()),
                        String.valueOf(clan.getWarWinStreak()),
                        String.valueOf(clan.getClanPoints()),
                        String.valueOf(clan.getClanBuilderBasePoints()),
                        String.valueOf(clan.getClanCapitalPoints()),
                        String.valueOf(clan.getRequiredTrophies()),
                        String.valueOf(clan.getRequiredBuilderBaseTrophies()),
                        String.valueOf(clan.getRequiredTownhallLevel()),
                        String.valueOf(clan.isFamilyFriendly()),
                        String.valueOf(clan.isWarLogPublic()),
                        String.valueOf(clan.getMembers()),
                        sanitizeStrings(location != null ? location.getName() : ""),
                        sanitizeStrings(location != null ? location.getCountryCode() : ""),
                        sanitizeStrings(chatLanguage != null ? chatLanguage.getName() : ""),
                        sanitizeStrings(chatLanguage != null ? chatLanguage.getLanguageCode() : "")
                )) + "\n");
            }
            return file;
        } catch (IOException e) {
           ErrorLogger.log(e.getMessage());
            throw new IOException("Error generating csv file");
        }
    }
}

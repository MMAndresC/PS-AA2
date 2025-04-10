package com.svalero.psaa2.constants;


import java.util.List;

public class Constants {

    public static final String BASE_URL = "https://api.clashofclans.com/v1/";
    public static final String CR_URL = "https://api.clashroyale.com/v1/";

    public final static int LIMIT = 60; //Max de la API 60
    public final static int LIMIT_RANKING = 10;

    public final static int LOCATION_ID_SPAIN = 32000218;

    public final static List<WarFrequencyStructure> WAR_FRECUENCY = List.of(
            new WarFrequencyStructure("noSelect", "Filtrar por frecuencia"),
            new WarFrequencyStructure("unknown", "Desconocido"),
            new WarFrequencyStructure("always", "Siempre"),
            new WarFrequencyStructure("moreThanOncePerWeek", "Más que una vez por semana"),
            new WarFrequencyStructure("oncePerWeek", "Una vez por semana"),
            new WarFrequencyStructure("lessThanOncePerWeek", "Menos que una vez por semana"),
            new WarFrequencyStructure("never", "Nunca")
    );

    //Clash royal locationId - Clash of clans LocationId, difference between locationId of same countries
    public final static int DIFFERENCE = 25000000;
}

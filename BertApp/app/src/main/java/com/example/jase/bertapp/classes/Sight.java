package com.example.jase.bertapp.classes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Sight {

    private final LatLng coords;
    private final String title;
    private final String description;

    private Sight(LatLng coords, String title, String description) {
        this.coords = coords;
        this.title = title;
        this.description = description;
    }

    public LatLng getCoords() {
        return coords;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public static List<Sight> getSights() {
        return Arrays.asList(
                new Sight(new LatLng(51.9244201, 4.4777325), "Coolsingel", "Feijenoord kampioen 2000nooit"),
                new Sight(new LatLng(51.9054472, 4.4644487), "Euromast", "Een lange toren."),
                new Sight(new LatLng(51.9090073, 4.484934), "Erasmusbrug", "Brug."),
                new Sight(new LatLng(51.9200634, 4.4846116), "Markthal", "..?")
        );
    }

}

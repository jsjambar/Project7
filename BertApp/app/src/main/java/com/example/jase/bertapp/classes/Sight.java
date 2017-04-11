package com.example.jase.bertapp.classes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jase on 11/04/2017.
 */

public class Sight {

    public LatLng coords;
    public String title;
    public String description;


    public Sight(LatLng coords, String title, String description) {
        this.coords = coords;
        this.title = title;
        this.description = description;
    }

    public static List<Sight> getSights() {
        List<Sight> sights = new ArrayList<Sight>();

        // Coolsingel
        sights.add(new Sight(new LatLng(51.9244201, 4.4777325), "Coolsingel", "Feijenoord kampioen 2000nooit"));
        // Euromast
        sights.add(new Sight(new LatLng(51.9054472, 4.4644487), "Euromast", "Een lange toren."));
        // Erasmusbrug
        sights.add(new Sight(new LatLng(51.9090073, 4.484934), "Erasmusbrug", "Brug."));
        // Markthal
        sights.add(new Sight(new LatLng(51.9200634, 4.4846116), "Markthal", "..?"));

        return sights;

    }
}

package com.example.jase.bertapp.classes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Sight {

    private final String title;
    private final String description;
    private final LatLng coords;

    private Sight(String title, String desc, LatLng coords) {
        this.coords = coords;
        this.title = title;
        this.description = desc;
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
            new Sight("Euromast", "TV tower offering 360 degree views, restaurant, 2 sleek, luxury hotel rooms, " +
                    "abseiling & zipline.", new LatLng(51.9054439, 4.4644487)),
            new Sight("Cube Houses", "Furnished house with models, photos & computers explaining architect Piet Blom's cube project.",
                    new LatLng(51.920158, 4.4885137)),
            new Sight("Museum Boijmans Van Beuningen", "1930s art museum with collections of Dutch & European masterpieces, " +
                    "from early Middle Ages to today.", new LatLng(51.9142145, 4.471152)),
            new Sight("Erasmusbrug", "The Erasmus Bridge is a combined cable-stayed and bascule bridge in the centre of Rotterdam, " +
                    "connecting the north and south parts of this city, second largest in the Netherlands",
                    new LatLng(51.909004,4.484934)),
            new Sight("Kunsthal", "Striking steel & glass museum hosting program of temporary art, design & photography exhibitions.",
                    new LatLng(51.9108402,4.4713964)),
            new Sight("Wereldmuseum", "More than 1800 objects from around the world, including temporary exhibitions, plus a restaurant.",
                    new LatLng(51.9079096,4.4782088)),
            new Sight("Grote of Sint-Laurenskerk", "Grote of Sint-Laurenskerk is a Protestant church in Rotterdam. It is the only " +
                    "remnant of the medieval city of Rotterdam.",
                    new LatLng(51.9215168,4.4833288)),
            new Sight("Diergaarde Blijdorp", "Diergaarde Blijdorp is a zoo in the northwestern part of Rotterdam, one of the oldest" +
                    " zoos in the Netherlands. In 2007 it celebrated its 150th anniversary.", new LatLng(51.927354,4.4469521)),
            new Sight("Witte Huis", "The Witte Huis or White House is a building and National Heritage Site in Rotterdam, Netherlands, " +
                    "built in 1898 in the Art Nouveau style. The building is 43 m tall, with 10 floors. It was also the first hoogbouw in Europe.",
                    new LatLng(51.9188697,4.4895136)),
            new Sight("Maritime Museum Rotterdam", "Vintage ships & historic port models, plus hands-on kids' exhibits in a multimedia maritime museum.",
                    new LatLng(51.917526,4.4800383)),
            new Sight("Netherlands Photo Museum", "The Netherlands Photo Museum is a museum in the Netherlands primarily focused on photography. " +
                    "The museum collection consists of many historical, social and cultural images from the 20th and 21st century, from the Netherlands and elsewhere.",
                    new LatLng(51.9053598,4.4846595)),
            new Sight("Witte de With Center for Contemporary Art", "Innovative public cultural center with changing international contemporary art exhibitions & events.",
                    new LatLng(51.915478,4.4748713))
        );
    }
}

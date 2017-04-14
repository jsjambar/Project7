package com.example.jase.bertapp.database;


import com.google.android.gms.maps.model.LatLng;

import org.bson.Document;

import java.util.Iterator;


public class SightsCollectionManager {

    private static SightsCollectionManager collectionManager;

    private SightsCollectionManager() {
        collectionManager = this;
    }

    public boolean sightExists(String title) {
        Iterator<Document> iterator = MlabDatabase.getInstance().getSightCollection().find().iterator();
        while (iterator.hasNext()) {
            String t = iterator.next().getString("title");
            if (t == null)
                continue;
            if (t.equalsIgnoreCase(title))
                return true;
        }
        return false;
    }

    public Document getSightDocument(String title) {
        for (Document doc : MlabDatabase.getInstance().getSightCollection().find()) {
            String t = doc.getString("title");
            if (t == null)
                continue;
            if (t.equalsIgnoreCase(title))
                return doc;
        }
        return null;
    }

    public void insertNewSight(String title, String desc, LatLng coords) {
        insertNewSight(title, desc, coords, "Unknown");
    }

    public void insertNewSight(String title, String desc, LatLng coords, String type) {
        if (sightExists(title))
            return;
        Document doc = new Document();
        doc.put("title", title);
        doc.put("desc", desc);
        doc.put("lat", coords.latitude);
        doc.put("long", coords.longitude);
        doc.put("type", type);
        MlabDatabase.getInstance().getSightCollection().insertOne(doc);
    }

    public static SightsCollectionManager getInstance() {
        if (collectionManager == null)
            collectionManager = new SightsCollectionManager();
        return collectionManager;
    }

    //TODO: remove when we have data.
    public void insertSightsToDB() {
        if (!sightExists("Euromast")) {
            insertNewSight("Euromast", "TV tower offering 360 degree views, restaurant, 2 sleek, luxury hotel rooms, " +
                            "abseiling & zipline.",
                    new LatLng(51.9054439, 4.4644487));
        }
        if (!sightExists("Cube Houses")) {
            insertNewSight("Cube Houses", "Furnished house with models, photos & computers explaining architect Piet Blom's cube project.",
                    new LatLng(51.920158, 4.4885137)
            );
        }
        if (!sightExists("Museum Boijmans Van Beuningen")) {
            insertNewSight("Museum Boijmans Van Beuningen", "1930s art museum with collections of Dutch & European masterpieces, " +
                    "from early Middle Ages to today.", new LatLng(51.9142145, 4.471152)
            );
        }
        if (!sightExists("Erasmusbrug")) {
            insertNewSight("Erasmusbrug", "The Erasmus Bridge is a combined cable-stayed and bascule bridge in the centre of Rotterdam, " +
                    "connecting the north and south parts of this city, second largest in the Netherlands",
                    new LatLng(51.909004,4.484934)
            );
        }
        if (!sightExists("Kunsthal")) {
            insertNewSight("Kunsthal", "Striking steel & glass museum hosting program of temporary art, design & photography exhibitions.",
                    new LatLng(51.9108402,4.4713964)
            );
        }
        if (!sightExists("Wereldmuseum")) {
            insertNewSight("Wereldmuseum", "More than 1800 objects from around the world, including temporary exhibitions, plus a restaurant.",
                    new LatLng(51.9079096,4.4782088)
            );
        }
        if (!sightExists("Grote of Sint-Laurenskerk")) {
            insertNewSight("Grote of Sint-Laurenskerk", "Grote of Sint-Laurenskerk is a Protestant church in Rotterdam. It is the only " +
                    "remnant of the medieval city of Rotterdam.",
                    new LatLng(51.9215168,4.4833288)
            );
        }
        if (!sightExists("Diergaarde Blijdorp")) {
            insertNewSight("Diergaarde Blijdorp", "Diergaarde Blijdorp is a zoo in the northwestern part of Rotterdam, one of the oldest" +
                    " zoos in the Netherlands. In 2007 it celebrated its 150th anniversary.", new LatLng(51.927354,4.4469521)
            );
        }
        if (!sightExists("Witte Huis")) {
            insertNewSight("Witte Huis", "The Witte Huis or White House is a building and National Heritage Site in Rotterdam, Netherlands, " +
                    "built in 1898 in the Art Nouveau style. The building is 43 m tall, with 10 floors. It was also the first hoogbouw in Europe.",
                    new LatLng(51.9188697,4.4895136)
            );
        }
        if (!sightExists("Maritime Museum Rotterdam")) {
            insertNewSight("Maritime Museum Rotterdam", "Vintage ships & historic port models, plus hands-on kids' exhibits in a multimedia maritime museum.",
                    new LatLng(51.917526,4.4800383)
            );
        }
        if (!sightExists("Netherlands Photo Museum")) {
            insertNewSight("Netherlands Photo Museum", "The Netherlands Photo Museum is a museum in the Netherlands primarily focused on photography. " +
                    "The museum collection consists of many historical, social and cultural images from the 20th and 21st century, from the Netherlands and elsewhere.",
                    new LatLng(51.9053598,4.4846595)
            );
        }
        if (!sightExists("Witte de With Center for Contemporary Art")) {
            insertNewSight("Witte de With Center for Contemporary Art", "Innovative public cultural center with changing international contemporary art exhibitions & events.",
                new LatLng(51.915478,4.4748713)
            );
        }
    }

}

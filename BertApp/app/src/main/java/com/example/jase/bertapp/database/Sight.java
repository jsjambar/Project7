package com.example.jase.bertapp.database;

import com.google.android.gms.maps.model.LatLng;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class Sight {

    private final Document doc;

    public Sight(Document doc) {
        this.doc = doc;
    }

    public Sight (String title) {
        this.doc = SightsCollectionManager.getInstance().getSightDocument(title);
    }

    public String getTitle() {
        return doc.getString("title");
    }

    public String getDesc() {
        return doc.getString("desc");
    }

    public LatLng getCords() {
        return new LatLng(doc.getDouble("lat"), doc.getDouble("long"));
    }

    public String getType() {
        return doc.getString("type");
    }

    public int getAmountForNationality(String nationality) {
        List<Document> natList = doc.containsKey("nationality") ? (List<Document>) doc.get("nationality") : new ArrayList<>();
        for (Document doc : natList)
            if (doc.getString("nat").equalsIgnoreCase(nationality))
                return doc.getInteger("amount");
        return 0;
    }

    public void setAmountForNationality(String nationality, int amount) {
        List<Document> natList = doc.containsKey("nationality") ? (List<Document>) doc.get("nationality") : new ArrayList<>();
        for (Document doc : natList) {
            if (doc.getString("nat").equalsIgnoreCase(nationality)) {
                doc.put("amount", amount);
                natList.remove(doc);
                natList.add(doc);
                doc.put("nationality", natList);
                return;
            }
        }
        Document natDoc = new Document();
        natDoc.put("nat", nationality);
        natDoc.put("amount", amount);
        natList.add(natDoc);
        doc.put("nationality", natList);
    }

    public Document getDoc() {
        return doc;
    }

    public void updateToDB() {
        MlabDatabase.getInstance().getSightCollection().updateOne(new Document("title", getTitle()), getUpdateOperationDocument(doc));
    }

    private Bson getUpdateOperationDocument(Bson newValue) {
        return new Document("$set", newValue);
    }

}

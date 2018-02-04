package mayuresh.com.retrofitjson.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayureshjakhotia on 2/3/18.
 */

/*
 * Saves The Top Elements Returned In The Json Response And
 * Contains getters and setters for them - kind and data
 */
public class Feed {

    @SerializedName("kind")
    @Expose
    private String kind;

    @SerializedName("data")
    @Expose
    private Data data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "kind='" + kind + '\'' +
                ", data=" + data +
                '}';
    }
}

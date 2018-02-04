package mayuresh.com.retrofitjson.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import mayuresh.com.retrofitjson.model.children.Children;

/**
 * Created by mayureshjakhotia on 2/3/18.
 */

/*
 * Saves The Elements Needed Under data Returned In The Json Response And
 * Contains getters and setters to access after, before to paginate & children to process further
 */
public class Data {

    @SerializedName("after")
    @Expose
    private String after;

    @SerializedName("before")
    @Expose
    private String before;

    @SerializedName("children")
    @Expose
    private ArrayList<Children> children;

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public ArrayList<Children> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Children> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Data{" +
                "after='" + after + '\'' +
                ", before='" + before + '\'' +
                ", children=" + children +
                '}';
    }
}

package mayuresh.com.retrofitjson.model.children;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mayureshjakhotia on 2/3/18.
 */

/*
 * Last Level - Saves The Elements Needed For Displaying Posts Under data-->children-->data Returned In The Json Response And
 * Contains getters and setters to access title, author, permalink, thumbnail and body
 */
public class Data {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("author")
    @Expose
    private String author;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("permalink")
    @Expose
    private String url;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", body='" + body + '\'' +
                ", url='" + url + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}

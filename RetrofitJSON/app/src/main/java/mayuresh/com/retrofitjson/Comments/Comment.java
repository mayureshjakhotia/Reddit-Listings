package mayuresh.com.retrofitjson.Comments;

/**
 * Created by mayureshjakhotia on 2/4/18.
 */

/*
 * Contains The Values Needed For Displaying Comment & Author
 * Contains getters and setters for accessing the same
 */
public class Comment {

    private String comment;
    private String author;

    public Comment(String comment, String author) {
        this.comment = comment;
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}

package mayuresh.com.retrofitjson;

import java.util.List;

import mayuresh.com.retrofitjson.model.Feed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mayureshjakhotia on 2/3/18.
 */

// Service Interface (Defines API Endpoints)
public interface RedditAPI {

    @GET("/r/all/{listing_name}.json")
    Call<List<Feed>> getArrayData(@Path("listing_name") String listing_name);

    // Query Parameters such as after, before, count, limit in order to traverse through all the pages back and forth successfully
    @GET("/r/all/{listing_name}.json")
    Call<Feed> getObjectData(@Path("listing_name") String listing_name, @Query("after") String after, @Query("before") String before, @Query("count") int count, @Query("limit") int limit);

    // Set encode=true to maintain the URL formatting (otherwise the API call would fail returning 404 most of the times)
    @GET("{listing_name}.json")
    Call<List<Feed>> getData(@Path(value = "listing_name", encoded=true) String listing_name);

}

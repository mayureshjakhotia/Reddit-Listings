package mayuresh.com.retrofitjson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mayuresh.com.retrofitjson.Comments.CommentsActivity;
import mayuresh.com.retrofitjson.model.Feed;
import mayuresh.com.retrofitjson.model.children.Children;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Retrofit retrofit;
    private RedditAPI redditAPI;

    private Button btnRefreshListing;
    private EditText inputListingName;
    private String currentListing;

    // Buttons Allow Pagination (Traverse Next and Previous)
    private Button btnNext;
    private Button btnPrevious;

    // count is required in order to return "before" values, else Reddit API returns them as null
    private int count = 10;

    /*
     * Maintaining beforeCount so that we know how many pages we have traversed (and need to come back).
     * Initializing it to count so that we know how many results are present in the first page too.
     */
    private int beforeCount = count;

    // Setting limit in order to return only those many results in one call (displaying only limited posts on a page)
    private int limit = 10;

    /*
     * Maintaining these values dynamically to pass in the API call once the User clicks Next.
     * Initially set to null to not pass after/before in the API call.
     */
    private String after = null;
    private String before = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting");

        btnRefreshListing = (Button) findViewById(R.id.btnRefreshListing);
        inputListingName = (EditText) findViewById(R.id.inputListingName);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);

        // Setting it to count so that initially we know how many results are present in the first page
        beforeCount = count;

        // On clicking Refresh button, the input endpoint will be called and the posts would be shown
        btnRefreshListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String listingName = inputListingName.getText().toString();

                // Proceed if input endpoint name is not empty
                if(!listingName.equals("")){
                    currentListing = listingName;

                    // Initialize - Creates retrofit instance & implementation of service interface
                    init();

                    /*
                     * If endpoint is "random" then an array of objects is returned which is handled by a List<Feed>
                     * Can be modified to identify response types & call a function appropriately (maybe using TypeAdapter)
                     */
                    if (currentListing.equalsIgnoreCase("random")) {
                        getArrayContent();
                    }
                    else {
                        // In all other cases, call the getObjectContent() method
                        getObjectContent();
                    }
                }
            }
        });

        /*
         * On clicking NEXT button, the beforeCount will be increased by count, before will be set to null
         * so that API call only has limit, count & after to display next set of posts
         */
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Keep adding the count in order to retrieve before value (else it returns it as null)
                beforeCount += count;
                before = null;
                // If it is random endpoint, then it doesn't have after/before values. So just call API again to display different posts
                if (currentListing.equalsIgnoreCase("random")) {
                    getArrayContent();
                }
                else {
                    // Call the API to display Next 10 posts
                    getObjectContent();
                }
            }
        });

        /*
         * On clicking PREVIOUS button, the beforeCount will be decremented by count, count will be set to beforeCount & after
         * will be set to null so that API call only has limit, appropriate count & before (ID of the post 10 spots before)
         * to display next set of posts.
         */
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Decrement beforeCount so that on reaching the first page again, it'll be reset to 0
                beforeCount = beforeCount - count;
                count = beforeCount;

                after = null;

                // If it is random endpoint, then it doesn't have after/before values. So just call API again to display different posts
                if (currentListing.equalsIgnoreCase("random")) {
                    getArrayContent();
                }
                else {
                    // Call the API to display Previous 10 posts
                    getObjectContent();
                }
            }
        });

    }

    /*
     * Creates a new Retrofit instance using the configured values such as BASE_URL.
     * Creates an implementation of the API endpoints defined by the Service Interface - RedditAPI
     */
    private void init() {

        retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        redditAPI = retrofit.create(RedditAPI.class);
    }

    /*
     * Processes The Content Returned In An Object
     * after, before set to the values returned in the earlier response & based upon which button gets clicked(NEXT/PREVIOUS)
     * count is set before in the on click listeners above based on which page & button is clicked(NEXT/PREVIOUS)
     * limit is the limit of total results to return (in order to display on one page)
     */
    private void getObjectContent() {

        Call<Feed> call = redditAPI.getObjectData(currentListing, after, before, count, limit);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                // Returns True If The Response Is In 200-300 range. Otherwise outputs "Some error occurred"
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Some error occurred. Type a valid listing endpoint.", Toast.LENGTH_SHORT).show();
                }
                // Enters whenever the response is successful
                else {

                    // To keep track of all the posts (children)
                    final ArrayList<Children> childrenList = response.body().getData().getChildren();

                    // Count of all the children (displayed in a single page)
                    count = childrenList.size();

                    // Gets The "after" & "before" values from the response
                    after = response.body().getData().getAfter();
                    before = response.body().getData().getBefore();

                    /*
                     * NEXT/PREVIOUS buttons are disabled by default.
                     * Based on after/before values, the buttons are enabled/disabled dynamically
                     */
                    if (after == null) {
                        btnNext.setEnabled(false);
                    }
                    else {
                        btnNext.setEnabled(true);
                    }
                    if (before == null) {
                        btnPrevious.setEnabled(false);
                    }
                    else {
                        btnPrevious.setEnabled(true);
                    }

                    // Looping through all the posts to get the content, setting default values if not present
                    for (int i = 0; i < childrenList.size(); i++) {

                        if (childrenList.get(i).getData().getTitle() == null) {
                            childrenList.get(i).getData().setTitle("No Title Present");
                        }
                        if (childrenList.get(i).getData().getUrl() == null) {
                            childrenList.get(i).getData().setUrl("No Link Present");
                        }
                        if (childrenList.get(i).getData().getAuthor() == null) {
                            childrenList.get(i).getData().setAuthor("No Author Present");
                        }
                        if (childrenList.get(i).getData().getThumbnail() == null) {
                            childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/self_default.png");
                        } else if (childrenList.get(i).getData().getThumbnail().equals("self")) {
                            childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/self_default.png");
                        } else if (childrenList.get(i).getData().getThumbnail().equals("default")) {
                            childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/noimage.png");
                        } else if (childrenList.get(i).getData().getThumbnail().equals("nsfw")) {
                            childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/nsfw.png");
                        }
                    }

                    // Displaying all the posts in a card layout using a CustomListAdapter object
                    ListView listView = (ListView) findViewById(R.id.listView);
                    CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, childrenList);
                    listView.setAdapter(customListAdapter);

                    /*
                     * On click listener for the posts in listView.
                     * Once a post is clicked, it's relevant data is saved in an intent and that CommentsActivity is called to load the comments.
                     */
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d(TAG, "onClick: Clicked " + childrenList.get(i).toString());
                            Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                            intent.putExtra("@string/post_title", childrenList.get(i).getData().getTitle());
                            intent.putExtra("@string/post_author", childrenList.get(i).getData().getAuthor());
                            intent.putExtra("@string/post_url", childrenList.get(i).getData().getUrl());
                            intent.putExtra("@string/post_thumbnail", childrenList.get(i).getData().getThumbnail());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Something went wrong. Type a valid listing endpoint.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Processes The Content Returned In An Array (In this case, specifically for the "random" endpoint)
     */
    private void getArrayContent() {
        Call<List<Feed>> callsList = redditAPI.getArrayData(currentListing);
        callsList.enqueue(new Callback<List<Feed>>() {
            @Override
            public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {

                // Returns True If The Response Is In 200-300 range. Otherwise outputs "Some error occurred"
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Some error occurred. Type a valid listing endpoint.", Toast.LENGTH_SHORT).show();
                }
                // Enters whenever the response is successful
                else {

                    // In order to save all the posts (children)
                    final ArrayList<Children> totalChildrenList = new ArrayList<Children>();

                    /*
                     * For each Objects In Array, Looping through all the posts to get the content,
                     * setting default values if not present & adding to the totalChildrenList
                     */
                    for (Feed size : response.body()) {
                        ArrayList<Children> childrenList = size.getData().getChildren();
                        for (int i = 0; i < childrenList.size(); i++) {

                            if (childrenList.get(i).getData().getTitle() == null) {
                                childrenList.get(i).getData().setTitle("No Title Present");
                            }
                            if (childrenList.get(i).getData().getUrl() == null) {
                                childrenList.get(i).getData().setUrl("No Link Present");
                            }
                            if (childrenList.get(i).getData().getAuthor() == null) {
                                childrenList.get(i).getData().setAuthor("No Author Present");
                            }
                            if (childrenList.get(i).getData().getThumbnail() == null) {
                                childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/self_default.png");
                            } else if (childrenList.get(i).getData().getThumbnail().equals("self")) {
                                childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/self_default.png");
                            } else if (childrenList.get(i).getData().getThumbnail().equals("default")) {
                                childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/noimage.png");
                            } else if (childrenList.get(i).getData().getThumbnail().equals("nsfw")) {
                                childrenList.get(i).getData().setThumbnail("https://www.reddit.com/static/nsfw.png");
                            }

                            totalChildrenList.add(childrenList.get(i));
                        }

                    }

                    // Displaying all the posts in a card layout using a CustomListAdapter object
                    ListView listView = (ListView) findViewById(R.id.listView);
                    CustomListAdapter customListAdapter = new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, totalChildrenList);
                    listView.setAdapter(customListAdapter);

                    /*
                     * On click listener for the posts in listView.
                     * Once a post is clicked, it's relevant data is saved in an intent and that CommentsActivity is called to load the comments.
                     */
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d(TAG, "onClick: Clicked " + totalChildrenList.get(i).toString());
                            Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                            intent.putExtra("@string/post_title", totalChildrenList.get(i).getData().getTitle());
                            intent.putExtra("@string/post_author", totalChildrenList.get(i).getData().getAuthor());
                            intent.putExtra("@string/post_url", totalChildrenList.get(i).getData().getUrl());
                            intent.putExtra("@string/post_thumbnail", totalChildrenList.get(i).getData().getThumbnail());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Feed>> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());
            }
        });
    }
}

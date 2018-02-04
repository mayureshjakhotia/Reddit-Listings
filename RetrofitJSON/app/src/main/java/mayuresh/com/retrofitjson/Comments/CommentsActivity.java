package mayuresh.com.retrofitjson.Comments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import mayuresh.com.retrofitjson.CommentsListAdapter;
import mayuresh.com.retrofitjson.R;
import mayuresh.com.retrofitjson.RedditAPI;
import mayuresh.com.retrofitjson.URLs;
import mayuresh.com.retrofitjson.model.Feed;
import mayuresh.com.retrofitjson.model.children.Children;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mayureshjakhotia on 2/3/18.
 */

/*
 * Called from MainActivity
 * Once a Post In The ListView In MainActivity Gets Clicked, This Activity Is Called To Get Its Comments
 */
public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    // Gets the details of original post to display at the top
    private static String postURL;
    private static String postAuthor;
    private static String postTitle;
    private static String postThumbnail;

    private String currentFeed;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private TextView progressText;

    // In order to save all the comments related to the post
    private final ArrayList<Comment> mComments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mProgressBar = (ProgressBar) findViewById(R.id.commentsLoadingProgressBar);
        progressText = (TextView) findViewById(R.id.progressText);
        Log.d(TAG, "onCreate: Started.");

        // Start The Progressbar until the comments get fetched
        mProgressBar.setVisibility(View.VISIBLE);

        // Set up the Image Loader Library
        setupImageLoader();

        // Initializes values from the clicked (original) post & displays it at the top
        initPost();

        // Initializes
        init();

    }

    /*
     * Creates a new Retrofit instance using the configured values such as BASE_URL.
     * Creatse an implementation of the API endpoints defined by the Service Interface - RedditAPI
     */
    private void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditAPI redditAPI = retrofit.create(RedditAPI.class);
        Log.d(TAG, "init: Current Feed: "+currentFeed);

        Call<List<Feed>> callsList = redditAPI.getData(currentFeed);

        callsList.enqueue(new Callback<List<Feed>>() {
                              @Override
                              public void onResponse(Call<List<Feed>> call, Response<List<Feed>> response) {
                                  // Used in order to omit the some data which is not actually a comment
                                  int authorAbsent = 0;
                                  int commentAbsent = 0;

                                  /*
                                   * For each Objects In Array, Looping through all the comments to get the content,
                                   * setting default values if not present & adding to mComments to display it
                                   */
                                  for(Feed size: response.body()) {
                                      ArrayList<Children> entrys = size.getData().getChildren();

                                      for(int i = 0; i < entrys.size(); i++) {
                                          if (entrys.get(i).getData().getAuthor() == null) {
                                              entrys.get(i).getData().setAuthor("No Author Present");
                                              authorAbsent = 1;
                                          }
                                          if (entrys.get(i).getData().getBody() == null) {
                                              entrys.get(i).getData().setBody("No Comment Present");
                                              commentAbsent = 1;
                                          }

                                          if (authorAbsent == 1 && commentAbsent == 1) {
                                              authorAbsent = 0;
                                              commentAbsent = 0;
                                          }
                                          else {
                                              mComments.add(new Comment(entrys.get(i).getData().getBody(), entrys.get(i).getData().getAuthor()));
                                          }
                                      }

                                  }
                                  // Setting text to display if no comments are present
                                  if (mComments.size() == 0) {
                                      mComments.add(new Comment("No Comments Found",""));
                                  }
                                  // Removing the leading part which is related to original post & aren't comments (if there are at least some comments present)
                                  else {
                                      mComments.remove(0);
                                  }

                                  // Displaying all the comments using a CommentsListAdapter object
                                  mListView = (ListView) findViewById(R.id.commentsListView);
                                  CommentsListAdapter commentsListAdapter = new CommentsListAdapter(CommentsActivity.this, R.layout.comments_layout, mComments);
                                  mListView.setAdapter(commentsListAdapter);

                                  // Remove the progress bar and clears the loading comments text
                                  mProgressBar.setVisibility(View.GONE);
                                  progressText.setText("");
                              }

                              @Override
                              public void onFailure(Call<List<Feed>> call, Throwable t) {
                                  Log.e(TAG, "onFailure: Something went wrong: " + t.getMessage());
                              }
                          });

    }

    /*
     * Get the content of original post from the Incoming Intent
     * Calls displayImage to display the image of the original post at the top of the screen.
     * Saves the postURL in currentFeed (which is the permalink (which will be used to get all comments for that post)
     */
    private void initPost() {
        Intent incomingIntent = getIntent();
        postURL = incomingIntent.getStringExtra("@string/post_url");
        postAuthor = incomingIntent.getStringExtra("@string/post_author");
        postTitle = incomingIntent.getStringExtra("@string/post_title");
        postThumbnail = incomingIntent.getStringExtra("@string/post_thumbnail");

        TextView title = (TextView) findViewById(R.id.postTitle);
        TextView author = (TextView) findViewById(R.id.postAuthor);

        ImageView thumbnail = (ImageView) findViewById(R.id.postThumbnail);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.postLoadingProgressBar);

        title.setText(postTitle);
        author.setText(postAuthor);

        displayImage(postThumbnail, thumbnail, progressBar);

        currentFeed = postURL;
    }

    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar) {
        // Get the ImageLoader instance
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Default Image if Failure Occurs
        int defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/image_failed",null,CommentsActivity.this.getPackageName());

        // Create display options
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();

        // Download from imageURL & display Image Into The imageView
        imageLoader.displayImage(imageURL, imageView, options , new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    // Setting up the Universal Image loader Library
    private void setupImageLoader(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }
}

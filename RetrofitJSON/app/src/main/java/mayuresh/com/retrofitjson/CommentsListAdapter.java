package mayuresh.com.retrofitjson;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import mayuresh.com.retrofitjson.Comments.Comment;


/**
 * Created by mayureshjakhotia on 2/4/18.
 */

public class CommentsListAdapter  extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentsListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    // Holds variables in a View
    private static class ViewHolder {
        TextView comment;
        TextView author;
        ProgressBar mProgressBar;
    }

    // Default constructor for the CommentsListAdapter
    public CommentsListAdapter(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the Data To Display For Comments
        String comment = getItem(position).getComment();
        String author = getItem(position).getAuthor();

        try{
            // Create the view result
            final View result;

            // ViewHolder object
            final ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ViewHolder();
                holder.comment = (TextView) convertView.findViewById(R.id.comment);
                holder.author = (TextView) convertView.findViewById(R.id.commentAuthor);
                holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.commentProgressBar);

                result = convertView;
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
                holder.mProgressBar.setVisibility(View.VISIBLE);
            }

            lastPosition = position;

            // Populate The Comment & Author In TextViews
            holder.comment.setText(comment);
            holder.author.setText(author);

            // Remove the progress bar
            holder.mProgressBar.setVisibility(View.GONE);

            return convertView;
        }
        catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }

}
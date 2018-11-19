package com.example.kleimaj.jamr_v2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView mFeedsList;
    private DatabaseReference mUsersDatabase;

    private Query query;


    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mFeedsList = view.findViewById(R.id.feeds_list);
        mFeedsList.setHasFixedSize(true);

        mFeedsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // -----------------------------------------------------------------
        // ----------------------  MAGIC  ----------------------------------
        // https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md -----
        // ------------------------------------------------------------------------------
        // Get the Query From db.
        query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(50);
        
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, FeedsViewHolder>(options) {
            @Override
            public FeedsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_single_layout, parent, false);
                return new FeedsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(FeedsViewHolder holder, int position, Users model) {
                // Bind the User object to the FeedsViewHolder
                holder.setName(model.getName());
                holder.setMusicIdentity(model.getMusic_identity());
                holder.setThumbImage(model.getThumb_image());
            }
        };

        mFeedsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FeedsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FeedsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        // The first letter of name is capitalized.
        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.feed_user_name);

            userNameView.setText(name.substring(0,1).toUpperCase() + name
                    .substring(1));
        }

        public void setMusicIdentity(ArrayList<String> music_identity){
            TextView userIdentity = mView.findViewById(R.id.feed_user_identity);
            userIdentity.setText(music_identity.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .trim()
                    .replaceAll(",$", ""));
        }

        public void setThumbImage(String thumbImage){
            CircleImageView userThumbImage = mView.findViewById(R.id
                    .feed_user_image);
            Picasso.get().load(thumbImage).placeholder(R.drawable.default_avatar)
                    .into(userThumbImage);
        }
    }









}

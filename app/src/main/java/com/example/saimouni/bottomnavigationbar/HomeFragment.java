package com.example.saimouni.bottomnavigationbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements NetworkStateReceiver.NetworkStateReceiverListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView imageView;
    private NetworkStateReceiver networkStateReceiver;
    private FirebaseAuth mAuth;
    private View rootView;
    private FrameLayout homeFragmentLayout;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Query query;
    private boolean like = false;
    private Long count;
    private DatabaseReference likeDatabaseReference,likesCountRef;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReferenceList;
    private BottomNavigationView bottomNavigationView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("This post will be deleted.");
        alertDialogBuilder.setTitle("Confirm ?");
        alertDialog = alertDialogBuilder.create();

        mAuth = FirebaseAuth.getInstance();


        databaseReferenceList = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseReferenceList.keepSynced(true);
        query = databaseReferenceList.orderByChild("timestamp");

        likesCountRef =FirebaseDatabase.getInstance().getReference().child("likes");


        query.keepSynced(true);//sai

        likeDatabaseReference = FirebaseDatabase.getInstance().getReference().child("likes");
        likeDatabaseReference.keepSynced(true);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        getContext().registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void networkAvailable() {
        snackbar.dismiss();
    }

    @Override
    public void networkUnavailable() {
        dialog.cancel();
        snackbar.show();
    }

    @Override
    public void onRefresh() {
        onStart();

    }

    @Override
    public void onStart() {
        super.onStart();

 /*       if(!isOnline(getContext())){
           Toast.makeText(getContext(),"No internet connection",Toast.LENGTH_LONG).show();
        }



*/

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.layout_row,
                BlogViewHolder.class,
                query   //databaseReferenceList     sai
        ) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final Blog model, final int position) {

                viewHolder.setDesc(model.getDesc());
                viewHolder.setTitle(model.getTitle());


                viewHolder.setImage(model.getImage());
                viewHolder.setLikeBtn(model.getTitle());



                viewHolder.setDate(model.getDate());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent postIntent = new Intent(getContext(), PostItemActivity.class);
                        postIntent.putExtra("titleIntent", model.getTitle());
                        postIntent.putExtra("descIntent", model.getDesc() + "\n\n\n");
                        postIntent.putExtra("imageIntent", model.getImage());
                        postIntent.putExtra("dateIntent", model.getDate());
                        startActivity(postIntent);
                    }
                });


                if (!mAuth.getCurrentUser().getProviders().get(0).equals("google.com")) {
                    viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final DatabaseReference dr = databaseReferenceList.child(model.getTitle());
                                    Log.d("DeleteSainath", model.getTitle());
                                    dr.getRef().removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("likes").child(model.getTitle()).removeValue();         //to remove likes node for that post
                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();


                                    FirebaseDatabase.getInstance().getReference().child("likes").child(model.getTitle()).removeValue();


                                }
                            });
                            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialogBuilder.show();

                            return true;
                        }
                    });
                }


                viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        like = true;


                        likeDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if (like) {

                                    if (dataSnapshot.child(model.getTitle()).hasChild(mAuth.getCurrentUser().getUid())) {
                                        likeDatabaseReference.child(model.getTitle()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        like = false;
                                    } else {
                                        likeDatabaseReference.child(model.getTitle()).child(mAuth.getCurrentUser().getUid()).setValue("liked");
                                        like = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                });

            }

        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recyclerView);


        imageView = (ImageView) getView().findViewById(R.id.image);
        homeFragmentLayout = getView().findViewById(R.id.homeFragmentLayout);
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);


        //      BottomNavigationView bottomNavigationView = getView().findViewById(R.id.navigation);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        snackbar = Snackbar.make(homeFragmentLayout, "No internet connection", Snackbar.LENGTH_LONG);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((MainActivity) getActivity()).setNavigationVisibility(false);
                } else if (dy < 0) {
                    ((MainActivity) getActivity()).setNavigationVisibility(true);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);


        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        getContext().unregisterReceiver(networkStateReceiver);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

/*    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
    */

    public static class BlogViewHolder extends RecyclerView.ViewHolder {


        View mView;
        int likesCount;
        ImageButton likeBtn;
        TextView likesCountTV;
        DatabaseReference dr,likesCountRef;
        FirebaseAuth auth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            likeBtn = mView.findViewById(R.id.likeBtn);
            likesCountTV = mView.findViewById(R.id.likesCount);

            dr = FirebaseDatabase.getInstance().getReference().child("likes");
            auth = FirebaseAuth.getInstance();
            dr.keepSynced(true);
            likesCountRef  = FirebaseDatabase.getInstance().getReference().child("likes");
        }

        public void setLikeBtn(final String title) {

            dr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(title).hasChild(auth.getCurrentUser().getUid())) {
                        likeBtn.setImageResource(R.drawable.redlove);
                        likesCount = (int)dataSnapshot.child(title).getChildrenCount();
                        likesCountTV.setText(Integer.toString(likesCount));



                    } else {
                        likeBtn.setImageResource(R.drawable.blacklove);
                        likesCount = (int)dataSnapshot.child(title).getChildrenCount();
                        likesCountTV.setText(Integer.toString(likesCount));

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title) {
            TextView postTitle = (TextView) mView.findViewById(R.id.title);
            postTitle.setText(title);
        }

        public void setDesc(String desc) {
            TextView postDesc = (TextView) mView.findViewById(R.id.desc);
            postDesc.setText(desc);
        }

        public void setImage(String image) {
            ImageView postImage = (ImageView) mView.findViewById(R.id.image);
            showImageLoadPB();
            Picasso.get().load(image).into(postImage);
            hideImageLoadPB();
        }

        public void setDate(String date) {
            TextView postDate = mView.findViewById(R.id.date);
            postDate.setText("on :" + date);
        }



        public void showImageLoadPB() {
            ProgressBar pb = mView.findViewById(R.id.imageProgressBar);
            pb.setVisibility(View.VISIBLE);
            Log.d("PB", "VISIBLE");
        }

        public void hideImageLoadPB() {
            ProgressBar pb = mView.findViewById(R.id.imageProgressBar);
            pb.setVisibility(View.GONE);
            Log.d("PB", "GONE");
        }
    }
}

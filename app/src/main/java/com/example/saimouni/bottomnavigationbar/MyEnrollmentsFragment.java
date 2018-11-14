package com.example.saimouni.bottomnavigationbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyEnrollmentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyEnrollmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEnrollmentsFragment extends Fragment  implements NetworkStateReceiver.NetworkStateReceiverListener,SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference databaseReference;
    private ListView listView;
    private Query query;
    private ArrayList<String> arrayList =new ArrayList<>();;
    private ArrayAdapter<String> arrayAdapter;
    private FrameLayout myEnrollmentsLayout;
    private NetworkStateReceiver networkStateReceiver;
    private Snackbar snackbar;

    private String htno;
    private  ProgressDialog dialog;
    EditText rollNumber;
    private OnFragmentInteractionListener mListener;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alert;
    private TextView tv;
    private SwipeRefreshLayout swipeRefreshLayout;

    public MyEnrollmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyEnrollmentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyEnrollmentsFragment newInstance(String param1, String param2) {
        MyEnrollmentsFragment fragment = new MyEnrollmentsFragment();
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


        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_activated_1,arrayList);
       dialog = new ProgressDialog(getContext());
        dialog.setMessage("please wait...");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Enrolled");

        alert=new AlertDialog.Builder(getContext());
        alert.setMessage("You will be no longer registered in this event");
        alert.setTitle("Confirm ?");
        alertDialog=alert.create();
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        getContext().registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_enrollments, container, false);
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
        snackbar.show();
    }

    @Override
    public void onRefresh() {
        events();
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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button check = getView().findViewById(R.id.check);

         listView=getView().findViewById(R.id.myEnrollmentsListView);
        rollNumber= getView().findViewById(R.id.rollNumber);

        tv = getView().findViewById(R.id.longPressDeleteTextView);
        myEnrollmentsLayout = getView().findViewById(R.id.myEnrollmentsLayout);

        snackbar = Snackbar.make(myEnrollmentsLayout,"No internet connection",Snackbar.LENGTH_LONG);
        swipeRefreshLayout = getView().findViewById(R.id.fragMyEnrollSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                events();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),arrayList.get(position)+"",Toast.LENGTH_SHORT).show();
                final DatabaseReference dr=databaseReference.child(arrayList.get(position)+"");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dr.getRef().child(htno).removeValue();
                        arrayAdapter.notifyDataSetChanged();
                        events();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert.show();

                return true;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("Sainath",arrayList.get(position)+"");
                Intent intent = new Intent(getContext(),MyEnrollmentsClickActivity.class);
                intent.putExtra("title",arrayList.get(position)+"");
                intent.putExtra("htno",htno);
                startActivity(intent);

            }
        });



        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_BACK) {
                    getActivity().finish();
                    return true;
                }

                return false;
            }
        });

    }




    private void events() {

        arrayList.clear();
        htno = rollNumber.getText().toString().toUpperCase();
        if(!TextUtils.isEmpty(htno)) {

            query = databaseReference.orderByChild(htno);
            dialog.show();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child(htno).exists()) {
                            //          Log.d("Sainath123456789",ds.getKey()+"");
                            arrayList.add(ds.getKey());
                            arrayAdapter.notifyDataSetChanged();




                        }
                    }
                    tv.setVisibility(View.VISIBLE);

                    if(arrayList.size()==0) {
                        Toast.makeText(getContext(), "No enrollments found", Toast.LENGTH_LONG).show();

                        tv.setVisibility(View.VISIBLE);
                    }
                    dialog.cancel();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }
        else
            Toast.makeText(getContext(),"Please enter roll number",Toast.LENGTH_SHORT).show();
        listView.setAdapter(arrayAdapter);
      //  dialog.cancel();
  //      arrayList.clear();

        swipeRefreshLayout.setRefreshing(false);

    }



}

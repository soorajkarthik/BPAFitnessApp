package com.example.sooraj.fitnessapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.sooraj.fitnessapp.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SocialFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference users;
    String username;
    User user;
    private View view;
    private ListView friendList, searchList;
    private SearchView search;


    @Override
    public void onStart() {
        super.onStart();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        user = ((MainActivity) getActivity()).getUser();
        username = user.getUsername();
        friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_social, container, false);
        friendList = view.findViewById(R.id.friendsList);
        searchList = view.findViewById(R.id.searchList);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_social, menu);
        search = (SearchView) menu.getItem(0).getActionView();
        search.setQueryHint("Start typing to search");

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                    setFriendListInvisible();
                else
                    setFriendListVisible();
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {

                }

                return false;
            }
        });
    }

    public void setFriendListVisible() {
        friendList.setVisibility(View.VISIBLE);
        searchList.setVisibility(View.INVISIBLE);
    }

    public void setFriendListInvisible() {
        friendList.setVisibility(View.INVISIBLE);
        searchList.setVisibility(View.VISIBLE);
    }

    public View getMyView() {
        return view;
    }

    class FriendListAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<String> friends = new ArrayList<>();


        public FriendListAdapter(Context context, ArrayList<String> friends) {
            layoutInflater = LayoutInflater.from(context);
            this.friends.addAll(friends);
            count = friends.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return friends.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final int index = i;
            final View thisView = layoutInflater.inflate(R.layout.friends_view, null);
            final FriendListHolder holder = new FriendListHolder();

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User friend = dataSnapshot.child(friends.get(index)).getValue(User.class);
                    holder.usernameText = thisView.findViewById(R.id.usernameText);
                    holder.lastSeen = thisView.findViewById(R.id.lastSeenText);
                    holder.inviteToWorkout = thisView.findViewById(R.id.inviteToWorkout);
                    holder.usernameText.setText(friend.getUsername());
                    holder.lastSeen.setText(friend.getLastSeen());
                    holder.inviteToWorkout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Schedule Workout");
                            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.workout_invite_dialog, (ViewGroup) getMyView(), false);
                            final EditText date = viewInflated.findViewById(R.id.date);
                            final EditText location = viewInflated.findViewById(R.id.location);
                            builder.setView(viewInflated);

                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    friend.addWorkoutInvite(username, date.getText().toString(), location.getText().toString());
                                    users.child(friend.getUsername()).child("workoutInvites").setValue(friend.getWorkoutInvites());

                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();


                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            view = thisView;
            view.setTag(holder);


            return view;
        }


        class FriendListHolder {

            TextView usernameText;
            TextView lastSeen;
            Button inviteToWorkout;

        }
    }

    class FriendSearchHolder {

        TextView usernameText;
        Button sendFriendRequest;
        TextView requestStatus;


    }


}

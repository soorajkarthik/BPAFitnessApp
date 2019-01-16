package com.example.sooraj.getfit;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class SocialFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference users;
    String username;
    User user;
    private View view;
    private ListView friendList, searchList, workoutInviteList, friendRequestList;
    private SearchView search;
    String searchText;


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
        view = inflater.inflate(com.example.sooraj.getfit.R.layout.fragment_social, container, false);
        friendList = view.findViewById(com.example.sooraj.getfit.R.id.friendsList);
        searchList = view.findViewById(com.example.sooraj.getfit.R.id.searchList);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(com.example.sooraj.getfit.R.menu.menu_social, menu);
        search = (SearchView) menu.getItem(2).getActionView();
        search.setQueryHint("Start typing to search");


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchText = newText;

                if (newText.length() > 0) {
                    searchForUsers(searchText);
                    setFriendListInvisible();
                } else if (newText.length() == 0) {
                    setFriendListVisible();
                }

                return false;
            }
        });

        setFriendListVisible();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == com.example.sooraj.getfit.R.id.workout_requests) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Workout Requests");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.workout_request_view, (ViewGroup) getMyView(), false);
            workoutInviteList = viewInflated.findViewById(R.id.workoutRequestList);
            workoutInviteList.setAdapter(new WorkoutInviteAdapter(getActivity(), user.getWorkoutInvites()));
            builder.setView(viewInflated);

            builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        } else if (item.getItemId() == com.example.sooraj.getfit.R.id.friend_requests) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Friend Requests");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.friend_request_view, (ViewGroup) getMyView(), false);
            friendRequestList = viewInflated.findViewById(R.id.friendRequestList);
            friendRequestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));
            builder.setView(viewInflated);

            builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }


        return false;
    }

    public void searchForUsers(final String searchText) {

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<DataSnapshot> userList = new ArrayList<>();
                ArrayList<String> usernameList = new ArrayList<>();
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    userList.add(iterator.next());
                }

                for (DataSnapshot ds : userList) {
                    usernameList.add(ds.child("username").getValue(String.class));
                }

                updateSearchResults(searchText, usernameList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateSearchResults(String searchText, ArrayList<String> usernameList) {
        ArrayList<String> filteredUsernameList = new ArrayList<>();

        usernameList.remove(username);

        for (String tempUsername : usernameList) {

            if (tempUsername.toLowerCase().contains(searchText.toLowerCase())) {
                filteredUsernameList.add(tempUsername);
            }
        }


        searchList.setAdapter(new SearchResultsAdapter(getActivity(), filteredUsernameList));

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
            holder.usernameText = thisView.findViewById(com.example.sooraj.getfit.R.id.usernameText);
            holder.lastSeen = thisView.findViewById(com.example.sooraj.getfit.R.id.lastSeenText);
            holder.inviteToWorkout = thisView.findViewById(com.example.sooraj.getfit.R.id.inviteToWorkout);

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User friend = dataSnapshot.child(friends.get(index)).getValue(User.class);

                    String lastSeenText = getLastSeenText(friend);

                    holder.usernameText.setText(friend.getUsername());
                    holder.lastSeen.setText(lastSeenText);
                    holder.inviteToWorkout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Schedule Workout");
                            View viewInflated = LayoutInflater.from(getContext()).inflate(com.example.sooraj.getfit.R.layout.workout_invite_dialog, (ViewGroup) getMyView(), false);
                            final EditText date = viewInflated.findViewById(com.example.sooraj.getfit.R.id.date);
                            final EditText location = viewInflated.findViewById(com.example.sooraj.getfit.R.id.location);
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

        private String getLastSeenText(User friend) {

            String lastSeen = "Just now";
            long currentTime = System.currentTimeMillis();
            long timeSeen = friend.getLastSeen();
            long difference = currentTime - timeSeen;

            long months = difference / ((long) 30 * 24 * 60 * 60 * 1000);
            long weeks = difference / (7 * 24 * 60 * 60 * 1000);
            long days = difference / (24 * 60 * 60 * 1000);
            long hours = difference / (24 * 60 * 60 * 1000);
            long minutes = difference / (60 * 60 * 1000);

            if (months > 0)
                lastSeen = months + " months ago";

            else if (weeks > 0)
                lastSeen = weeks + " weeks ago";

            else if (days > 0)
                lastSeen = days + " days ago";

            else if (hours > 0)
                lastSeen = hours + " hours ago";

            else if (minutes > 0)
                lastSeen = minutes + " minutes ago";

            return lastSeen;
        }

        class FriendListHolder {

            TextView usernameText;
            TextView lastSeen;
            Button inviteToWorkout;

        }
    }

    class SearchResultsAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<String> filteredUsernameList = new ArrayList<>();

        public SearchResultsAdapter(Context context, ArrayList<String> filteredUsernameList) {

            layoutInflater = LayoutInflater.from(context);
            this.filteredUsernameList.addAll(filteredUsernameList);
            this.count = filteredUsernameList.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return filteredUsernameList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final int index = i;
            final View thisView = layoutInflater.inflate(com.example.sooraj.getfit.R.layout.user_search_view, null);
            final FriendSearchHolder holder = new FriendSearchHolder();
            holder.usernameText = thisView.findViewById(com.example.sooraj.getfit.R.id.usernameText);
            holder.requestStatusText = thisView.findViewById(com.example.sooraj.getfit.R.id.requestStatusText);
            holder.updateFriendStatus = thisView.findViewById(com.example.sooraj.getfit.R.id.updateFriendStatus);

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final User searchedUser = dataSnapshot.child(filteredUsernameList.get(index)).getValue(User.class);
                    final int requestType;

                    holder.usernameText.setText(searchedUser.getUsername());
                    if (searchedUser.hasFriendRequestFromUser(username)) {
                        holder.requestStatusText.setText("Requested");
                        holder.updateFriendStatus.setText("Cancel");
                        requestType = 0;
                    } else if (user.isFriendOfUser(searchedUser.getUsername())) {
                        holder.requestStatusText.setText("Accepted");
                        holder.updateFriendStatus.setText("Remove");
                        requestType = 1;
                    } else {
                        holder.requestStatusText.setText("None");
                        holder.updateFriendStatus.setText("Request");
                        requestType = 2;
                    }

                    switch (requestType) {
                        case 0:

                            holder.updateFriendStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    searchedUser.removeFriendRequestFromUser(username);
                                    users.child(searchedUser.getUsername()).child("friendRequests").setValue(searchedUser.getFriendRequests());
                                    updateSearchResults(searchText, filteredUsernameList);
                                }
                            });

                            break;
                        case 1:

                            holder.updateFriendStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    user.removeFriend(searchedUser.getUsername());
                                    searchedUser.removeFriend(username);
                                    users.child(searchedUser.getUsername()).child("friendList").setValue(searchedUser.getFriendList());
                                    users.child(username).child("friendList").setValue(user.getFriendList());
                                    updateSearchResults(searchText, filteredUsernameList);
                                    friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList()));
                                }
                            });

                            break;
                        case 2:

                            holder.updateFriendStatus.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    searchedUser.addFriendRequest(username);
                                    users.child(searchedUser.getUsername()).child("friendRequests").setValue(searchedUser.getFriendRequests());
                                    updateSearchResults(searchText, filteredUsernameList);
                                    friendRequestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));
                                }
                            });

                            break;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            thisView.setTag(holder);
            view = thisView;

            return view;
        }


        class FriendSearchHolder {

            TextView usernameText;
            Button updateFriendStatus;
            TextView requestStatusText;


        }
    }

    class WorkoutInviteAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private HashMap<String, ArrayList<String>> workoutInvites = new HashMap<>();
        private ArrayList<String> inviteSenders = new ArrayList<>();

        public WorkoutInviteAdapter(Context context, HashMap<String, ArrayList<String>> workoutInvites) {
            layoutInflater = LayoutInflater.from(context);
            this.workoutInvites.putAll(workoutInvites);
            inviteSenders.addAll(workoutInvites.keySet());
            count = inviteSenders.size();
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return inviteSenders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final int index = i;
            final View thisView = layoutInflater.inflate(R.layout.workout_request_list_element, null);
            final WorkoutInviteHolder holder = new WorkoutInviteHolder();
            holder.usernameText = thisView.findViewById(R.id.usernameText);
            holder.dateAndTimeText = thisView.findViewById(R.id.dateAndTimeText);
            holder.locationText = thisView.findViewById(R.id.locationText);
            holder.acceptRequest = thisView.findViewById(R.id.acceptRequest);
            holder.declineRequest = thisView.findViewById(R.id.declineRequest);
            final String inviteSenderUsername = inviteSenders.get(i);

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User inviteSender = dataSnapshot.child(inviteSenderUsername).getValue(User.class);

                    holder.usernameText.setText(inviteSenderUsername);
                    holder.dateAndTimeText.setText(workoutInvites.get(inviteSenderUsername).get(0));
                    holder.locationText.setText(workoutInvites.get(inviteSenderUsername).get(1));

                    holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            user.acceptWorkoutInviteFromUser(inviteSenderUsername);
                            inviteSender.acceptWorkoutInviteFromUser(username);
                            users.child(username).child("acceptedWorkouts").setValue(user.getAcceptedWorkouts());
                            users.child(username).child("workoutInvites").setValue(user.getWorkoutInvites());
                            users.child(inviteSenderUsername).child("acceptedWorkouts").setValue(user.getAcceptedWorkouts());
                            workoutInviteList.setAdapter(new WorkoutInviteAdapter(getActivity(), user.getWorkoutInvites()));

                        }
                    });

                    holder.declineRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            user.declineWorkoutRequestFromUser(inviteSenderUsername);
                            users.child(username).child("workoutInvites").setValue(user.getWorkoutInvites());
                            workoutInviteList.setAdapter(new WorkoutInviteAdapter(getActivity(), user.getWorkoutInvites()));
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            thisView.setTag(holder);
            view = thisView;

            return view;
        }

        class WorkoutInviteHolder {
            TextView usernameText;
            TextView dateAndTimeText;
            TextView locationText;
            Button acceptRequest;
            Button declineRequest;
        }
    }

    class FriendRequestAdapter extends BaseAdapter {

        int count;
        Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<String> friendRequests = new ArrayList<>();

        public FriendRequestAdapter(Context context, ArrayList<String> friendRequests) {

            this.context = context;
            this.friendRequests.addAll(friendRequests);
            layoutInflater = LayoutInflater.from(context);
            count = friendRequests.size();

        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return friendRequests.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            final int index = i;
            final View thisView = layoutInflater.inflate(R.layout.friend_request_list_element, null);
            final FriendRequestHolder holder = new FriendRequestHolder();
            final String requestSenderUsername = friendRequests.get(index);
            holder.usernameText = thisView.findViewById(R.id.usernameText);
            holder.acceptRequest = thisView.findViewById(R.id.acceptRequest);
            holder.declineRequest = thisView.findViewById(R.id.declineRequest);

            holder.usernameText.setText(requestSenderUsername);

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User requestSender = dataSnapshot.child(requestSenderUsername).getValue(User.class);

                    holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            user.addFriend(requestSenderUsername);
                            user.removeFriendRequestFromUser(requestSenderUsername);
                            requestSender.addFriend(username);
                            users.child(username).child("friendList").setValue(user.getFriendList());
                            users.child(requestSenderUsername).child("friendList").setValue(requestSender.getFriendList());
                            friendRequestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));
                            friendList.setAdapter(new FriendListAdapter(getActivity(), user.getFriendList()));

                        }
                    });

                    holder.declineRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            user.removeFriendRequestFromUser(requestSenderUsername);
                            users.child(username).child("friendRequests").setValue(user.getFriendRequests());
                            friendRequestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            thisView.setTag(holder);
            view = thisView;

            return view;
        }

        class FriendRequestHolder {
            TextView usernameText;
            Button acceptRequest;
            Button declineRequest;
        }
    }


}
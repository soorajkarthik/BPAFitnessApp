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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sooraj.getfit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SocialFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference users;
    String username;
    User user;
    String searchText;
    private View view;
    private ListView friendList, searchList, workoutInviteList, friendRequestList, workoutCalendarList;
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
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_social, menu);
        search = (SearchView) menu.getItem(3).getActionView();
        search.setQueryHint("Start typing to search");

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                    getActivity().getActionBar().hide();
            }
        });

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

        if (item.getItemId() == R.id.workout_calendar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Workout Calendar");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.list_view, (ViewGroup) getMyView(), false);
            workoutCalendarList = viewInflated.findViewById(R.id.listView);
            workoutCalendarList.setAdapter(new WorkoutCalendarAdapter(getActivity(), user.getAcceptedWorkouts()));
            builder.setView(viewInflated);

            builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } else if (item.getItemId() == R.id.workout_requests) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Workout Requests");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.list_view, (ViewGroup) getMyView(), false);
            workoutInviteList = viewInflated.findViewById(R.id.listView);
            workoutInviteList.setAdapter(new WorkoutInviteAdapter(getActivity(), user.getWorkoutInvites()));
            builder.setView(viewInflated);

            builder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

        } else if (item.getItemId() == R.id.friend_requests) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Friend Requests");
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.list_view, (ViewGroup) getMyView(), false);
            friendRequestList = viewInflated.findViewById(R.id.listView);
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
            holder.usernameText = thisView.findViewById(R.id.usernameText);
            holder.lastSeen = thisView.findViewById(R.id.lastSeenText);
            holder.inviteToWorkout = thisView.findViewById(R.id.inviteToWorkout);

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
                            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.workout_invite_dialog, (ViewGroup) getMyView(), false);
                            final EditText day = viewInflated.findViewById(R.id.day);
                            final EditText month = viewInflated.findViewById(R.id.month);
                            final EditText year = viewInflated.findViewById(R.id.year);
                            final EditText hour = viewInflated.findViewById(R.id.hour);
                            final EditText minute = viewInflated.findViewById(R.id.minute);
                            final Spinner timeOfDay = viewInflated.findViewById(R.id.timeOfDaySpinner);

                            final EditText location = viewInflated.findViewById(R.id.location);
                            builder.setView(viewInflated);

                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String dateString = month.getText().toString() + "/"
                                            + day.getText().toString() + "/"
                                            + year.getText().toString() + " "
                                            + hour.getText().toString() + ":"
                                            + minute.getText().toString()
                                            + ((TextView)timeOfDay.getSelectedView()).getText().toString();

                                    if(isDateValid(dateString)) {
                                        dialog.dismiss();
                                        friend.addWorkoutInvite(username, dateString, location.getText().toString());
                                        users.child(friend.getUsername()).child("workoutInvites").setValue(friend.getWorkoutInvites());
                                    } else {
                                        Toast.makeText(getActivity(), "Please ensure your date is entered is valid and in the correct format and try again", Toast.LENGTH_SHORT).show();
                                    }
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

            long currentTime = System.currentTimeMillis();
            long timeSeen = friend.getLastSeen();
            long difference = currentTime - timeSeen;

            long months = difference / ((long) 30 * 24 * 60 * 60 * 1000);
            long weeks = difference / (7 * 24 * 60 * 60 * 1000);
            long days = difference / (24 * 60 * 60 * 1000);
            long hours = difference / (24 * 60 * 60 * 1000);
            long minutes = difference / (60 * 60 * 1000);

            if (months > 0)
                return months + " months ago";

            else if (weeks > 0)
                return weeks + " weeks ago";

            else if (days > 0)
                return days + " days ago";

            else if (hours > 0)
                return hours + " hours ago";

            else if (minutes > 0)
                return minutes + " minutes ago";

            else
                return "Just now";

        }

        private boolean isDateValid(String dateString) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");
            simpleDateFormat.setLenient(false);
            try {
                Date date = simpleDateFormat.parse(dateString.trim());
                if (date.getTime() < System.currentTimeMillis()) {
                    return false;
                }
            } catch (ParseException pe) {
                return false;
            }

            return true;
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
            final View thisView = layoutInflater.inflate(R.layout.user_search_view, null);
            final FriendSearchHolder holder = new FriendSearchHolder();
            holder.usernameText = thisView.findViewById(R.id.usernameText);
            holder.requestStatusText = thisView.findViewById(R.id.requestStatusText);
            holder.updateFriendStatus = thisView.findViewById(R.id.updateFriendStatus);

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
                                    //friendRequestList.setAdapter(new FriendRequestAdapter(getActivity(), user.getFriendRequests()));
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
            deleteExpiredInvites();
            inviteSenders.addAll(workoutInvites.keySet());
            count = inviteSenders.size();
            this.context = context;
        }

        public void deleteExpiredInvites() {
            ArrayList<String> oldWorkoutKeys = new ArrayList<>();

            for (Map.Entry<String, ArrayList<String>> entry : workoutInvites.entrySet()) {

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");
                    Date date = simpleDateFormat.parse(entry.getValue().get(0));

                    if (date.getTime() < System.currentTimeMillis()) {
                        oldWorkoutKeys.add(entry.getKey());
                    }
                } catch (ParseException e) {
                }
            }

            for (String oldKey : oldWorkoutKeys) {
                workoutInvites.remove(oldKey);
            }
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

            holder.usernameText.setText(inviteSenderUsername);
            System.out.println(workoutInvites.toString());
            holder.dateAndTimeText.setText(workoutInvites.get(inviteSenderUsername).get(0));
            holder.locationText.setText(workoutInvites.get(inviteSenderUsername).get(1));

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User inviteSender = dataSnapshot.child(inviteSenderUsername).getValue(User.class);

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
                            users.child(username).child("friendRequests").setValue(user.getFriendRequests());
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

    class WorkoutCalendarAdapter extends BaseAdapter {

        private Context context;
        private HashMap<String, ArrayList<String>> acceptedWorkouts = new HashMap<>();
        private ArrayList<String> workoutPartners = new ArrayList<>();
        private LayoutInflater layoutInflater;
        private int count;

        public WorkoutCalendarAdapter(Context context, HashMap<String, ArrayList<String>> acceptedWorkouts) {
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.acceptedWorkouts.putAll(acceptedWorkouts);
            sortAcceptedWorkoutsByDate();
            deleteOldWorkouts();
            workoutPartners.addAll(this.acceptedWorkouts.keySet());
            count = workoutPartners.size();

        }

        public void sortAcceptedWorkoutsByDate() {
            List<Map.Entry<String, ArrayList<String>>> list = new LinkedList<>(acceptedWorkouts.entrySet());

            Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<String>>>() {
                @Override
                public int compare(Map.Entry<String, ArrayList<String>> pair1, Map.Entry<String, ArrayList<String>> pair2) {

                    Date date1 = new Date(), date2 = new Date();

                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");
                        date1 = simpleDateFormat.parse(pair1.getValue().get(0));
                        date2 = simpleDateFormat.parse(pair2.getValue().get(0));
                    } catch (ParseException e) {
                    }

                    return date1.compareTo(date2);
                }
            });

            HashMap<String, ArrayList<String>> temp = new LinkedHashMap<>();
            for (Map.Entry<String, ArrayList<String>> entry : list) {
                temp.put(entry.getKey(), entry.getValue());
            }

            acceptedWorkouts.clear();
            acceptedWorkouts.putAll(temp);
        }

        public void deleteOldWorkouts() {
            ArrayList<String> oldWorkoutKeys = new ArrayList<>();

            for (Map.Entry<String, ArrayList<String>> entry : acceptedWorkouts.entrySet()) {

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");
                    Date date = simpleDateFormat.parse(entry.getValue().get(0));

                    if (date.getTime() < System.currentTimeMillis()) {
                        oldWorkoutKeys.add(entry.getKey());
                    }
                } catch (ParseException e) {
                }
            }

            for (String oldKey : oldWorkoutKeys) {
                acceptedWorkouts.remove(oldKey);
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return workoutPartners.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final int index = i;
            final View thisView = layoutInflater.inflate(R.layout.accepted_workout_list_element, null);
            final WorkoutCalendarHolder holder = new WorkoutCalendarHolder();
            holder.usernameText = thisView.findViewById(R.id.usernameText);
            holder.dateAndTimeText = thisView.findViewById(R.id.dateAndTimeText);
            holder.locationText = thisView.findViewById(R.id.locationText);
            holder.cancelWorkout = thisView.findViewById(R.id.cancelWorkout);
            final String workoutPartnerUsername = workoutPartners.get(i);

            holder.usernameText.setText(workoutPartnerUsername);
            holder.dateAndTimeText.setText(acceptedWorkouts.get(workoutPartnerUsername).get(0));
            holder.locationText.setText(acceptedWorkouts.get(workoutPartnerUsername).get(1));

            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    final User workoutPartner = dataSnapshot.child(workoutPartnerUsername).getValue(User.class);


                    holder.cancelWorkout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            user.cancelWorkoutFromUser(workoutPartnerUsername);
                            workoutPartner.cancelWorkoutFromUser(username);
                            users.child(username).child("acceptedWorkouts").setValue(user.getAcceptedWorkouts());
                            users.child(workoutPartnerUsername).child("acceptedWorkouts").setValue(workoutPartner.getAcceptedWorkouts());
                            workoutCalendarList.setAdapter(new WorkoutCalendarAdapter(getActivity(), user.getAcceptedWorkouts()));

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

        class WorkoutCalendarHolder {
            TextView usernameText;
            TextView dateAndTimeText;
            TextView locationText;
            Button cancelWorkout;
        }
    }
}

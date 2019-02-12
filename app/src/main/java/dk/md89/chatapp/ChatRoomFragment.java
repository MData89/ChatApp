package dk.md89.chatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment

{
    private View roomsFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private DatabaseReference RoomReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        roomsFragmentView = inflater.inflate(R.layout.fragment_rooms, container, false); // collects data from firebase of created chat rooms

        RoomReference = FirebaseDatabase.getInstance().getReference().child("Chat Rooms");

        InitializeFields();

        RetrieveAndDisplayRooms();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) // shows a list with created chat rooms
            {
                String currentRoomName = adapterView.getItemAtPosition(position).toString();

                Intent chatRoomIntent = new Intent(getContext(), ChatRoomActivity.class);
                chatRoomIntent.putExtra("roomName", currentRoomName);
                startActivity(chatRoomIntent);
            }
        });

        return roomsFragmentView;
    }



    private void InitializeFields()
    {
        list_view = roomsFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_rooms);
        list_view.setAdapter(arrayAdapter);
    }


    private void RetrieveAndDisplayRooms()
    {
        RoomReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot)
            {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }

}

package dk.md89.chatapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class ChatRoomActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ImageButton SendMessage;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersReference, RoomNameReference, RoomMessageKeyReference;

    private String currentRoomName, currentUserId, currentUserName, currentDate, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        currentRoomName = getIntent().getExtras().get("roomName").toString();
        Toast.makeText(ChatRoomActivity.this, currentRoomName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        RoomNameReference = FirebaseDatabase.getInstance().getReference().child("Chat Rooms").child(currentRoomName);


        InitializerField();

        GetUserInfo();

        SendMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SaveMessageDatabase();

                userMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        RoomNameReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    private void InitializerField()
    {
        mToolbar =(Toolbar) findViewById(R.id.chat_room_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentRoomName);

        SendMessage = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_room_message);
        displayTextMessage = (TextView) findViewById(R.id.chat_room_text_display);
        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
    }

    private void GetUserInfo()
    {
        UsersReference.child(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void SaveMessageDatabase()
    {
        String message = userMessageInput.getText().toString();
        String messageKey = RoomNameReference.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this,"Please write a message a", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Calendar calDate = Calendar.getInstance();
            SimpleDateFormat currentDataFormat = new SimpleDateFormat("dd MMM, YYYY");
            currentDate = currentDataFormat.format(calDate.getTime());

            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            currentTime = currentTimeFormat.format(calTime.getTime());

            HashMap<String, Object> roomMessageKey = new HashMap<>();
            RoomNameReference.updateChildren(roomMessageKey);

            RoomMessageKeyReference = RoomNameReference.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name", currentUserName);
                messageInfoMap.put("message", message);
                messageInfoMap.put("date", currentDate);
                messageInfoMap.put("time", currentTime);
            RoomMessageKeyReference.updateChildren(messageInfoMap);

        }
    }


    private void DisplayMessages(DataSnapshot dataSnapshot)
    {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "   " + chatDate + "\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }


}

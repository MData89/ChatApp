package dk.md89.chatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        RootReference = FirebaseDatabase.getInstance().getReference();

        mToolbar =(Toolbar) findViewById(R.id.main_page_toolbar); //In the OnCreate method we start the main_page_toolbar and set the title of it to ChatApp.
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() //Here we check if the user is logged in or not. If not logged in the user will be send to the Login page.
    {
        super.onStart();

        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }

        else
        {
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance()
    {
        String currentUserId = mAuth.getCurrentUser().getUid();

        RootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)

            {
                if ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }

                else
                    {
                        SendUserToSettingsActivity();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option)
        {
            mAuth.signOut();
            SendUserToLoginActivity();
        }

        if (item.getItemId() == R.id.main_settings_option)
        {
            SendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.main_create_chat_room_option)
        {
            RequestNewGroup();
        }

        return true;

    }


    private void RequestNewGroup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog); // lets the user create a new chat room
        builder.setTitle("Enter Chat Room Name");

        final EditText chatRoomNameField = new EditText(MainActivity.this);
        chatRoomNameField.setHint("example Family");
        builder.setView(chatRoomNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                String chatRoomName = chatRoomNameField.getText().toString();

                if (TextUtils.isEmpty(chatRoomName))
                {
                    Toast.makeText(MainActivity.this, "Please write a chat room name", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    CreateNewChatRoom(chatRoomName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int which)
            {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewChatRoom(final String chatRoomName)
    {
        RootReference.child("Chat Rooms").child(chatRoomName).setValue("") // if chat room created show a Toast message to user.
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, chatRoomName + " is created!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent (MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent = new Intent (MainActivity.this, SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }


}

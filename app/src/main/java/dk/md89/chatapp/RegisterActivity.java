package dk.md89.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity
{
    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword;
    private TextView AlreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private DatabaseReference RootReference;

    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState)                                              // this method saves registration data to firebase database
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootReference = FirebaseDatabase.getInstance().getReference();


        InitializeFields();

        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener()                         // when app launches and user have an account it sends the user to LoginActivity
        {
            @Override
            public void onClick(View view)
            {
                SendUserToLoginActivity();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener()                           // creates new account when CreateNewAccount is pushed.
        {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();
            }
        });

    }

    private void CreateNewAccount()                                                                 // CreateNewAccount method, where it checks for input and tells the user to write inputs if its empty, else creates an account
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter correct password", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we create a new account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootReference.child("Users").child(currentUserID).setValue("");

                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "New Account Created", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }


    }

    private void InitializeFields()
    {
        CreateAccountButton = (Button) findViewById(R.id.register_button);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        AlreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account_link);

        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

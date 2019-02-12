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

public class LoginActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // below code should add the sign in method for google and facebook, but I have not managed to make it appear!
//        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setProviders(AuthUI.FACEBOOK_PROVIDER, AuthUI.GOOGLE_PROVIDER).build(), 1);

        mAuth = FirebaseAuth.getInstance();


        InitializeFields(); // created a method to initialize the fields on line 118-126.

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener()  // sends the user to Register Activity so they can sign up.
        {
            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) 
            {
             AllowUserToLogin();   
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() //allows the user to sign up with phone number.
        {
            @Override
            public void onClick(View v)
            {
                Intent phoneLoginIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);
            }
        });
    }


    private void AllowUserToLogin() // checks for input (mail and password), if no input Toast tells the user to insert input. else the user gets an account.
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
                loadingBar.setTitle("Sign in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful())
                                {
                                    SendUserToMainActivity();
                                    Toast.makeText(LoginActivity.this, "You are now logged in", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                }
                                else
                                {
                                    String message = task.getException().toString();
                                    Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }
    }


    private void InitializeFields()
    {
        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.new_account_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);


    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class); // a method sends user to mainActivity after account successfully created.
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent registerIntent = new Intent (LoginActivity.this, RegisterActivity.class); // a method sends user to RegistrationActivity method
        startActivity(registerIntent);
    }

}

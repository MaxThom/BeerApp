package view.beerapp.activities.login;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import view.beerapp.R;
import view.beerapp.activities.navigation.newsFeed.DashBoard;
import view.beerapp.contract.IAsyncResponse;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseUserRepository;
import view.beerapp.utility.LoadData;

/**
 * Signup Activity class is the page where the user can create a new account and Login at the app.
 * The informations of user are stored at Firebase (with FirebaseAuth).
 */
public class SignupActivity extends AppCompatActivity implements View.OnClickListener, IAsyncResponse<String> {

    private Button btnSignup;
    private TextView btnLogin;
    private EditText input_email,input_password;
    private RelativeLayout activity_sign_up;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;
    private LoadData loadData;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loadData = new LoadData();
        loadData.delegate = this;

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //Initialize .xml objects
        btnSignup = (Button)findViewById(R.id.signup_btn_register);
        input_email = (EditText) findViewById(R.id.signup_email);
        input_password = (EditText) findViewById(R.id.signup_password);
        btnLogin = (TextView)findViewById(R.id.signup_btn_login);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        auth= FirebaseAuth.getInstance();

    }

    /**
     * OnCick method based on the buttons of the page.
     * According to user's choice, it transferred to the corresponding page
     *
     *  @param view the button view
     */
    @Override
    public void onClick(View view) {

        closeVirtualKeyboard();

        if(view.getId() == R.id.signup_btn_login)//Login Activity(LoginActivity), if user has already account
        {
            finish();
        }else if(view.getId() == R.id.signup_btn_register)//DashBoard Activity, if user wants to register into app
        {
            if (input_email.getText().toString().equals("") || input_password.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),getString(R.string.dialog_fill_info),Toast.LENGTH_LONG).show();
            }
            else
                signUpUser(input_email.getText().toString(),input_password.getText().toString());
        }

    }

    /**
     * SignUpUser method focus at the given data of the user, who wants to register at the app.
     * Provides an email and a password, and if the email is available and correct and the length of password is
     * more than 6 letters/digits, the user subscribed at the firebase and as a result at the app.
     *
     * @param email given from the user (EditText)
     * @param password given from the user (EditText)
     */
    private void signUpUser(final String email, String password) {

        progressDialog = ProgressDialog.show(SignupActivity.this,getString(R.string.dialog_please_wait), getString(R.string.dialog_new_account), true);

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){//If the user subscribed successfully, waits for the corresponding message
                    progressDialog.setMessage(getString(R.string.dialog_data));
                    loadData.execute();

                }else{ //Otherwise an error message appears
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * ProcessFinish it the last method that called, before user transfer at DashBoard,
     * where the app save him/her as the current user and a welcome message appears
     *
     * @param output the result of the task
     */
    @Override
    public void processFinish(String output) {
        Toast.makeText(getApplicationContext(),getString(R.string.dialog_registration_succesful),Toast.LENGTH_LONG).show();
        progressDialog.dismiss();

        //Set current user
        User user = new User(input_email.getText().toString());
        user.idFb = FirebaseUserRepository.getInstance().addUser(user);
        FirebaseUserRepository.getInstance().setCurrentUser(user);

        startActivity(new Intent(SignupActivity.this, DashBoard.class));
        finish();
    }

    /**
     * If the back button is clicked, goes back to the login page by starting its activity.
     * @param item menu item
     * @return selected item action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       closeVirtualKeyboard();
        switch (item.getItemId()) {
            //Navigation menu button pressed
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Closes the virtual keyboard if it is opened
     */
    private void closeVirtualKeyboard(){

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Respecting the interface contract
     * @param progress progress
     */
    @Override
    public void progressUpdate(int progress) {
        // Override not needed here
    }

}

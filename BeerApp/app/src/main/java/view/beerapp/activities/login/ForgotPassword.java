package view.beerapp.activities.login;


import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.FirebaseAuth;

import view.beerapp.R;
import view.beerapp.repository.FirebaseDrinkRepository;

/**
 * ForgotPassword Activity class is the page where the user can reset his/her password
 * by receiving an email at the given field of the user and logging back into the app
 */
public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private EditText input_email;
    private Button btnResetPass;
    private TextView btnBack;
    private RelativeLayout activity_forgot;

    private FirebaseAuth auth;

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Sets up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //Initialize .xml objects
        input_email = (EditText)findViewById(R.id.forgot_email);
        btnResetPass = (Button)findViewById(R.id.fogrot_btn_reset);
        btnBack = (TextView) findViewById(R.id.forgot_btn_back);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

        Bundle data = getIntent().getExtras();
        String email = data.getString("EMAIL");
        if (email != null && !email.equals("")) {
            input_email.setText(email);
        }
    }

    /**
     * OnCick method based on the buttons of the page.
     * According to user's choice, it transferred to the corresponding page
     *
     * @param view view
     */
    @Override
    public void onClick(View view) {

        closeVirtualKeyboard();

        if(view.getId() == R.id.forgot_btn_back)//Login Activity(LoginActivity)
        {
            finish();
        }else if(view.getId() == R.id.fogrot_btn_reset)
        {
            if (input_email.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(),getString(R.string.forgot_password_valid_email),Toast.LENGTH_LONG).show();
            } else {
                resetPassword(input_email.getText().toString().trim());
            }
        }
    }

    /**
     * Starts the loginActivity and closes keyboard when toolbar back button is pressed
     * @param item menu item
     * @return super method result
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
     * ResetPassword method works at sending an email at user's given field,
     * as a result he/she can reset the password and connect again at the app
     *
     * @param email , that given from the user (Edit Text)
     */
    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())//If the email sent successfully at the user, it receives a hit message
                {
                    Toast.makeText(getApplicationContext(),getString(R.string.forgot_password_send_email),Toast.LENGTH_LONG).show();
                    finish();
                }
                else //Otherwise an error message appears
                {
                    Toast.makeText(getApplicationContext(),getString(R.string.forgot_password_fail_password),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
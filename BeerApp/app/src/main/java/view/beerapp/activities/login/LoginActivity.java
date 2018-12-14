package view.beerapp.activities.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import view.beerapp.R;
import view.beerapp.activities.hardware.DefaultCameraActivity;
import view.beerapp.activities.navigation.newsFeed.DashBoard;
import view.beerapp.contract.IAsyncResponse;
import view.beerapp.entities.User;
import view.beerapp.repository.FirebaseUserRepository;
import view.beerapp.utility.LoadData;

/**
 * LoginActivity class is the welcome page for the user, that can login or sign up or reset the password
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IAsyncResponse<String> {

    private CallbackManager callbackManager;
    private ProgressDialog mDialog;
    private  ImageView imgAvatar;
    private  Button btnLogin;
    private  EditText input_email,input_password;
    private  TextView btnSingup,btnForgotPass;
    private CheckBox chkRemember;

    private  RelativeLayout activity_main;

    private FirebaseAuth auth;
    private LoadData dataAsync;
    private ProgressDialog progressDialog;
    private int devCounter;

    /**
     * Receive result for the facebook call to login
     * @param requestCode match the back result
     * @param resultCode result
     * @param data the extras
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Starting point of the activity.
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        devCounter = 0;
        dataAsync = new LoadData();
        dataAsync.delegate = this;

        callbackManager = CallbackManager.Factory.create();
        imgAvatar = (ImageView)findViewById(R.id.avatar);

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile"));

        //Login Button works for the Facebook connection, where the user can connect with his/her facebook account
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mDialog = new ProgressDialog(LoginActivity.this);
                mDialog.setMessage(getString(R.string.dialog_retreiving_message));
                mDialog.show();

                String accesstoken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        mDialog.dismiss();
                        getData(object);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id");
                request.setParameters(parameters);
                request.executeAsync();

                Toast.makeText(getApplicationContext(),getString(R.string.toast_login_successful),Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this, DashBoard.class));

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        //Initialize .xml objects
        btnLogin = (Button)findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnSingup = (TextView)findViewById(R.id.login_btn_signup);
        chkRemember = (CheckBox)findViewById(R.id.chkRemember);

        btnForgotPass = (TextView)findViewById(R.id.login_btn_forgot_password);

        //Setting the listeners
        btnSingup.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);
        findViewById(R.id.lblCopyright).setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // Get saved credentials
        getSavedCredentials();

        //init Firebase
        auth = FirebaseAuth.getInstance();

        //If the user have already connext to the app, he/she transfer directly at the DashBoard
        if(auth.getCurrentUser() != null)
        {
            progressDialog = ProgressDialog.show(LoginActivity.this,getString(R.string.dialog_please_wait),
                    getString(R.string.dialog_data), true);
            dataAsync.execute();
        }
    }

    /**
     * Get Json data for facebook login (feature still in development)
     * @param obj json object
     */
    private void getData(JSONObject obj) {
        try{
            URL profile_picture = new URL("https://graph.facebook.com/"+obj.getString("id")+"/picture?width=250&height=250");
            Picasso.with(this).load(profile_picture.toString()).into(imgAvatar);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
        if (view.getId() == R.id.lblCopyright) {
            devCounter++;
            if (devCounter == 10) {
                input_email.setText("a@b.com");
                input_password.setText("1234567");
                devCounter = 0;
            }
        }
        else if(view.getId() == R.id.login_btn_forgot_password) //ForgotPassword Activity
        {
            Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
            intent.putExtra("EMAIL", input_email.getText().toString().trim());
            startActivity(intent);
        }else if (view.getId() == R.id.login_btn_signup) { //Signup Activity
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        }else if (view.getId() == R.id.login_btn_login) { //DashBoard Activity
            //Check if the user filled the email and the password
            if (input_email.getText().toString().equals("") || input_password.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),getString(R.string.dialog_fill_info),Toast.LENGTH_LONG).show();
            }
            else {
                if (chkRemember.isChecked())
                    saveCredentials();
                else
                    deleteCredentials();
                loginUser(input_email.getText().toString().trim(), input_password.getText().toString().trim());
            }
        }
    }

    /**
     * The loginUser method focus at the given data of the user and check them if are true and existing at the firebase.
     * If the user exists, the connection is confirmed.
     *
     * @param email , given from the user (Edit text)
     * @param password , given from the user (Edit text)
     */
    private void loginUser(String email, final String password) {

        progressDialog = ProgressDialog.show(LoginActivity.this,getString(R.string.dialog_please_wait), getString(R.string.dialog_processing), true);
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            //If something else went wrong, like wrong password, the user show a message and try to connect again
            if(!task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.toast_email_not_found), Toast.LENGTH_LONG).show();

            }else{ //Successfully connection into the app
                progressDialog.setMessage(getString(R.string.dialog_data));
                dataAsync.execute();
            }
            }
        });
    }

    /**
     * ProcessFinish it the last method that called, before user transfer at DashBoard,
     * where the app save him/her as the current user and a welcome message appears
     *
     * @param output message
     */
    @Override
    public void processFinish(String output) {

        for (User usr : FirebaseUserRepository.getInstance().getUsers()) {
            if (usr.getEmail().equals(auth.getCurrentUser().getEmail())) {
                //Set current user
                FirebaseUserRepository.getInstance().setCurrentUser(usr);
            }
        }

        Toast.makeText(getApplicationContext(),getString(R.string.toast_login_successful),Toast.LENGTH_LONG).show();
        progressDialog.dismiss();

        startActivity(new Intent(LoginActivity.this, DashBoard.class));
        finish();
    }

    /**
     * Receive the progress of the fetch data from firebase
     * @param progress the progress indicator
     */
    @Override
    public void progressUpdate(int progress) {

    }

    /**
     * Saved the user's credentials if the remember me checkbox is checked
     */
    private void saveCredentials() {
        SharedPreferences sharedPref = this.getSharedPreferences("CREDENTIAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("EMAIL", input_email.getText().toString());
        editor.putString("PASSWORD", input_password.getText().toString());
        editor.apply();
    }

    /**
     * Delete the user's credential if the remember me checkbox is not checked
     */
    private void deleteCredentials() {
        SharedPreferences sharedPref = this.getSharedPreferences("CREDENTIAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("EMAIL");
        editor.remove("PASSWORD");
        editor.apply();
    }

    /**
     * Retrieve the user's credential if the remember me checkbox was checked before
     */
    private void getSavedCredentials() {
        SharedPreferences sharedPref = this.getSharedPreferences("CREDENTIAL", Context.MODE_PRIVATE);
        String email = sharedPref.getString("EMAIL", "");
        String password = sharedPref.getString("PASSWORD", "");
        input_email.setText(email);
        input_password.setText(password);
    }
}

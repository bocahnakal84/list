package org.creativecommons.thelist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment {
    public static final String TAG = LoginFragment.class.getSimpleName();
    RequestMethods requestMethods = new RequestMethods(getActivity());
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(getActivity());


    //For Request
    protected JSONObject mUserData;

    //UI Elements
    protected EditText mUsernameField;
    protected EditText mEmailField;
    protected EditText mPasswordField;
    protected Button mLoginButton;
    //protected ProgressBar mProgressBar;

    String mUsername;
    String mPassword;
    String mEmail;

    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mUsernameField = (EditText)getView().findViewById(R.id.nameField);
        mEmailField = (EditText)getView().findViewById(R.id.emailField);
        mPasswordField = (EditText)getView().findViewById(R.id.passwordField);
        mLoginButton = (Button)getView().findViewById(R.id.loginButton);
        mUsernameField.requestFocus();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = mUsernameField.getText().toString().trim();
                mPassword = mPasswordField.getText().toString().trim();
                mEmail = mEmailField.getText().toString().trim();

                if(mUsername.isEmpty() || mPassword.isEmpty() || mEmail.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    //Login user or Create account
                    Log.v(TAG, "Login user or create account here");
                    //If user exists login, else create new user
                    if(requestMethods.isUser()) {
                        loginUser();
                    } else {
                        createNewUser();
                    }
                }
            }
        });
    }


    //TODO: Login existing user
    private void loginUser() {
        //Verify user identity
    }

    private void createNewUser() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //Genymotion Emulator
        String url = ApiConstants.CREATE_NEW_USER;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user";

        //Create Object to send
        JSONObject jso = sharedPreferencesMethods.createCategoryListObject
                (ApiConstants.USER_CATEGORIES);
        try {
            jso.put(ApiConstants.USER_NAME, mUsername);
            jso.put(ApiConstants.USER_EMAIL,mEmail);
            jso.put(ApiConstants.USER_PASSWORD,mPassword);

        } catch (JSONException e) {
            Log.v(TAG,e.getMessage());
        }

        //Data to be sent

//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(ApiConstants.USER_NAME, mUsername);
//        params.put(ApiConstants.USER_EMAIL,mEmail);
//        params.put(ApiConstants.USER_PASSWORD,mPassword);
        //TODO: send user preferences

        JsonObjectRequest newUserRequest = new JsonObjectRequest(Request.Method.POST, url, jso,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Handle Data
                            mUserData = response.getJSONObject(ApiConstants.RESPONSE_CONTENT);
                            Log.v(TAG,mUserData.toString());
                            //mProgressBar.setVisibility(View.INVISIBLE);
                            //TODO: Update UI

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
                //TODO: Where does this take you?
            }
        });
        queue.add(newUserRequest);
    }

}

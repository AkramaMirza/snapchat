package com.akramamirza.photobabble;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;

/**
 * Created by CR7 on 10/14/2015.
 */
public class LoginFragment extends Fragment {

    ProgressBar progressBar;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText reenterPasswordEditText;
    EditText emailEditText;
    Button actionButton;
    Button changeFormButton;
    TextView changeFormText;
    boolean isLoginForm;
    int result;

    public LoginFragment(){
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            isLoginForm = true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        usernameEditText = (EditText) rootView.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);
        reenterPasswordEditText = (EditText) rootView.findViewById(R.id.reenterPasswordEditText);
        emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
        changeFormText = (TextView) rootView.findViewById(R.id.changeFormText);

        actionButton = (Button) rootView.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAction(usernameEditText.getText().toString(), passwordEditText.getText().toString(),
                        reenterPasswordEditText.getText().toString(), emailEditText.getText().toString());
            }
        });

        changeFormButton = (Button) rootView.findViewById(R.id.changeFormButton);
        changeFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoginForm) {
                    changeFormButton.setText("Log in"); //if it is currently the login form that means the button currently says "sign up" so we need to change it to "login"
                    changeFormText.setText("Already have an account?");
                    actionButton.setText("Sign up");

                    doAnimation(R.animator.login_translation_down,
                            changeFormButton, changeFormText, actionButton);

                    doAnimation(R.animator.login_translation_right_in, reenterPasswordEditText, emailEditText);

                    passwordEditText.setBackgroundColor(Color.parseColor("#fff2f2f2"));

                } else {
                    changeFormButton.setText("Sign up");
                    changeFormText.setText("Need to create an account?");
                    actionButton.setText("Log in");

                    doAnimation(R.animator.login_translation_up,
                            changeFormButton, changeFormText, actionButton);

                    doAnimation(R.animator.login_translation_right_out, reenterPasswordEditText, emailEditText);

                    passwordEditText.setBackground(getResources().getDrawable(R.drawable.grey_rounded_corners_bottom));
                }
                isLoginForm = !isLoginForm;
            }
        });

        return rootView;
    }

    private void doAnimation(int animationId, Object... myObjects) {

        ArrayList<AnimatorSet> animatorSets = new ArrayList<AnimatorSet>();

        for (Object myObject : myObjects) {
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainApplication.getAppContext(), animationId);
            set.setTarget(myObject);
            animatorSets.add(set);
        }

        for (AnimatorSet animatorSet : animatorSets) {
            animatorSet.start();
        }
    }

    private void onAction(final String username, String password, String reenterPassword, String email) {

        //result will == Activity.RESULT_CANCELED unless user successfully logs in
        getActivity().setResult(Activity.RESULT_CANCELED);

        // PLACING CIRCULAR PROGRESS BAR IN CENTER OF ACTION BUTTON
        float actionButtonWidth = actionButton.getWidth();
        float actionButtonHeight = actionButton.getHeight();
        float progressBarRadius = progressBar.getWidth() / 2;
        progressBar.setX(actionButton.getX() + actionButtonWidth / 2 - progressBarRadius);
        progressBar.setY(actionButton.getY() + actionButtonHeight / 2 - progressBarRadius);

        //disable form
        resetForm(false, "");

        if (isLoginForm) { //user wants to log in

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (parseUser != null) {
                        Toast.makeText(MainApplication.getAppContext(), "Welcome back " + username + "!", Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        Toast.makeText(MainApplication.getAppContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                        // let the user try again
                        resetForm(true, "Log in");

                    }
                }
            });

        } else { //user wants to create an account

            if (!password.equals(reenterPassword)) {
                Toast.makeText(MainApplication.getAppContext(), "Your passwords do not match", Toast.LENGTH_LONG).show();

                // let the user try again
                resetForm(true, "Sign up");

                return;
            }

            ParseUser user = new ParseUser();

            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(MainApplication.getAppContext(), "Welcome " + username + "!", Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        Toast.makeText(MainApplication.getAppContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                        // let the user try again
                        resetForm(true, "Sign up");
                    }
                }
            });
        }
    }

    private void resetForm(boolean enable, String actionButtonText) {

        if (enable){
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        actionButton.setText(actionButtonText);

        actionButton.setEnabled(enable);
        changeFormButton.setEnabled(enable);
        usernameEditText.setEnabled(enable);
        passwordEditText.setEnabled(enable);
        reenterPasswordEditText.setEnabled(enable);
        emailEditText.setEnabled(enable);
    }

}
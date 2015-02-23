package info.jeanlopes.jeantcc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class ConvidadoActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ConvidadoRegisterTask mConvidadoTask = null;

    // UI references.
    private EditText mNomeView;
    private RadioGroup mGenderGroup;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convidado);

        // Set up the login form.
        mNomeView = (EditText) findViewById(R.id.nome);
        mGenderGroup = (RadioGroup) findViewById(R.id.genderGroup);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarConvidado();
            }
        });

        mLoginFormView = findViewById(R.id.convidado_form);
        mProgressView = findViewById(R.id.convidado_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void registrarConvidado() {

        int genero;

        if (mConvidadoTask != null) {
            return;
        }

        // Reset errors.
        mNomeView.setError(null);

        // Store values at the time of the login attempt.
        String nome = mNomeView.getText().toString();
        int generoId = mGenderGroup.getCheckedRadioButtonId();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(nome) ) {
            mNomeView.setError(getString(R.string.error_field_required));
            focusView = mNomeView;
            cancel = true;
        }

        if (generoId == findViewById(R.id.homem).getId())
            genero = 1;
        else if (generoId == findViewById(R.id.mulher).getId())
            genero = 0;
        else {
            mNomeView.setError(getString(R.string.error_missing_gender));
            return;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mConvidadoTask = new ConvidadoRegisterTask(nome, genero, this);
            mConvidadoTask.execute((Void) null);

        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    /**
     * Representa o acesso assincrono ao banco de dados
     */
    public class ConvidadoRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNome;
        private final int mGenero;
        private Activity activity;
        private int status;

        ConvidadoRegisterTask(String nome, int genero, Activity activity) {
            this.mNome = nome;
            this.mGenero = genero;
            this.activity = activity;
            this.status = 0;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //***********************************************
            // Enviar dados do convidado por aqui
            // **********************************************

            /*
            try {
                // Simulate network access.
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                return false;
            } */

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost =
                    new HttpPost("http://localhost:8080/jeantcc/ConvidadoAction?nome=fulano&genero=1");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("nome", this.mNome));
                nameValuePairs.add(new BasicNameValuePair("genero", String.valueOf(this.mGenero) ));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                this.status = response.getStatusLine().getStatusCode();

            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mConvidadoTask = null;
            showProgress(false);

            if (success) {
                new AlertDialog.Builder(this.activity)
                        .setTitle("Sucesso!")
                        .setMessage("O nome foi registrado com sucesso! status=" + this.status);
            } else {
                mNomeView.setError(getString(R.string.server_error));
                mNomeView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mConvidadoTask = null;
            showProgress(false);
        }
    }
}




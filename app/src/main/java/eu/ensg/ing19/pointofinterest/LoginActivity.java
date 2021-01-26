package eu.ensg.ing19.pointofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.ensg.ing19.pointofinterest.dataaccess.DataBaseHelper;
import eu.ensg.ing19.pointofinterest.dataaccess.UserDAO;
import eu.ensg.ing19.pointofinterest.dataobject.User;

public class LoginActivity extends AppCompatActivity implements Constants{

    private EditText ed_email, ed_password;
    private Button bt_login, bt_signin;
    private SQLiteDatabase db;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_email = this.findViewById(R.id.ed_email);
        ed_password = this.findViewById(R.id.ed_pass);

        bt_login = this.findViewById(R.id.bt_login);
        bt_signin = this.findViewById(R.id.bt_cancel);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });

        bt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInActivity();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String email = intent.getStringExtra(Constants.EXTRA_EMAIL);
            ed_email.setText(email);
        }

        // Database
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        db = dataBaseHelper.getWritableDatabase();
        userDAO = new UserDAO(db);

    }

    private void tryLogin() {

        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();
        String encodedPassword = EncryptUtils.encrypt(password);

        User user = userDAO.findByEmailAndEncodedPassword(email, encodedPassword);

        if (user != null) {
            openMapsActivity(user);
        } else {
            Toast.makeText(this, "Email or password not correct.", Toast.LENGTH_SHORT).show();
        }

    }

    private void openSignInActivity() {
        String email = ed_email.getText().toString();

        // Create intent
        Intent intent = new Intent(this, SignInActivity.class);

        // Put extra
        intent.putExtra(Constants.EXTRA_EMAIL, email);

        // Start activity
        this.startActivity(intent);

    }

    private void openMapsActivity(User user) {
        // Create intent
        Intent intent = new Intent(this, MapsActivity.class);

        // Put extra
        intent.putExtra(Constants.EXTRA_USER, user);

        // Start activity
        this.startActivity(intent);
    }
}
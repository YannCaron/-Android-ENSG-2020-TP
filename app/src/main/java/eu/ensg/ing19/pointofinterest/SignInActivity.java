package eu.ensg.ing19.pointofinterest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import eu.ensg.ing19.pointofinterest.dataaccess.DataBaseHelper;
import eu.ensg.ing19.pointofinterest.dataaccess.UserDAO;
import eu.ensg.ing19.pointofinterest.dataobject.User;

public class SignInActivity extends AppCompatActivity implements Constants {

    private EditText ed_firstname, ed_lastname, ed_email, ed_pass;
    private Button bt_login, bt_cancel;

    private SQLiteDatabase db;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ed_firstname = findViewById(R.id.ed_firstname);
        ed_lastname = findViewById(R.id.ed_lastname);
        ed_email = findViewById(R.id.ed_email);
        ed_pass = findViewById(R.id.ed_pass);
        bt_login = findViewById(R.id.bt_login);
        bt_cancel = findViewById(R.id.bt_cancel);

        bt_login .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });

        // Database
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        db = dataBaseHelper.getWritableDatabase();
        userDAO = new UserDAO(db);

        Intent intent = getIntent();
        if (intent != null) {
            String email = intent.getStringExtra(Constants.EXTRA_EMAIL);
            ed_email.setText(email);
        }

    }

    private void createUser() {

        User user = new User(
                ed_email.getText().toString(),
                EncryptUtils.encrypt(ed_pass.getText().toString()),
                ed_firstname.getText().toString(),
                ed_lastname.getText().toString()
        );

        user = userDAO.create(user);

        openMapsActivity(user);

    }

    private void openLoginActivity() {
        String email = ed_email.getText().toString();

        // Create intent
        Intent intent = new Intent(this, LoginActivity.class);

        // Put extra
        intent.putExtra(Constants.EXTRA_EMAIL, email);

        // Start activity
        startActivity(intent);

    }

    private void openMapsActivity(User user) {
        // Create intent
        Intent intent = new Intent(this, MapsActivity.class);

        // Put extra
        intent.putExtra(EXTRA_USER, user);

        // Start activity
        this.startActivity(intent);
    }
}
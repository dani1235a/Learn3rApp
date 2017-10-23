package group7.tcss450.uw.edu.uilearner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements TeacherFragment.OnFragmentInterationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method for button press.
     * @param view
     */
    public void onButtonPress(View view) {

        switch(view.getId()) {
            case R.id.buttonStudent:
                loadFragment(new StudentFragment());
                break;
            case R.id.buttonTeacher:
                loadFragment(new TeacherFragment());
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null);
        transaction.commit();
    }

}

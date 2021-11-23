package es.tta.example;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import es.tta.example.model.Status;
import es.tta.prof.view.ProgressTask;


public class MainActivity extends ModelActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editLogin = (EditText)findViewById(R.id.login);
        if( editLogin.getText().toString().isEmpty() ) {
            String login = prefs.loadLogin();
            if( login != null && !login.isEmpty() ) {
                editLogin.getText().append(login);
                ((EditText)findViewById(R.id.passwd)).requestFocus();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login( View view ) {
        final String user = ((EditText)findViewById(R.id.login)).getText().toString();
        String passwd = ((EditText)findViewById(R.id.passwd)).getText().toString();
        prefs.saveLogin(user);
        rest.setHttpBasicAuth(user, passwd);
        new ProgressTask<Status>() {
            @Override
            protected es.tta.example.model.Status work() throws Exception {
                return server.getStatus(user);
            }

            @Override
            protected void onFinish(es.tta.example.model.Status result) {
                data.putAuthToken(rest.getAuthorization());
                data.putUserId(result.getUserId());
                data.putUserName(result.getUserName());
                data.putLessonNumber(result.getLessonNumber());
                data.putLessonTitle(result.getLessonTitle());
                data.putNextTest(1);
                data.putNextExercise(1);
                startModelActivity(MenuActivity.class);
            }
        }.execute();
    }
}

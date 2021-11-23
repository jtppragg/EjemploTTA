package es.tta.example;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import es.tta.example.model.Exercise;
import es.tta.example.model.Test;
import es.tta.prof.view.ProgressTask;


public class MenuActivity extends ModelActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView textLogin = (TextView)findViewById(R.id.menu_login);
        textLogin.setText(String.format("%s %s", getString(R.string.welcome), data.getUserName()));
        TextView textLesson = (TextView)findViewById(R.id.menu_lesson);
        textLesson.setText(String.format("%s %d: %s", getString(R.string.lesson), data.getLessonNumber(), data.getLessonTitle()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    public void track( View view ) {
    }

    public void test( View view ) {
        new ProgressTask<Test>() {
            @Override
            protected Test work() throws Exception {
                return server.getTest(data.getNextTest());
            }

            @Override
            protected void onFinish(Test result) {
                data.putTest(result);
                startModelActivity(TestActivity.class);
            }
        }.execute();
    }

    public void exercise( View view ) {
        new ProgressTask<Exercise>() {
            @Override
            protected Exercise work() throws Exception {
                return server.getExercise(data.getNextExercise());
            }

            @Override
            protected void onFinish(Exercise result) {
                data.putExercise(result);
                startModelActivity(ExerciseActivity.class);
            }
        }.execute();
    }
}
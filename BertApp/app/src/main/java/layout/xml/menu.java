package layout.xml;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jase.bertapp.R;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button btnFood = (Button) findViewById(R.id.btnFood);
        Button btnGo = (Button) findViewById(R.id.btnGo);
        Button btnRelax = (Button) findViewById(R.id.btnRelax);

        Intent step2 = new Intent(getBaseContext(), menu2.class);

        btnFood.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2.putExtra("step1", "Food");
                startActivity(step2);
            }
        });

        btnGo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2.putExtra("step1", "Go");
                startActivity(step2);
            }
        });

        btnRelax.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step2.putExtra("step1", "Relax");
                startActivity(step2);
            }
        });

    }
}

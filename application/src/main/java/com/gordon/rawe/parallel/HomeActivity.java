package com.gordon.rawe.parallel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private View father, son, daughter, flat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        father = findViewById(R.id.father);
        son = findViewById(R.id.son);
        daughter = findViewById(R.id.daughter);
        flat = findViewById(R.id.flat);
        father.setOnClickListener(this);
        son.setOnClickListener(this);
        daughter.setOnClickListener(this);
        flat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.father) {
            try {
                startActivity(new Intent(this, Class.forName("com.gordon.rawe.father.FatherHomeActivity")));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.son) {

        } else if (v.getId() == R.id.daughter) {

        } else if (v.getId() == R.id.flat) {

        }
    }
}

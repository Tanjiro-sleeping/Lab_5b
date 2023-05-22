package com.sp.restaurantlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.widget.AdapterView;

public class RestaurantList extends AppCompatActivity {
    private Cursor model = null;
    private RestaurantAdapter adapter = null;
    private ListView list;
    private TextView empty =null;
    private RestaurantHelper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        empty = findViewById(R.id.empty);
        helper = new RestaurantHelper(this);
        list = findViewById(R.id.list);
        model = helper.getAll();
        adapter = new RestaurantAdapter(this, model, 0);
        list.setOnItemClickListener(onListClick);
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (model !=null){
            model.close();
        }
        model = helper.getAll();
        if (model.getCount() > 0) {
            empty.setVisibility(View.INVISIBLE);
        }
        adapter.swapCursor(model);
    }

    @Override
    protected void onDestroy(){
        helper.close();
        super.onDestroy();
    }

    @Override

    protected void onStart(){
        invalidateOptionsMenu();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       getMenuInflater().inflate(R.menu.option, menu);
       return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.add){
            // start activity
            Intent intent;
            intent = new Intent(RestaurantList.this, DetailForm.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.about){
            Intent intent;
            intent = new Intent(RestaurantList.this, About.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            model.moveToPosition(position);
            String recordID = helper.getID(model);
            Intent intent;
            intent = new Intent(RestaurantList.this, DetailForm.class);
            intent.putExtra("ID", recordID);
            startActivity(intent);
        }
    };

    static class RestaurantHolder {
        private TextView restName = null;
        private TextView addr = null;
        private ImageView icon = null;
        private String addrandnumber = null;

        RestaurantHolder(View row) {
            restName = row.findViewById(R.id.restName);
            addr = row.findViewById(R.id.restAddr); //row outputs
            icon = row.findViewById(R.id.icon);
        }

        void populateFrom(Cursor c, RestaurantHelper helper) {
            restName.setText(helper.getRestaurantName(c));
            addrandnumber = (helper.getRestaurantAddress(c) + ", " + helper.getRestaurantTel(c));
            addr.setText(addrandnumber);
            if (helper.getRestaurantType(c).equals("Chinese")) {
                icon.setImageResource(R.drawable.ball_red);
            } else if (helper.getRestaurantType(c).equals("Western")) {
                icon.setImageResource(R.drawable.ball_yellow);
            } else {
                icon.setImageResource(R.drawable.ball_green);
            }
        }
    }

    class RestaurantAdapter extends CursorAdapter {
        RestaurantAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            RestaurantHolder holder = (RestaurantHolder) view.getTag();
            holder.populateFrom(cursor, helper);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            RestaurantHolder holder = new RestaurantHolder(row);
            row.setTag(holder);
            return (row);
        }
    }


}
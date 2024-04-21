package com.example.prepcheck;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.prepcheck.Constants.MyConstants;
import com.example.prepcheck.Data.AppData;
import com.example.prepcheck.DataBase.RoomDB;
import com.example.prepcheck.Models.Items;
import com.example.prepcheck.adapter.CheckListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckList extends AppCompatActivity {

    RecyclerView recyclerView;
    CheckListAdapter checkListAdapter;
    RoomDB database;
    List<Items> itemsList = new ArrayList<>();
    String header, show;
    EditText txtAdd; 
    Button btnAdd;
    LinearLayout linearLayout;


    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);

        if(MyConstants.MY_SELECTIONS.equals(header)){
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
        }else if(MyConstants.MY_LIST_CAMEL_CASE.equals(header)){
            menu.getItem(1).setVisible(false);
        }


        MenuItem menuItem = menu.findItem(R.id.btnSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Items> mFinalList = new ArrayList<>();
                for(Items items: itemsList){
                    if(items.getItemname().toLowerCase().startsWith(newText.toLowerCase())){
                        mFinalList.add(items);
                    }
                }
                updateRecycler(mFinalList);
                return false;
            }
        });

        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, CheckList.class);
        AppData appData = new AppData(database,this);

        if (item.getItemId() == R.id.my_selections) {
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_SELECTIONS);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.FALSE_STRING);
            startActivityForResult(intent, 101);
            return true;
        }else if(item.getItemId() == R.id.custom_list){
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_LIST_CAMEL_CASE);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.TRUE_STRING);
            startActivity(intent);
            return true;
        }else if(item.getItemId() == R.id.delete_default){
            new AlertDialog.Builder(this)
                    .setTitle("Delete default data")
                    .setMessage("Are you sure?\n\nAs this will delete the data provided by (PrepCheck) while installing.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            appData.persistDataByCategory(header,true);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(R.drawable.ic_alert)
                    .show();

            return true;

        }else if(item.getItemId() == R.id.reset_default){
            new AlertDialog.Builder(this)
                    .setTitle("Reset to default")
                    .setMessage("Are you sure?\n\nAs this will load the data provided by (PrepCheck) "+
                            "and will delete the custom data you have added in ("+header+")")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            appData.persistDataByCategory(header, false);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(R.drawable.ic_alert)
                    .show();
        }else if(item.getItemId() == R.id.about_developer){
            intent = new Intent(this, AboutUs.class);
            startActivity(intent);
            return true;
        }else if(item.getItemId() == R.id.reminder){
            intent = new Intent(this, ReminderActivity.class);
            startActivity(intent);
            return true;
        } else if(item.getItemId() == R.id.exit){
            this.finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101){
            itemsList = database.mainDao().getAll(header);
            updateRecycler(itemsList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        header = intent.getStringExtra(MyConstants.HEADER_SMALL);
        show = intent.getStringExtra(MyConstants.SHOW_SMALL);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(header);
        }


        txtAdd = findViewById(R.id.addItemEditTxt);
        btnAdd = findViewById(R.id.addBtn);

        recyclerView = findViewById(R.id.recycler_view2);
        linearLayout = findViewById(R.id.linear_layout2);

        database = RoomDB.getInstance(this);

        if(MyConstants.FALSE_STRING.equals(show)){
            linearLayout.setVisibility(View.GONE);
            itemsList = database.mainDao().getAllSelected(true);
        }else{
            itemsList = database.mainDao().getAll(header);
        }

        updateRecycler(itemsList);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = txtAdd.getText().toString();
                if(itemName != null && !itemName.isEmpty()){
                    addNewItem(itemName);
                    Toast.makeText(CheckList.this, "Item Added", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CheckList.this, "Can't add empty value", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setStatusBarColor(getResources().getColor(R.color.status_bar_color));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    // Method to change status bar color
    private void setStatusBarColor(@ColorInt int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    private void addNewItem(String itemName){
        Items item = new Items();
        item.setChecked(false);
        item.setCategory(header);
        item.setItemname(itemName);
        item.setAddedby(MyConstants.USER_SMALL);
        database.mainDao().saveItem(item);
        itemsList = database.mainDao().getAll(header);
        updateRecycler(itemsList);
        recyclerView.scrollToPosition(checkListAdapter.getItemCount()-1);
        txtAdd.setText("");

    }

    private void updateRecycler(List<Items> itemsList){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        checkListAdapter = new CheckListAdapter(CheckList.this, itemsList,database,show);
        recyclerView.setAdapter(checkListAdapter);
    }


}
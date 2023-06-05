package com.ict.librarybooking;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ict.adapter.RentAdapter;
import com.ict.classes.RentClass;
import com.ict.classes.Set_font;
import com.ict.dialogs.AddRentDialog;
import com.ict.dialogs.MenuDialog;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RentListActivity extends AppCompatActivity {
    ArrayList<RentClass> rents = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_list_activity);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();;
        Set_font.setKodakFont(RentListActivity.this, godfatherView);
        RecyclerView recycler = findViewById(R.id.rentRecycler);
        recycler.setHasFixedSize(true);

        final RentAdapter adapter = new RentAdapter(rents, RentListActivity.this);
        adapter.setOnItemClickListener((row, view) -> {
            (new MenuDialog()).ShowMenuDialog(RentListActivity.this, new String[]{"ویرایش", "برگشت داده شد", "حذف"}, row1 -> {
                switch (row1)
                {
                    case 1:
                        (new AddRentDialog()).ShowRentDialog(RentListActivity.this, mm -> {
                            rents.set(row , mm);
                        },rents.get(row));
                        break;
                    case 2:
                        long effected = (new DBhelper(RentListActivity.this)).RentReturned(rents.get(row));
                        rents.remove(row);
                        adapter.notifyItemRemoved(row);
                        Log.d("test",effected+"");
                        break;
                    case 3:
                       if( (new DBhelper(RentListActivity.this)).DeleteRent(rents.get(row).id) > 0)
                       {
                           rents.remove(row);
                           adapter.notifyItemRemoved(row);
                       }
                        break;
                }
            });
        });

        findViewById(R.id.addRent).setOnClickListener(view ->
                (new AddRentDialog()).ShowRentDialog(RentListActivity.this, mm -> {
                    rents.add(0,mm);
                    adapter.notifyDataSetChanged();
                }));
        // Attach the adapter to the recyclerview to populate items
        recycler.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
// Attach layout manager to the RecyclerView
        recycler.setLayoutManager(layoutManager);
        rents.clear();
        rents.addAll((new DBhelper(RentListActivity.this)).GetRents(""));
        adapter.notifyDataSetChanged();

        SearchView search = findViewById(R.id.tvSearchRent);
        search.setOnSearchClickListener(view -> {
            findViewById(R.id.tvTitle).setVisibility(View.GONE);
        });
        search.setOnCloseListener(() -> {
            findViewById(R.id.tvTitle).setVisibility(View.VISIBLE);
            return false;
        });
        ImageView icon = search.findViewById(androidx.appcompat.R.id.search_button);
        icon.setColorFilter(Color.WHITE);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rents.clear();
                rents.addAll((new DBhelper(RentListActivity.this)).GetRents(query));
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rents.clear();
                rents.addAll((new DBhelper(RentListActivity.this)).GetRents(newText));
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        findViewById(R.id.tvBtRent).setOnClickListener(view -> {
            view.setBackgroundColor(getColor(R.color.colorPrimary));
            findViewById(R.id.tvBtRetuned).setBackgroundColor(getColor(R.color.colorPrimaryDark));

            rents.clear();
            rents.addAll((new DBhelper(RentListActivity.this)).GetRents(""));
            adapter.notifyDataSetChanged();
        });

        findViewById(R.id.tvBtRetuned).setOnClickListener(view -> {
            view.setBackgroundColor(getColor(R.color.colorPrimary));
            findViewById(R.id.tvBtRent).setBackgroundColor(getColor(R.color.colorPrimaryDark));

            rents.clear();
            rents.addAll((new DBhelper(RentListActivity.this)).GetRents("",1));
            adapter.notifyDataSetChanged();
        });
    }
}

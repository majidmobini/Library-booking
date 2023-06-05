package com.ict.librarybooking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ict.adapter.MemberAdapter;
import com.ict.classes.MemberClass;
import com.ict.classes.Set_font;
import com.ict.dialogs.AddUserDialog;
import com.ict.dialogs.MenuDialog;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MemberListActivity extends AppCompatActivity {
    ArrayList<MemberClass> members = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list_activity);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();;
        Set_font.setKodakFont(MemberListActivity.this, godfatherView);
        TextView tvTitle = findViewById(R.id.tvTitle);
        RecyclerView recycler = findViewById(R.id.memberRecycler);
        recycler.setHasFixedSize(true);

        final MemberAdapter adapter = new MemberAdapter(members,MemberListActivity.this);
        adapter.setOnItemClickListener((view, position) -> {
            (new MenuDialog()).ShowMenuDialog(MemberListActivity.this, new String[]{"ویرایش",  "حذف"}, row -> {
                switch (row)
                {
                    case 1:
                        (new AddUserDialog()).ShowMemberDialog(MemberListActivity.this,mm -> {
                            members.set(position,mm);
                            adapter.notifyDataSetChanged();
                        },members.get(position));
                        break;
                    case 2:
                        if((new DBhelper(MemberListActivity.this)).DeleteMember(members.get(position).id) > 0)
                        {
                            members.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                        break;
                }
            });
        });
        findViewById(R.id.addMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new AddUserDialog()).ShowMemberDialog(MemberListActivity.this, new AddUserDialog.OnMemberAdded() {
                    @Override
                    public void onMemberAdded(MemberClass mm) {
                        members.add(0,mm);
                        tvTitle.setText(getString(R.string.memeberList)+" ("+(members.size())+")");

                        adapter.notifyDataSetChanged();
                    }
                });
        }});
        // Attach the adapter to the recyclerview to populate items
        recycler.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
// Attach layout manager to the RecyclerView
        recycler.setLayoutManager(layoutManager);
        members.clear();
        members.addAll((new DBhelper(MemberListActivity.this)).GetMembers(""));
        adapter.notifyDataSetChanged();

        tvTitle.setText(getString(R.string.memeberList)+" ("+(members.size())+")");


        SearchView search = findViewById(R.id.tvSearchMember);
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
                members.clear();
                members.addAll((new DBhelper(MemberListActivity.this)).GetMembers(query));
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                members.clear();
                members.addAll((new DBhelper(MemberListActivity.this)).GetMembers(newText));
                adapter.notifyDataSetChanged();
                return false;
            }
        });

    }
}

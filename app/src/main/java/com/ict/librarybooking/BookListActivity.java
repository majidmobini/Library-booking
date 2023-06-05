package com.ict.librarybooking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ict.adapter.BookAdapter;
import com.ict.classes.BookClass;
import com.ict.classes.Set_font;
import com.ict.dialogs.AddBookDialog;
import com.ict.dialogs.MenuDialog;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookListActivity extends AppCompatActivity {
    ArrayList<BookClass> books = new ArrayList<>();
    ArrayList<BookClass> allBooks = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list_activity);
        TextView tvTitle = findViewById(R.id.tvTitle);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();;
        Set_font.setKodakFont(BookListActivity.this, godfatherView);
//https://guides.codepath.com/android/using-the-recyclerview
        RecyclerView recycler = findViewById(R.id.bookRecycler);
        recycler.setHasFixedSize(true);

        final BookAdapter adapter = new BookAdapter(books,BookListActivity.this);
        adapter.setOnItemClickListener((itemView, position) -> (new MenuDialog()).ShowMenuDialog(BookListActivity.this, new String[]{"ویرایش", "وضعیت", "حذف"}, row -> {
            switch (row)
            {
                case 1:

                    (new AddBookDialog()).ShowAddBook(BookListActivity.this, bk -> {
                        books.set(position,bk);
                        adapter.notifyItemChanged(position);
                        tvTitle.setText(getString(R.string.bookList)+" ("+(new DBhelper(BookListActivity.this).GetBookNumbers())+")");
                    },books.get(position));

                    break;
                case 2:
                    int number = (new DBhelper(BookListActivity.this)).GetBookRented(books.get(position).id+"");
                    Toast t =Toast.makeText(BookListActivity.this, (books.get(position).count - number)+" کتاب موجود است ", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER,0,0);
                    t.show();
                    break;
                case 3:
                    if ((new DBhelper(BookListActivity.this)).DeleteBook(books.get(position).id) > 0)
                    {
                        books.remove(position);
                        adapter.notifyItemRemoved(position);
                        tvTitle.setText(getString(R.string.bookList)+" ("+(new DBhelper(BookListActivity.this).GetBookNumbers())+")");
                    }

            }
        }));
        findViewById(R.id.addBook).setOnClickListener(view ->

                (new AddBookDialog()).ShowAddBook(BookListActivity.this, bk -> {
            books.add(0,bk);
            adapter.notifyDataSetChanged();
        })

        );

        // Attach the adapter to the recyclerview to populate items
        recycler.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
// Attach layout manager to the RecyclerView
        recycler.setLayoutManager(layoutManager);
       books.clear();

       books.addAll((new DBhelper(BookListActivity.this)).GetBooks(""));
       adapter.notifyDataSetChanged();
        tvTitle.setText(getString(R.string.bookList)+" ("+(new DBhelper(BookListActivity.this).GetBookNumbers())+")");

        SearchView search = findViewById(R.id.tvSearchBook);
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
                books.clear();
                books.addAll((new DBhelper(BookListActivity.this)).GetBooks(query));
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                books.clear();
                books.addAll((new DBhelper(BookListActivity.this)).GetBooks(newText));
                adapter.notifyDataSetChanged();
                return false;
            }
        });

    }
}

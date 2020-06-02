package com.example.ezbook;



import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;



import android.os.Bundle;

import android.widget.ArrayAdapter;

import android.widget.ListView;



import java.util.ArrayList;



public class LibraryActivity extends AppCompatActivity {



    SearchView searchView;

    ListView libraryBookList;



    ArrayList<String> list;

    ArrayAdapter<String> adapter;



    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_library);



        searchView = (SearchView)findViewById(R.id.search_view);

        libraryBookList = (ListView) findViewById(R.id.library_book_list);



        list=new ArrayList<String>();

        list.add("bookA");

        list.add("bookB");

        list.add("bookC");

        list.add("bookD");

        list.add("bookE");



        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);

        libraryBookList.setAdapter(adapter);





        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override

            public boolean onQueryTextSubmit(String s) {

                return false;

            }



            @Override

            public boolean onQueryTextChange(String s) {

                adapter.getFilter().filter(s);



                return false;

            }

        });

    }

}
package com.example.ezbook;

import android.widget.Filter;

import com.example.ezbook.Adapters.AdapterProductAdmin;
import com.example.ezbook.Models.ModelProduct;

import java.util.ArrayList;

public class FilterProduct extends Filter {

    private AdapterProductAdmin adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProduct(AdapterProductAdmin adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //validate data for search query
        if(constraint != null && constraint.length()>0){
            //search filed not empty,searching something,perform search


            //change to upper case to make case insensitvie
            constraint = constraint.toString().toUpperCase();
            //store our fileter list
            ArrayList<ModelProduct> filterModels = new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                //check, search by title and category
                if(filterList.get(i).getProductTitle().toUpperCase().contains(constraint)||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    //add filtered data to list
                    filterModels.add(filterList.get(i));
                }
            }
            results.count=filterModels.size();
            results.values=filterModels;
        }
        else {
            //search filed empty,not searching
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.productList=(ArrayList<ModelProduct>)results.values;

        adapter.notifyDataSetChanged();
    }
}

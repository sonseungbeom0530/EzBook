package com.example.ezbook;

import android.widget.Filter;

import com.example.ezbook.Adapters.AdapterOrderShop;
import com.example.ezbook.Adapters.AdapterProductAdmin;
import com.example.ezbook.Models.ModelOrderShop;
import com.example.ezbook.Models.ModelProduct;

import java.util.ArrayList;

public class FilterOrderShop extends Filter {

    private AdapterOrderShop adapter;
    private ArrayList<ModelOrderShop> filterList;

    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> filterList) {
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
            ArrayList<ModelOrderShop> filterModels = new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                //check, search by title and category
                if(filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
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

        adapter.orderShopArrayList=(ArrayList<ModelOrderShop>)results.values;

        adapter.notifyDataSetChanged();
    }
}

package com.example.knowyourgovernment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CivilGovernmentAdapter extends RecyclerView.Adapter<CivilGovernmentViewHolder> {
    MainActivity mainActivity;
    Context context;
    List<CivilGovernmentOfficial> civilGovermentOfficialList;

    public CivilGovernmentAdapter(MainActivity mainActivity, List<CivilGovernmentOfficial> civilGovermentOfficialList) {
        this.mainActivity =  mainActivity;
        this.civilGovermentOfficialList = civilGovermentOfficialList;
    }

    @Override
    public CivilGovernmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_gov_list,
                parent, false);
        itemsView.setOnClickListener(mainActivity);
        return new CivilGovernmentViewHolder(itemsView);
    }

    @Override
    public void onBindViewHolder(CivilGovernmentViewHolder holder, int position) {
        CivilGovernmentOfficial civilGovermentOfficial = civilGovermentOfficialList.get(position);
        holder.cilvilOfficeNameText.setText(civilGovermentOfficial.getOfficeName());
        holder.civilOfficialNameText.setText(civilGovermentOfficial.getCivilOfficial().getOfficialName()
                + "(" + civilGovermentOfficial.getCivilOfficial().getOfficialParty() + ")" );
    }

    @Override
    public int getItemCount() { return civilGovermentOfficialList.size(); }

}

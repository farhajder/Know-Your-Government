package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CivilGovernmentViewHolder extends RecyclerView.ViewHolder {
    public TextView cilvilOfficeNameText;
    public TextView civilOfficialNameText;

    public CivilGovernmentViewHolder(View itemView) {
        super(itemView);
        cilvilOfficeNameText = itemView.findViewById(R.id.ofcNameText);
        civilOfficialNameText = itemView.findViewById(R.id.officialNameText);
    }
}

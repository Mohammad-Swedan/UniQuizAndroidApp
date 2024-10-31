// MaterialsAdapter.java
package com.example.myapplicationuq.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.R;
import com.example.myapplicationuq.Responses.MaterialResponse;
import com.example.myapplicationuq.TestActivity;

import java.util.List;
// MaterialsAdapter.java
public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder> {

    private List<MaterialResponse> materialList;

    public MaterialsAdapter(List<MaterialResponse> materialList) {
        this.materialList = materialList;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new MaterialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        MaterialResponse material = materialList.get(position);
        holder.textViewMaterialName.setText(material.getMaterialName());
        holder.textViewFacultyID.setText("Faculty ID: " + material.getFacultyID());

        // Set click listener on the itemView
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TestActivity.class);
            intent.putExtra("material_name", material.getMaterialName());
            intent.putExtra("material_id", material.getMaterialID()); // Pass Material ID
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public static class MaterialViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMaterialName, textViewFacultyID;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMaterialName = itemView.findViewById(R.id.textViewMaterialName);
            textViewFacultyID = itemView.findViewById(R.id.textViewFacultyID);
        }
    }

    // Optional: Method to update the list
    public void updateMaterials(List<MaterialResponse> materials) {
        this.materialList = materials;
        notifyDataSetChanged();
    }
}

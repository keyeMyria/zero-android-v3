package com.ekylibre.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekylibre.android.R;
import com.ekylibre.android.SelectEquipmentFragment;
import com.ekylibre.android.database.models.Equipment;
import com.ekylibre.android.database.pojos.Equipments;
import com.ekylibre.android.database.relations.InterventionEquipment;
import com.ekylibre.android.utils.Enums;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public class SelectEquipmentAdapter extends RecyclerView.Adapter<SelectEquipmentAdapter.ViewHolder> {

    private static final String TAG = "SelectEquipmentAdapter";

    private List<Equipment> dataset;
    private Context context;

    private SelectEquipmentFragment.OnFragmentInteractionListener fragmentListener;


    public SelectEquipmentAdapter(Context context, List<Equipment> dataset, SelectEquipmentFragment.OnFragmentInteractionListener fragmentListener) {
        this.dataset = dataset;
        this.context = context;
        this.fragmentListener = fragmentListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, typeTextView;
        AppCompatImageView typeImageView;
        Equipment equipment;

        ViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.equipment_name);
            typeTextView = itemView.findViewById(R.id.equipment_type);
            typeImageView = itemView.findViewById(R.id.equipment_icon);

            itemView.setOnClickListener(v -> {
                Equipments selection = new Equipments();
                selection.equipment = Collections.singletonList(equipment);
                selection.inter = new InterventionEquipment(equipment.id);
                fragmentListener.onFragmentInteraction(selection);
            });
        }

        void display(Equipment item) {
            equipment = item;
            nameTextView.setText(item.name);
            Timber.e("eq %s", item.type);
            typeTextView.setText(item.type != null ? Enums.EQUIMPMENT_NAMES.get(Enums.EQUIMPMENT_TYPES.indexOf(item.type)) : "Indéfini");
            typeImageView.setImageResource(context.getResources().getIdentifier("tool_" + item.type.toLowerCase(), "drawable", context.getPackageName()));

        }
    }

    @NonNull
    @Override
    public SelectEquipmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_equipment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.display(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

}
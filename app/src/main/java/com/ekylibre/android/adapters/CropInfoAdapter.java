package com.ekylibre.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekylibre.android.MainActivity;
import com.ekylibre.android.R;
import com.ekylibre.android.adapters.CropInfo.CropItem;
import com.ekylibre.android.adapters.CropInfo.ListItem;
import com.ekylibre.android.adapters.CropInfo.ProductionItem;
import com.ekylibre.android.database.models.Intervention;
import com.ekylibre.android.utils.RecyclerViewClickListener;

import java.util.List;


public class CropInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ListItem> dataset;
    private RecyclerViewClickListener listener;

    public CropInfoAdapter(Context context, List<ListItem> dataset, RecyclerViewClickListener listener) {
        this.context = context;
        this.dataset = dataset;
        this.listener = listener;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView headerTextView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.crop_info_header);
        }
    }

    class CropViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTextView, areaTextView;
        LinearLayoutCompat interContainer;
        CropItem currentItem;
        RecyclerViewClickListener itemListener;

        CropViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            itemListener = listener;

            nameTextView = itemView.findViewById(R.id.crop_info_name);
            areaTextView = itemView.findViewById(R.id.crop_info_area);
            interContainer = itemView.findViewById(R.id.crop_info_inter_container);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            itemListener.onClick(view, getAdapterPosition());
        }

        void display() {

            String name = currentItem.getName().split(" \\| ")[0];
            nameTextView.setText(name);
            areaTextView.setText(String.format(MainActivity.LOCALE, "%.1f ha", currentItem.getSurface()));

            interContainer.removeAllViews();

            if (currentItem.getInterventions().size() > 0) {
                interContainer.setVisibility(View.VISIBLE);
                for (Intervention intervention : currentItem.getInterventions()) {
                    int ressourceId = context.getResources().getIdentifier("procedure_" + intervention.type.toLowerCase(), "drawable", context.getPackageName());

                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(ressourceId);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(50, 0, 0, 0);
                    imageView.setLayoutParams(lp);

//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(imageView.getLayoutParams());
//                marginParams.setMarginStart(15);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(marginParams);
//                imageView.setLayoutParams(lp);

                    interContainer.addView(imageView);
                }
            }
        }
    }

//
//    class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView plotArea;
//        CheckBox plotCheckBox;
//        ImageView plotArrow;
//        LinearLayoutCompat cropContainer;
//        LayoutInflater inflater;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//
//            plotArea = itemView.findViewById(R.id.plot_area);
//            plotCheckBox = itemView.findViewById(R.id.plot_checkbox);
//            plotArrow = itemView.findViewById(R.id.plot_arrow);
//            cropContainer = itemView.findViewById(R.id.crop_container);
//
//            inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        }
//
//        void display(Plots item) {
//
//            plotCheckBox.setText(item.plot.name);
//            plotCheckBox.setChecked(item.plot.is_checked);
//            plotArea.setText(String.format(MainActivity.LOCALE, "%.1f ha", item.plot.surface_area));
//
//            // Display all crops associated to a plot
//            displayCrops(item);
//
//        }
//
//        void displayCrops(Plots item) {
//
//            // First remove all childs view
//            cropContainer.removeAllViews();
//
//            int index = 0;
//            for (Crop crop : item.crops) {
//
//                // Inflate crom item layout
//                View child = inflater.inflate(R.layout.item_crop, null);
//
//                // Set bottom border if not latest element
//                if (++index < item.crops.size())
//                    child.setBackgroundResource(R.drawable.border_bottom_lightgrey);
//
//                ConstraintLayout cropLayout = child.findViewById(R.id.item_crop_layout);
//                CheckBox cropCheckBox = child.findViewById(R.id.crop_checkbox);
//                TextView cropName = child.findViewById(R.id.crop_name);
//                TextView cropArea = child.findViewById(R.id.crop_area);
//                cropName.setText(crop.name);
//                cropArea.setText(String.format(MainActivity.LOCALE, "%.1f ha travaillés", crop.surface_area));
//                cropCheckBox.setChecked(crop.is_checked);
//
//                cropCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    // Save action (checked/unchecked) to dataset item crop and update total
//                    crop.is_checked = isChecked;
//                    int cropSelected = 0;
//                    for (Crop mCrop : item.crops) {
//                        if (mCrop.is_checked)
//                            cropSelected++;
//                    }
//                    if (cropSelected > 0) {
//                        plotCheckBox.setChecked(true);
//                        item.plot.is_checked = true;
//                    }
//                    else {
//                        plotCheckBox.setChecked(false);
//                        item.plot.is_checked = false;
//                    }
////                    updateTotal();
//                });
//
//                cropLayout.setOnClickListener(view -> {
//                    if (crop.is_checked) {
//                        cropCheckBox.setChecked(false);
//                    } else {
//                        cropCheckBox.setChecked(true);
//                    }
//                });
//
//                // Add rendered view to container layout
//                cropContainer.addView(child);
//            }
//        }

//        void updateTotal() {
//
//            float total = 0;
//            int count = 0;
//
//            for (Plots plot : dataset)
//                for (Crop crop : plot.crops)
//                    if (crop.is_checked) {
//                        total += crop.surface_area;
//                        ++count;
//                    }
//
//            String totalString;
//            String cropCount;
//            if (total > 0) {
//                cropCount = context.getResources().getQuantityString(R.plurals.crops, count, count);
//                totalString = String.format(MainActivity.LOCALE, "%s • %.1f ha", cropCount, total);
//            }
//            else {
//                totalString = context.getString(R.string.no_crop_selected);
//            }
//            SelectCropFragment.totalTextView.setText(totalString);
//            InterventionActivity.cropSummaryText = totalString;
//            InterventionActivity.surface = total;
//
//        }
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ListItem.TYPE_HEADER) {
            View itemView = inflater.inflate(R.layout.item_crop_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_crop_info, parent, false);
            return new CropViewHolder(itemView, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);

        if (type == ListItem.TYPE_HEADER) {
            ProductionItem headerItem = (ProductionItem) dataset.get(position);
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.headerTextView.setText(headerItem.getName());
        } else {
            CropItem cropItem = (CropItem) dataset.get(position);
            CropViewHolder cropViewHolder = (CropViewHolder) holder;
            cropViewHolder.currentItem = cropItem;
            cropViewHolder.display();
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position).getType();
    }

}
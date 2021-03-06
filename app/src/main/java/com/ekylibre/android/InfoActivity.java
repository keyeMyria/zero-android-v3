package com.ekylibre.android;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ekylibre.android.adapters.CropInfo.CropItem;
import com.ekylibre.android.adapters.CropInfo.ListItem;
import com.ekylibre.android.adapters.CropInfo.ProductionItem;
import com.ekylibre.android.adapters.CropInfoAdapter;
import com.ekylibre.android.database.AppDatabase;
import com.ekylibre.android.database.models.Crop;
import com.ekylibre.android.database.models.Intervention;
import com.ekylibre.android.database.pojos.Crops;
import com.ekylibre.android.database.pojos.SimpleInterventions;
import com.ekylibre.android.utils.Converters;
import com.ekylibre.android.utils.RecyclerViewClickListener;
import com.ekylibre.android.utils.SimpleDividerItemDecoration;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class InfoActivity extends AppCompatActivity
        implements InfoFragment.OnFragmentInteractionListener{

    private HashMap<String, Multimap> map;
    private List<ListItem> dataset;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("Mes cultures");

        map = new HashMap<>();
        dataset = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.crop_info_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        RecyclerViewClickListener listener = (view, position) -> {

            CropItem cropItem = (CropItem) dataset.get(position);

            Bundle args = new Bundle();
            args.putString("full_name", cropItem.getName());
            args.putString("start_date", Converters.toString(cropItem.getStartDate()));
            args.putString("stop_date", Converters.toString(cropItem.getStopDate()));
            args.putString("yield", cropItem.getYield());
            args.putFloat("surface", cropItem.getSurface());
            args.putString("uuid", cropItem.getUUID());

            ArrayList<Integer> interIDs = new ArrayList<>();
            for (Intervention inter : cropItem.getInterventions())
                interIDs.add(inter.id);
            args.putIntegerArrayList("interventionsIDs", interIDs);
            Timber.i("InterIDs --> %s", interIDs);

            InfoFragment infoFragment = InfoFragment.newInstance(args);
            infoFragment.show(getFragmentTransaction(), "dialog");
        };

        adapter = new CropInfoAdapter(this, dataset, listener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new RequestCropList(this).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class RequestCropList extends AsyncTask<Void, Void, List<SimpleInterventions>> {

        Context context;
        String farm;

        RequestCropList(final Context context) {
            this.context = context;
            this.farm = MainActivity.FARM_ID;
        }

        protected List<SimpleInterventions> doInBackground(Void... voids) {
            AppDatabase database = AppDatabase.getInstance(context);
            return database.dao().getSimpleInterventionList(farm);
        }

        protected void onPostExecute(List<SimpleInterventions> result) {

            dataset.clear();

            for (SimpleInterventions item : result) {
                for (Crops inter : item.crops) {

                    // Get production nature from crop
                    StringBuilder sb = new StringBuilder();
                    sb.append(inter.crop.get(0).production_nature);
                    if (inter.crop.get(0).production_mode.equals("Agriculture biologique"))
                        sb.append(" bio");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(inter.crop.get(0).stop_date);
                    sb.append(" ").append(cal.get(Calendar.YEAR));

                    if (map.containsKey(sb.toString())) {
                        Multimap<Crop, Intervention>  multimap = map.get(sb.toString());
                        multimap.put(inter.crop.get(0), item.intervention);
                    } else {
                        Multimap<Crop, Intervention> multimap = ArrayListMultimap.create();
                        multimap.put(inter.crop.get(0), item.intervention);
                        map.put(sb.toString(), multimap);
                    }
                }
            }

            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                ProductionItem header = new ProductionItem();
                header.setName(pair.getKey().toString());
                dataset.add(header);

                Multimap<Crop, Intervention> cropsInProd = (Multimap) pair.getValue();
                for (Crop crop : cropsInProd.keySet()) {
                    CropItem cropItem = new CropItem();
                    cropItem.setName(crop.name);
                    cropItem.setSurface(crop.surface_area);
                    cropItem.setStartDate(crop.start_date);
                    cropItem.setStopDate(crop.stop_date);
                    cropItem.setYield(crop.provisional_yield);
                    cropItem.setInterventions((List) cropsInProd.get(crop));
                    cropItem.setUUID(crop.uuid);
                    dataset.add(cropItem);
                }
                it.remove();
            }

            Timber.i("Dataset %s", dataset);

            adapter.notifyDataSetChanged();

            it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Timber.i(pair.getKey() + " = " + pair.getValue());
                it.remove();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crop_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_by_production:
                return true;

            case R.id.filter_by_proximity:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentInteraction(Object selection) {

    }

    public FragmentTransaction getFragmentTransaction() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);
        return ft;
    }
}

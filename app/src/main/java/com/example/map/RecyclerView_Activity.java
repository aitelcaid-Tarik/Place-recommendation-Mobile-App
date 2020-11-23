package com.example.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RecyclerView_Activity extends Activity {

    DB_sqlite db = new DB_sqlite(this);

    RecyclerView recyclerView;
    CoordinatesAdapter coordinatesAdapter;
    ArrayList<Coordinates> myData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recyclerview);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(animationController);

        myData = db.getAll();

        Collections.sort(myData, new Comparator<Coordinates>() {
            @Override
            public int compare(Coordinates c1, Coordinates c2) {

                float rating1 = (c1.getRating() == null || c1.getRating().equals("") ? 0.0f : Float.parseFloat(c1.getRating()));
                float rating2 = (c2.getRating() == null || c2.getRating().equals("") ? 0.0f : Float.parseFloat(c2.getRating()));

                float res = rating2 - rating1;

                if (res > 0) return 1;

                else if (res < 0) return -1;

                return 0;

            }
        });

        coordinatesAdapter = new CoordinatesAdapter(this, myData);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(coordinatesAdapter);


    }


    public class CoordinatesAdapter extends RecyclerView.Adapter<CoordinatesAdapter.ViewHolder> {

        Context context;
        ArrayList<Coordinates> Data;


        public CoordinatesAdapter(Context c, ArrayList<Coordinates> d) {
            this.context = c;
            this.Data = d;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {


            public CardView cardView;
            TextView name, description;
            ImageView img;
            RatingBar ratingBar;
            Button back, showAll;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                description = (TextView) itemView.findViewById(R.id.descr);
                ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar2);
                img = (ImageView) itemView.findViewById(R.id.img);
                back = (Button) itemView.findViewById(R.id.back);
                showAll = (Button) itemView.findViewById(R.id.showAll);
                cardView = itemView.findViewById(R.id.itemWraper);

            }

        }


        public int getItemViewType(int position) {
            return (position == Data.size()) ? R.layout.footer : R.layout.single_row;
        }


        @Override
        public CoordinatesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView;

            if (viewType == R.layout.single_row) {
                itemView = LayoutInflater.from(context).inflate(R.layout.single_row, parent, false);
            } else {

                itemView = LayoutInflater.from(context).inflate(R.layout.footer, parent, false);
            }

            return new ViewHolder(itemView);

        }


        @Override
        public void onBindViewHolder(final CoordinatesAdapter.ViewHolder holder, final int position) {


            if (position == Data.size()) {

                holder.back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                holder.showAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("showAll", true);
                        startActivity(intent);
                        finish();
                    }
                });

            } else {

                final float rating= (Data.get(position).getRating() == null || Data.get(position).getRating().equals("") ? 0.0f : Float.parseFloat(Data.get(position).getRating()));


                holder.name.setText("Name            : " + Data.get(position).getName());
                holder.description.setText("Descritption : " + Data.get(position).getDescription());
                holder.ratingBar.setRating(rating);
                holder.img.setImageResource(R.drawable.place);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                        intent.putExtra("name", Data.get(position).getName());
                        intent.putExtra("description", Data.get(position).getDescription());
                        intent.putExtra("latitude", Double.parseDouble(Data.get(position).getLatitude()));
                        intent.putExtra("longitude", Double.parseDouble(Data.get(position).getLongitude()));
                        intent.putExtra("rating", rating);


                        startActivity(intent);
                        finish();

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return Data.size() + 1;
        }

    }

}





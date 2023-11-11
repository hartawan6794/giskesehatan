package com.example.giskesehatan.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Activitys.DetailLayananKesehatanActivity;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.GPSTracker;
import com.example.giskesehatan.Models.TempatKesehatanModel;
import com.example.giskesehatan.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TempatKesehatanAdapter extends RecyclerView.Adapter<TempatKesehatanAdapter.TempatKesehatanViewHolder> {

    private Context context;
    private List<TempatKesehatanModel> tempatKesehatanModels;
    private ViewPager2 viewPager2;
    private GPSTracker gpsTracker;
    int VIEW_TYPE;

    public TempatKesehatanAdapter(Context context, List<TempatKesehatanModel> tempatKesehatanModels,ViewPager2 viewPager2 , int VIEW_TYPE) {
        this.context                = context;
        this.tempatKesehatanModels  = tempatKesehatanModels;
        this.viewPager2             = viewPager2;
        this.gpsTracker             = new GPSTracker(context);
        this.VIEW_TYPE              = VIEW_TYPE;
    }

    @NonNull
    @Override
    public TempatKesehatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TempatKesehatanViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_kesehatan_terkini,
                        parent,
                        false
                )
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TempatKesehatanViewHolder holder, int position) {
        holder.bind(tempatKesehatanModels.get(position));
        if(VIEW_TYPE == 2){
            holder.cv_kesehatan.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.cv_kesehatan.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }else{
            if(position == tempatKesehatanModels.size() -2){
                viewPager2.post(runnable);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tempatKesehatanModels.size();
    }

    class TempatKesehatanViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_kesehatan;
        private RoundedImageView rv_img_kesehata;
        private AppCompatTextView tv_judul, tv_deskripsi, tv_jarak;

        public TempatKesehatanViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_img_kesehata     = itemView.findViewById(R.id.img_kesehatan);
            tv_judul            = itemView.findViewById(R.id.tv_nama_kesehatan);
            tv_deskripsi        = itemView.findViewById(R.id.tv_deskripsi);
            tv_jarak            = itemView.findViewById(R.id.tv_jarak);
            cv_kesehatan        = itemView.findViewById(R.id.cv_kesehatan);
        }

        void bind(TempatKesehatanModel tempatKesehatanModels) {
            String text = tempatKesehatanModels.getGambar();
            int pos = text.indexOf("-");
            String result = text.substring(0, pos);
            Glide.with(context)
                    .load(AppConfig.BASE_URL_IMG + result + "/" + tempatKesehatanModels.getGambar())
                    .centerCrop()
                    .placeholder(R.drawable.klinik_example)
                    .into(rv_img_kesehata);
            tv_judul.setText(tempatKesehatanModels.getNama());
            tv_deskripsi.setText(tempatKesehatanModels.getDeskripsi());

            String jarak = calculateDistance(gpsTracker.getLatitude()
                    , gpsTracker.getLongitude()
                    , tempatKesehatanModels.getLatitude()
                    , tempatKesehatanModels.getLongitude());
            tv_jarak.setText("Est. " + jarak + " km dari anda");

            cv_kesehatan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailLayananKesehatanActivity.class);
                    intent.putExtra("model", tempatKesehatanModels);
                    context.startActivity(intent);
                }
            });
        }

    }

    @NonNull
    private String calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        String formattedValue = String.format("%.1f", (results[0] / 1000));
        return formattedValue;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tempatKesehatanModels.addAll(tempatKesehatanModels);
            notifyDataSetChanged();
        }
    };


}

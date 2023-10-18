package com.example.giskesehatan.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.giskesehatan.Helpers.AppConfig;
import com.example.giskesehatan.Helpers.GPSTracker;
import com.example.giskesehatan.Models.TempatKesehatanTerkiniModel;
import com.example.giskesehatan.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class TempatKesehatanAdapter extends RecyclerView.Adapter<TempatKesehatanAdapter.TempatKesehatanViewHolder> {

    private Context context;
    private List<TempatKesehatanTerkiniModel> tempatKesehatanTerkiniModels;
    private ViewPager2 viewPager2;
    private GPSTracker gpsTracker;

    public TempatKesehatanAdapter(Context context, List<TempatKesehatanTerkiniModel> tempatKesehatanTerkiniModels, ViewPager2 viewPager2) {
        this.context = context;
        this.tempatKesehatanTerkiniModels = tempatKesehatanTerkiniModels;
        this.viewPager2 = viewPager2;
        this.gpsTracker = new GPSTracker(context);
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

    @Override
    public void onBindViewHolder(@NonNull TempatKesehatanViewHolder holder, int position) {
        holder.bind(tempatKesehatanTerkiniModels.get(position));
    }

    @Override
    public int getItemCount() {
        return tempatKesehatanTerkiniModels.size();
    }

    class TempatKesehatanViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView rv_img_kesehata;
        private AppCompatTextView tv_judul, tv_deskripsi, tv_jarak;

        public TempatKesehatanViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_img_kesehata = itemView.findViewById(R.id.img_kesehatan);
            tv_judul = itemView.findViewById(R.id.tv_nama_kesehatan);
            tv_deskripsi = itemView.findViewById(R.id.tv_deskripsi);
            tv_jarak = itemView.findViewById(R.id.tv_jarak);
        }

        void bind(TempatKesehatanTerkiniModel tempatKesehatanTerkiniModel) {
            String text = tempatKesehatanTerkiniModel.getGambar();
            int pos = text.indexOf("-");
            String result = text.substring(0, pos);
            Glide.with(context)
                    .load(AppConfig.BASE_URL_IMG + result +"/"+ tempatKesehatanTerkiniModel.getGambar())
                    .centerCrop()
                    .placeholder(R.drawable.klinik_example)
                    .into(rv_img_kesehata);
            tv_judul.setText(tempatKesehatanTerkiniModel.getNmKlinik());
            tv_deskripsi.setText(tempatKesehatanTerkiniModel.getDeskripsi());

            String jarak = calculateDistance(gpsTracker.getLatitude()
                    , gpsTracker.getLongitude()
                    , tempatKesehatanTerkiniModel.getLatitude()
                    , tempatKesehatanTerkiniModel.getLongitude());
            tv_jarak.setText("Est. "+jarak+" km dari anda");
        }

    }

    @NonNull
    private String calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        String formattedValue = String.format("%.1f", (results[0]/1000));
        return formattedValue;
    }
}

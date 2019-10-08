package com.github.piasy.biv.example;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.example.glide.SampleCustomImageSizeModel;
import com.github.piasy.biv.loader.glide.GlideCustomImageLoader;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigImageViewer.initialize(GlideCustomImageLoader.with(getApplicationContext(), SampleCustomImageSizeModel.class));

        setContentView(R.layout.activity_recycler_view);

        configRecycler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BigImageViewer.imageLoader().cancelAll();
    }

    private void configRecycler() {
        final List<String> imageUrlsList = new ArrayList<>();

        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/3/3f/Francisco_de_Goya_y_Lucientes_-_Los_fusilamientos_del_tres_de_mayo_-_1814.jpg");
        imageUrlsList.add(
                "https://mathiasbynens.be/demo/animated-webp-supported.webp");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/9/99/Las_Meninas_01.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/2/2c/Rotating_earth_%28large%29.gif");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/f/f1/El_caballero_de_la_mano_en_el_pecho.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/a/aa/SmallFullColourGIF.gif");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/6/62/The_Garden_of_Earthly_Delights_by_Bosch_High_Resolution_2.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/f/fb/La_Anunciaci%C3%B3n_%28Fra_Angelico-Prado%29.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Carlos_V_en_M%C3%BChlberg%2C_by_Titian%2C_from_Prado_in_Google_Earth.jpg/3000px-Carlos_V_en_M%C3%BChlberg%2C_by_Titian%2C_from_Prado_in_Google_Earth.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/b/bb/Rembrandt_Harmensz._van_Rijn_014.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/b/b2/Peter_Paul_Rubens_-_The_Three_Graces%2C_1635.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/1/16/Raffael_048.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/d/d4/Crucifixi%C3%B3n_Juan_de_Flandes.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/d/da/Albrecht_D%C3%BCrer_103.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/1/1a/Weyden_Deposition.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/b/b6/El_sue%C3%B1o_de_Jacob%2C_por_Jos%C3%A9_de_Ribera.jpg");
        imageUrlsList.add(
                "https://upload.wikimedia.org/wikipedia/commons/8/8b/Giovanni_Battista_Tiepolo_022.jpg");

        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(new RecyclerAdapter(imageUrlsList));

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recycler);
    }
}

package com.dokiwa.dokidoki.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.builder.GalleryBuilder;
import com.veinhorn.scrollgalleryview.builder.GallerySettings;

import java.util.ArrayList;
import java.util.List;

import static ogbe.ozioma.com.glideimageloader.dsl.DSL.image;

public class GalleryActivity extends FragmentActivity {

    private static final String EXTRA_DATA = "extra.data";
    private static final String EXTRA_CURRENT = "extra.current";

    public static void launchGallery(Context context, int currentIndex, List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        Intent intent = new Intent(context, GalleryActivity.class);
        if (currentIndex >= urls.size()) {
            currentIndex = urls.size() - 1;
        } else if (currentIndex < 0) {
            currentIndex = 0;
        }
        intent.putExtra(EXTRA_CURRENT, currentIndex);
        intent.putStringArrayListExtra(EXTRA_DATA, new ArrayList<>(urls));
        context.startActivity(intent);
    }

    private static final String TAG = "GalleryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_DATA);

        GalleryBuilder builder = ScrollGalleryView
                .from((ScrollGalleryView) findViewById(R.id.scroll_gallery_view))
                .settings(GallerySettings.from(getSupportFragmentManager()).thumbnailSize(200).enableZoom(true).build())
                .onImageClickListener(clickListener)
                .onImageLongClickListener(longClickListener)
                .onPageChangeListener(new CustomOnPageListener());

        for (int i = 0; i < urls.size(); i++) {
            builder.add(image(urls.get(i)));
        }

        final ScrollGalleryView galleryView = builder.build();

        galleryView.withHiddenThumbnails(false).hideThumbnailsOnClick(true).hideThumbnailsAfter(5000);

        final int currentIndex = getIntent().getIntExtra(EXTRA_CURRENT, 0);
        galleryView.setCurrentItem(currentIndex);

        // fix thumbnail position
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ViewGroup thumbnailContainer = galleryView.findViewById(R.id.thumbnails_container);
                View thumbnailView = thumbnailContainer.getChildAt(currentIndex);
                if (thumbnailView != null) {
                    thumbnailView.callOnClick();
                }
            }
        });
    }

    private ScrollGalleryView.OnImageClickListener clickListener = new ScrollGalleryView.OnImageClickListener() {
        @Override
        public void onClick(int position) {
            Log.INSTANCE.i(TAG, "lick image position = " + position);
        }
    };

    private ScrollGalleryView.OnImageLongClickListener longClickListener = new ScrollGalleryView.OnImageLongClickListener() {
        @Override
        public void onClick(int position) {
            Log.INSTANCE.i(TAG, "long click image position = " + position);
        }
    };

    private class CustomOnPageListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            Log.INSTANCE.i(TAG, "page selected #" + position);
        }
    }
}
package seproject14.enshare.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import seproject14.enshare.R;
import seproject14.enshare.ui.MainActivity;
import seproject14.enshare.ui.cloud.CloudFragment;
import seproject14.enshare.ui.gallery.GalleryFragment;
import seproject14.enshare.ui.MainActivity;

public class HomeFragment extends Fragment {

    LottieAnimationView lottieAnimationViewGallery;
    LottieAnimationView lottieAnimationViewCloud;
    LottieAnimationView lottieAnimationViewCamera;
    private FloatingActionButton takePhotoButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        MainActivity mainActivity = MainActivity._staticInstance;

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Hide the `Take a new photo'-Button
        takePhotoButton = mainActivity.getTakePhotoButton();
        takePhotoButton.hide();


//        return root;
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }


        lottieAnimationViewGallery = (LottieAnimationView)root.findViewById(R.id.lottie_animationGallery);

        lottieAnimationViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment galleryFragment = new GalleryFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup)(getView().getParent())).getId(), galleryFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
                NavigationView navigationView = mainActivity.getNavigationView();
                navigationView.setCheckedItem(R.id.nav_gallery);

                ActionBar actionBar = MainActivity._staticInstance.getOurActionBar();
                actionBar.setTitle(R.string.menu_gallery);
            }
        });

        lottieAnimationViewCloud = (LottieAnimationView)root.findViewById(R.id.lottie_animationCloud);

        lottieAnimationViewCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment cloudFragment = new CloudFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup)(getView().getParent())).getId(), cloudFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
                NavigationView navigationView = mainActivity.getNavigationView();
                navigationView.setCheckedItem(R.id.nav_cloud);

                ActionBar actionBar = MainActivity._staticInstance.getOurActionBar();
                actionBar.setTitle(R.string.menu_cloud);
            }
        });

        lottieAnimationViewCamera = (LottieAnimationView)root.findViewById(R.id.lottie_animationCamera);

        lottieAnimationViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Fragment someFragment = new GalleryFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.frag_container, someFragment ); // give your fragment container id in first parameter
//                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
//                transaction.commit();
                ((MainActivity)getActivity()).dispatchTakePictureIntent();
            }
        });


        ActionBar actionBar = MainActivity._staticInstance.getOurActionBar();
        actionBar.setTitle(R.string.menu_home);


     //   return inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onDestroyView() {
        // Show the `Take a new photo'-Button again
        takePhotoButton.show();

        super.onDestroyView();
    }
}

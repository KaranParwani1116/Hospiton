package com.example.hospiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Destination extends AppCompatActivity {
    PlacesClient placesClient;
    List<Place.Field> placefields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING);
    AutocompleteSupportFragment places_fragment;
    private String place_id="";
    private ImageView placephoto;
    List<PhotoMetadata>allphotos;
    private Button Find;
    private SliderView sliderView;
    private List<Uri>ImageUri;
    private SliderAdapterExample adapter;
    private String TAG=Destination.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        ImageUri=new ArrayList<>();
        requestpermission();
        setupPlacesAutocomplete();

        initplaces();

        getCurrentPlace();

        sliderView = findViewById(R.id.imageSlider);
        adapter = new SliderAdapterExample();

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();

    }

    private void getPhotoandDetail(String place_id) {
        FetchPlaceRequest request=FetchPlaceRequest.builder(place_id,Arrays.asList(Place.Field.PHOTO_METADATAS)).build();
         placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
             @Override
             public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                 Place place = fetchPlaceResponse.getPlace();

                 allphotos = place.getPhotoMetadatas();
                 Log.d(TAG, String.valueOf(allphotos.size()));

                  if(allphotos!=null) {
                      for (int i = 0; i < allphotos.size(); i++) {
                          FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(allphotos.get(i)).build();
                          placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                              @Override
                              public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                                  Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                  Uri uri = getImageUri(Destination.this, bitmap);
                                  ImageUri.add(uri);
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Toast.makeText(Destination.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                              }
                          });
                      }
                      adapter.datachanged(ImageUri);
                      ImageUri.clear();
                  }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(Destination.this,e.getMessage(),Toast.LENGTH_SHORT).show();
             }
         });
    }

    private void requestpermission() {
       Dexter.withActivity(this)
               .withPermissions(
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_COARSE_LOCATION,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE
               ).withListener(new MultiplePermissionsListener() {
           @Override
           public void onPermissionsChecked(MultiplePermissionsReport report) {

           }

           @Override
           public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                  Toast.makeText(Destination.this,"You must enable this permission",Toast.LENGTH_SHORT).show();
           }
       }).check();
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void getCurrentPlace() {
        Log.d("main","getcurrentactivity");
        final FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placefields).build();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<FindCurrentPlaceResponse> placeResponseTask = placesClient.findCurrentPlace(request);
        placeResponseTask.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
               FindCurrentPlaceResponse response=task.getResult();

               Log.d("main","taskcomplete");
                Collections.sort(response.getPlaceLikelihoods(), new Comparator<PlaceLikelihood>() {
                    @Override
                    public int compare(PlaceLikelihood o1, PlaceLikelihood o2) {
                        return new Double(o1.getLikelihood()).compareTo(o2.getLikelihood());
                    }
                });
                Collections.reverse(response.getPlaceLikelihoods());
                place_id=response.getPlaceLikelihoods().get(0).getPlace().getId();

                Toast.makeText(Destination.this,response.getPlaceLikelihoods().get(0).getPlace().getAddress()+response.getPlaceLikelihoods().get(0).getPlace().getLatLng(),Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                  Toast.makeText(Destination.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupPlacesAutocomplete() {
        places_fragment=(AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);

        places_fragment.setPlaceFields(placefields);

        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(Destination.this,""+place.getName(),Toast.LENGTH_SHORT).show();
                getPhotoandDetail(place.getId());

            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Destination.this,""+status.getStatusMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initplaces() {
        Places.initialize(this,getString(R.string.places_api_key));
        placesClient=Places.createClient(this);
    }
}

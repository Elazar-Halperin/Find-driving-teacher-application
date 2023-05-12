package com.elazarhalperin.fluentify.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.elazarhalperin.fluentify.Models.TeacherModel;
import com.elazarhalperin.fluentify.Models.UserModel;
import com.elazarhalperin.fluentify.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.grpc.Context;

public class EditProfileActivity extends AppCompatActivity {
    private static final int GO_TO_GALLERY_CODE = 121;

    FloatingActionButton fab_goBack;
    Button btn_save;

    EditText et_name, et_email, et_lessonPrice, et_info;

    ImageView iv_profileImage;

    FirebaseUser firebaseUser;
    StorageReference storageReference;

    Drawable updatedProfileImage;

    UserModel userModel;
    TeacherModel teacherModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fab_goBack = findViewById(R.id.fab_goBack);

        btn_save = findViewById(R.id.btn_save);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_lessonPrice = findViewById(R.id.et_lessonPrice);
        et_info = findViewById(R.id.et_info);

        iv_profileImage = findViewById(R.id.iv_profileImage);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures").child("profile_image" + firebaseUser.getUid() + ".jpg");

        teacherModel = getIntent().getParcelableExtra("teacherModel");
        userModel = getIntent().getParcelableExtra("userModel");


        putImageProfile();
        setListeners();
    }

    private void putImageProfile() {
        long MEGABYTE = 1024 * 1024;
        storageReference.getBytes(MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_profileImage.setImageBitmap(bitmap);
            }
        });
    }

    private void setListeners() {
        fab_goBack.setOnClickListener(v-> {
            finish();
        });

        btn_save.setOnClickListener(v-> {
            saveUserDetails();
        });

        iv_profileImage.setOnClickListener(v-> {
            openGallery();
        });
    }

    private void openGallery() {
        Intent intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentImage, GO_TO_GALLERY_CODE);
    }

    private void saveUserDetails() {
        if(isValidFields()) {
            return;
        }
    }

    private boolean isValidFields() {


        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GO_TO_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            iv_profileImage.setImageURI(data.getData());
            updatedProfileImage = iv_profileImage.getDrawable();
        }
    }
}
package com.example.quanlysinhvien; // Giữ nguyên dòng package của máy bạn

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.concurrent.Executors;

public class Student implements Serializable {
    public int id;
    public String name;
    public String mssv;
    public String email;
    public String avatar;

    public Student(int id, String name, String mssv, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.mssv = mssv;
        this.email = email;
        this.avatar = avatar;
    }

    // Hàm hỗ trợ hiển thị cả ảnh từ Thiết bị lẫn ảnh từ Link URL
    public static void loadAvatar(ImageView imageView, String avatarStr) {
        imageView.setImageResource(android.R.drawable.sym_def_app_icon);
        if (avatarStr == null || avatarStr.trim().isEmpty()) return;

        if (avatarStr.startsWith("http://") || avatarStr.startsWith("https://")) {
            Executors.newSingleThreadExecutor().execute(() -> {
                try (InputStream in = new URL(avatarStr).openStream()) {
                    Bitmap bmp = BitmapFactory.decodeStream(in);
                    if (bmp != null) {
                        new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(bmp));
                    }
                } catch (Exception ignored) {}
            });
        } else {
            try {
                imageView.setImageURI(Uri.parse(avatarStr));
            } catch (Exception ignored) {}
        }
    }
}
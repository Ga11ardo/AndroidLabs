package com.labs.lab4;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StorageUtils {
    public static File copyToInternal(Context ctx, Uri uri, String name) throws IOException {
        String sanitizedName = name.replaceAll("[/\\\\:]", "_");
        File dest = new File(ctx.getFilesDir(), sanitizedName);
        try (InputStream in = ctx.getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(dest)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        }
        return dest;
    }

    public static File downloadUrl(Context ctx, String urlStr) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            String baseUrl = url.getProtocol() + "://" + url.getHost();
            connection.setRequestProperty("Referer", baseUrl);

            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 400) {
                throw new IOException("Server returned non-OK status: " + responseCode + " " + connection.getResponseMessage());
            }

            String fileName = "";
            String disposition = connection.getHeaderField("Content-Disposition");
            if (disposition != null) {
                String dispositionLower = disposition.toLowerCase();
                if (dispositionLower.contains("filename=")) {
                    fileName = disposition.substring(dispositionLower.indexOf("filename=") + 9).replace("\"", "");
                }
            }

            if (TextUtils.isEmpty(fileName)) {
                fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
            }
            if (TextUtils.isEmpty(fileName)) {
                fileName = "downloaded_file_" + System.currentTimeMillis();
            }

            String sanitizedName = fileName.replaceAll("[/\\\\:?\"<>|*]", "_");
            File dest = new File(ctx.getFilesDir(), sanitizedName);

            try (InputStream in = connection.getInputStream();
                 OutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return dest;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}